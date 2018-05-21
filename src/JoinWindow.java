import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Class for the window of the host.
 */
public class JoinWindow extends JFrame {

	private JLabel ipLabel;
	private JTextField inputIPAddress;
	private JButton joinGame;
	private Container container;
	
	private JPanel allPanel, buttonPanel;
	
	private String ip;
	
	/**
	 * Constructor for class JoinWindow. 	
	 */
	public JoinWindow() {
		container = this.getContentPane();

		allPanel = new JPanel();
		allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.PAGE_AXIS));
		allPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		ipLabel = new JLabel("Input Server IP Address:");
		inputIPAddress = new JTextField(20);
		inputIPAddress.setMaximumSize(new Dimension(Integer.MAX_VALUE, inputIPAddress.getMinimumSize().height));
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		joinGame = new JButton("Join Game");
		joinGame.setMaximumSize(new Dimension(Integer.MAX_VALUE, joinGame.getMinimumSize().height));
		
		ip = "localhost";
	}
	
	/**
	 * Method that sets up the join window.
	 */
	public void setUpJoinWindow() {
		this.getContentPane().setPreferredSize(new Dimension(300,100));
		this.pack();
		this.setTitle("Join Window");

		inputIPAddress.setText("localhost");
		
		allPanel.add(ipLabel);
		allPanel.add(inputIPAddress);
		buttonPanel.add(joinGame);
		allPanel.add(buttonPanel);
		container.add(allPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getIP();
		this.setVisible(true);
	}
	
	/**
	 * Method that sets up the join game button.
	 */
	public void getIP() {
		ActionListener getIP = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ip = inputIPAddress.getText();
				closeJoinScreen();
				Player cf = new Player(800, 640, ip);
				cf.connectToServer();
				cf.setUpFrame();
				cf.setUpButtons();
			}
		};
		joinGame.addActionListener(getIP);
	}

	/**
	 * Method that closes the join window. 
	 */
	public void closeJoinScreen() {
		 this.setVisible(false);
		 this.dispose();
	}
}
