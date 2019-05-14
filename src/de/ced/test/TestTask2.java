package de.ced.test;

import de.ced.threadpool.Task;

class TestTask2 implements Task {
	
	@Override
	public void run() {
		print("hi");
	}
}
