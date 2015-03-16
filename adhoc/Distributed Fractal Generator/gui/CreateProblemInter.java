/**
 * CreateProblemInter.java
 * 
 * Version:
 * $Id: CreateProblemInter.java,v 1.4 2007/05/20 19:57:15 bisrael Exp $
 * 
 * Revisions:
 * $Log: CreateProblemInter.java,v $
 * Revision 1.4  2007/05/20 19:57:15  bisrael
 * Added comments.
 *
 * Revision 1.3  2007/05/13 23:27:10  jhays
 * The Interface needs an Name and author field
 *
 * Revision 1.2  2007/05/13 21:31:34  bisrael
 * Methods have to return void
 *
 *
 */

package gui;

/**
 * Interface for the create new problem gui.
 * Handles user actions.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public interface CreateProblemInter {

	/**
	 * Create New Problem button was clicked.
	 * 
	 * @param name - problem name
	 * @param author - author name
	 * @param height - image height
	 * @param width - image width
	 * @param xcenter - x center coordinate
	 * @param ycenter - y center coordinate
	 * @param resolution - pixels per unit
	 * @param params - constructor parameters
	 */
	public void createProblem(String name, String author, int height, int width, 
			double xcenter, double ycenter, double resolution, String[] params);
	
}
