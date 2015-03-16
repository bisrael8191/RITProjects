/*
 * Mole.java
 * 
 * Version:
 * 		$Id: Mole.java,v 1.10 2006/11/07 05:03:21 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: Mole.java,v $
 * 		Revision 1.10  2006/11/07 05:03:21  idp3448
 * 		Modified for use with levels starting at 1.
 * 		
 * 		Revision 1.9  2006/10/26 05:25:30  idp3448
 * 		Set lower limits on speed
 * 		
 * 		Revision 1.8  2006/10/26 02:29:19  idp3448
 * 		Fixed speed
 * 		
 * 		Revision 1.7  2006/10/19 04:20:50  idp3448
 * 		Added setSpeed method and implemented speed-related animation modifications
 * 		
 * 		Revision 1.6  2006/10/05 23:15:08  idp3448
 * 		Fixed overlay problems on second round.
 * 		
 * 		Revision 1.5  2006/10/05 23:04:34  idp3448
 * 		Added support for moles underneath the hole overlay image.
 * 		Added an instructions screen.
 * 		Changed component ZOrder
 * 		
 * 		Revision 1.4  2006/10/05 06:49:24  idp3448
 * 		Almost just about ready for R1, all RoundManager and Environment stuff seems to work.
 * 		
 * 		Revision 1.3  2006/09/23 08:05:13  idp3448
 * 		Changed some stupidly-named methods to be more appropriate.
 * 		
 * 		Revision 1.2  2006/09/17 03:18:29  idp3448
 * 		Added support for hiding the mole when another mole is clicked.
 * 		
 * 		Revision 1.1  2006/09/16 20:30:29  idp3448
 * 		Added commenting and clarified the names of misleading variables.
 * 		
 */

package GameUI; 

import java.awt.Point;
import java.awt.Rectangle;

/**
 * A mole creates itself with the proper image and performs the 
 * actions to get to its destination
 * 
 * @author Ian Paterson
 */
public class Mole extends Creature implements Runnable {
	
	/**
	 * The vertical offset in pixels required to hide the mole 
	 */
	private final int y0 = 140;
	
	/**
	 * The default time that the mole remains up
	 */
	private int uptime = 1000;
	
	/**
	 * The default speed at which the Mole moves up and down
	 */
	private int speed = 1;	
	
	/**
	 * The current y position with respect to the initial
	 */
	private int y;	
	
	/**
	 * Create the Creature and add the necessary MouseListener
	 */
	public Mole() {
		super("images/mole.png");
		
		// Start at the original position
		y = 0;
	}
	
	/**
	 * Set the position of the mole then offset it vertically by y0 
	 */
	public void setPosition(Point p) {
		super.setPosition(p.x, p.y + y0);
	}
	
	/**
	 * Moves the mole
	 * 
	 * @param dy the amount by which to move the mole
	 */
	private void move(int dy) {
		Rectangle r = new Rectangle(
				getBounds().x,
				getBounds().y - dy,
				getBounds().width,
				getBounds().height
		);
		
		setBounds(r);
	}
	
	/**
	 * Allows the overall speed of the mole and the time it remains
	 * up to increase or decrease 
	 * 
	 * @param s the speed of the mole, 1 is slowest, 10 is fastest
	 */
	public void setSpeed(int s) {
		// Decrease the amount of uptime for each speed level
		uptime = 1000 - (s - 1) * 100;
		
		// Ensure a positive uptime
		if (uptime < 100) {
			uptime = 100;
		}
		
		speed = 1 + (int) s / 3;
	}

	/**
	 * Defines the animation behavior of the mole
	 */
	public void run() {
		try {
			// Move the mole up one pixel at a time until it is all the way up
			while (y < y0 && !isHit() && !isHurrying()) {
				y += speed;
				
				// Move up
				move(speed);
				
				// Vary the time between movements to make the mole
				// slow down as it goes 
				Thread.sleep(Math.max(Math.round(y / 30), 1));
			}
			
			for (int i = 0; i < uptime && !isHit() && !isHurrying(); i++) {
				Thread.sleep(1);
			}
			
			// Move the mole one pixel at a time until it is back where it started
			while (y >= 0) {
				// The default distance to move
				int moveBy = -speed;
				
				// If the mole was hit, move faster
				if (isHit() || isHurrying())
					moveBy -= 5;
				
				y += moveBy;
				
				// Move down
				move(moveBy);

				// Vary the time between movements to make the mole
				// speed up as it goes
				Thread.sleep(Math.max(Math.round(y / 30), 1));
			}	
			
			getParent().remove(this);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
