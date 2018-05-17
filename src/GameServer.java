
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Class for the game server.
 */
public class GameServer {
	
	private ServerSocket ss;
	private int numPlayers, totalNumPlayers;
	private boolean continueAccepting;
	private int artistIndex1, artistIndex2;
	private String artist1Name, artist2Name;
	
	private ArrayList<ReadFromClient> readRunnables;
	private ArrayList<WriteToClient> writeRunnables;
	
	private String xyCoords1;
	
	private int teamNum;
	
	/**
	 * Constructor for class GameServer.
	 */
	public GameServer() {
		System.out.println("==== GAME SERVER ====");
		numPlayers = 0;
		continueAccepting = true;
		readRunnables = new ArrayList<ReadFromClient>();
		writeRunnables = new ArrayList<WriteToClient>();
		xyCoords1 = "";
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
		
		for(WriteToClient currRFC : writeRunnables) {
			Thread writeThread = new Thread(currRFC);
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
	 * Inner class for reading input from the client. Implements Runnable.
	 */
	private class ReadFromClient implements Runnable {
		private int playerID;
		private ObjectInputStream dataIn;
		
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
				while(true) {
					if(playerID == artistIndex1) {
						xyCoords1 = (String) dataIn.readUnshared();
					} else if (playerID == artistIndex2) {
						//
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
		private int playerID;
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
				teamNum = 1;
				dataOut.writeInt(teamNum);
				if(teamNum == 1)
					dataOut.writeInt(artistIndex1);
				else
					dataOut.writeInt(artistIndex2);
				dataOut.flush();
				System.out.println("Team Num #" + teamNum);
				System.out.println("Artist1 Num #" + artistIndex1);
				System.out.println("Artist2 Num #" + artistIndex2);
				
				while(true) {
					if(teamNum == 1 && playerID != artistIndex1) {
						dataOut.writeUnshared(xyCoords1);
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
		
		/**
		 * Method that determines the team number.
		 * 
		 * @return Integer for the team number.
		 */
		private int determineTeamNumber() {
			if(playerID <= totalNumPlayers/2)
				return 1;
			return 2;
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
