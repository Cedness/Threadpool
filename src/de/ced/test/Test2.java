package de.ced.test;

import de.ced.threadpool.ForLoop;
import de.ced.threadpool.Loop;
import de.ced.threadpool.Task;
import de.ced.threadpool.ThreadPool;

class Test2 {
	
	private Test2() {
		ThreadPool threadPool = new ThreadPool();
		String[] cancer = {"lol", "rofl", "kopter"};
		Loop loop = new ForLoop(threadPool, 0, 10, 1, new SuperTestTask());
		
		synchronized (this) {
			threadPool.addTask(loop);
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		threadPool.destroy();
	}
	
	private class SuperTestTask implements Task {
		@Override
		public void run(int argument) {
			print(String.valueOf(argument));
		}
	}
	
	public static void main(String[] args) {
		new Test2();
	}
}
