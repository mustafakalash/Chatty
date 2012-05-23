package com.kashew;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.print.attribute.standard.Media;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

public class ChatClient extends JFrame {
	private static final long serialVersionUID = 153585732743185407L;
	public static final String VERSION="V0.5.0";
	private static String lineSep;
	private static NetWorker netWorker;
	private static String userName;
	private static int idinc;
	private static int lastID=-1;
	private Dimension windowSize = new Dimension(320, 240);
	static JTabbedPane mainPane = new JTabbedPane();
	static JPanel mainPanel = new JPanel(new BorderLayout());
	static JTextPane chatPane = new JTextPane();
	static ChatClient window;
	Boolean hide = false;
	static Clip notif;
	static Clip secretSong;
	public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			window = new ChatClient();
    			window.setVisible(true);
    			window.addWindowListener(new WindowListener() {
    				@Override
    	            public void windowClosing(WindowEvent arg0) {
 /*  					if(Client.getSocket() != null) {
    						try {
								Client.getSocket().close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    					} else if(Server.getSocket() != null & Server.getServerSocket() != null) {
    						try {
    							Server.getSocket().close();
    							Server.getServerSocket().close();
    						} catch(IOException e) {
    							
    						}
    					} */
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
			System.out.println(usrField.getText()+", "+ipField.getText());
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
	public ChatClient() {
		chatPane.setEditable(false);
		chatPane.setText("Welcome to Chatty! Say hi!");
		setTitle("Chatty");
		setSize(windowSize);
		try {
			notif = loadAudioFile(System.getProperty("user.dir")+"/lib/notif.wav");
			secretSong = loadAudioFile(System.getProperty("user.dir")+"/lib/clndnce.wav");
		} catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
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
		JPanel typePanel = new JPanel();
		add(mainPane);
		JScrollPane scrollPane = new JScrollPane(chatPane);
		mainPanel.add(typePanel, BorderLayout.SOUTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		typePanel.add(chatField);
		typePanel.add(sendButton);
		init();
	}
	public void init() {
		lineSep=System.getProperty("line.separator");
	}
	public Clip loadAudioFile(String filename) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		File file = new File(filename);
		System.out.println(filename.toString());
		AudioInputStream stream = AudioSystem.getAudioInputStream(file);
		AudioFormat format = stream.getFormat();
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		Clip clip = (Clip) AudioSystem.getLine(info);
		clip.open(stream);
		return clip;
	}
	public static void update() {
		Packet p = netWorker.get();
		if(p != null){
			if(p.getID()!=-1&&p.getID()!=lastID){
				if(!window.isFocused()){
					notif.setFramePosition(0);
					notif.start();
					window.toFront();
				}
				if(p.getMessage().startsWith("/n")){
					secretSong.setFramePosition(0);
					secretSong.start();
					chatPane.setText(chatPane.getText()+lineSep+p.getUser()+" has started the clown dance!");
				}else{
					chatPane.setText(chatPane.getText()+lineSep+p.getUser()+": "+p.getMessage());
				}
				mainPane.setTitleAt(0, p.getUser());
				lastID=p.getID();
			}
		}
	}
	public static void initNetwork(boolean host, String ip) {
		if(host == true){
			netWorker = new Server();
			netWorker.start();
			System.out.println("Congrats! You are now hosting!");
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
			String nUserName=input.replaceFirst("/name ", "");
			netWorker.send(new Packet(userName, "has changed their name to"+nUserName, idinc));
			userName = nUserName;
			chatPane.setText(chatPane.getText()+lineSep+"Your name is now "+userName);
			idinc++;
		}else if(input.toLowerCase().contains("/help")){
			chatPane.setText(chatPane.getText()+lineSep+"Commands:"+lineSep+"/help returns these commands, duh."+lineSep+"/name changes your name."+lineSep+"/me speaks in third person"+lineSep+"/exit exits the application.");
		}else if(input.toLowerCase().contains("/exit")){
			chatPane.setText(chatPane.getText()+lineSep+"Bye!");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
		}else if(input.toLowerCase().contains("/me")){
			String message = input.replace("/me", userName);
			chatPane.setText(chatPane.getText()+lineSep+message);
			netWorker.send(new Packet(userName,message,idinc));
			idinc++;
		}else if(input.toLowerCase().startsWith("/n")){
			secretSong.setFramePosition(0);
			secretSong.start();
			chatPane.setText(chatPane.getText()+lineSep+userName+" has started the clown dance!");
			netWorker.send(new Packet(userName,"/n",idinc));
		}else{
			netWorker.send(new Packet(userName, input,idinc));
			chatPane.setText(chatPane.getText()+lineSep+userName+":"+input);
			idinc++;
		}
		
		
	}
}
