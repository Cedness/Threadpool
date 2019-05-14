package de.ced.threadpool;

@SuppressWarnings({"unused", "WeakerAccess"})
public class WhileLoop extends Loop {
	
	private Task loopTask;
	
	public WhileLoop(ThreadPool threadPool, Task loopTask, Task doneTask) {
		super(threadPool, doneTask);
		this.loopTask = loopTask;
	}
	
	public WhileLoop(ThreadPool threadPool, Task loopTask) {
		this(threadPool, loopTask, null);
	}
	
	@Override
	boolean condition() {
		return true;
	}
	
	@Override
	void increment() {
	}
	
	@Override
	Task addTask() {
		return new InternalSurroundTask(loopTask, this);
	}
	
	@Override
	Task reAddTask(InternalSurroundTask task) {
		return task;
	}
}
