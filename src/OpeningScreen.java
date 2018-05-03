import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

/*
 * Class for the opening screen.
 * This is where user can decide
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
			logo = ImageIO.read(new File("/pencil.png"));
			img.setImage(logo);
			iconLabel.setIcon(img);
		} catch (IOException e) {
			System.out.println("IOException in OpeningScreen constructor");
		}
	}
	
	public void setUpOpeningScreen() {
		container.setLayout(new BorderLayout());
		container.add(iconLabel);
		buttonPanel.add(hostGame);
		buttonPanel.add(joinGame);
		container.add(buttonPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
}
