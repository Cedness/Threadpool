package de.ced.threadpool;

import java.util.ArrayList;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Loop implements Task, Listener {
	
	private final ThreadPool threadPool;
	
	private Task doneTask;
	private ArrayList<Task> tasks = new ArrayList<>();
	private boolean broken = false;
	
	Loop(ThreadPool threadPool, Task doneTask) {
		this.threadPool = threadPool;
		this.doneTask = doneTask;
		threadPool.addListener(this);
	}
	
	public synchronized void setDoneTask(Task doneTask) {
		this.doneTask = doneTask;
	}
	
	@Override
	public synchronized void run() {
		for (int i = threadPool.getWorkerCount(); i > 0; i--) {
			increaseWorkerCount();
		}
	}
	
	abstract boolean condition();
	
	abstract void increment();
	
	abstract Task addTask();
	
	abstract Task reAddTask(InternalSurroundTask task);
	
	@Override
	public synchronized void increaseWorkerCount() {
		head();
		if (broken)
			return;
		Task task = addTask();
		threadPool.addTask(task);
		tasks.add(task);
		increment();
	}
	
	@Override
	public synchronized void decreaseWorkerCount() {
		tasks.remove(0);
	}
	
	private void head() {
		if (!condition())
			breakLoop();
	}
	
	private synchronized void end(InternalSurroundTask task) {
		head();
		if (broken)
			tasks.remove(task);
		if (!tasks.contains(task)) {
			if (tasks.isEmpty() && doneTask != null) {
				doneTask.run();
			}
			return;
		}
		threadPool.addTask(reAddTask(task));
		increment();
	}
	
	class InternalSurroundTask implements Task {
		private final Task task;
		private final Loop loop;
		
		InternalSurroundTask(Task task, Loop loop) {
			this.task = task;
			this.loop = loop;
		}
		
		Task getTask() {
			return task;
		}
		
		@Override
		public void run() {
			task.run();
			loop.end(this);
		}
	}
	
	public synchronized void breakLoop() {
		broken = true;
		threadPool.removeListener(this);
	}
}
