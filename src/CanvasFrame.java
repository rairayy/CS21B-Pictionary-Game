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
	private WaitingScreen ws;
	private JButton clear, black, red, blue, yellow, green, eraser, five, ten, twenty;
	private Canvas canvas;
	private WatchCanvas watchCanvas;
	private JPanel buttonPanel, info, canvasPanel;
	private JLabel typeAnswer, artistLabel, teamLabel, messageLabel;
	private JTextField answer;
	private JButton sendAnswer;
	private String answerString, artistString, teamString;
	
	private String ip;
	private int playerID;
    private int artistIndex;
    private int teamNum;
    private int mePoints, enemyPoints;
    private int roundNum;
    private final int maxRounds;
    private String wordToGuess;
    
	private ReadFromServer rfsRunnable;
	private WriteToServer wtsRunnable;
	private Socket socket;
	
//	private int setting;
	private String color;
	private String thickness;
		
	/**
	 * 
	 * Constructor for CanvasFrame()
	 * 
	 * @param w width of canvas
	 * @param h height of canvas
	 * @param n name of player
	 * @param i ip address
	 */
	public CanvasFrame(int w, int h, String i) {
		width = w;
		height = h;
		container = this.getContentPane();
		ip = i;
		answerString = "";
		mePoints = 0;
		enemyPoints = 0;
		roundNum = 1;
		maxRounds = 5;
		color = "1";
		thickness = "7";
		
		artistString = "";
		teamString = "";
		wordToGuess = "";
		
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
		typeAnswer = new JLabel("Type Answer Here:");
		sendAnswer = new JButton("Submit");
		sendAnswer.setMaximumSize(new Dimension(Integer.MAX_VALUE, sendAnswer.getMinimumSize().height));
		answer = new JTextField(10);
		answer.setMaximumSize(new Dimension(Integer.MAX_VALUE, answer.getMinimumSize().height));
		info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.PAGE_AXIS));
		info.setPreferredSize(new Dimension(200,576));
		info.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		ws = new WaitingScreen();
		Thread t = new Thread(ws);
		t.start();
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
				info.removeAll();
				
				// Canvas west
				canvasPanel.add(canvas);
				container.add(canvasPanel, BorderLayout.WEST);
				
				// Info east
				teamString = "Your Team Number is: " + teamNum;
				artistString = "You are the artist.";
				teamLabel = new JLabel(teamString);
				artistLabel = new JLabel(artistString);
				messageLabel = new JLabel("Your word is: " + wordToGuess + ". You may start drawing in 5 seconds.");
				info.add(teamLabel);
				info.add(artistLabel);
				info.add(messageLabel);
				info.add(typeAnswer);
				info.add(answer);
				info.add(sendAnswer);
				container.add(info, BorderLayout.EAST);
				
				container.revalidate();
				
				setButtons(true);
				setFields(false);
			}
			else {
				System.out.println("Watch Canvas added");
				canvasPanel.removeAll();
				info.removeAll();

				// Canvas west
				canvasPanel.add(watchCanvas);
				container.add(canvasPanel, BorderLayout.WEST);
				
				// Info east
				teamString = "Your Team Number is: " + teamNum;
				artistString = "Wait for your artist.";
				teamLabel = new JLabel(teamString);
				artistLabel = new JLabel(artistString);
				messageLabel = new JLabel("The artist will start drawing in 5 seconds.");
				info.add(teamLabel);
				info.add(artistLabel);
				info.add(messageLabel);
				info.add(typeAnswer);
				info.add(answer);
				info.add(sendAnswer);
				container.add(info, BorderLayout.EAST);
				
				container.revalidate();
				
				setButtons(false);
				setFields(true);
			}
		}
		this.setVisible(v);
	}
	
	/**
	 * Method that sets the buttons depending on player role.
	 * 
	 * @param v Boolean that indicates if buttons should be enabled or disabled.
	 */
	public void setButtons(boolean b) {
		black.setEnabled(b);
		red.setEnabled(b);
		blue.setEnabled(b);
		yellow.setEnabled(b);
		green.setEnabled(b);
		eraser.setEnabled(b);
		five.setEnabled(b);
		ten.setEnabled(b);
		twenty.setEnabled(b);
		clear.setEnabled(b);
	}
	
	/**
	 * Method that sets the text field and submit button depending on the player's role.
	 * 
	 * @param b Boolean that indicates if field and button should be enabled or disabled.
	 */
	public void setFields(boolean b) {
		sendAnswer.setEnabled(b);
		answer.setEditable(b);
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
					color = "1";
				} else if ( ae.getSource() == red ) {
					canvas.red();
					color = "2";
				} else if ( ae.getSource() == blue ) {
					canvas.blue();
					color = "3";
				} else if ( ae.getSource() == yellow ) {
					canvas.yellow();
					color = "4";
				} else if ( ae.getSource() == green ) {
					canvas.green();
					color = "5";
				} else if ( ae.getSource() == eraser ) {
					canvas.eraser();
					color = "6";
				} else if ( ae.getSource() == five ) {
					canvas.set5();
					thickness = "7";
				} else if ( ae.getSource() == ten ) {
					canvas.set10();
					thickness = "8";
				} else if ( ae.getSource() == sendAnswer ) {
					answerString = answer.getText();
					answer.setText("");
				} else {
					canvas.set20();
					thickness = "9";
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
			socket = new Socket(ip, 45371);
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
	
	private boolean checkWinner() {
		if(roundNum > maxRounds) {
			System.out.println("Rounds: " + roundNum);
			System.out.println("Me: " + mePoints);
			System.out.println("Enemy: " + enemyPoints);
			if(mePoints > enemyPoints && teamNum == 1) {
				this.setTitle("Team 1 has won. Congratulations!");
			} else if(mePoints > enemyPoints && teamNum == 2) {
				this.setTitle("Team 2 has won. Congratulations!");
			} else if(mePoints < enemyPoints && teamNum == 1) {
				this.setTitle("Team 2 has won. Better luck next time!");
			} else {
				this.setTitle("Team 1 has won. Better luck next time!");
			}
			this.setButtons(false);
			this.setFields(false);
			this.setVisible(true);
			return true;
		}
		return false;
	}
	
	private void changeMessageLabel() {
		canvas.changeEnabled(false);
		Timer timer = new Timer(7000, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(playerID == artistIndex) {
					messageLabel.setText("Start drawing! Your word is: " + wordToGuess);
				} else {
					messageLabel.setText("Start guessing!");
				}
				canvas.changeEnabled(true);
				revalidate();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	private void initialMessage() {
		if(playerID == artistIndex) {
			messageLabel.setText("Your word is: " + wordToGuess + ". You may start drawing in 5 seconds.");
		} else {
			messageLabel.setText("The artist will start drawing in 5 seconds.");
		}
		
		changeMessageLabel();
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
				wordToGuess = dataIn.readUTF();
				updateVisibility(true);
				ws.closeWaitingScreen();
				changeMessageLabel();
				
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
							roundNum++;
							roundEnd = false;
							if(checkWinner()) {
								dataIn.close();
								break;
							} else {
								wtsRunnable.dataOut.reset();
								updateVisibility(true);
							}
							continue;
						} 
					} else {
						color = "1";
						thickness = "7";
						wordToGuess = dataIn.readUTF();
						roundEnd = dataIn.readBoolean();
						initialMessage();
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
						String fromClientSettings = color + thickness;
						
						ArrayList<Integer> xCoords = canvas.getXCoords();
						ArrayList<Integer> yCoords = canvas.getYCoords();
						CopyOnWriteArrayList<Integer> xCoords2 = new CopyOnWriteArrayList<Integer>(xCoords);
						CopyOnWriteArrayList<Integer> yCoords2 = new CopyOnWriteArrayList<Integer>(yCoords);
						String x = xCoords2.toString();
						String y = yCoords2.toString();
						String z = fromClientSettings+x+y;
//						if(!fromClientSettings.equals("00")) {
//							z = fromClientSettings+x+y;
//						} else {
//							z = "0";
//						}
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
					if(checkWinner()) {
						dataOut.close();
						break;
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
