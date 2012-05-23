package com.kashew;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends NetWorker
{
  static Socket s = null;
  private String ip;

  public Client(String ip)
  {
    this.ip = ip;
  }

  public void run() {
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    try
    {
      s = new Socket(this.ip, 20554);
      out = new ObjectOutputStream(s.getOutputStream());
      in = new ObjectInputStream(s.getInputStream());
      ChatClient.mainPane.addTab(get().getUser(), ChatClient.mainPanel);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      while (true) {
        this.got = ((Packet)in.readObject());
        out.writeObject(this.send);
      }
    } catch (Exception e) {
      ChatClient.showDisconnect();
    }
  }
}