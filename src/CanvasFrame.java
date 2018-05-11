import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class CanvasFrame extends JFrame {
	
	private int width;
	private int height;
	private Container container;
	private JButton clear, black, red, blue, yellow, green, eraser, five, ten, twenty;
	private Canvas canvas;
	private WatchCanvas watchCanvas;
	private JPanel buttonPanel;
	private JPanel info;
	private JLabel playerName;
	private JLabel artistName;
	
	private String name, ip;
	private int playerID;
    private int artistIndex;
    private int teamNum;
	private ReadFromServer rfsRunnable;
	private WriteToServer wtsRunnable;
	private Socket socket;
	private int oldX, oldY, currX, currY;

	public CanvasFrame(int w, int h, String n, String i) {
		canvas = new Canvas();
		watchCanvas = new WatchCanvas();
		width = w;
		height = h;
		container = this.getContentPane();
		name = n;
		ip = i;
	}
	public void setUpFrame() {
		this.setSize(width, height);
		this.setTitle("Player #" + playerID);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		five = new JButton("5");
		ten = new JButton("10");
		twenty = new JButton("20");
		clear = new JButton("Clear");
		black = new JButton("Black");
		red = new JButton("Red");
		blue = new JButton("Blue");
		yellow = new JButton("Yellow");
		green = new JButton("Green");
		eraser = new JButton("Eraser");
		info = new JPanel();
		buttonPanel = new JPanel();
		playerName = new JLabel("Your Name: " + name);
		artistName = new JLabel("Your Team Artist: ");
		container.setLayout(new BorderLayout());
		if(playerID == artistIndex)
			container.add(canvas, BorderLayout.CENTER);
		else
			container.add(watchCanvas, BorderLayout.CENTER);			
		buttonPanel.setLayout(new GridLayout(1,10));
		buttonPanel.add(five);
		buttonPanel.add(ten);
		buttonPanel.add(twenty);
		buttonPanel.add(black);
		buttonPanel.add(red);
		buttonPanel.add(blue);
		buttonPanel.add(yellow);
		buttonPanel.add(green);
		buttonPanel.add(eraser);
		buttonPanel.add(clear);
		container.add(buttonPanel, BorderLayout.SOUTH);
//		WaitingScreen ws = new WaitingScreen();
//		ws.setUpGUI();
//		ws.setVisible(true);
		this.setVisible(true);
		this.getContentPane().setBackground(Color.WHITE);
	}
	
	public void setUpButtons() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if ( ae.getSource() == clear ) {
					canvas.clear();
				} else if ( ae.getSource() == black ) {
					canvas.black();
				} else if ( ae.getSource() == red ) {
					canvas.red();
				} else if ( ae.getSource() == blue ) {
					canvas.blue();
				} else if ( ae.getSource() == yellow ) {
					canvas.yellow();
				} else if ( ae.getSource() == green ) {
					canvas.green();
				} else if ( ae.getSource() == eraser ) {
					canvas.eraser();
				} else if ( ae.getSource() == five ) {
					canvas.set5();
				} else if ( ae.getSource() == ten ) {
					canvas.set10();
				} else {
					canvas.set20();
				}
			}
		};
		five.addActionListener(al);
		ten.addActionListener(al);
		twenty.addActionListener(al);
		clear.addActionListener(al);
		black.addActionListener(al);
		red.addActionListener(al);
		blue.addActionListener(al);
		yellow.addActionListener(al);
		green.addActionListener(al);
		eraser.addActionListener(al);
	}
	
	public void connectToServer() {
    	try {
			socket = new Socket("localhost", 45371);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			playerID = in.readInt();
			System.out.println("You are player#" + playerID);
			rfsRunnable = new ReadFromServer(in);
			wtsRunnable = new WriteToServer(out);
			Thread readThread = new Thread(rfsRunnable);
			Thread writeThread = new Thread(wtsRunnable);
			readThread.start();
			writeThread.start();
		} catch(IOException ex) {
			System.out.println("IOException from connectToServer()");
		}
    }
	
	private class ReadFromServer implements Runnable {
		
		private DataInputStream dataIn;
		
		public ReadFromServer(DataInputStream in) {
			dataIn = in;
			System.out.println("RFS Runnable created");
		}
		
		public void run() {
			try {
				teamNum = dataIn.readInt();
				artistIndex = dataIn.readInt();
				System.out.println("Team Num #" + teamNum);
				System.out.println("Artist Num #" + artistIndex);
				
				while(true) {
					if(artistIndex != playerID) {
						oldX = dataIn.readInt();
						oldY = dataIn.readInt();
						currX = dataIn.readInt();
						currY = dataIn.readInt();
						watchCanvas.setNewCoords(oldX, oldY, currX, currY);
						watchCanvas.repaint();
					}
				}
				
			} catch(IOException ex) {
				System.out.println("IOException from RFS run()");
			}
		}
	}

	private class WriteToServer implements Runnable {
		
		private DataOutputStream dataOut;
		
		public WriteToServer(DataOutputStream out) {
			dataOut = out;
			System.out.println("WTC Runnable created");
		}
		
		public void run() {
			try {
				if(playerID == artistIndex)
					dataOut.writeUTF(name);
				dataOut.flush();
				
				while(true) {
					oldX = canvas.getOldX();
					oldY = canvas.getOldY();
					currX = canvas.getCurrX();
					currY = canvas.getCurrY();
					dataOut.writeInt(oldX);
					dataOut.writeInt(oldY);
					dataOut.writeInt(currX);
					dataOut.writeInt(currY);
					try {
						Thread.sleep(25);
					} catch(InterruptedException ex) {
						System.out.println("InterruptedException from WTS run()");
					}
				}
				
			} catch(IOException ex) {
				System.out.println("IOException from WTS run()");
			}
		}	
	}
	
	public static void main(String[] args) {
		CanvasFrame cf = new CanvasFrame(800, 640, "Hello", "localhost");
		cf.connectToServer();
		cf.setUpFrame();
		cf.setUpButtons();
	}
}
