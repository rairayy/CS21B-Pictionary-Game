import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Class for the server.
 */
public class Server {
    private ServerSocket ss;
    private int numPlayers;
    private ArrayList<ServerSideConnection> players;
    private boolean stop;
    private int artistIndex;
    
    /**
     * Constructor for class Server.
     */
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
    
    /**
     * Method that allows the server to start accepting connections.
     */
    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");
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
    
    /**
     * Inner class for the server side connection. Implements Runnable.
     */
    private class ServerSideConnection implements Runnable {
        
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;
        
        /**
         * Constructor for class ServerSideConnection.
         * 
         * @param s Socket object.
         * @param id Player ID.
         */
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
        
        /**
         * Method that runs the ServerSideConnection.
         */
        public void run() {
            try {
                dataOut.writeInt(playerID);
                dataOut.writeInt(artistIndex);
                dataOut.flush();

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
        
        /**
         * Method that sends the coordinates to the client as a string.
         * 
         * @param str String of coordinates.
         */
        public void sendCoord(String str) {
            try {
            	dataOut.writeUTF(str);
                dataOut.flush();
            } catch(IOException ex) { 
                System.out.println("IOException from sendButtonNum() CSC");
            }
        }
        
        /**
         * Method that closes the coordinates.
         */
        public void closeConnection() {
            try {
                socket.close();
                System.out.println("Connection closed");
            } catch(IOException ex) {
                System.out.println("IOException on closeConnection() SSC");
            }
        }
    }
    
    /**
     * Main method.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Server gs = new Server();
        gs.acceptConnections();
    }
}