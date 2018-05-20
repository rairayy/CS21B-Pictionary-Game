import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Class for the opening screen.
 * This is where users decide if they want to host a game or join a game.
 */
public class OpeningScreen extends JFrame {
	
	private int width;
	private int height;
	private JButton hostGame, joinGame;
	private JPanel buttonPanel, allPanel;
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
		container = this.getContentPane();
		container.setLayout(new BorderLayout());
		
		allPanel = new JPanel();
		allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.PAGE_AXIS));
		allPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		hostGame = new JButton("Host Game");
		hostGame.setMaximumSize(new Dimension(Integer.MAX_VALUE, hostGame.getMinimumSize().height));
		joinGame = new JButton("Join Game");
		joinGame.setMaximumSize(new Dimension(Integer.MAX_VALUE, joinGame.getMinimumSize().height));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.setPreferredSize(new Dimension(100,60));
	
		iconLabel = new JLabel();
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
	 * Method that sets up the opening screen.
	 */
	public void setUpOpeningScreen() {
		this.getContentPane().setPreferredSize(new Dimension(width, height));
		this.pack();
		this.setTitle("Opening Screen");
		
		allPanel.add(iconLabel);
		buttonPanel.add(hostGame);
		buttonPanel.add(joinGame);
		allPanel.add(buttonPanel);
		container.add(allPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.hostButton();
		this.joinButton();	
		this.setVisible(true);
	}
	
	/**
	 * Method that sets up the Host Game button.
	 */
	public void hostButton() {
		ActionListener host = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
					Thread t = new Thread(new Runnable() {
						public void run() {
							HostWindow hw = new HostWindow(300,80);
							hw.setUpHostWindow();
							closeOpeningScreen();
						}
					});
					t.start();
			}
		};
		hostGame.addActionListener(host);
	}
	
	/**
	 * Method that sets up the Join Game button.
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
	 * Method that closes the opening screen. 
	 */
	public void closeOpeningScreen() {
		 this.setVisible(false);
		 this.dispose();
	}
	
	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		OpeningScreen os = new OpeningScreen(300,370);
		os.setUpOpeningScreen();
	}
	
}
