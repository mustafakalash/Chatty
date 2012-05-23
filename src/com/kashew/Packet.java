package com.kashew;

import java.io.Serializable;

public class Packet implements Serializable{
	private static final long serialVersionUID = -4853696701653384959L;
	private String userName;
	private String message;
	private int id;
	public Packet(String user, String msg, int id){
		userName=user;
		message=msg;
		this.id=id;
	}
	public String getUser(){
		return userName;
	}
	public String getMessage(){
		return message;
	}
	public int getID(){
		return id;
	}

}
