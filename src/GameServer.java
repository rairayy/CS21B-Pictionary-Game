
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Class for the game server.
 */
public class GameServer {
	
	private ServerSocket ss;
	private int numPlayers, totalNumPlayers, team1Points, team2Points;
	private boolean continueAccepting;
	private int artistIndex1, artistIndex2, roundNum, maxRounds;
	private String artist1Name, artist2Name;
	
	private ArrayList<ReadFromClient> readRunnables;
	private ArrayList<WriteToClient> writeRunnables;
	private ArrayList<String> guesses;
	
	private String xyCoords1, xyCoords2;

	/**
	 * Constructor for class GameServer.
	 */
	public GameServer() {
		System.out.println("==== GAME SERVER ====");
		numPlayers = 0;
		team1Points = 0;
		team2Points = 0;
		roundNum = 1;
		maxRounds = 5;
		continueAccepting = true;
		readRunnables = new ArrayList<ReadFromClient>();
		writeRunnables = new ArrayList<WriteToClient>();
		guesses = new ArrayList<String>();
		xyCoords1 = "";
		xyCoords2 = "";
		try {
			ss = new ServerSocket(45371);
		} catch(IOException ex) {
			System.out.println("IOException from GameServer()");
		}
	}
	
	/**
	 * Method that starts accepting connections to the server.
	 */
	public void acceptConnections() {
		System.out.println("Waiting for connections...");
		try {
			while(continueAccepting) {
				Socket s = ss.accept();
				ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				
				numPlayers++;
				out.writeInt(numPlayers);
				System.out.println("Player #" + numPlayers + " has connected.");
				
				ReadFromClient rfc = new ReadFromClient(numPlayers, in);
				WriteToClient wtc = new WriteToClient(numPlayers, out);
				
				readRunnables.add(rfc);
				writeRunnables.add(wtc);
			}
		} catch(IOException ex) {
			System.out.println("IOException from acceptConnections()");
		}
		
		totalNumPlayers = numPlayers;
		System.out.println("Total: " + totalNumPlayers);
		
		for(WriteToClient currWTC : writeRunnables) {
			Thread writeThread = new Thread(currWTC);
			writeThread.start();
		}

		for(ReadFromClient currRFC : readRunnables) {
			Thread readThread = new Thread(currRFC);
			readThread.start();
		}
		
		System.out.println("No longer accepting connections");
	}
	
	/**
	 * Method that stops accepting connections to the server.
	 */
	public void stopAccepting() {
		try {
			continueAccepting = false;
			ss.close();
			System.out.println("Stopped accepting");
			System.out.println("Total num: " + numPlayers);
		} catch(IOException ex) {
			System.out.println("IOException from stopAccepting()");
		}
	}
	
