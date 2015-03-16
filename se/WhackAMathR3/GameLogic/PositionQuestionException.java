/*
 * PositionQuestionException.java
 *
 * Version:
 *     $Id: PositionQuestionException.java,v 1.1 2006/11/07 04:33:57 exl2878 Exp $
 *
 * Revisions:
 *     $Log: PositionQuestionException.java,v $
 *     Revision 1.1  2006/11/07 04:33:57  exl2878
 *     Initial revision
 *
 */

package GameLogic;
/**
 * Exception thrown when the constructor for a position question receives
 * invalid parameters.
 *
 * @author Eric Lutley
 */
public class PositionQuestionException extends RuntimeException {

	/**
	 * Constructor for a PositionQuestionException object
	 * 
	 * @param message - string describing the exception
	 */
	public PositionQuestionException( String message ) {
		super( message );	
	}
}
