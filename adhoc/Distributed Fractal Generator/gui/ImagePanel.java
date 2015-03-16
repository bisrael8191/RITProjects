package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Image;
import javax.swing.ImageIcon;

import javax.swing.JLabel;
import javax.swing.Scrollable;

public class ImagePanel extends JLabel implements Scrollable {

	private int width;
	private int height;
	private Image image;
	
	public ImagePanel(int width, int height){
		this.width = width;
		this.height = height;
		setSize(width, height);
	}
	
	public void setImage(Image image){
		this.image = image;
		setIcon(new ImageIcon(image));
	}
	
	
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return new Dimension(width, height);
	}

	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void paint(Graphics g){
		super.paint(g);

		g.drawImage(image, 0, 0, this);
		
	}

}
