/*
 * Creature.java
 * 
 * Version:
 * 		$Id: Creature.java,v 1.6 2006/10/19 04:20:50 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: Creature.java,v $
 * 		Revision 1.6  2006/10/19 04:20:50  idp3448
 * 		Added setSpeed method and implemented speed-related animation modifications
 * 		
 * 		Revision 1.5  2006/10/05 23:04:34  idp3448
 * 		Added support for moles underneath the hole overlay image.
 * 		Added an instructions screen.
 * 		Changed component ZOrder
 * 		
 * 		Revision 1.4  2006/10/05 06:49:24  idp3448
 * 		Almost just about ready for R1, all RoundManager and Environment stuff seems to work.
 * 		
 * 		Revision 1.3  2006/09/23 08:10:20  idp3448
 * 		Changed nonsensical method names to more cheery and logical names.
 * 		
 * 		Revision 1.2  2006/09/17 03:10:13  idp3448
 * 		Improved ability to cause the Creature to hide itself and differentiated between whether the Creature went into its hole because it was clicked or because someone else was clicked.
 * 		
 * 		Revision 1.1  2006/09/16 18:28:36  idp3448
 * 		Changed extension type to new SmoothLabel in order to automate text antialiasing and added a mechanism for storing the Creature's number.  Improved commenting.
 * 		
 */

package GameUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.accessibility.Accessible;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * An abstract class which defines global characteristics of all creatures.
 * This class is capable of responding to events fired on it and all subclasses
 * MUST define their motion by overriding the run() and act() methods.  
 * 
 * @author Ian Paterson
 */
public abstract class Creature extends SmoothLabel implements Runnable, Accessible {

	/**
	 * The number of the Creature
	 */
	private int myNumber;
	
	/**
	 * True if the Creature was hit, false otherwise.
	 * Volatile means that it will be reloaded each time its value is checked
	 */
	private volatile boolean hit;	
	
	/**
	 * True if the Creature needs to be hide, false otherwise.
	 */
	private volatile boolean hurrying;		
	
	/**
	 * Constructs a Creature.  A Creature must be constructed with an image to 
	 * represent it on the screen.  
	 * 
	 * @param creatureImage an ImageIcon of the Creature 
	 */
	public Creature(String img) {
		// Create the JLabel with the specified ImageIcon
		super(img);
	
		hit = false;
		hurrying = false;
		
		ImageIcon creatureImage = new ImageIcon(img);
		
		// Set the bounds of the JLabel to match the image
		setBounds(0, 0, creatureImage.getIconWidth(), creatureImage.getIconHeight());
		
		// Font settings will position the number in an open space on the image
		setFont(new Font("Trebuchet MS", Font.PLAIN, 42));
		setForeground(Color.YELLOW);
		setHorizontalTextPosition(CENTER);
		setVerticalTextPosition(CENTER);
		setVerticalAlignment(BOTTOM);
	}
	
	/**
	 * Sets the Creature's number and displays it as a label
	 * 
	 * @param n the creature's number
	 */
	public void setNumber(int n) {
		myNumber = n;
		
		// Convert the int to a String to set the label text
		setText(String.valueOf(n));
	}
	
	/**
	 * Sets the Creature's speed
	 * 
	 * @param s the speed of the Creature
	 */
	public abstract void setSpeed(int s);
	
	/**
	 * Set the position of the Creature based on its destination
	 */
	public abstract void setPosition(Point p);	
	
	/**
	 * Hits the Creature
	 */
	public void hit() {
		hit = true;
	}	
	
	/**
	 * Tells whether the Creature should hide
	 *  
	 * @return true if the Creature should hide
	 */
	public void hurryUp() {
		hurrying = true;
	}		
	
	/**
	 * Tells whether the Creature has been hit
	 *  
	 * @return true if the Creature has been hit
	 */
	public boolean isHit() {
		return hit;
	}
	
	/**
	 * Tells whether the Creature has been hidden
	 *  
	 * @return true if the Creature has been hidden
	 */
	public boolean isHurrying() {
		return hurrying;
	}
	
	/**
	 * Manages the Creature's actions
	 */
	public abstract void run();

}
