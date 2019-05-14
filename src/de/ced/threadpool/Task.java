package de.ced.threadpool;

public interface Task extends Runnable {
	
	@Override
	default void run() {
	
	}
	
	default void run(int i) {
		run();
	}
	
	default void run(Object argument) {
		run();
	}
	
	default void print(String s) {
		String name = Thread.currentThread().getName();
		System.out.println("[" + ("main".equals(name) ? "X" : name) + "] -> " + s);
	}
}
