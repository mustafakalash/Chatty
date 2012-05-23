package com.kashew;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends NetWorker {
	Socket s = null;
	private String ip;
	public Client(String ip){
		this.ip=ip;
	}
	@Override
	public void run() {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		
		try{
			s = new Socket(ip,PORT);
			out = new ObjectOutputStream(s.getOutputStream());
			in  = new ObjectInputStream(s.getInputStream());
			ChatClient.mainPane.addTab(get().getUser(), ChatClient.mainPanel);
		}catch(Exception e){
			e.printStackTrace();
		}
		while(true){
			try {
				got=(Packet) in.readObject();
				out.writeObject(send);
			} catch (Exception e){
				System.out.println(System.getProperty("line.separator")+"Server is shutting down...");
				ChatClient.showOption();
				break;
			}
		}	
	}
}
