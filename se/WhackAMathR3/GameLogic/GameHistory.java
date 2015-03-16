/*
 * GameHistory.java
 *
 * Version:
 *     $Id: GameHistory.java,v 1.6 2006/11/04 15:36:42 exl2878 Exp $
 *
 * Revisions:
 *     $Log: GameHistory.java,v $
 *     Revision 1.6  2006/11/04 15:36:42  exl2878
 *     GameHistory constructor now takes a game type for an argument
 *
 *     Revision 1.5  2006/10/26 06:15:43  emm4674
 *     updated toString method
 *
 *     Revision 1.4  2006/10/23 20:01:00  exl2878
 *     GameHistory objects are now serializable
 *
 *     Revision 1.3  2006/10/23 00:59:01  emm4674
 *     *** empty log message ***
 *
 *     Revision 1.2  2006/10/23 00:29:17  emm4674
 *     added a toString() method
 *
 *     Revision 1.1  2006/10/21 16:49:09  exl2878
 *     Initial version
 *
 */
package GameLogic;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Holds the records for a student's single sitting of the game Whack Whack Math
 * Attack.
 * 
 * @author Eric Lutley exl2878@rit.edu
 */
public class GameHistory implements Comparable, Serializable {

	/**
	 * The date of a student's game.
	 */
	private Date date;

	/**
	 * Date format used when returning a date string.
	 */
	private static DateFormat DATE_FORMAT = DateFormat
			.getDateInstance(DateFormat.SHORT);

	/**
	 * The game type of this game session.
	 */
	private int gameType;
	
	/**
	 * The score of a student's game.
	 */
	private int score;

	/**
	 * The number of questions a student answered correctly.
	 */
	private int questionsCorrect;

	/**
	 * The number of questions a student answered correctly on the first try.
	 */
	private int numCorrectOnFirstTry;

	/**
	 * The number of questions the student attempted to answer, including
	 * questions that were wrongly answered.
	 */
	private int questionsTotal;
	
	/**
	 * Constructor for a GameHistory object. The date is set to the current
	 * date, and the score and question counts are set to 0.  The game type
	 * passed into this constructor should match one of the game constants in
	 * GameManager.
	 * 
	 * @param gameType - GameManager constant representing game played this session 
	 */
	public GameHistory( int gameType ) {
		date = new Date(new GregorianCalendar().getTimeInMillis());
		this.gameType = gameType;
		score = 0;
		questionsCorrect = 0;
		numCorrectOnFirstTry = 0;
		questionsTotal = 0;
	}

	/**
	 * Compares two GameHistory objects for ordering. GameHistory objects are
	 * ordered according to the dates they were created.
	 * 
	 * @param obj -
	 *            the GameHistory to be compared
	 * @return 0 if they were created at the same time, less than 0 if this
	 *         GameHistory was created first, and greater than zero if the
	 *         argument GameHistory was created first
	 */
	public int compareTo(Object obj) {
		GameHistory other = (GameHistory) obj;
		return date.compareTo(other.date);
	}

	/**
	 * @return Returns the date.
	 */
	public String getDateString() {
		return DATE_FORMAT.format(date);
	}
	
	/**
	 * Returns this session's game type.  The game type is a constant
	 * in GameManager.
	 *
	 * @return the game type of this session
	 */
	public int getGameType() {
		return gameType;
	}
	
	/**
	 * Returns a string representation of this session's game type.
	 *
	 * @return string representation of this session's game type
	 */
	public String getGameTypeString() {
		String retVal = null;
		if ( gameType == GameManager.ADD_SUBT )
			retVal = "Addition and Subtraction";
		else if ( gameType == GameManager.MULT_DIVIDE )
			retVal = "Multiplication and Division";
		else if ( gameType == GameManager.MIX )
			retVal = "Mixed Game";
		else
			retVal = "Unknown Game Type";
		return retVal;
	}

	/**
	 * Returns the number of questions correctly answered by the student on his
	 * or her first try.
	 * 
	 * @return the number of questions correctly answered on the first try
	 */
	public int getNumCorrectOnFirstTry() {
		return numCorrectOnFirstTry;
	}

	/**
	 * @return Returns the questionsCorrect.
	 */
	public int getQuestionsCorrect() {
		return questionsCorrect;
	}

	/**
	 * @return Returns the questionsTotal.
	 */
	public int getQuestionsTotal() {
		return questionsTotal;
	}

	/**
	 * @return Returns the score.
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Formats this GameHistory object as a String object
	 * 
	 * @return this GameHistory object as a String object
	 */
	public String toString() {
		return (getDateString() + ": " + getScore() + ", "
				+ getQuestionsCorrect() + "/" + getQuestionsTotal() + ", " + getNumCorrectOnFirstTry());
	}

	/**
	 * Records that a student correctly answered a question. Its boolean
	 * parameter specifies whether or not the student used more than one try in
	 * the course of correctly answering the question.
	 * 
	 * @param firstTry -
	 *            true if the student used only one try, else false
	 */
	public void addCorrectAnswer(boolean firstTry) {
		questionsCorrect += 1;
		questionsTotal += 1;
		if (firstTry)
			numCorrectOnFirstTry += 1;
	}

	/**
	 * Records that a student incorrectly answered a question.
	 */
	public void addIncorrectAnswer() {
		questionsTotal += 1;
	}

	/**
	 * @param score
	 *            The score to set.
	 */
	public void increaseScore(int roundScore) {
		score += roundScore;
	}
}
