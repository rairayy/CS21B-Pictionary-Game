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
	private GameServer s;
	private boolean stopBool;
	
	public HostWindow( int w, int h) {
		width = w;
		height = h;
		startAccepting = new JButton("Start Accepting Connections");
		stopAccepting = new JButton("Stop Accepting Connections");
		container = this.getContentPane();
		stuff = new JTextArea("AAA", 5, 2);
		buttons = new JPanel();
		stopBool = false;
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
		this.stopAcceptingConnections();
		this.setVisible(true);
	}

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
			}
		};
		startAccepting.addActionListener(accept);
	}
	
	public void stopAcceptingConnections() {
		ActionListener stop = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				s.stopAccepting();
			}
		};
		stopAccepting.addActionListener(stop);
		
	}
	
	public void closeHostScreen() {
		 this.setVisible(false);
		 this.dispose();
	}
	
	/*private class HostThread implements Runnable {
		
		public void run() {
			s = new GameServer();
			s.acceptConnections();
			
			while(true) {
				if(stopBool) {
					System.out.println("stop bool true");
					s.stopAccepting();
					break;
				}
			}
		}
	}*/
}
