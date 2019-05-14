package de.ced.threadpool;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ForLoop extends Loop {
	
	private int i;
	private int limit;
	private int increment;
	private final Task loopTask;
	
	public ForLoop(ThreadPool threadPool, int start, int limit, int increment, Task loopTask, Task doneTask) {
		super(threadPool, doneTask);
		this.i = start;
		this.limit = limit;
		this.increment = increment;
		this.loopTask = loopTask;
	}
	
	public ForLoop(ThreadPool threadPool, int start, int limit, int increment, Task loopTask) {
		this(threadPool, start, limit, increment, loopTask, null);
	}
	
	@Override
	boolean condition() {
		return i != limit;
	}
	
	@Override
	void increment() {
		i += increment;
	}
	
	@Override
	Task reAddTask(InternalSurroundTask task) {
		((InternalIntTask) task.getTask()).setI(i);
		return task;
	}
	
	@Override
	Task addTask() {
		return new InternalSurroundTask(new InternalIntTask(i, loopTask), this);
	}
	
	private class InternalIntTask implements Task {
		private int i;
		private final Task task;
		
		InternalIntTask(int i, Task task) {
			this.i = i;
			this.task = task;
		}
		
		void setI(int i) {
			this.i = i;
		}
		
		@Override
		public void run() {
			task.run(i);
		}
	}
}
