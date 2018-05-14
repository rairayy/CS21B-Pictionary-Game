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
	private ArrayList<Integer> xCoords1, yCoords1, xCoords2, yCoords2;
	private int teamNum;
	
	public GameServer() {
		System.out.println("==== GAME SERVER ====");
		numPlayers = 0;
		continueAccepting = true;
		readRunnables = new ArrayList<ReadFromClient>();
		writeRunnables = new ArrayList<WriteToClient>();
		xCoords1 = new ArrayList<Integer>();
		yCoords1 = new ArrayList<Integer>();
		xCoords2 = new ArrayList<Integer>();
		yCoords2 = new ArrayList<Integer>();
				
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
		private ObjectInputStream dataIn;
		
		public ReadFromClient(int pid, ObjectInputStream in) {
			playerID = pid;
			dataIn = in;
			System.out.println("RFC" + playerID + " Runnable created");
		}
		
		public void run() {
			try {
				
//				artist1Name = dataIn.readUTF();
//				artist2Name = dataIn.readUTF();
				
				while(true) {
					if(playerID == artistIndex1) {
//						int size = dataIn.readInt();
						xCoords1 = (ArrayList<Integer>) dataIn.readUnshared();
						yCoords1 = (ArrayList<Integer>) dataIn.readUnshared();
						int size = xCoords1.size();
						
						for(int i =0 ; i < size; i++) {
//							int currX = dataIn.readInt();
//							int currY = dataIn.readInt();
							int currX = xCoords1.get(i);
							int currY = yCoords1.get(i);
//							xCoords1.add(currX);
//							yCoords1.add(currY);
							if(currX != 0 && currY != 0)
								System.out.println("ServerFrameRead: " + currX + ", " + currY);
						}
//						oldX1 = dataIn.readInt();
//						oldY1 = dataIn.readInt();
//						currX1 = dataIn.readInt();
//						currY1 = dataIn.readInt();
					} else if (playerID == artistIndex2) {
						int size = dataIn.readInt();
						for(int i = 0; i < size; i++) {
							int currX = dataIn.readInt();
							int currY = dataIn.readInt();
							xCoords2.add(currX);
							yCoords2.add(currY);
						}
//						oldX2 = dataIn.readInt();
//						oldY2 = dataIn.readInt();
//						currX2 = dataIn.readInt();
//						currY2 = dataIn.readInt();
					}
//					try {
//						Thread.sleep(25);
//					} catch(InterruptedException ex) {
//						System.out.println("InterruptedException from WTS run()");
//					}
				}
				
			} catch(IOException ex) {
				System.out.println("IOException from RFC run()");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	private class WriteToClient implements Runnable {
		private int playerID;
		private ObjectOutputStream dataOut;
		
		public WriteToClient(int pid, ObjectOutputStream out) {
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
				System.out.println("Team Num #" + teamNum);
				System.out.println("Artist1 Num #" + artistIndex1);
				System.out.println("Artist2 Num #" + artistIndex2);
				
				while(true) {
					if(teamNum == 1 && playerID != artistIndex1) {
//						dataOut.writeInt(xCoords1.size());
//						dataOut.writeBoolean(xCoo);
//						dataOut.flush();
						if(xCoords1.size() > 0) {
							dataOut.writeUnshared(xCoords1);
							dataOut.writeUnshared(yCoords1);
							for(int i = 0; i < xCoords1.size(); i++) {
	//							if(yCoords1.size() > 0) {
	//								dataOut.writeInt(xCoords1.get(i));
	//								dataOut.writeInt(yCoords1.get(i));
									if(xCoords1.get(i) != 0 && yCoords1.get(i) != 0)
										System.out.println("ServerFrameWrite: " + xCoords1.get(i) + ", " + yCoords1.get(i));
	//							}
	//								dataOut.writeBoolean(true);
							}
							dataOut.flush();
						}
//						dataOut.writeBoolean(false);
						xCoords1 = new ArrayList<Integer>();
						yCoords1 = new ArrayList<Integer>();
//						dataOut.writeInt(oldX1);
//						dataOut.writeInt(oldY1);
//						dataOut.writeInt(currX1);
//						dataOut.writeInt(currY1);
					} else if(teamNum == 2 && playerID != artistIndex2){
						dataOut.writeInt(xCoords2.size());
						for(int i = 0; i < xCoords2.size(); i++) {
//							if(yCoords2.size() > 0) {
								dataOut.writeInt(xCoords2.get(i));
								dataOut.writeInt(yCoords2.get(i));
//							}
//							System.out.println(xCoords2.get(i));
//							System.out.println(yCoords2.get(i));
						}
						xCoords2 = new ArrayList<Integer>();
						yCoords2 = new ArrayList<Integer>();
//						dataOut.flush();
//						dataOut.writeInt(oldX2);
//						dataOut.writeInt(oldY2);
//						dataOut.writeInt(currX2);
//						dataOut.writeInt(currY2);
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
