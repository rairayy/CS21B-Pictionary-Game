import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public class Canvas extends JComponent {
	private Image image;
	private Graphics2D g2d;
	private int currX, currY, oldX, oldY;
	private float thickness;
	private ArrayList<Integer> xCoords, yCoords;
	
	private boolean mousePressed, mouseDragged, mouseReleased;
	
	public Canvas() {
		thickness = 5;
		setDoubleBuffered(false);
		mousePressed = false;
		mouseDragged = false;
		xCoords = new ArrayList<Integer>();
		yCoords = new ArrayList<Integer>();
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mousePressed = true;
				oldX = e.getX();
				oldY = e.getY();
				xCoords.add(oldX);
				yCoords.add(oldY);
			}
			
			public void mouseReleased(MouseEvent e) {
				mousePressed = false;
				mouseDragged = false;
				xCoords = new ArrayList<Integer>();
				yCoords = new ArrayList<Integer>();
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				mouseDragged = true;
				currX = e.getX();
				currY = e.getY();
				if(currX != 0 && currY != 0) {
					xCoords.add(currX);
					yCoords.add(currY);
				}
				BasicStroke bs = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			    g2d.setStroke(bs);  
			    g2d.drawLine(oldX, oldY, currX, currY);
				repaint();
				oldX = currX;
				oldY = currY;
			}
		});
	}
	
	protected void paintComponent(Graphics g) {
		if (image == null) {
			image = createImage(getSize().width, getSize().height);
			g2d = (Graphics2D) image.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			clear();
		}
		g.drawImage(image, 0, 0, null);
	}
	
	public void set5() {
		thickness = 5;
	}
	
	public void set10() {
		thickness = 10;
	}
	
	public void set20() {
		thickness = 20;
	}
	
	public void clear() {
		g2d.setPaint(Color.WHITE);
		g2d.fillRect(0, 0, getSize().width, getSize().height);
		g2d.setPaint(Color.BLACK);
		repaint();
	}
	
	public void black() {
		g2d.setPaint(Color.BLACK);
	}
	
	public void red() {
		g2d.setPaint(Color.RED);
	}

	public void blue() {
		g2d.setPaint(Color.BLUE);
	}
	
	public void yellow() {
		g2d.setPaint(Color.YELLOW);
	}
	
	public void green() {
		g2d.setPaint(Color.GREEN);
	}
	
	public void eraser() {
		g2d.setPaint(Color.WHITE);
	}
	
	public int getOldX() {
		return oldX;
	}
	
	public int getOldY() {
		return oldY;
	}
	
	public int getCurrX() {
		return currX;
	}
	
	public int getCurrY() {
		return currY;
	}
	
	public boolean getMousePressed() {
		return mousePressed;
	}
	
	public boolean getMouseDragged() {
		return mouseDragged;
	}
	
	public ArrayList<Integer> getXCoords() {
		return xCoords;
	}

	public ArrayList<Integer> getYCoords() {
		return yCoords;
	}
}
