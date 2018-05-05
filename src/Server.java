import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket ss;
    private int numPlayers;
    private ArrayList<ServerSideConnection> players;
    private boolean stop;
    private int artistIndex;
    
    public Server() {
        System.out.println("----Game Server----");
        numPlayers = 0;
        stop = true;
        players = new ArrayList<ServerSideConnection>();
        
        try {
            ss = new ServerSocket(51734);
        }
        catch(IOException ex) {
            System.out.println("IOException from GameServer constructor");
        }
    }
    
    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");
            //First client to connect is the artist
            artistIndex = 1;
            while(numPlayers < 4) {
                Socket s = ss.accept();
                numPlayers++;
                System.out.println("Player #" + numPlayers + " has connected.");
                ServerSideConnection ssc = new ServerSideConnection(s, numPlayers);
                players.add(ssc);
                
                Thread t = new Thread(ssc);
                t.start();
            }
            System.out.println("We now have " + numPlayers + " players. No longer accepting connections.");
        }
        catch(IOException ex) {
            System.out.println("IOException from acceptConnections()");
        }
    }
    
    private class ServerSideConnection implements Runnable {
        
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;
        
        public ServerSideConnection(Socket s, int id) {
            socket = s;
            playerID = id;
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            }
            catch (IOException ex) {
                System.out.println("IOException from SSC constructor");
            }
        }
        
        public void run() {
            try {
                dataOut.writeInt(playerID);
                dataOut.writeInt(artistIndex);
                dataOut.flush();
                
                //Loop will constantly run until the stop is false
                //(idk yet how to make the stop false)
                while(true) {
                	String message = dataIn.readUTF();
                    System.out.println(message);
                    for(ServerSideConnection s : players)
                    	s.sendCoord(message);
                    if(!stop) {
                    	break;
                    }
                }
                for(ServerSideConnection s : players)
                	s.closeConnection();
            }
            catch (IOException ex) {
                System.out.println("IOException from run() SSC");
            }
        }
        
        public void sendCoord(String str) {
            try {
            	dataOut.writeUTF(str);
                dataOut.flush();
            } catch(IOException ex) { 
                System.out.println("IOException from sendButtonNum() CSC");
            }
        }
        
        public void closeConnection() {
            try {
                socket.close();
                System.out.println("Connection closed");
            } catch(IOException ex) {
                System.out.println("IOException on closeConnection() SSC");
            }
        }
    }
    
    public static void main(String[] args) {
        Server gs = new Server();
        gs.acceptConnections();
    }
}