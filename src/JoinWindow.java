import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Class for the window of the host.
 */
public class JoinWindow extends JFrame {

	private JLabel label;
	private JTextField inputIPAddress;
	private JButton joinGame;
	private Container container;
	private JPanel textField;
	private String ip;
	
	/**
	 * Constructor for class JoinWindow. 	
	 */
	public JoinWindow() {
		label = new JLabel("Input Server IP Address:");
		inputIPAddress = new JTextField(20);
		joinGame = new JButton("Join Game");
		container = this.getContentPane();
		textField = new JPanel();
		ip = "localhost";
	}
	
	/**
	 * Sets up the join window.
	 */
	public void setUpJoinWindow() {
		this.setSize(300,120);
		container.setLayout(new BorderLayout());
		container.add(label, BorderLayout.NORTH);
		textField.add(inputIPAddress);
		container.add(textField, BorderLayout.CENTER);
		container.add(joinGame, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getIP();
		this.setVisible(true);
	}
	
	/**
	 * Closes the join window. 
	 */
	public void closeOpeningScreen() {
		 this.setVisible(false);
		 this.dispose();
	}
	
	/**
	 * Sets up the "Join Game" button.
	 */
	public void getIP() {
		ActionListener getIP = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ip = inputIPAddress.getText();
				Player p = new Player(300, 300, ip);
				p.connectToServer();
				p.setUpGUI();
				closeOpeningScreen();
			}
		};
		joinGame.addActionListener(getIP);
	}
}
