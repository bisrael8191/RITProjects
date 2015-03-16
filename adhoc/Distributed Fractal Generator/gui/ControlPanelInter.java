/**
 * ControlPanelInter.java
 * 
 * Version:
 * $Id: ControlPanelInter.java,v 1.4 2007/05/20 19:55:33 bisrael Exp $
 * 
 * Revisions:
 * $Log: ControlPanelInter.java,v $
 * Revision 1.4  2007/05/20 19:55:33  bisrael
 * Added comments.
 *
 * Revision 1.3  2007/05/14 09:11:16  bisrael
 * Uses problem names instead of the index number.
 *
 * Revision 1.2  2007/05/13 21:31:15  bisrael
 * Methods have to return void
 *
 *
 */

package gui;
import java.io.File;

/**
 * Interface for the control panel gui that handles user actions.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public interface ControlPanelInter {

	/**
	 * Stop button was clicked.
	 * 
	 * @param problemName - selected problem name
	 */
	public void stop(String problemName);
	
	/**
	 * Start button was clicked.
	 * 
	 * @param problemName - selected problem name
	 */
	public void start(String problemName);
	
	/**
	 * Quit button was clicked.
	 */
	public void quit();
	
	/**
	 * Create New Problem button was clicked.
	 * 
	 * @param classFile - pointer to the implemented class file
	 */
	public void createNew(File classFile);
	
}
