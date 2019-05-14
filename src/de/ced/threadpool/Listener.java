package de.ced.threadpool;

interface Listener {
	
	void increaseWorkerCount();
	
	void decreaseWorkerCount();
}
