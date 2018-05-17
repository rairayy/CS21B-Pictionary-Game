import javax.swing.*;
import java.awt.*;

/**
 * Class for the waiting screen. Extends JFrame.
 */
public class WaitingScreen extends JFrame {
	
	private JLabel waitLabel;
	
	/**
	 * Constructor for class WaitingScreen.
	 */
	public WaitingScreen() {
		waitLabel = new JLabel("Waiting for other players...");
	}
	
	/**
	 * Method that sets up the GUI of the waiting screen.
	 */
	public void setUpGUI() {
		this.setSize(300,100);
		this.setTitle("Waiting Screen");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		this.getContentPane().add(waitLabel);
	}
	
	/**
	 * Method that closes the waiting screen. 
	 */
	public void closeWaitingScreen() {
		 this.setVisible(false);
		 this.dispose();
	}
}
