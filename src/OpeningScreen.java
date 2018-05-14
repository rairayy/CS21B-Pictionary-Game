import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

/**
 * Class for the opening screen.
 * This is where users decide if they want to host a game or join a game.
 */
public class OpeningScreen extends JFrame {
	
	private int width;
	private int height;
	private JButton hostGame, joinGame;
	private JPanel buttonPanel;
	private JLabel iconLabel;
	private Container container;
	private ImageIcon img;
	private BufferedImage logo;
	
	/**
	 * Constructor for class OpeningScreen.
	 * 
	 * @param	w	width of frame
	 * @param	h	height of frame
	 */
	public OpeningScreen( int w, int h ) {
		width = w;
		height = h;
		hostGame = new JButton("Host Game");
		joinGame = new JButton("Join Game");
		buttonPanel = new JPanel();
		iconLabel = new JLabel();
		container = this.getContentPane();
		img = new ImageIcon();
		try {
			logo = ImageIO.read(new File("pencil.png"));
			img.setImage(logo);
			iconLabel.setIcon(img);
		} catch (IOException e) {
			System.out.println("IOException in OpeningScreen constructor");
		}
	}
	
	/**
	 * Sets up the opening screen.
	 */
	public void setUpOpeningScreen() {
		this.setSize(width, height);
		this.setTitle("Opening Screen");
		container.setLayout(new BorderLayout());
		container.add(iconLabel, BorderLayout.NORTH);
		buttonPanel.add(hostGame);
		buttonPanel.add(joinGame);
		container.add(buttonPanel, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.hostButton();
		this.joinButton();	
		this.setVisible(true);
	}
	
	/**
	 * Sets up the Host Game button.
	 */
	public void hostButton() {
		ActionListener host = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
					Thread t = new Thread(new Runnable() {
						public void run() {
							HostWindow hw = new HostWindow(300,200);
							hw.setUpHostWindow();
						}
					});
					t.start();
			}
		};
		hostGame.addActionListener(host);
	}
	
	/**
	 * Sets up the Join Game button.
	 */
	public void joinButton() {
		ActionListener join = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JoinWindow jw = new JoinWindow();
				jw.setUpJoinWindow();
				closeOpeningScreen();
			}
		};
		joinGame.addActionListener(join);
	}
	
	/**
	 * Closes the opening screen. 
	 */
	public void closeOpeningScreen() {
		 this.setVisible(false);
		 this.dispose();
	}
	
	
	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		OpeningScreen os = new OpeningScreen(300,370);
		os.setUpOpeningScreen();
	}
	
}
