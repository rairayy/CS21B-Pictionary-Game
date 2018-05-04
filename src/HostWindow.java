import java.awt.*;
import javax.swing.*;

/**
 * Class for the window of the host.
 */
public class HostWindow extends JFrame {

	private int width;
	private int height;
	private JButton stopAccepting, startAccepting;
	private Container container;
	
	public HostWindow( int w, int h) {
		width = w;
		height = h;
		startAccepting = new JButton("Start Accepting Connections");
		stopAccepting = new JButton("Stop Accepting Connections");
		container = this.getContentPane();
	}
	
	public void setUpHostWindow() {
		this.setSize(width, height);
		container.setLayout(new BorderLayout());
		container.add(stopAccepting, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

}
