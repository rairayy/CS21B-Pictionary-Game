import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Class for the window of the host. Extends JFrame.
 */
public class HostWindow extends JFrame {

	private int width;
	private int height;
	private JButton stopAccepting, startAccepting;
	private JPanel buttons;
	private Container container;
	private GameServer s;
	
	/**
	 * Constructor for class HostWindow.
	 * 
	 * @param w Witdh of frame.
	 * @param h Height of frame.
	 */
	public HostWindow( int w, int h) {
		width = w;
		height = h;
		container = this.getContentPane();
		container.setLayout(new BorderLayout());

		startAccepting = new JButton("Start Accepting Connections");
		startAccepting.setMinimumSize(new Dimension(Integer.MAX_VALUE, startAccepting.getMinimumSize().height));
		stopAccepting = new JButton("Stop Accepting Connections");
		stopAccepting.setMinimumSize(new Dimension(Integer.MAX_VALUE, stopAccepting.getMinimumSize().height));
		buttons = new JPanel();
	}
	
	/**
	 * Method that sets up the GUI of the host window.
	 */
	public void setUpHostWindow() {
		this.getContentPane().setPreferredSize(new Dimension(width, height));
		this.pack();
		this.setTitle("Host Window");
		
		buttons.setLayout(new FlowLayout());
		buttons.add(startAccepting);
		buttons.add(stopAccepting);
		container.add(buttons, BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.startAcceptingConnections();
		this.stopAcceptingConnections();
		stopAccepting.setEnabled(false);
		this.setVisible(true);
	}

	/**
	 * Method that starts accepting connections to the server.
	 */
	public void startAcceptingConnections() {
		ActionListener accept = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						s = new GameServer();
						s.acceptConnections();
					}
				});
				t.start();
				startAccepting.setEnabled(false);
				stopAccepting.setEnabled(true);
			}
		};
		startAccepting.addActionListener(accept);
	}
	
	/**
	 * Method that stops accepting connections to the server.
	 */
	public void stopAcceptingConnections() {
		ActionListener stop = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				closeHostScreen();
				s.stopAccepting();
			}
		};
		stopAccepting.addActionListener(stop);
	}
	
	/**
	 * Method that closes the host window.
	 */
	public void closeHostScreen() {
		 this.setVisible(false);
		 this.dispose();
	}
}
