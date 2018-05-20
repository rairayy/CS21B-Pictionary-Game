import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
	private JPanel buttonPanel, info, canvasPanel;
	private JLabel playerName, artistNameL, teamMembersL, typeAnswer;
	private JTextField answer;
	private JButton sendAnswer;
	private String answerString;
	
	private String name, ip, teamMembers, artistName;
	private int playerID;
    private int artistIndex;
    private int teamNum;
    private int mePoints, enemyPoints;
    private int roundNum, maxRounds;
    
	private ReadFromServer rfsRunnable;
	private WriteToServer wtsRunnable;
	private Socket socket;
	
	private int setting;
		
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
		width = w;
		height = h;
		container = this.getContentPane();
		name = n;
		ip = i;
		artistName = "dani";
		name = "raymond";
		teamMembers = "riana\ncisco\ncasey";
		answerString = "";
		mePoints = 0;
		enemyPoints = 0;
		roundNum = 1;
		maxRounds = 5;
		setting = 1;
		
		// Canvas center
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(600,576));
		watchCanvas = new WatchCanvas();
		watchCanvas.setPreferredSize(new Dimension(600,576));
		canvasPanel = new JPanel();
		canvasPanel.setPreferredSize(new Dimension(600,576));
		
		// Buttons south
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
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,10));
		buttonPanel.setPreferredSize(new Dimension(800,58));

		// Info east
		playerName = new JLabel("Your Name: " + name);
		artistNameL = new JLabel("Your Team Artist: " + artistName);
		teamMembersL = new JLabel(teamMembers);
		typeAnswer = new JLabel("Type Answer Here:");
		sendAnswer = new JButton("Submit");
		sendAnswer.setMaximumSize(new Dimension(Integer.MAX_VALUE, sendAnswer.getMinimumSize().height));
		answer = new JTextField(10);
		answer.setMaximumSize(new Dimension(Integer.MAX_VALUE, answer.getMinimumSize().height));
		info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.PAGE_AXIS));
		info.setPreferredSize(new Dimension(200,576));
		info.setBorder(new EmptyBorder(10, 10, 10, 10));
	}
	
	/**
	 * Method that sets up the CanvasFrame.
	 */
	public void setUpFrame() {
		this.getContentPane().setPreferredSize(new Dimension(width, height));
		this.pack();
		this.setTitle("Player #" + playerID);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setLayout(new BorderLayout());
		
		// Buttons south
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
		
		// Info east
		info.add(playerName);
		info.add(artistNameL);
		info.add(teamMembersL);
		info.add(typeAnswer);
		info.add(answer);
		info.add(sendAnswer);
		container.add(info, BorderLayout.EAST);
		
		this.getContentPane().setBackground(Color.WHITE);
	}
	
	/**
	 * Method that updates the canvas and its visibility.
	 * 
	 * @param v Boolean that indicates if the canvas should be visible or not.
	 */
	public void updateVisibility(boolean v) {
		if(v) {
			if(playerID == artistIndex) {
				System.out.println("Canvas added");
				canvasPanel.removeAll();
				canvasPanel.add(canvas);
				container.add(canvasPanel, BorderLayout.CENTER);
				container.revalidate();
			}
			else {
				System.out.println("Watch Canvas added");
				canvasPanel.removeAll();
				canvasPanel.add(watchCanvas);
				container.add(canvasPanel, BorderLayout.CENTER);
				container.revalidate();
			}
		}
		this.setVisible(v);
	}
	
	/**
	 * Sets up the buttons.
	 */
	public void setUpButtons() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if ( ae.getSource() == clear ) {
					canvas.clear();
					wtsRunnable.sendClear();
				} else if ( ae.getSource() == black ) {
					canvas.black();
					setting = 1;
				} else if ( ae.getSource() == red ) {
					canvas.red();
					setting = 2;
				} else if ( ae.getSource() == blue ) {
					canvas.blue();
					setting = 3;
				} else if ( ae.getSource() == yellow ) {
					canvas.yellow();
					setting = 4;
				} else if ( ae.getSource() == green ) {
					canvas.green();
					setting = 5;
				} else if ( ae.getSource() == eraser ) {
					canvas.eraser();
					setting = 6;
				} else if ( ae.getSource() == five ) {
					canvas.set5();
					setting = 7;
				} else if ( ae.getSource() == ten ) {
					canvas.set10();
					setting = 8;
				} else if ( ae.getSource() == sendAnswer ) {
					answerString = answer.getText();
					answer.setText("");
				} else {
					canvas.set20();
					setting = 9;
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
		sendAnswer.addActionListener(al);
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
				updateVisibility(true);
				System.out.println("Team Num #" + teamNum);
				System.out.println("Artist Num #" + artistIndex);
				System.out.println("C Width: " + canvas.getSize().width);
				System.out.println("C Height: " + canvas.getSize().height);
				System.out.println("WC Width: " + watchCanvas.getSize().width);
				System.out.println("WC Height: " + watchCanvas.getSize().height);
				boolean roundEnd = true;
				while(true) {
					if(roundEnd) {
						if(artistIndex != playerID && artistIndex != 0) {
							String xyCoords = (String) dataIn.readUnshared();
							watchCanvas.receiveCoords(xyCoords);
							watchCanvas.repaint();
						}
						
						int roundWinner = dataIn.readInt();
						if(roundWinner != 0) {
							System.out.println("Round ends");
							if(teamNum == 1) {							
								mePoints = dataIn.readInt();
								enemyPoints = dataIn.readInt();
							} else {
								enemyPoints = dataIn.readInt();
								mePoints = dataIn.readInt();
							}
							String winningMessage = dataIn.readUTF();
							System.out.println(winningMessage);
							updateVisibility(false);						
							if ( artistIndex == playerID ) {
								canvas.empty();
								wtsRunnable.dataOut.writeUnshared("done");
							} else {
								watchCanvas.empty();
							}
							artistIndex = dataIn.readInt();
							updateVisibility(true);
							roundEnd = false;
							wtsRunnable.dataOut.reset();
							continue;
						} 
					} else {
						setting = 1;
						roundEnd = dataIn.readBoolean();
						System.out.println("Round started: " + roundEnd);
					}
				}
			} catch(IOException ex) {
				System.out.println("IOException from RFS run()");
				ex.printStackTrace();
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
						int fromClientSettings = setting;
						
						ArrayList<Integer> xCoords = canvas.getXCoords();
						ArrayList<Integer> yCoords = canvas.getYCoords();
						CopyOnWriteArrayList<Integer> xCoords2 = new CopyOnWriteArrayList<Integer>(xCoords);
						CopyOnWriteArrayList<Integer> yCoords2 = new CopyOnWriteArrayList<Integer>(yCoords);
						String x = xCoords2.toString();
						String y = yCoords2.toString();
						String z = fromClientSettings+x+y;
						if(fromClientSettings != 0) {
							z = fromClientSettings+x+y;
						} else {
							z = "0";
						}
						if(xCoords.size() > 0) {
							dataOut.writeUnshared(z);
							dataOut.flush();
						}
					} else {
						if ( answerString.length() > 0 ) {
							dataOut.writeUnshared(answerString);
							System.out.println("Guess is " + answerString);
							dataOut.flush();
							answerString = "";
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
		
		public void sendClear() {
			try {
				dataOut.writeUnshared("0");
				dataOut.flush();
			} catch(IOException ex) {
				System.out.println("IOException from sendClear()");
			}
		}
	}
}
