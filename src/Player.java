import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class for setting up the CanvasFrame. Extends JFrame.
 */
public class Player extends JFrame {
	
	private int width;
	private int height;
	private Container container;
	private JButton clear, black, red, blue, yellow, green, eraser, five, ten, twenty;
	private Canvas canvas;
	private WatchCanvas watchCanvas;
	private JPanel buttonPanel, info, canvasPanel, messagePanel;
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
	
	private String color;
	private String thickness;
		
	/**
	 * 
	 * Constructor for CanvasFrame()
	 * 
	 * @param w width of canvas
	 * @param h height of canvas
	 * @param i ip address
	 */
	public Player(int w, int h, String i) {
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
		
		BufferedImage fiveIcon, tenIcon, twentyIcon, clearIcon, blackIcon, redIcon, blueIcon, yellowIcon, greenIcon, eraserIcon;
		try {
			fiveIcon = ImageIO.read(new File("five.png"));
			tenIcon = ImageIO.read(new File("ten.png"));
			twentyIcon = ImageIO.read(new File("twenty.png"));
			clearIcon = ImageIO.read(new File("clear.png"));
			blackIcon = ImageIO.read(new File("black.png"));
			redIcon = ImageIO.read(new File("red.png"));
			blueIcon = ImageIO.read(new File("blue.png"));
			yellowIcon = ImageIO.read(new File("yellow.png"));
			greenIcon = ImageIO.read(new File("green.png"));
			eraserIcon = ImageIO.read(new File("eraser.png"));
			
			five = new JButton(new ImageIcon(fiveIcon));
			five.setBorder(BorderFactory.createEmptyBorder());
			five.setContentAreaFilled(false);
			ten = new JButton(new ImageIcon(tenIcon));
			ten.setBorder(BorderFactory.createEmptyBorder());
			ten.setContentAreaFilled(false);
			twenty = new JButton(new ImageIcon(twentyIcon));
			twenty.setBorder(BorderFactory.createEmptyBorder());
			twenty.setContentAreaFilled(false);
			clear = new JButton(new ImageIcon(clearIcon));
			clear.setBorder(BorderFactory.createEmptyBorder());
			clear.setContentAreaFilled(false);
			black = new JButton(new ImageIcon(blackIcon));
			black.setBorder(BorderFactory.createEmptyBorder());
			black.setContentAreaFilled(false);
			red = new JButton(new ImageIcon(redIcon));
			red.setBorder(BorderFactory.createEmptyBorder());
			red.setContentAreaFilled(false);
			blue = new JButton(new ImageIcon(blueIcon));
			blue.setBorder(BorderFactory.createEmptyBorder());
			blue.setContentAreaFilled(false);
			yellow = new JButton(new ImageIcon(yellowIcon));
			yellow.setBorder(BorderFactory.createEmptyBorder());
			yellow.setContentAreaFilled(false);
			green = new JButton(new ImageIcon(greenIcon));
			green.setBorder(BorderFactory.createEmptyBorder());
			green.setContentAreaFilled(false);
			eraser = new JButton(new ImageIcon(eraserIcon));
			eraser.setBorder(BorderFactory.createEmptyBorder());
			eraser.setContentAreaFilled(false);
		} catch (IOException e) {
			System.out.println("IOException in CanvasFrame()");
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
		}
		
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
		info.setBorder(new EmptyBorder(20, 10, 10, 10));
		messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.PAGE_AXIS));
		messagePanel.setBorder(new EmptyBorder(20,0,20,0));
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
				messagePanel.removeAll();
				
				// Canvas west
				canvasPanel.add(canvas);
				container.add(canvasPanel, BorderLayout.WEST);
				
				// Info east
				teamString = "Your Team Number is: " + teamNum;
				artistString = "You are the artist.";
				teamLabel = new JLabel(teamString);
				artistLabel = new JLabel(artistString);
				messageLabel = new JLabel("<html>Your word is: " + wordToGuess + ".<br>You may start drawing<br>in 7 seconds.</html>");
				info.add(teamLabel);
				info.add(artistLabel);
				messagePanel.add(messageLabel);
				info.add(messagePanel);
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
				messagePanel.removeAll();

				// Canvas west
				canvasPanel.add(watchCanvas);
				container.add(canvasPanel, BorderLayout.WEST);
				
				// Info east
				teamString = "Your Team Number is: " + teamNum;
				artistString = "Wait for your artist.";
				teamLabel = new JLabel(teamString);
				artistLabel = new JLabel(artistString);
				messageLabel = new JLabel("<html>The artist will<br>start drawing<br>in 7 seconds.</html>");
				info.add(teamLabel);
				info.add(artistLabel);
				messagePanel.add(messageLabel);
				info.add(messagePanel);
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
	
	
	/**
	 * Checks if the game is over and who the winning team is
	 * 
	 * @return boolean to determine if game has ended
	 */
	private boolean checkWinner() {
		if(roundNum > maxRounds) {
			artistLabel.setText("<html>My Team's Points: " + mePoints +"<br>Enemy Team's Points: " + enemyPoints + "</html>");
			if(mePoints > enemyPoints && teamNum == 1) {
				this.setTitle("Team 1 has won. Congratulations!");
				messageLabel.setText("CONGRATULATIONS!");
			} else if(mePoints > enemyPoints && teamNum == 2) {
				this.setTitle("Team 2 has won. Congratulations!");
				messageLabel.setText("CONGRATULATIONS!");
			} else if(mePoints < enemyPoints && teamNum == 1) {
				this.setTitle("Team 2 has won. Better luck next time!");
				messageLabel.setText("YOU'LL GET 'EM NEXT TIME!");
			} else {
				this.setTitle("YOU'LL GET 'EM NEXT TIME!");
			}
			this.setButtons(false);
			this.setFields(false);
			this.setVisible(true);
			canvas.changeEnabled(false);
			return true;
		}
		return false;
	}
	
	/**
	 * Changes the message label according to who the artist is. Uses a timer to count down the pre-round.
	 */
	private void changeMessageLabel() {
		canvas.changeEnabled(false);
		setButtons(false);
		setFields(false);
		Timer timer = new Timer(1000, new ActionListener() {
			int counter = 7;
			public void actionPerformed(ActionEvent ae) {
				if(playerID == artistIndex) {
					messageLabel.setText("<html>Your word is: " + wordToGuess + ".<br>You may start <br>drawing in " + counter + " seconds.</html>");
					counter--;
					if(counter == 0) {
						messageLabel.setText("<html>Start drawing! Your word<br>is: " + wordToGuess +"</html>");
						setButtons(true);
						setFields(false);
						((Timer)ae.getSource()).stop();
						canvas.changeEnabled(true);
						revalidate();
					}
				} else {
					messageLabel.setText("<html>The artist will start<br>drawing in " + counter + " seconds.</html>");
					counter--;
					if(counter == 0) {
						messageLabel.setText("<html>Start guessing!</html>");
						setButtons(false);
						setFields(true);
						((Timer)ae.getSource()).stop();
						canvas.changeEnabled(true);
						revalidate();
					}
				}
			}
		});
		timer.start();
	}
	
	/**
	 * Sets the initial message of the messageLabel.
	 */
	private void initialMessage() {
		if(playerID == artistIndex) {
			messageLabel.setText("<html>Your word is: " + wordToGuess + ".<br>You may start <br>drawing in 7 seconds.</html>");
		} else {
			messageLabel.setText("<html>The artist will start<br>drawing in 7 seconds.</html>");
		}
		
		changeMessageLabel();
	}
	
	/**
	 * Changes the title of the screen at the start of each round to show the winner of the previous round
	 * 
	 * @param roundWinner team that won the round
	 * @param currRound round that just ended
	 */
	private void endOfRoundScreen(int roundWinner, int currRound) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				setTitle("Team " + roundWinner + " won Round " + currRound);
				messageLabel.setText("The word was: " + wordToGuess);
				Timer timer = new Timer(7000, new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						setTitle("Player #" + playerID);
					}
				});
				timer.start();
				timer.setRepeats(false);
			}
		});
		t.start();
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
				changeMessageLabel();
				
				System.out.println("Team Num #" + teamNum);
				System.out.println("Artist Num #" + artistIndex);
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
								endOfRoundScreen(roundWinner, roundNum-1);
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