	/**
	 * Method that determines the round winner.
	 * 
	 * @return Integer indicating team that won the round.
	 */
	public int determineRoundWinner() {
		for(String curr : guesses) {
			if(curr.substring(0, curr.length()-1).equals("fellow")) {
				if(curr.charAt(curr.length()-1) == '1') {
					team1Points += 2;
					return 1;
				}
				else {
					team2Points += 2;
					return 2;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Inner class for reading input from the client. Implements Runnable.
	 */
	private class ReadFromClient implements Runnable {
		private int playerID;
		private ObjectInputStream dataIn;
		private int teamNum;
		
		/**
		 * Constructor for class ReadFromClient.
		 * 
		 * @param pid Player ID.
		 * @param in ObjectInputStream.
		 */
		public ReadFromClient(int pid, ObjectInputStream in) {
			playerID = pid;
			dataIn = in;
			System.out.println("RFC" + playerID + " Runnable created");
		}
		
		/**
		 * Method that runs ReadFromClient.
		 */
		public void run() {
			try {
				
				if(playerID <= totalNumPlayers/2) {
					teamNum = 1;
					System.out.println("Player #" + playerID + " is Team 1!");
				} else {
					teamNum = 2;
					System.out.println("Player #" + playerID + " is Team 2!");
				}
				
				while(true) {
					if(playerID == artistIndex1) {
						xyCoords1 = (String) dataIn.readUnshared();
					} else if (playerID == artistIndex2) {
						xyCoords2 = (String) dataIn.readUnshared();
					} else if(teamNum == 1 && playerID != artistIndex1) {
						String guess = dataIn.readUTF();
						guesses.add(guess + "1");
						System.out.println("PID #" + playerID + "guessed: " + guess);
					} else {
						guesses.add(dataIn.readUTF() + "2");
					}
					if(guesses.size() > 0) {
						System.out.println(guesses.toString());
					}
				}
			} catch(IOException ex) {
				System.out.println("IOException from RFC run()");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}		
	}
	
	/**
	 * Inner class for writing output to the client. Implements Runnable.
	 */
	private class WriteToClient implements Runnable {
		private int playerID, teamNum;
		private ObjectOutputStream dataOut;
		
		/**
		 * Constructor for class WriteToClient.
		 * 
		 * @param pid
		 * @param out
		 */
		public WriteToClient(int pid, ObjectOutputStream out) {
			playerID = pid;
			dataOut = out;
			System.out.println("WTC" + playerID + " Runnable created");
		}
		
		/**
		 * Method that runs WriteToClient.
		 */
		public void run() {
			try {
				artistIndex1 = 1;
				artistIndex2 = totalNumPlayers/2 + 1;
				if(playerID <= totalNumPlayers/2) {
					teamNum = 1;
					System.out.println("Player #" + playerID + " is Team 1!");
				} else {
					teamNum = 2;
					System.out.println("Player #" + playerID + " is Team 2!");
				}
				dataOut.writeInt(teamNum);
				if(teamNum == 1)
					dataOut.writeInt(artistIndex1);
				else
					dataOut.writeInt(artistIndex2);
				dataOut.flush();
				System.out.println("Player#" + playerID + ": Team Num #" + teamNum);
				System.out.println("Artist1 Num #" + artistIndex1);
				System.out.println("Artist2 Num #" + artistIndex2);
				
				boolean roundEnd = true;
				int currArtist1 = artistIndex1;
				int currArtist2 = artistIndex2;
				
				while(true) {
					if(roundEnd) {
						if(teamNum == 1 && playerID != currArtist1) {
							dataOut.writeUnshared(xyCoords1);
							System.out.println("PID #" + playerID + ": " + xyCoords1.toString());
						}
						else if(teamNum == 2 && playerID != currArtist2) {
							dataOut.writeUnshared(xyCoords2);
							System.out.println("PID #" + playerID + ": " + xyCoords2.toString());
						}
						
						int roundWinner = determineRoundWinner();
						dataOut.writeInt(roundWinner);
						System.out.println("Round Winner: " + roundWinner);
						if(roundWinner != 0) {
							System.out.println("#" + playerID + " round ends");
							if(currArtist1 != totalNumPlayers/2) {
								currArtist1++;
							} else {
								currArtist1 = 1;
							}
							if(currArtist2 != totalNumPlayers) {
								currArtist2++;
							} else {
								currArtist2 = totalNumPlayers/2+1;
							}
							
							artistIndex1 = currArtist1;
							artistIndex2 = currArtist2;
							
							dataOut.writeInt(team1Points);
							dataOut.writeInt(team2Points);
							if(roundWinner == 1) {
								dataOut.writeUTF("Team 1 won the round wow!");
							} else {
								dataOut.writeUTF("Team 2 won the round wow!");
							}
							if(teamNum == 1) {
								dataOut.writeInt(currArtist1);
							} else {
								dataOut.writeInt(currArtist2);
							}
							roundEnd = false;
						}
						dataOut.flush();
					}
					
					try {
						Thread.sleep(5);
					} catch(InterruptedException ex) {
						System.out.println("InterruptedException from WTC run()");
					}
				}
				
			} catch(IOException ex) {
				System.out.println("IOException from WTC run()");
			}
		}
	}
	
	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GameServer gs = new GameServer();
		gs.acceptConnections();
	}
}
