/**
 * AdHocFractals.java
 * 
 * Version:
 * $Id: AdHocFractals.java,v 1.2 2007/05/14 00:18:24 bisrael Exp $
 * 
 * Revisions:
 * $Log: AdHocFractals.java,v $
 * Revision 1.2  2007/05/14 00:18:24  bisrael
 * Starts the control panel
 *
 * Revision 1.1  2007/05/14 00:02:32  bisrael
 * Created the main class for the project.
 * Will add arguments if needed.
 *
 *
 */

import controlpanellogic.ControlPanelLogic;

/**
 * Main class for the project.
 * Loads the control panel
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class AdHocFractals {
	
	/**
	 * Main method for the project.
	 * @param args - no arguments for this program
	 */
	public static void main(String[] args) throws Exception{
		new ControlPanelLogic().init();
	}
}
