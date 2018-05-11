import javax.swing.*;
import java.awt.*;

public class WaitingScreen extends JFrame {
	
	private JLabel waitLabel;
	
	public WaitingScreen() {
		waitLabel = new JLabel("Waiting for other players...");
	}
	
	public void setUpGUI() {
		this.setSize(300,100);
		this.setTitle("Waiting Screen");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		this.getContentPane().add(waitLabel);
	}
	
	/**
	 * Closes the join window. 
	 */
	public void closeWaitingScreen() {
		 this.setVisible(false);
		 this.dispose();
	}
}
