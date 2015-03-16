/**
 * myTestFractal.java
 * 
 * Version:
 * $Id: myTestFractal.java,v 1.2 2007/05/16 05:10:10 bisrael Exp $
 * 
 * Revisions:
 * $Log: myTestFractal.java,v $
 * Revision 1.2  2007/05/16 05:10:10  bisrael
 * Implements FractalImplementation from the tools package.
 *
 * Revision 1.1  2007/05/13 20:04:53  jhays
 * *** empty log message ***
 *
 * Revision 1.1  2007/05/13 17:32:59  jhays
 * *** empty log message ***
 *
 *
 */

import java.awt.Color;
import java.util.Random;

import tools.FractalImplementation;

/**
 * Description
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class myTestFractal implements FractalImplementation {

	private int maxIter;
	private double breakOut;
	
	public myTestFractal(String maxIter, String breakOut){
		this.maxIter = Integer.parseInt(maxIter);
		this.breakOut = Double.parseDouble(breakOut);
	}
	
	/* (non-Javadoc)
	 * @see FractalImplementation#getPixelColor(double, double)
	 */
	public Color getPixelColor(double x, double y) {
		//System.out.println("Const Args: " + maxIter + ", " + breakOut);
		Random chooser = new Random();
		Color pixel = new Color( chooser.nextInt(255), chooser.nextInt(255), chooser.nextInt(255));
		return pixel;
	}

}
