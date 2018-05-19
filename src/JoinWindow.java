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
		container = this.getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		ipLabel = new JLabel("Input Server IP Address:");
		nameLabel = new JLabel("Input your Name:");
		ipPanel = new JPanel();
		namePanel = new JPanel();
		inputIPAddress = new JTextField(20);
		inputName = new JTextField(20);
		joinGame = new JButton("Join Game");
		joinGame.setMaximumSize(new Dimension(Integer.MAX_VALUE, joinGame.getMinimumSize().height));
		ip = "localhost";
	}
	
	/**
	 * Method that sets up the join window.
	 */
	public void setUpJoinWindow() {
		this.getContentPane().setPreferredSize(new Dimension(300,200));
		this.pack();
		this.setTitle("Join Window");
		inputIPAddress.setText("localhost");
		container.setPreferredSize(new Dimension(300,200));
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
	 * Method that sets up the join game button.
	 */
	public void getIP() {
		ActionListener getIP = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ip = inputIPAddress.getText();
				name = inputName.getText();
				closeJoinScreen();
				CanvasFrame cf = new CanvasFrame(800, 640, name, ip);
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
