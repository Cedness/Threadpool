package de.ced.test;

import de.ced.threadpool.Task;

class TestTask implements Task {
	
	@Override
	public void run() {
		double x = 2;
		for (int i = 0; i < (int) (Math.random() * 2000000000); i++) {
			x += Math.sqrt(17);
		}
		print(String.valueOf(x));
	}
}
