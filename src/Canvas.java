import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Class for the canvas of the game. Extends JComponent.
 */
public class Canvas extends JComponent {
	private Image image;
	private Graphics2D g2d;
	private int currX, currY, oldX, oldY;
	private float thickness;
	private ArrayList<Integer> xCoords, yCoords;
	private boolean enabled;
	
	/**
	 * Constructor for class Canvas.
	 */
	public Canvas() {
		thickness = 5;
		setDoubleBuffered(false);
		xCoords = new ArrayList<Integer>();
		yCoords = new ArrayList<Integer>();
		enabled = false;
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(enabled) {
					oldX = e.getX();
					oldY = e.getY();
					xCoords.add(oldX);
					yCoords.add(oldY);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if(enabled) {
					xCoords.clear();
					yCoords.clear();
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(enabled) {
					currX = e.getX();
					currY = e.getY();
					BasicStroke bs = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				    g2d.setStroke(bs);  
				    g2d.drawLine(oldX, oldY, currX, currY);
					repaint();
					oldX = currX;
					oldY = currY;
					xCoords.add(currX);
					yCoords.add(currY);
				}
			}
		});
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
		}
		g.drawImage(image, 0, 0, null);
	}
	
	/**
	 * Makes the canvas editable/uneditable.
	 * 
	 * @param e  Variable for being enabled
	 */
	public void changeEnabled(boolean e) {
		enabled = e;
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
	 * Method that clears the x and y coordinate ArrayLists and nullifies the image.
	 */
	public void empty() {
		xCoords.clear();
		yCoords.clear();
		image = null;
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
	 * 
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
	
	
	/**
	 * Method that gets the x-coordinate ArrayList.
	 * 
	 * @return ArrayList of x-coordinate integers.
	 */
	public ArrayList<Integer> getXCoords() {
		return xCoords;
	}

	/**
	 * Method that gets the y-coordinate ArrayList.
	 * 
	 * @return ArrayList of y-coordinate integers.
	 */
	public ArrayList<Integer> getYCoords() {
		return yCoords;
	}
}
