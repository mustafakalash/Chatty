package com.kashew;

public class Ticker implements Runnable {
	public Ticker() {
		Thread t = new Thread(this);
		t.start();
	}
	public void run() {
		while(true) {
			ChatClient.update();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
