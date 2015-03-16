/*
 * BackgroundPanel.java
 * 
 * Version:
 * 		$Id: BackgroundPanel.java,v 1.1 2006/09/16 18:26:14 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: BackgroundPanel.java,v $
 * 		Revision 1.1  2006/09/16 18:26:14  idp3448
 * 		Added commenting.
 * 		
 */

package GameUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Creates a panel with a stretchable painted background
 * 
 * @author Ian Paterson
 */
public class BackgroundPanel extends JPanel {
	
	/**
	 * The background of the panel 
	 */
	private ImageIcon background;
	
	/**
	 * General constructor with null LayoutManager
	 * 
	 * @param bkg the background image of the panel
	 */
	public BackgroundPanel(ImageIcon bkg) {
		this(null, bkg);	
	}	
	
	/**
	 * Constructor with specific LayoutManager
	 * 
	 * @param l the LayoutManager to use for the panel
	 * @param bkg the background image of the panel
	 */
	public BackgroundPanel(LayoutManager l, ImageIcon bkg) {
		super(l);
		
		// Make the default background of the panel transparent
		setOpaque(false);
		
		background = bkg;
	}
	
	/**
	 * Sets and repaints the background
	 * 
	 * @param bkg the new background image
	 */
	public void setBackground(ImageIcon bkg) {
		background = bkg;
		
		// Changes the displayed background
		repaint();
	}
	
	/**
	 * Overrides the paintComponent method to paint the current background
	 * stretched to fit the container size
	 */
	public void paintComponent(Graphics g) {
		// Get the size of this BackgroundPanel
		Dimension d = getSize();
		
		// Draw the background stretched to fit
		g.drawImage(background.getImage(), 0, 0, d.width, d.height, null);
		
		// Resume standard painting procedure
		super.paintComponent(g);
	}
}
