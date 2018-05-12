/* Del Rio, Daniel - 170718

I have not discussed the Java language code 
in my program with anyone
other than my instructor or the teaching
assistants assigned to this course.

I have not used Java language code 
obtained from another student, or
any other unauthorized source, either 
modified or unmodified.

If any Java language
code or documentation used in my program was
obtained from another source, such as a text
book or course notes, those have been clearly
noted with a proper citation in the 
comments of my code. */

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
	private ServerSocket ss;
	private int numPlayers, totalNumPlayers;
	private boolean continueAccepting;
	private int artistIndex1, artistIndex2;
	private String artist1Name, artist2Name;
	
	private ArrayList<ReadFromClient> readRunnables;
	private ArrayList<WriteToClient> writeRunnables;
	private int oldX1, oldY1, currX1, currY1;
	private int oldX2, oldY2, currX2, currY2;
	private int teamNum;
	private boolean canvasPressed1, canvasDragged1, canvasPressed2, canvasDragged2;
	
	public GameServer() {
		System.out.println("==== GAME SERVER ====");
		numPlayers = 0;
		continueAccepting = true;
		canvasPressed1 = false;
		canvasDragged1 = false;
		canvasPressed2 = false;
		canvasDragged2 = false;
		readRunnables = new ArrayList<ReadFromClient>();
		writeRunnables = new ArrayList<WriteToClient>();
				
		try {
			ss = new ServerSocket(45371);
		} catch(IOException ex) {
			System.out.println("IOException from GameServer()");
		}
	}
	
	public void acceptConnections() {
		System.out.println("Waiting for connections...");
		try {
			while(continueAccepting) {
				Socket s = ss.accept();
				DataInputStream in = new DataInputStream(s.getInputStream());
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				
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
		System.out.println("Total:" + totalNumPlayers);
		
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
	
	private class ReadFromClient implements Runnable {
		private int playerID;
		private DataInputStream dataIn;
		
		public ReadFromClient(int pid, DataInputStream in) {
			playerID = pid;
			dataIn = in;
			System.out.println("RFC" + playerID + " Runnable created");
		}
		
		public void run() {
			try {
				
//				if(playerID == artistIndex1) {
//					artist1Name = dataIn.readUTF();
//					System.out.println(artist1Name);
//				} else if(playerID == artistIndex2) {
//					artist2Name = dataIn.readUTF();
//					System.out.println(artist2Name);
//				}
				
				while(true) {
					if(playerID == artistIndex1) {
						canvasPressed1 = dataIn.readBoolean();
						canvasDragged1 = dataIn.readBoolean();
						if(canvasPressed1 && canvasDragged1) {
							oldX1 = dataIn.readInt();
							oldY1 = dataIn.readInt();
							currX1 = dataIn.readInt();
							currY1 = dataIn.readInt();
						}
					} else if (playerID == artistIndex2) {
						canvasPressed2 = dataIn.readBoolean();
						canvasDragged2 = dataIn.readBoolean();
						if(canvasPressed2 && canvasDragged2) {
							oldX2 = dataIn.readInt();
							oldY2 = dataIn.readInt();
							currX2 = dataIn.readInt();
							currY2 = dataIn.readInt();
						}
					}
				}
				
			} catch(IOException ex) {
				System.out.println("IOException from RFC run()");
			}
		}		
	}
	
	private class WriteToClient implements Runnable {
		private int playerID;
		private DataOutputStream dataOut;
		
		public WriteToClient(int pid, DataOutputStream out) {
			playerID = pid;
			dataOut = out;
			System.out.println("WTC" + playerID + " Runnable created");
		}
		
		public void run() {
			try {
				artistIndex1 = 1;
				artistIndex2 = totalNumPlayers/2 + 1;
//				teamNum = determineTeamNumber();
				teamNum = 1;
				dataOut.writeInt(teamNum);
				if(teamNum == 1)
					dataOut.writeInt(artistIndex1);
				else
					dataOut.writeInt(artistIndex2);
				dataOut.flush();
				System.out.println("Server: Team Num #" + teamNum);
				System.out.println("Server: Artist1 Num #" + artistIndex1);
				System.out.println("Server: Artist2 Num #" + artistIndex2);
				
				while(true) {
					if(playerID != artistIndex1 && teamNum == 1) {
						dataOut.writeBoolean(canvasPressed1);
						dataOut.writeBoolean(canvasDragged1);
						if(canvasPressed1 && canvasDragged1) {
							dataOut.writeInt(oldX1);
							dataOut.writeInt(oldY1);
							dataOut.writeInt(currX1);
							dataOut.writeInt(currY1);
						}
					}
					else if(playerID != artistIndex2 && teamNum == 2) {
						dataOut.writeBoolean(canvasPressed2);
						dataOut.writeBoolean(canvasDragged2);
						if(canvasPressed2 && canvasDragged2) {
							dataOut.writeInt(oldX2);
							dataOut.writeInt(oldY2);
							dataOut.writeInt(currX2);
							dataOut.writeInt(currY2);
						}
					}
					dataOut.flush();
//					try {
//						Thread.sleep(5);
//					} catch(InterruptedException ex) {
//						System.out.println("InterruptedException from WTC run()");
//					}
				}
				
			} catch(IOException ex) {
				System.out.println("IOException from WTC run()");
			}
		}
		
		private int determineTeamNumber() {
			if(playerID <= totalNumPlayers/2)
				return 1;
			return 2;
		}
	}
	
	public static void main(String[] args) {
		GameServer gs = new GameServer();
		gs.acceptConnections();
	}
}
