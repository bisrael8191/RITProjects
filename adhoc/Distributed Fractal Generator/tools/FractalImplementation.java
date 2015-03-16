/**
 * FractalImplementation.java
 * 
 * Version:
 * $Id: FractalImplementation.java,v 1.2 2007/05/20 19:00:51 bisrael Exp $
 * 
 * Revisions:
 * $Log: FractalImplementation.java,v $
 * Revision 1.2  2007/05/20 19:00:51  bisrael
 * Added comments.
 *
 * Revision 1.1  2007/05/16 05:14:16  bisrael
 * FractalImplementation is now in the tools package.
 *
 *
 */
package tools;

import java.awt.Color;

/**
 * When Implementing this class make sure of the following:
 *   There must be only one declared constructor
 *   It must not be inside of a package
 *   Constructor should take in strings and then cast them to the correct types
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public interface FractalImplementation {

	/**
	 * Does the computations for the fractal and then returns
	 * the color of the inputted pixel.
	 * 
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @return - color of the single pixel in the image
	 */
	public Color getPixelColor( double x, double y);
	
}