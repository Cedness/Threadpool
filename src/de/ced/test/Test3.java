package de.ced.test;

import de.ced.threadpool.Task;
import de.ced.threadpool.ThreadPool;

class Test3 {
	
	private Test3() {
		ThreadPool threadPool = new ThreadPool(8, new boolean[]{true, false});
		
		threadPool.lockWorker(0, true);
		threadPool.lockWorker(1, true);
		
		threadPool.addTask(new Task() {
			@Override
			public void run() {
				print("lol");
				print("hi");
			}
		}, 0);
		
		
	}
	
	public static void main(String[] args) {
		new Test3();
	}
}
