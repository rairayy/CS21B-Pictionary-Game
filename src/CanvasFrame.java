import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Class for setting up the CanvasFrame. Extends JFrame.
 */
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
		
	private boolean mousePressed, mouseDragged;
	
	/**
	 * 
	 * Constructor for CanvasFrame()
	 * 
	 * @param w width of canvas
	 * @param h height of canvas
	 * @param n name of player
	 * @param i ip address
	 */
	public CanvasFrame(int w, int h, String n, String i) {
		canvas = new Canvas();
		watchCanvas = new WatchCanvas();
		width = w;
		height = h;
		container = this.getContentPane();
		name = n;
		ip = i;
		mousePressed = false;
		mouseDragged = false;
	}
	
	/**
	 * Sets up the CanvasFrame()
	 */
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
//		this.setVisible(true);
		this.getContentPane().setBackground(Color.WHITE);
	}
	
	public void updateVisibility() {
		if(playerID == artistIndex) {
			System.out.println("Canvas added");
			container.add(canvas, BorderLayout.CENTER);
		}
		else {
			System.out.println("Watch Canvas added");
			container.add(watchCanvas, BorderLayout.CENTER);
		}
		this.setVisible(true);
	}
	
	/*
	 * AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
	 */
	public void setMouse() {
		watchCanvas.setMousePressed(mousePressed);
		watchCanvas.setMouseDragged(mouseDragged);
	}
	
	/**
	 * Sets up the buttons
	 */
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
	
	/**
	 * Connects the CanvasFrame to the server
	 */
	public void connectToServer() {
    	try {
			socket = new Socket("localhost", 45371);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
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
	
	/**
	 * Inner class that reads from the server
	 * Implements Runnable
	 */
	private class ReadFromServer implements Runnable {
		
		private ObjectInputStream dataIn;
		
		public ReadFromServer(ObjectInputStream in) {
			dataIn = in;
			System.out.println("RFS Runnable created");
		}
		
		public void run() {
			try {
				teamNum = dataIn.readInt();
				artistIndex = dataIn.readInt();
				updateVisibility();
				System.out.println("Team Num #" + teamNum);
				System.out.println("Artist Num #" + artistIndex);
				while(true) {
					if(artistIndex != playerID && artistIndex != 0) {
//						ArrayList<Integer> xCoords = new ArrayList<Integer>();
//						ArrayList<Integer> yCoords = new ArrayList<Integer>();
						ArrayList<Integer> xCoords = (ArrayList<Integer>) dataIn.readUnshared();
						ArrayList<Integer> yCoords = (ArrayList<Integer>) dataIn.readUnshared();
//						int size = dataIn.readInt();
//						int size = dataIn.readInt();
						int size = xCoords.size();
						
						for(int i = 0; i < size; i++) {
//							int currX = dataIn.readInt();
//							int currY = dataIn.readInt();
							if(currX != 0 && currY != 0) {
//								xCoords.add(currX);
//								yCoords.add(currY);
								System.out.println("Frame: " + currX + ", " + currY);
							}
						}
//						if(yCoords.size() > 0)
//							System.out.println("CanvasFrame Size:" + yCoords.size());
						watchCanvas.setArrayList(xCoords, yCoords);
//						oldX = dataIn.readInt();
//						System.out.println(xCoords.get(i+1) + yCoords.get(i+1));
//						oldY = dataIn.readInt();
//						currX = dataIn.readInt();
//						currY = dataIn.readInt();
//						watchCanvas.setNewCoords(oldX, oldY, currX, currY);
						watchCanvas.repaint();
					}
//					try {
//						Thread.sleep(25);
//					} catch(InterruptedException ex) {
//						System.out.println("InterruptedException from WTC run()");
//					}
				}
				
			} catch(IOException ex) {
				System.out.println("IOException from RFS run()");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Class that writes to the server
	 * Implements Runnable 
	 */
	private class WriteToServer implements Runnable {
		
		private ObjectOutputStream dataOut;
		
		public WriteToServer(ObjectOutputStream out) {
			dataOut = out;
			System.out.println("WTC Runnable created");
		}
		
		public void run() {
			try {
//				if(playerID == artistIndex)
//					dataOut.writeUTF(name);
//				dataOut.flush();
				
				while(true) {
					if(playerID == artistIndex) {
						ArrayList<Integer> xCoords = canvas.getXCoords();
						ArrayList<Integer> yCoords = canvas.getYCoords();
//						dataOut.writeInt(xCoords.size());
//						dataOut.flush();
						if(xCoords.size() > 0) {
							dataOut.writeUnshared(xCoords);
							dataOut.writeUnshared(yCoords);
							for(int i = 0; i < xCoords.size(); i++) {
	//							if(xCoords.get(i) != 0 && yCoords.get(i) != 0) {
	//								dataOut.writeInt(xCoords.get(i));
	//								dataOut.writeInt(yCoords.get(i));
									if(xCoords.get(i) != 0 && yCoords.get(i) != 0)
										System.out.println("Artist: " + xCoords.get(i) + ", " + yCoords.get(i));
	//							}
							}
							dataOut.flush();
						}
					}
//					oldX = canvas.getOldX();
//					oldY = canvas.getOldY();
//					currX = canvas.getCurrX();
//					currY = canvas.getCurrY();
//					dataOut.writeInt(oldX);
//					dataOut.writeInt(oldY);
//					dataOut.writeInt(currX);
//					dataOut.writeInt(currY);
//					mousePressed = canvas.getMousePressed();
//					mouseDragged = canvas.getMouseDragged();
//					System.out.println(mousePressed);
//					System.out.println(mouseDragged);
					try {
						Thread.sleep(5);
					} catch(InterruptedException ex) {
						System.out.println("InterruptedException from WTS run()");
					}
				}
				
			} catch(IOException ex) {
				System.out.println("IOException from WTS run()");
			}
		}	
	}
	
	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CanvasFrame cf = new CanvasFrame(800, 640, "Hello", "localhost");
		cf.connectToServer();
		cf.setUpFrame();
		cf.setUpButtons();
	}
}
