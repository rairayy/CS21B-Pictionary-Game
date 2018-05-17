import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

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
	 * Inner class that reads from the server. Implements Runnable.
	 */
	private class ReadFromServer implements Runnable {
		
		private ObjectInputStream dataIn;
		
		/**
		 * Constructor for class ReadFromServer.
		 * 
		 * @param in ObjectInputStream object.
		 */
		public ReadFromServer(ObjectInputStream in) {
			dataIn = in;
			System.out.println("RFS Runnable created");
		}
		
		/**
		 * Method that runs ReadFromServer.
		 */
		public void run() {
			try {
				teamNum = dataIn.readInt();
				artistIndex = dataIn.readInt();
				updateVisibility();
				System.out.println("Team Num #" + teamNum);
				System.out.println("Artist Num #" + artistIndex);
				while(true) {
					if(artistIndex != playerID && artistIndex != 0) {
						String xyCoords = (String) dataIn.readUnshared();
						watchCanvas.receiveCoords(xyCoords);
						watchCanvas.repaint();
					}
				}
			} catch(IOException ex) {
				System.out.println("IOException from RFS run()");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Class that writes to the server. Implements Runnable.
	 */
	private class WriteToServer implements Runnable {
		
		private ObjectOutputStream dataOut;
		
		/**
		 * Constructor for class WriteToServer.
		 * 
		 * @param out ObjectOutputStream object.
		 */
		public WriteToServer(ObjectOutputStream out) {
			dataOut = out;
			System.out.println("WTC Runnable created");
		}
		
		/**
		 * Method that runs WriteToServer.
		 */
		public void run() {
			try {
				while(true) {
					if(playerID == artistIndex) {
						ArrayList<Integer> xCoords = canvas.getXCoords();
						ArrayList<Integer> yCoords = canvas.getYCoords();
						CopyOnWriteArrayList<Integer> xCoords2 = new CopyOnWriteArrayList<Integer>(xCoords);
						CopyOnWriteArrayList<Integer> yCoords2 = new CopyOnWriteArrayList<Integer>(yCoords);
						String x = xCoords2.toString();
						String y = yCoords2.toString();
						String z = x+y;
						if(xCoords.size() > 0) {
							dataOut.writeUnshared(z);
							dataOut.flush();
						}
					}
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
	 * Main method.
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
