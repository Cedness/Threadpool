package de.ced.threadpool;

import java.util.Collection;

@SuppressWarnings({"unused", "WeakerAccess"})
public class IterationLoop extends Loop {
	
	private Object[] objects;
	private Collection<?> collection = null;
	private final Task loopTask;
	private int current = 0;
	
	private IterationLoop(ThreadPool threadPool, Task loopTask, Task doneTask) {
		super(threadPool, doneTask);
		this.loopTask = loopTask;
	}
	
	public IterationLoop(ThreadPool threadPool, Object[] objects, Task loopTask, Task doneTask) {
		this(threadPool, loopTask, doneTask);
		this.objects = objects.clone();
	}
	
	public IterationLoop(ThreadPool threadPool, Object[] objects, Task loopTask) {
		this(threadPool, objects, loopTask, null);
	}
	
	public IterationLoop(ThreadPool threadPool, Collection<?> objects, Task loopTask, Task doneTask) {
		this(threadPool, loopTask, doneTask);
		collection = objects;
	}
	
	public IterationLoop(ThreadPool threadPool, Collection<?> objects, Task loopTask) {
		this(threadPool, objects, loopTask, null);
	}
	
	@Override
	public void run() {
		if (collection != null) {
			objects = collection.toArray();
			collection = null;
		}
		super.run();
	}
	
	@Override
	boolean condition() {
		return current < objects.length;
	}
	
	@Override
	void increment() {
		current++;
	}
	
	@Override
	Task reAddTask(InternalSurroundTask task) {
		((InternalArgumentTask) task.getTask()).setArgument(objects[current]);
		return task;
	}
	
	@Override
	Task addTask() {
		return new InternalSurroundTask(new InternalArgumentTask(objects[current], loopTask), this);
	}
	
	private class InternalArgumentTask implements Task {
		private Object argument;
		private final Task task;
		
		InternalArgumentTask(Object argument, Task task) {
			this.argument = argument;
			this.task = task;
		}
		
		void setArgument(Object argument) {
			this.argument = argument;
		}
		
		@Override
		public void run() {
			task.run(argument);
		}
	}
}
