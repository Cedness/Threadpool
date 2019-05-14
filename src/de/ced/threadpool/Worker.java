package de.ced.threadpool;

import static de.ced.threadpool.Worker.WorkerState.*;

@SuppressWarnings("WeakerAccess")
class Worker implements Runnable {
	
	private static int workerCount = 0;
	
	private final int id;
	private WorkerState state = BUSY;
	private boolean locked = false;
	private final ThreadPool threadPool;
	private final boolean[] logmode;
	private volatile boolean running = true;
	private Task task;
	
	Worker(ThreadPool threadPool, boolean[] logmode) {
		id = workerCount++;
		this.threadPool = threadPool;
		this.logmode = logmode;
		print("created");
	}
	
	int getID() {
		return id;
	}
	
	WorkerState getState() {
		return state;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	Task getTask() {
		return task;
	}
	
	void destroy() {
		running = false;
	}
	
	@Override
	public void run() {
		long timestamp;
		double time;
		
		run:
		while (running) {
			
			////
			
			synchronized (threadPool) {
				while (threadPool.allDone(this)) {
					state = IDLE;
					print("idle");
					try {
						threadPool.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!running)
						break run;
				}
				state = BUSY;
				task = threadPool.begin(this);
			}
			
			print("next Task");
			timestamp = System.nanoTime();
			
			//noinspection SynchronizeOnNonFinalField
			synchronized (task) {
				task.run();
			}
			
			//noinspection IntegerDivisionInFloatingPointContext
			time = ((System.nanoTime() - timestamp) / 1000) / 1000d;
			threadPool.finish(task);
			task = null;
			
			state = DONE;
			print("done in " + time + " ms");
			
			////
			
		}
		state = DESTROYED;
		print("destroyed");
	}
	
	private void print(String s) {
		if (logmode[0])
			System.out.println("[Worker " + id + "] " + s);
		else if (logmode[1])
			threadPool.printState();
	}
	
	public enum WorkerState {
		IDLE,
		BUSY,
		DONE,
		DESTROYED
	}
}
