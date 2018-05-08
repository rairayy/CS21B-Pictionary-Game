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
	private JButton stopAccepting, startAccepting, joinGame;
	private JPanel buttons;
	private Container container;
	private JTextArea stuff;
	private Server s;
	
	public HostWindow(int w, int h) {
		width = w;
		height = h;
		startAccepting = new JButton("Start Accepting Connections");
		stopAccepting = new JButton("Stop Accepting Connections");
		joinGame = new JButton("Join Game");
		container = this.getContentPane();
		stuff = new JTextArea("AAA", 5, 2);
		buttons = new JPanel();
		s = new Server();
	}
	
	public void setUpHostWindow() {
		this.setSize(width, height);
		container.setLayout(new BorderLayout());
		container.add(stuff, BorderLayout.NORTH);
		stuff.setEditable(false);
		buttons.setLayout(new FlowLayout());
		buttons.add(startAccepting);
		buttons.add(stopAccepting);
		buttons.add(joinGame);
		joinGame.setEnabled(false);
		container.add(buttons, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.startAcceptingConnections();
		this.stopAcceptingConnections();
		this.joinGame();
		this.setVisible(true);
	}

	public void startAcceptingConnections() {
		ActionListener accept = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				joinGame.setEnabled(true);
				startAccepting.setEnabled(false);
				Thread t = new Thread(new Runnable() {
					public void run() {
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
			}
		};
		stopAccepting.addActionListener(stop);
	}

	public void joinGame() {
		ActionListener join = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						Player p = new Player(300, 300, "localhost");
						p.connectToServer();
						p.setUpGUI();
						joinGame.setEnabled(false);
					}
				});
				t.start();
			}
		};
		joinGame.addActionListener(join);
	}
	
}
