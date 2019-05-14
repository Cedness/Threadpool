package de.ced.threadpool;

import java.util.ArrayList;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ThreadPool {
	
	private ArrayList<Thread> threads = new ArrayList<>();
	private ArrayList<Worker> workers = new ArrayList<>();
	
	private ArrayList<Task> waiting = new ArrayList<>();
	private ArrayList<ArrayList<Task>> waitingPerWorker = new ArrayList<>();
	private ArrayList<Task> running = new ArrayList<>();
	private ArrayList<Integer> done = new ArrayList<>();
	
	private static final int LOGMODE_COUNT = 2;
	private final boolean[] logmode;
	
	private final ArrayList<Listener> listeners = new ArrayList<>();
	
	public ThreadPool(int workerCount, boolean[] logmode) {
		synchronized (this) {
			this.logmode = logmode != null && LOGMODE_COUNT == logmode.length ? logmode : new boolean[LOGMODE_COUNT];
			print("Creating ThreadPool");
			increaseWorkerCount(workerCount);
			print("ThreadPool creation done");
		}
	}
	
	public ThreadPool(int workerCount) {
		this(workerCount, null);
	}
	
	public ThreadPool(boolean[] logmode) {
		this(Runtime.getRuntime().availableProcessors(), logmode);
	}
	
	public ThreadPool() {
		this(null);
	}
	
	private void addWorker() {
		Worker worker = new Worker(this, logmode);
		Thread thread = new Thread(worker, "Worker " + worker.getID());
		
		threads.add(thread);
		workers.add(worker);
		waitingPerWorker.add(new ArrayList<>());
		thread.start();
		
		for (Listener listener : listeners) {
			listener.increaseWorkerCount();
		}
	}
	
	private void removeWorker() {
		workers.remove(workers.size() - 1).destroy();
		threads.remove(threads.size() - 1);
		waitingPerWorker.remove(waitingPerWorker.size() - 1);
		
		for (Listener listener : listeners) {
			listener.decreaseWorkerCount();
		}
		
		notifyAll();
	}
	
	public void increaseWorkerCount(int increment) {
		boolean inc = increment > 0;
		for (int i = 0; i < Math.abs(increment); i++) {
			if (inc) addWorker();
			else removeWorker();
		}
		print("Worker Count: " + getWorkerCount());
	}
	
	public int getWorkerCount() {
		return workers.size();
	}
	
	public void setWorkerCount(int newWorkerCount) {
		increaseWorkerCount(newWorkerCount - getWorkerCount());
	}
	
	public void lockWorker(int worker, boolean locked) {
		workers.get(worker).setLocked(locked);
	}
	
	public synchronized void destroy() {
		setWorkerCount(0);
	}
	
	void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	@SuppressWarnings("StatementWithEmptyBody")
	void printState() {
		double aRaw = Math.sqrt(getWorkerCount());
		int a = (int) aRaw;
		if (a < aRaw)
			a++;
		
		int workerID = 0;
		int maxIDLength = String.valueOf(a * a).length();
		
		synchronized (System.out) {
			StringBuilder lineBuilder = new StringBuilder();
			for (int i = 1; i < a; i++) {
				lineBuilder.append("------------");
			}
			String line = lineBuilder.toString();
			System.out.println("\n" + line + "---ThreadPool State---" + line);
			System.out.println("Waiting: " + waiting.size());
			System.out.println("Running: " + running.size());
			System.out.println("Done: " + done.size());
			for (int i = 0; i < a; i++) {
				for (int j = 0; j < a; j++) {
					Worker worker;
					try {
						worker = workers.get(workerID);
					} catch (IndexOutOfBoundsException e) {
						break;
					}
					String id = String.valueOf(worker.getID());
					for (; id.length() < maxIDLength; id = " " + id) ;
					String state = worker.getState().name().toLowerCase();
					Task currentTask = worker.getTask();
					String task = currentTask != null ? String.valueOf(currentTask.hashCode()) : "          ";
					for (; task.length() < 10; task = " " + task) ;
					
					System.out.print(id + " [" + state + "] [" + task + "]  ");
					
					workerID++;
				}
				System.out.println();
			}
			
			System.out.println(line + "----------------------" + line + "\n");
		}
	}
	
	private void print(String s) {
		if (logmode[0])
			System.out.println("[X] " + s);
	}
	
	synchronized void finish(Task task) {
		running.remove(task);
		done.add(task.hashCode());
		if (done.size() > 10 * workers.size())
			done.remove(0);
	}
	
	synchronized boolean allDone(Worker worker) {
		return (waiting.isEmpty() || worker.isLocked()) && waitingPerWorker.get(worker.getID()).isEmpty();
	}
	
	synchronized Task begin(Worker worker) {
		ArrayList<Task> waitingForThis = waitingPerWorker.get(worker.getID());
		Task task = waitingForThis.isEmpty() ? (waiting.remove(0)) : waitingForThis.remove(0);
		
		running.add(task);
		
		return task;
	}
	
	public synchronized void addTask(Task task) {
		if (task == null)
			return;
		
		waiting.add(task);
		notifyAll();
	}
	
	public synchronized void addTask(Task task, int worker) {
		if (task == null)
			return;
		
		waitingPerWorker.get(worker).add(task);
		notifyAll();
	}
}
