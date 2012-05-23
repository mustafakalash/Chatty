package com.kashew;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends NetWorker
{
  static Socket s = null;
  static ServerSocket ss = null;
  private String locIP;

  public String getlocIP()
  {
    return this.locIP;
  }

  public void run() {
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    try
    {
      ss = new ServerSocket(20554);
      this.locIP = ss.getInetAddress().getHostAddress();
    } catch (Exception e) {
      e.printStackTrace();
    }
    while (true) {
      try {
        s = ss.accept();
        ChatClient.mainPane.addTab(get().getUser(), ChatClient.mainPanel);
        ChatClient.window.setTitle("Chatty - Hosting");
        out = new ObjectOutputStream(s.getOutputStream());
        in = new ObjectInputStream(s.getInputStream());
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        while (true) {
          out.writeObject(this.send);
          this.got = ((Packet)in.readObject());
        }
      } catch (Exception e) {
        ChatClient.showDisconnect();
      }
    }
  }
}