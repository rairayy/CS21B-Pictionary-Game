
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
	private int artistIndex1, artistIndex2, roundNum;
	private final int maxRounds;
	
	private ArrayList<ReadFromClient> readRunnables;
	private ArrayList<WriteToClient> writeRunnables;
	private ArrayList<String> guesses;
	
	private String xyCoords1, xyCoords2;
	private boolean[] checkRoundEnd;
	private String[] wordList;
	private String[] wordsToGuess;
	
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
		wordList = new String[50];
		wordsToGuess = new String[5];
		try {
			ss = new ServerSocket(45371);
		} catch(IOException ex) {
			System.out.println("IOException from GameServer()");
		}
		try {
			FileReader reader = new FileReader("PictionaryWords");
			Scanner in = new Scanner(reader);
			int i = 0;
			while(in.hasNextLine()) {
				String line = in.next();
				wordList[i] = line;
				i++;
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException from GameServer()");
		}
		
		determineWords();
		
	}
	
	/**
	 * Method for determining the words to guess.
	 */
	private void determineWords() {
		List<String> shuffledList = Arrays.asList(wordList);
		Collections.shuffle(shuffledList);
		for(int i = 0; i <wordsToGuess.length; i++) {
			wordsToGuess[i] = shuffledList.get(i);
		}
		System.out.println(Arrays.toString(wordsToGuess));
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
		
		checkRoundEnd = new boolean[totalNumPlayers];
		for(int i = 0; i < checkRoundEnd.length; i++) {
			checkRoundEnd[i] = false;
		}
		
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
			if(curr.substring(0, curr.length()-1).equalsIgnoreCase(wordsToGuess[roundNum-1])) {
				if(curr.charAt(curr.length()-1) == '1') {
					return 1;
				}
				else {
					return 2;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Checks if the game has ended
	 * 
	 * @param currRound the current round that just ended
	 * @return boolean for if the game has ended
	 */
	public boolean checkWinner(int currRound) {
		if(currRound > maxRounds) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the game can move to the next round
	 * @return true if the game can move to the next round
	 */
	public boolean moveToNextRound() {
		for(int i = 0; i < checkRoundEnd.length; i++) {
			if(!checkRoundEnd[i]) {
				return false;
			}
		}
		guesses.clear();
		xyCoords1 = "";
		xyCoords2 = "";
		return true;
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
						System.out.println("#" + playerID + " Artist: " + xyCoords1);
					} else if (playerID == artistIndex2) {
						xyCoords2 = (String) dataIn.readUnshared();
						System.out.println("#" + playerID + " Artist: " + xyCoords2);
					} else if(teamNum == 1 && playerID != artistIndex1) {
						String guess = (String) dataIn.readUnshared();
						if(guess.charAt(0) != '[' && guess.charAt(guess.length()-1) != ']') {
							guesses.add(guess + "1");
							System.out.println("#" + playerID + " guessed: " + guess);
						}
					} else {
						String guess = (String)dataIn.readUnshared();
						if(guess.charAt(0) != '[' && guess.charAt(guess.length()-1) != ']') {
							guesses.add(guess + "2");
							System.out.println("#" + playerID + " guessed: " + guess);
						}
					}
					if(guesses.size() > 0) {
						System.out.println(guesses.toString());
					}
					if(checkWinner(roundNum)) {
						dataIn.close();
						break;
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
		private boolean roundEnd;
		private int currRound;
		
		/**
		 * Constructor for class WriteToClient.
		 * 
		 * @param pid
		 * @param out
		 */
		public WriteToClient(int pid, ObjectOutputStream out) {
			playerID = pid;
			dataOut = out;
			roundEnd = true;
			currRound = 1;
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
				dataOut.writeUTF(wordsToGuess[0]);
				dataOut.flush();
				System.out.println("Player#" + playerID + ": Team Num #" + teamNum);
				System.out.println("Artist1 Num #" + artistIndex1);
				System.out.println("Artist2 Num #" + artistIndex2);
				
				int currArtist1 = artistIndex1;
				int currArtist2 = artistIndex2;
				int team1Tracker = 0;
				int team2Tracker = 0;
				
				while(true) {
					if(roundEnd) {
						if(teamNum == 1 && playerID != currArtist1) {
							dataOut.writeUnshared(xyCoords1);
						}
						else if(teamNum == 2 && playerID != currArtist2) {
							dataOut.writeUnshared(xyCoords2);
						}
						
						int roundWinner = determineRoundWinner();
						dataOut.writeInt(roundWinner);
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
							
							if(roundWinner == 1)
								team1Tracker += 2;
							else
								team2Tracker += 2;
							team1Points = team1Tracker;
							team2Points = team2Tracker;
							
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
							currRound++;
							checkRoundEnd[playerID-1] = true;
							dataOut.reset();
							if(checkWinner(currRound)) {
								dataOut.close();
								break;
							}
						}
						dataOut.flush();
					} else {
						if(moveToNextRound()) {
							roundNum = currRound;
							roundEnd = true;
							dataOut.writeUTF(wordsToGuess[roundNum-1]);
							dataOut.writeBoolean(roundEnd);
							System.out.println("Move to next round: " + roundEnd);
						}
					}
					dataOut.flush();
					
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
