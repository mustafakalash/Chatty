package com.kashew;

public abstract class NetWorker implements Runnable{
	protected static final int PORT=20554;
	protected Packet send,got;
	protected ChatClient chatClient;
	public NetWorker(){
	}
	public void send(Packet p){
		send = p;
	}
	public Packet get(){
		return got;
	}
	public void start(){
		send = new Packet("[null]","[null]",-1);
		got = new Packet("[null]","[null]",-1);
		Thread t = new Thread(this);
		t.start();
	}
	public abstract void run();
	
	
	
}
