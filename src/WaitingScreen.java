import javax.swing.*;
import java.awt.*;

/**
 * Class for the waiting screen. Extends JFrame.
 */
public class WaitingScreen extends JFrame implements Runnable {
	
	private JLabel waitLabel;
	private Container container;
	
	/**
	 * Constructor for class WaitingScreen.
	 */
	public WaitingScreen() {
		container = this.getContentPane();
		waitLabel = new JLabel("Waiting for other players...");
	}
	
	/**
	 * Method that sets up the GUI of the waiting screen.
	 */
	public void setUpGUI() {
		this.getContentPane().setPreferredSize(new Dimension(300,100));
		this.pack();
		this.setTitle("Waiting Screen");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		container.add(waitLabel);
		this.setVisible(true);
//		this.pack();
		this.revalidate();
	}
	
	public void run() {
		setUpGUI();
	}
	
	/**
	 * Method that closes the waiting screen. 
	 */
	public void closeWaitingScreen() {
		 this.setVisible(false);
		 this.dispose();
	}
}
