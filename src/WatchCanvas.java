import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WatchCanvas extends JComponent {
	private Image image;
	private Graphics2D g2d;
	private int currX, currY, oldX, oldY;
	private float thickness;
	private boolean mousePressed, mouseDragged;
	
	public WatchCanvas() {
		thickness = 5;
		setDoubleBuffered(false);
		mousePressed = true;
		mouseDragged = true;
	}
	
	protected void paintComponent(Graphics g) {
		if (image == null) {
			image = createImage(getSize().width, getSize().height);
			g2d = (Graphics2D) image.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			clear();
		}
//		if ( mousePressed || mouseDragged ) {
//			BasicStroke bs = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
//			g2d.setStroke(bs);  
//			g2d.drawLine(oldY, oldX, currY, currX);
//			repaint();
//			oldX = currX;
//			oldY = currY;
////		}	
		if((oldX != 0 && oldY != 0)) {
			BasicStroke bs = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			g2d.setStroke(bs);  
			g2d.drawLine(oldY, oldX, currY, currX);
			repaint();
		}
//		oldX = currX;
//		oldY = currY;
		g.drawImage(image, 0, 0, null);
	}
	
	public void setNewCoords(int oX, int oY, int cX, int cY) {		
		oldX = oX;
		oldY = oY;
		currX = cX;
		currY = cY;
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
	
	public void setMousePressed(boolean mp) {
		mousePressed = mp;
	}
	
	public void setMouseDragged(boolean md) {
		mouseDragged = md;
	}
	
}
