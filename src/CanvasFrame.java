import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class CanvasFrame extends JFrame {
	
	private int width;
	private int height;
	private Container container;
	private JButton clear, black, red, blue, yellow, green, eraser, five, ten, twenty;
	private Canvas canvas;
	private JPanel buttonPanel;

	public CanvasFrame(int w, int h, Canvas c) {
		canvas = c;
		width = w;
		height = h;
		container = this.getContentPane();
		five = new JButton("5");
		ten = new JButton("10");
		twenty = new JButton("20");
		clear = new JButton("Clear");
		black = new JButton("Black");
		red = new JButton("Red");
		blue = new JButton("Blue");
		yellow = new JButton("Yellow");
		green = new JButton("Green");
		eraser = new JButton("Eraser");
		buttonPanel = new JPanel();
	}
	
	public void setUpFrame() {
		this.setSize(width, height);
//		this.setTitle("Tester");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setLayout(new BorderLayout());
		container.add(canvas, BorderLayout.CENTER);
		buttonPanel.setLayout(new GridLayout(1,10));
		buttonPanel.add(five);
		buttonPanel.add(ten);
		buttonPanel.add(twenty);
		buttonPanel.add(black);
		buttonPanel.add(red);
		buttonPanel.add(blue);
		buttonPanel.add(yellow);
		buttonPanel.add(green);
		buttonPanel.add(eraser);
		buttonPanel.add(clear);
		container.add(buttonPanel, BorderLayout.SOUTH);
		this.setVisible(true);
		this.getContentPane().setBackground(Color.WHITE);
	}
	
	public void setUpButtons() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if ( ae.getSource() == clear ) {
					canvas.clear();
				} else if ( ae.getSource() == black ) {
					canvas.black();
				} else if ( ae.getSource() == red ) {
					canvas.red();
				} else if ( ae.getSource() == blue ) {
					canvas.blue();
				} else if ( ae.getSource() == yellow ) {
					canvas.yellow();
				} else if ( ae.getSource() == green ) {
					canvas.green();
				} else if ( ae.getSource() == eraser ) {
					canvas.eraser();
				} else if ( ae.getSource() == five ) {
					canvas.set5();
				} else if ( ae.getSource() == ten ) {
					canvas.set10();
				} else {
					canvas.set20();
				}
			}
		};
		five.addActionListener(al);
		ten.addActionListener(al);
		twenty.addActionListener(al);
		clear.addActionListener(al);
		black.addActionListener(al);
		red.addActionListener(al);
		blue.addActionListener(al);
		yellow.addActionListener(al);
		green.addActionListener(al);
		eraser.addActionListener(al);
	}
	
}
