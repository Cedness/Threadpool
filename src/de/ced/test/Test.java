package de.ced.test;

import de.ced.threadpool.*;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Thread.sleep;

class Test {
	
	private ArrayList<String> test = new ArrayList<>(Arrays.asList("x", "y"));
	
	private Test() throws InterruptedException {
		ThreadPool threadPool = new ThreadPool(8, new boolean[]{false, false});
		
		Loop fillLoop = new ForLoop(threadPool, 0, 10, 1, new Filler());
		Loop printLoop = new IterationLoop(threadPool, new String[]{"a", "b", "c"}, new Printer());
		fillLoop.setDoneTask(printLoop);
		
		
		Loop loop = new WhileLoop(threadPool, new TestTask2());
		
		threadPool.addTask(printLoop);
		
		sleep(1000);
		
	}
	
	private class Filler implements Task {
		@Override
		public void run() {
			synchronized (test) {
				test.add(String.valueOf((int) (100 * Math.random())));
			}
		}
	}
	
	private class Printer implements Task {
		@Override
		public void run(Object argument) {
			System.out.println(argument);
		}
	}
	
	public static void main(String[] args) {
		try {
			new Test();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
