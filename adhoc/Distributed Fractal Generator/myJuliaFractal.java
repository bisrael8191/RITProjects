/**
 * myTestFractal.java
 * 
 * Version:
 * $Id: myJuliaFractal.java,v 1.8 2007/05/17 14:33:29 jhays Exp $
 * 
 * Revisions:
 * $Log: myJuliaFractal.java,v $
 * Revision 1.8  2007/05/17 14:33:29  jhays
 * Fixed color bug
 *
 * Revision 1.7  2007/05/17 04:01:58  jhays
 * Added 2 extra arguments. A and B for the making of different julia sets
 *
 * Revision 1.6  2007/05/17 02:11:58  jhays
 * *** empty log message ***
 *
 * Revision 1.5  2007/05/16 18:45:20  jhays
 * *** empty log message ***
 *
 * Revision 1.4  2007/05/16 17:10:57  jhays
 * *** empty log message ***
 *
 * Revision 1.3  2007/05/16 16:49:00  jhays
 * Fractals
 *
 * Revision 1.2  2007/05/16 05:10:21  bisrael
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

import tools.FractalImplementation;

/**
 * Description
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class myJuliaFractal implements FractalImplementation {

	private int maxIter;
	private double breakOut;
	
	//The start value for this Julia set
	private double a;
	private double b;
	
	// Table of hues.
	static float[] huetable;
	
	public myJuliaFractal(String max, String bOut, String aa, String bb){
		this.maxIter = Integer.parseInt(max);
		this.breakOut = Double.parseDouble(bOut);
		a = Double.parseDouble(aa);
		b = Double.parseDouble(bb);
		
		// Create table of hues for different iteration counts.
		huetable = new float [maxIter+1];
		for (int i = 0; i < maxIter; ++ i)
			{
			huetable[i] = 
				/*hue*/ (float)Math.pow( (double)i/((double)maxIter) ,0.4);
	
			}
		huetable[maxIter] = 1.0f;
	}
	
	/* (non-Javadoc)
	 * @see FractalImplementation#getPixelColor(double, double)
	 */
	public Color getPixelColor(double x, double y) {
		// Iterate until convergence.
		int i = 0;
		double fzx, fzy;
		double xold = x;
		double yold = y;
		//double fza, fzb;
		//double a = 0.3;
		//double b = 0.6;
		double zmagsqr = 0.0;
		while (i < maxIter && zmagsqr <= 4.0){
			i++;
			fzx = xold*xold - yold*yold + a;
			fzy = 2.0*xold*yold + b;
			zmagsqr = fzx*fzx + fzy*fzy;
			xold = fzx;
			yold = fzy;
		}
		

		return Color.getHSBColor(huetable[i], 1.0f, 1.0f);
		
		/*if( i%256 >= 0 && i%256 < 32 ) return Color.BLACK;
		else if ( i%256 >= 32 && i%256 < 64 ) return Color.WHITE;
		else if ( i%256 >= 64 && i%256 < 96 ) return Color.YELLOW;
		else if ( i%256 >= 96 && i%256 < 128 ) return Color.GREEN;
		else if ( i%256 >= 128 && i%256 < 160 ) return Color.BLUE;
		else if ( i%256 >= 160 && i%256 < 192 ) return Color.MAGENTA;
		else if ( i%256 >= 192 && i%256 < 224 ) return Color.DARK_GRAY;
		else if ( i%256 >= 224 && i%256 <= 256 ) return Color.BLACK;
		else return Color.BLACK;*/
	}

}
