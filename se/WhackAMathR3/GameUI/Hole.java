/*
 * Hole.java
 * 
 * Version:
 * 		$Id: Hole.java,v 1.3 2006/10/05 06:49:24 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: Hole.java,v $
 * 		Revision 1.3  2006/10/05 06:49:24  idp3448
 * 		Almost just about ready for R1, all RoundManager and Environment stuff seems to work.
 * 		
 * 		Revision 1.2  2006/10/02 01:19:33  idp3448
 * 		Somehow the isOccupied boolean was implemented backwards which must have been the product of late-night work.
 * 		
 * 		Revision 1.1  2006/09/23 08:05:50  idp3448
 * 		Initial revision, this basically deals with occupancy or vacancy of a hole at any given point.
 * 		
 */

package GameUI;

import java.awt.Point;

/**
 * Provides an extension to the standard Point to provide vacancy
 * information.
 * 
 * @author Ian Paterson
 */
public class Hole extends Point {
	
	/**
	 * Whether the hole is occupied by a Creature or not
	 */
	boolean isOccupied;
	
	/**
	 * Standard constructor accepts x and y coordinates of the upper left corner
	 * of the final destination
	 * 
	 * @param x the x component of the upper left corner
	 * @param y the y component of the upper left corner
	 */
	public Hole(int x, int y) {
		super(x, y);
			
		isOccupied = false;
	}

	/**
	 * Returns whether the hole is currently occupied
	 * 
	 * @return whether the hole is occupied
	 */
	public boolean isOccupied() {
		return isOccupied;
	}
	
	/**
	 * Causes the Hole to no longer be occupied
	 */
	public void occupy() {
		isOccupied = true;
	}
	
	/**
	 * Causes the Hole to become occupied
	 */
	public void vacate() {
		isOccupied = false;
	}
}
