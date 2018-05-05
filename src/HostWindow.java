import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Class for the window of the host.
 */
public class HostWindow extends JFrame {

	private int width;
	private int height;
	private JButton stopAccepting, startAccepting;
	private JPanel buttons;
	private Container container;
	private JTextArea stuff;
	
	public HostWindow( int w, int h) {
		width = w;
		height = h;
		startAccepting = new JButton("Start Accepting Connections");
		container = this.getContentPane();
		stuff = new JTextArea("AAA", 5, 2);
		buttons = new JPanel();
	}
	
	public void setUpHostWindow() {
		this.setSize(width, height);
		container.setLayout(new BorderLayout());
		container.add(stuff, BorderLayout.NORTH);
		stuff.setEditable(false);
		buttons.setLayout(new FlowLayout());
		buttons.add(startAccepting);
		buttons.add(stopAccepting);
		container.add(buttons, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.startAcceptingConnections();
		this.setVisible(true);
	}

	public void startAcceptingConnections() {
		ActionListener accept = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Server s = new Server();
				s.acceptConnections();
			}
		};
		startAccepting.addActionListener(accept);
	}
	
	
	
}
