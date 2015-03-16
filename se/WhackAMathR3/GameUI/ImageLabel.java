/*
 * ImageLabel.java
 * 
 * Version:
 * 		$Id: ImageLabel.java,v 1.1 2006/09/16 20:20:22 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: ImageLabel.java,v $
 * 		Revision 1.1  2006/09/16 20:20:22  idp3448
 * 		Created to simplify code for creating a label with an image in the rest of the program
 * 		
 */

package GameUI;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Creates a label with an ImageIcon to avoid specifying an ImageIcon
 * when creating the label
 * 
 * @author Ian Paterson
 */
public class ImageLabel extends JLabel {

	/**
	 * Constructor with ImageIcon argument
	 * 
	 * @param img the ImageIcon to use on the label
	 */
	public ImageLabel(String img) {
		super(new ImageIcon(img));
		
		ImageIcon i = new ImageIcon(img);
		
		// Set the default bounds of the label to the dimensions of the image
		setBounds(0,0,i.getIconWidth(), i.getIconHeight());
		
		// Set the positionl of the text relative to the icon
		setHorizontalTextPosition(CENTER);
		setVerticalTextPosition(CENTER);		
	}
	
	/**
	 * Sets the position of the label in the environment
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void setPosition(int x, int y) {
		// Change the x and y coords while maintaining the width and height
		setBounds(x,y,getBounds().width, getBounds().height);
	}
	
}
