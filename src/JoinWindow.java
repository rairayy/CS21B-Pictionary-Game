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

	private JLabel ipLabel, nameLabel;
	private JTextField inputIPAddress;
	private JTextField inputName;
	private JButton joinGame;
	private Container container;
	
	private JPanel ipPanel, namePanel;
	
	private String name;
	private String ip;
	
	/**
	 * Constructor for class JoinWindow. 	
	 */
	public JoinWindow() {
		ipLabel = new JLabel("Input Server IP Address:");
		nameLabel = new JLabel("Input your Name:");
		ipPanel = new JPanel();
		namePanel = new JPanel();
		inputIPAddress = new JTextField(20);
		inputName = new JTextField(20);
		joinGame = new JButton("Join Game");
		container = this.getContentPane();
		ip = "localhost";
	}
	
	/**
	 * Sets up the join window.
	 */
	public void setUpJoinWindow() {
		this.setSize(300,200);
		this.setTitle("Join Window");
		inputIPAddress.setText("localhost");
		container.setLayout(new GridLayout(3,1));
		ipPanel.add(ipLabel);
		ipPanel.add(inputIPAddress);
		namePanel.add(nameLabel);
		namePanel.add(inputName);
		container.add(ipPanel);
		container.add(namePanel);
		container.add(joinGame);
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
				name = inputName.getText();
				CanvasFrame cf = new CanvasFrame(800, 640, name, ip);
				cf.connectToServer();
				cf.setUpFrame();
				cf.setUpButtons();
				closeOpeningScreen();
			}
		};
		joinGame.addActionListener(getIP);
	}
}
