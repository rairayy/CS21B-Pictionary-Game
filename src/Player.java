import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

public class Player extends JFrame{
    private int width;
    private int height;
    private Container contentPane;
    private JTextArea message;
    private int playerID;
    private int artistIndex;
    private DrawingComponent dc;
    
    //If button is clicked client will stop receiving coordinates
    private JButton b;
    private boolean stop;
    
    private ClientSideConnection csc;
    private String ip;
    
    public Player(int w, int h, String ip) {
        width = w;
        height = h;
        contentPane = this.getContentPane();
        message = new JTextArea();
        dc = new DrawingComponent();
        b = new JButton("Stop");
        stop = true;
        ip = "localhost";
    }
    
    public void setUpGUI() {
    	System.out.println("Arist: " + artistIndex);
        this.setSize(width, height);
        this.setTitle("Player #: " + playerID);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(message, BorderLayout.SOUTH);
        contentPane.add(dc, BorderLayout.CENTER);
        contentPane.add(b, BorderLayout.NORTH);
        message.setText("");
        message.setWrapStyleWord(true);
        message.setLineWrap(true);
        message.setEditable(false);
        
        //If the client is the artist assigned
        if(playerID == artistIndex)
        	//Click anywhere on the drawing component and coordinates will be obtained
	        dc.addMouseListener(new MouseAdapter() {
	        	public void mousePressed(MouseEvent e) {
	        		int x = e.getX();
	        		int y = e.getY();
	        		csc.sendCoord("Player " + playerID + ": " + x + ", " + y);
	        	}
	        });
        //Stop action for button
        b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stop = false;
			}
        });
        
        Thread t = new Thread(new Runnable() {
        	public void run() {
        		//Only non-artists will get the coordinates
        		if(playerID != artistIndex) {
        			//Loop will constantly run until button is clicked
	        		while(true) {
	        			//Client will receive the coordinates and will update the
	        			//text area with these coords
	        			csc.receiveCoord();
	        			if(!stop)
	        				break;
	        		}
	        		csc.closeConnection();
        		}
        	}
        });
        t.start();
        
        this.setVisible(true);
    }
    
    public void connectToServer() {
        csc = new ClientSideConnection();
    }
    
    // Client Connection Inner Class
    private class ClientSideConnection {
        
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        
        public ClientSideConnection() {
            System.out.println("---Client---");
            try {
                socket = new Socket(ip, 51734);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt();
                artistIndex = dataIn.readInt();
                System.out.println("Connected to server as Player #" + playerID + ".");
            }
            catch(IOException ex) {
                System.out.println("IOException from CSC constructor");
            }
        }
        
        public void sendCoord(String str) {
            try {
                dataOut.writeUTF(str);
                dataOut.flush();
            } catch (IOException ex) {
                System.out.println("IOException from sendCoord() CSC");
            }
        }
        
        public String receiveCoord() {
        	String str = "";
        	try {
        		str = dataIn.readUTF();
        		message.append(str + "\n");
        		System.out.println(str);
        	} catch(IOException ex) {
        		System.out.println("IOException from receiveCoord() CSC");
        	}
        	return str;
        }
        
        public void closeConnection() {
            try {
                socket.close();
                System.out.println("---CONNECTION CLOSED---");
            } catch(IOException ex) {
                System.out.println("IOException on closeConnection() CSC");
            }
        }
    }

    private class DrawingComponent extends JComponent {
    	
    }
    
//    public static void main(String[] args) {
//        Player p = new Player(1000, 1000, ip);
//        p.connectToServer();
//        p.setUpGUI();
//    }
    
}
