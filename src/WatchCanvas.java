import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.swing.*;

/**
 * Class for the non-artist canvas.
 * This is where the drawing of the artist is reflected on the screen of the non-artists.
 */
public class WatchCanvas extends JComponent {
	private Image image;
	private Graphics2D g2d;
	private float thickness;
	private String xCoords, yCoords;
	private int color, thicknessLevel;
	
	/**
	 * Constructor for class WatchCanvas.
	 */
	public WatchCanvas() {
		thickness = 5;
		setDoubleBuffered(false);
		xCoords = "";
		yCoords = "";
		color = 1;
		thicknessLevel = 7;
	}
	
	/**
	 * Overrides paintComponent.
	 */
	protected void paintComponent(Graphics g) {
		if (image == null) {
			image = createImage(getSize().width, getSize().height);
			g2d = (Graphics2D) image.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			clear();
		} else {
			updateColor(color);
			updateThickness(thicknessLevel);
			parseCoordinates(xCoords, yCoords, g2d);
		}
		g.drawImage(image, 0, 0, null);
	}
	
	/**
	 * 
	 * Method that receives the coordinates as a string, and splits them into the string containing the x-coordinates and the string containing the y-coordinates.
	 * 
	 * @param z String of coordinates.
	 */
	public void receiveCoords( String z ) {
		if ( z.length() > 6 ) {
			String[] zA = z.split(Pattern.quote("]["));
			String x = zA[0].substring(3, zA[0].length());
			String y = zA[1].substring(0, zA[1].length()-1);
			color = Integer.parseInt(zA[0].substring(0,1));
			thicknessLevel = Integer.parseInt(zA[0].substring(1,2));
			xCoords = x;
			yCoords = y;
		} else if(z.equals("0")) {
			clear();
			xCoords = "";
			yCoords = "";
		}
	}
	
	/**
	 * Updates the color based on the color setting
	 * @param c color setting
	 */
	public void updateColor(int c) {
		switch(c) {
			case 0:
				clear();
				break;
			case 1:
				black();
				break;
			case 2:
				red();
				break;
			case 3:
				blue();
				break;
			case 4:
				yellow();
				break;
			case 5:
				green();
				break;
			case 6:
				eraser();
				break;
		}
	}
	
	/**
	 * Updates the thickness based on the setting
	 * 
	 * @param t thickness setting
	 */
	private void updateThickness(int t) {
		switch(t) {
			case 7:
				set5();
				break;
			case 8:
				set10();
				break;
			case 9:
				set20();
				break;
		}
	}
	
	/**
	 * 
	 * Method that parses the string coordinates into integers to form the lines.
	 * 
	 * @param x String of x-coordinates.
	 * @param y String of y-coordinates.
	 * @param g2d Graphics object.
	 */
	public void parseCoordinates( String x, String y, Graphics2D g2d) {
		if (x.length() > 2 && y.length() > 2) {
			String[] xCoordsA = x.split(", ");
			String[] yCoordsA = y.split(", ");
			for(int i = 0; i < xCoordsA.length-1; i++) {
				BasicStroke bs = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				g2d.setStroke(bs);  
				g2d.drawLine(Integer.parseInt(xCoordsA[i]), Integer.parseInt(yCoordsA[i]), Integer.parseInt(xCoordsA[i+1]), Integer.parseInt(yCoordsA[i+1]));
			}
		}
	}
	
	/**
	 * Method that sets brush thickness to 5.
	 */
	public void set5() {
		thickness = 5;
	}
	
	/**
	 * Method that sets brush thickness to 10.
	 */
	public void set10() {
		thickness = 10;
	}
	
	/**
	 * Method that sets brush thickness to 20.
	 */
	public void set20() {
		thickness = 20;
	}
	
	/**
	 * Method that clears the canvas.
	 */
	public void clear() {
		g2d.setPaint(Color.WHITE);
		g2d.fillRect(0, 0, getSize().width, getSize().height);
		g2d.setPaint(Color.BLACK);
		repaint();
	}
	
	/**
	 * Method that nullifies the image and clears the x and y coordinate strings.
	 */
	public void empty() {
		xCoords = "";
		yCoords = "";
		image = null;
		color = 1;
		thicknessLevel = 7;
		repaint();
	}
	
	/**
	 * Method that sets paint color to black.
	 */
	public void black() {
		g2d.setPaint(Color.BLACK);
	}
	
	/**
	 * Method that sets paint color to red.
	 */
	public void red() {
		g2d.setPaint(new Color(223, 38, 45));
	}

	/**
	 * Method that sets paint color to blue.
	 */
	public void blue() {
		g2d.setPaint(new Color(0, 83, 159));
	}
	
	/**
	 * Method that sets paint color to yellow.
	 */
	public void yellow() {
		g2d.setPaint(new Color(255, 162, 0));
	}
	
	/**
	 * Method that sets paint color to green.
	 */
	public void green() {
		g2d.setPaint(new Color(45, 177, 53));
	}
	
	/**
	 * Method that activates the eraser.
	 */
	public void eraser() {
		g2d.setPaint(Color.WHITE);
	}
}
