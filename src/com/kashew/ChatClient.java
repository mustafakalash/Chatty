package com.kashew;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class ChatClient extends JFrame {
	private static final long serialVersionUID = 153585732743185407L;
	public static final String VERSION="V0.4.7";
	private static String lineSep;
	private static NetWorker netWorker;
	private static String userName;
	private static int idinc;
	private static int lastID=-1;
	private Dimension windowSize = new Dimension(320, 240);
	static JTabbedPane mainPane = new JTabbedPane();
	static JPanel mainPanel = new JPanel(new BorderLayout());
	static JTextArea chatPane = new JTextArea();
	static ChatClient window;
	Boolean hide = false;
	static File notif = new File(System.getProperty("user.dir")+"/lib/notif.WAV");
	static File iconFile = new File(System.getProperty("user.dir")+"/lib/chatty.png");
	static File trayIconFile = new File(System.getProperty("user.dir")+"/lib/tray.png");
	static TrayIcon trayIcon = new TrayIcon(new ImageIcon(trayIconFile.toString(), "Tray icon").getImage());
	SystemTray tray = SystemTray.getSystemTray();
	static Clip clip;
	static AudioInputStream ais;
	public ChatClient() {
		Image icon = null;
		try {
			icon = ImageIO.read(iconFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		chatPane.setEditable(false);
		chatPane.setText("Welcome to Chatty! Say hi!");
		chatPane.setLineWrap(true);
		setTitle("Chatty");
		setIconImage(icon);
		setSize(windowSize);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		final JTextField chatField = new JTextField();
		chatField.setColumns(20);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = chatField.getText();
				if(input.length() > 0) {
					checkCommands(input);
					chatField.setText("");
				}
			}
		});
		chatField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = chatField.getText();
				if(input.length() > 0) {
					checkCommands(input);
					chatField.setText("");
				}
			}
		});
		try {
			tray.add(trayIcon);
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JPanel typePanel = new JPanel();
		add(mainPane);
		JScrollPane scrollPane = new JScrollPane(chatPane);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(windowSize);
		mainPanel.add(typePanel, BorderLayout.SOUTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		typePanel.add(chatField);
		typePanel.add(sendButton);
		init();
	}
	public static void showDisconnect() {
		try {
			clip = AudioSystem.getClip();
			ais = AudioSystem.getAudioInputStream(ChatClient.class.getResourceAsStream(notif.toString()));
			clip.open(ais);
			clip.start();
		} catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Chat closed.", "Notice", JOptionPane.ERROR_MESSAGE);
		if(!window.hasFocus()) {
			trayIcon.displayMessage(null, "Chat closed.", TrayIcon.MessageType.ERROR);
		}
		showOption();
	}
	public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			window = new ChatClient();
    			window.setVisible(true);
    			window.addWindowListener(new WindowListener() {
    				@Override
    	            public void windowClosing(WindowEvent arg0) {
  					if(Client.s != null) {
    						try {
								Client.s.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    					} else if(Server.s != null & Server.ss != null) {
    						try {
    							Server.s.close();
    							Server.ss.close();
    						} catch(IOException e) {
    							
    						}
    					}
    	            }
					@Override
					public void windowActivated(WindowEvent e) {
					}
					@Override
					public void windowClosed(WindowEvent e) {
					}
					@Override
					public void windowDeactivated(WindowEvent e) {
					}
					@Override
					public void windowDeiconified(WindowEvent e) {
					}
					@Override
					public void windowIconified(WindowEvent e) {
					}
					public void windowOpened(WindowEvent e) {
						showOption();
					}
    	        });
    		}
    	});
	}
	public static void showOption() {
		JTextField ipField = new JTextField();
	    JTextField usrField = new JTextField();
	    Object[] options = {"Host", "Connect", "Close"};
		Object[] inputs = new Object[] {
                new JLabel("Username:"),
                usrField,
				new JLabel("IP (not required if hosting):"),
                ipField,
		};
		int result = JOptionPane.showOptionDialog(null, inputs, "Chatty", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
		if(result == JOptionPane.YES_OPTION) {
			if(usrField.getText().length() > 0) {
				userName = usrField.getText();
				initNetwork(true, "localhost");
				new Ticker();
			} else {
				JOptionPane.showMessageDialog(null, "You must enter a username.", "Error!", JOptionPane.ERROR_MESSAGE);
				showOption();
			}
		} else if(result == JOptionPane.NO_OPTION) {
			if(usrField.getText().length() > 0 & ipField.getText().length() > 0) {
				userName = usrField.getText();
				String ip = ipField.getText();
				initNetwork(false, ip);
				new Ticker();
			} else {
				JOptionPane.showMessageDialog(null, "You must enter a username and a valid IP.", "Error!", JOptionPane.ERROR_MESSAGE);
				showOption();
			}
		} else if(result == JOptionPane.CANCEL_OPTION) {
			System.exit(0);
		}
	}
	public void init() {
		lineSep=System.getProperty("line.separator");
	}
	public static void update() {
		Packet p = netWorker.get();
		if(p != null){
			if(p.getID()!=-1&&p.getID()!=lastID){
				if(p.getMessage().length() > 0) {
					if(p.getMessage().toLowerCase().contains("/me")){
						chatPane.append(lineSep+p.getMessage().replace("/me", p.getUser()));
					} else {
						chatPane.append(lineSep+p.getUser()+": "+p.getMessage());
					}
				} else {
					chatPane.append(lineSep+"Your partner has changed their name to "+p.getUser());
				}
				if(!window.hasFocus()) {
					try {
						clip = AudioSystem.getClip();
						ais = AudioSystem.getAudioInputStream(ChatClient.class.getResourceAsStream(notif.toString()));
						clip.open(ais);
						clip.start();
					} catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					trayIcon.displayMessage(null, p.getMessage(), TrayIcon.MessageType.NONE);
				}
				chatPane.selectAll();
				mainPane.setTitleAt(0, p.getUser());
				lastID=p.getID();
			}
		}
	}
	public static void initNetwork(boolean host, String ip) {
		if(host == true){
			netWorker = new Server();
			netWorker.start();
			window.setTitle("Chatty - Waiting for client");
			netWorker.send(new Packet(userName,"Connected.",idinc));
			idinc++;
		} else {
			netWorker = new Client(ip);
			netWorker.start();
			netWorker.send(new Packet(userName,"Connected.",idinc));
			idinc++;
			window.setTitle("Chatty - Connected to: "+ip);
		}
	}
	public void checkCommands(String input){
		if(input.toLowerCase().contains("/name")){
			userName=input.replaceFirst("/name ", "");
			netWorker.send(new Packet(userName, "", idinc));
			chatPane.append(lineSep+"Your name is now "+userName+".");
			idinc++;
		}else if(input.toLowerCase().contains("/disconnect")) {
			if(Client.s != null) {
				try {
					Client.s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(Server.s != null & Server.ss != null) {
				try {
					Server.s.close();
					Server.ss.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else if(input.toLowerCase().contains("/help")){
			chatPane.append(lineSep+"Commands:"+lineSep+"/help returns these commands, duh."+lineSep+"/name changes your name."+lineSep+"/me speaks in third person"+lineSep+"/exit exits the application."+lineSep+"/disconnect closes the chat, but leaves the application open.");
		}else if(input.toLowerCase().contains("/exit")){
			chatPane.append(lineSep+"Bye!");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
		}else if(input.toLowerCase().contains("/me")){
			netWorker.send(new Packet(userName,input,idinc));
			String message = input.replace("/me", userName);
			chatPane.append(lineSep+message);
			idinc++;
		}else{
			netWorker.send(new Packet(userName, input,idinc));
			chatPane.append(lineSep+userName+": "+input);
			idinc++;
		}
	}
}