package com.kashew;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends NetWorker {
	Socket s = null;
	static ServerSocket ss = null;
	private String locIP;
	public Server(){
	}
	public String getlocIP(){
		return locIP;
	}
	@Override
	public void run() {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		
		try{
			ss = new ServerSocket(PORT);
			locIP = ss.getInetAddress().getHostAddress();
		}catch(Exception e){
			e.printStackTrace();
		}
		while(true){
			try{
				s = ss.accept();
				System.out.println(System.getProperty("line.separator")+"Client connected from "+s.getInetAddress());
				ChatClient.mainPane.addTab(get().getUser(), ChatClient.mainPanel);
				ChatClient.window.setTitle("Chatty - Hosting");
				out = new ObjectOutputStream(s.getOutputStream());
				in  = new ObjectInputStream(s.getInputStream());
			}catch(Exception e){
				e.printStackTrace();
			}
			while(true){
				try {
					out.writeObject(send);
					got=(Packet) in.readObject();
				} catch (Exception e){
					System.out.println(System.getProperty("line.separator")+"Client has disconnected. Waiting for new client.");
					ChatClient.showOption();
					break;
				}
			}
		}	
	}
}
