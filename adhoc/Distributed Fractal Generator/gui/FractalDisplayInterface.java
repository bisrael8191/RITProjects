/**
 * FractalDisplayInterface.java
 * 
 * Version:
 * $Id: FractalDisplayInterface.java,v 1.5 2007/05/20 07:06:00 bisrael Exp $
 * 
 * Revisions:
 * $Log: FractalDisplayInterface.java,v $
 * Revision 1.5  2007/05/20 07:06:00  bisrael
 * Only neede the name of the problem, not the ui jframe.
 *
 * Revision 1.4  2007/05/15 01:40:54  jhays
 * *** empty log message ***
 *
 * Revision 1.3  2007/05/14 21:55:17  jhays
 * *** empty log message ***
 *
 * Revision 1.2  2007/05/13 21:32:14  bisrael
 * Added cvs comments
 *
 *
 */

package gui;

public interface FractalDisplayInterface {

	//Stops this fractal
	public void windowClosed(String fractalName);
	
}
