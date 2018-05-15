import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.*;

public class WatchCanvas extends JComponent {
	private Image image;
	private Graphics2D g2d;
	private int currX, currY, oldX, oldY;
	private float thickness;
	
	private boolean mousePressed, mouseDragged;
	private String xCoords, yCoords;
	
	public WatchCanvas() {
		thickness = 5;
		setDoubleBuffered(false);
		mousePressed = false;
		mouseDragged = false;
//		xCoords = new ArrayList<Integer>();
//		yCoords = new ArrayList<Integer>();
		xCoords = "";
		yCoords = "";
	}
	
	protected void paintComponent(Graphics g) {
		if (image == null) {
			image = createImage(getSize().width, getSize().height);
			g2d = (Graphics2D) image.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			clear();
		} else {
			parseCoordinates(xCoords, yCoords, g2d);
		}
//		if ( oldX != 0 && oldY != 0 ) {
//			if ( !mousePressed || mouseDragged ) {
//				oldX = currX;
//				oldY = currY;
//				BasicStroke bs = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
//				g2d.setStroke(bs);  
//				g2d.drawLine(oldY, oldX, currY, currX);
//				repaint();
//			}
//			System.out.println(xCoords.size());
			
			// DOESNT WORK
//			if(xCoords.size() > 0) {
//				System.out.println("Canvas SizeX:" + xCoords.size());
//				System.out.println("Canvas SizeY:" + yCoords.size());
//			}
			
//		}		
		g.drawImage(image, 0, 0, null);
	}
	
	public void setNewCoords(int oX, int oY, int cX, int cY) {		
		oldX = oX;
		oldY = oY;
		currX = cX;
		currY = cY;
	}
	
	public void receiveCoords( String z ) {
		if ( z.length() > 6 ) {
//			String x = z.substring(1, z.length()/2-1);
//			String y = z.substring(z.length()/2+2, z.length()-1);
//			if (x.length() > 2 && y.length() > 2) {
			
			String[] zA = z.split(Pattern.quote("]["));
			String x = zA[0].substring(1, zA[0].length());
			String y = zA[1].substring(0, zA[1].length()-1);
			xCoords = x;
			yCoords = y;
			System.out.println("X: " + x);
			System.out.println("--------");
			System.out.println("Y: " + y);
		}
	}
	
	public void parseCoordinates( String x, String y, Graphics2D g2d) {
		if (x.length() > 2 && y.length() > 2) {
//			x = x.substring(1, x.length()-1);
//			y = y.substring(1, y.length()-1);
			String[] xCoordsA = x.split(", ");
			String[] yCoordsA = y.split(", ");
//			System.out.println("x length: " + x.length());
//			System.out.println("y length: " + y.length());
			for(int i = 0; i < xCoordsA.length-1; i++) {
				BasicStroke bs = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				g2d.setStroke(bs);  
				g2d.drawLine(Integer.parseInt(xCoordsA[i]), Integer.parseInt(yCoordsA[i]), Integer.parseInt(xCoordsA[i+1]), Integer.parseInt(yCoordsA[i+1]));
			}
		}
	}
	
	public void setArrayList(String x, String y) {
		xCoords = x;
		yCoords = y;
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
	
	public void setMousePressed( boolean mp ) {
		mousePressed = mp;
	}
	
	public void setMouseDragged( boolean md ) {
		mouseDragged = md;
	}
}
