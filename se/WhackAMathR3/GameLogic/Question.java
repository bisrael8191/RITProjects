/**
 * Question.java
 * 
 * Version:
 * $Id: Question.java,v 1.18 2006/11/07 05:33:19 exl2878 Exp $
 * 
 * Revisions:
 * $Log: Question.java,v $
 * Revision 1.18  2006/11/07 05:33:19  exl2878
 * Changed error messages thrown with PositionQuestionExceptions
 *
 * Revision 1.17  2006/11/07 04:35:00  exl2878
 * Constructor may now throw PositionQuestionExceptions when given bad
 * parameters
 *
 * Revision 1.16  2006/11/03 05:45:10  jmf8241
 * Removed TODO as it will no longer be done in this class.
 *
 * Revision 1.15  2006/10/26 16:29:20  emm4674
 * added findType method to be able to look up question types based on a given symbol
 *
 * Revision 1.14  2006/10/25 21:21:38  jmf8241
 * Changed "*" in toString to "x" as per user requirements.
 *
 * Revision 1.13  2006/10/25 21:06:02  jmf8241
 * Fixed problem with Division check.
 *
 * Revision 1.12  2006/10/25 04:08:46  jmf8241
 * Added Equals() method
 *
 * Revision 1.11  2006/10/23 20:05:02  exl2878
 * Question objects are now serializable
 *
 * Revision 1.10  2006/10/20 20:29:39  jmf8241
 * Added division check catch.
 *
 * Revision 1.9  2006/10/20 20:18:25  jmf8241
 * Checks for division problem error and sets it to a known good problem.
 *
 * Revision 1.8  2006/10/19 03:49:52  emm4674
 * updated formatQuestion to not include an answer for POSITION type questions
 *
 * Revision 1.7  2006/10/17 22:49:10  emm4674
 * *** empty log message ***
 *
 * Revision 1.6  2006/10/17 22:48:07  emm4674
 * added formatQuestion method to allow for easy display of a question
 *
 * Revision 1.5  2006/10/16 03:21:16  jmf8241
 * Updated to support multiplication and division problems
 *
 * Revision 1.4  2006/10/09 02:02:41  idp3448
 * Fixed HTML display issue to ensure the text is rendered immediately as HTML, not text.
 *
 * Revision 1.3  2006/10/06 19:32:21  jmf8241
 * Fixed String problem.
 *
 * Revision 1.2  2006/10/04 21:53:49  jmf8241
 * Added toString method.  Changed 1,2,3 to 1,10,100.
 *
 * Revision 1.1  2006/10/04 00:13:09  exl2878
 * Moved to GameLogic package
 *
 * Revision 1.1  2006/09/27 16:56:25  jmf8241
 * Initial Revision.  May eventually need to be updated when it
 * is determined how we will read in questions.
 *
 */
package GameLogic;

import javax.swing.JOptionPane;
import java.util.Random;
import java.io.Serializable;

/**
 * This class reads a question from a text file and then properly formats it.
 * 
 * @author Justin Field
 */
public class Question implements Serializable {

	/*
	 * Enum representing the type of question. SUM = addition problem DIFF =
	 * subtraction problem POSITION = Identify digit in x position problems.
	 * MULT = Multiplication Problem DIV = Division Problem
	 */
	public enum Type {
		SUM, DIFF, POSITION, MULT, DIV
	};

	/*
	 * Class Variables.
	 */
	private int firstTerm, SecondTerm; // The terms of the question.

	private Type theType; // The type of the problem.

	private Random numGenerator; // A random number generator.

	/**
	 * Constructor.
	 * 
	 * @param Type
	 *            theType - the type of the question.
	 * @param int
	 *            firstTerm - the first term of the equation, or, if type =
	 *            POSITION, the number to check for.
	 * @param int
	 *            secondTerm - the second term of the equation, or, if type =
	 *            POSITION, the magnitude to check for(units, tens etc.) 1
	 *            represents units, 10 tens, 100 hundreds.
	 */
	public Question(Type theType, int firstTerm, int secondTerm) {
		if (theType == Question.Type.DIV) {
			if ((secondTerm == 0) || (firstTerm % secondTerm != 0)) {
				throw new DivisionRemainderException("Division problem has a "
						+ "remainder, or attempts to divide by zero.");
			}
		} else if ( theType == Type.POSITION ) {
			if ( firstTerm < 0 || firstTerm > 9 ) {
				throw new PositionQuestionException( "The digit " +firstTerm +
													" is not between 0 and 9." );
			} else if(secondTerm != 1 && secondTerm != 10 && secondTerm != 100 )  {
				throw new PositionQuestionException( "The place " + secondTerm 
													+ " is not 1, 10, or 100." );
			}
		}
		setTheType(theType);
		setFirstTerm(firstTerm);
		setSecondTerm(secondTerm);
	}

	/**
	 * Returns a String representation of the question.
	 */
	public String toString() {
		String retString = ""; // String to return.

		if (getTheType() == Question.Type.SUM) {
			retString = "What is " + getFirstTerm() + " + " + getSecondTerm()
					+ "?";
		} else if (getTheType() == Question.Type.DIFF) {
			retString = "What is " + getFirstTerm() + " - " + getSecondTerm()
					+ "?";
		} else if (getTheType() == Question.Type.MULT) {
			retString = "What is " + getFirstTerm() + " x " + getSecondTerm()
					+ "?";
		} else if (getTheType() == Question.Type.DIV) {
			retString = "What is " + getFirstTerm() + " \u00F7 "
					+ getSecondTerm() + "?";
		} else {
			if (getSecondTerm() == 1) {
				retString = "<html><center>" + "Whack the mole that has a"
						+ "<br>" + getFirstTerm() + " in the units place."
						+ "</center></html>";
			} else if (getSecondTerm() == 10) {
				retString = "<html><center>" + "Whack the mole that has a"
						+ "<br>" + getFirstTerm() + " in the tens place."
						+ "</center></html>";
			} else if (getSecondTerm() == 100) {
				retString = "<html><center>" + "Whack the mole that has a"
						+ "<br>" + getFirstTerm() + " in the hundreds place."
						+ "</center></html>";
			} else {
				JOptionPane.showMessageDialog(null, "Bad Input!", "Error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}

		return retString;
	}

	/**
	 * Returns a String representation of this Question. Ex: "1 + 1 = 2"
	 * 
	 * @return a String representation of this Question
	 */
	public String formatQuestion() {
		String retVal = String.valueOf(getFirstTerm());
		Type type = getTheType();

		if (type == Type.SUM) {
			retVal += " + ";
		} else if (type == Type.DIFF) {
			retVal += " - ";
		} else if (type == Type.POSITION) {
			retVal += " : ";
		} else if (type == Type.MULT) {
			retVal += " x ";
		} else if (type == Type.DIV) {
			retVal += " \u00F7 ";
		}

		retVal += getSecondTerm();
		// add the answer if there exists only one answer
		if (type != Type.POSITION) {
			retVal += " = " + AnswerGenerator.getCorrectAnswer(this);
		}

		return retVal;
	}

	/**
	 * Determines if two questions are equal to each other by checking the first
	 * term, second term, and the type of the question. If they are all equal
	 * then the question is the same. Note: 5 + 1 and 1 + 5 would be considered
	 * the same question.
	 * 
	 * @param o -
	 *            another object.
	 */
	public boolean equals(Object o) {
		boolean retVal = false;

		// Determine if the object is an instance of Question.
		if (o instanceof Question) {

			// Determine if the questions have the same type.
			if (getTheType() == ((Question) o).getTheType()) {

				// Determine if order matters.
				if ((getTheType() == Question.Type.SUM)
						|| (getTheType() == Question.Type.MULT)) {

					// Order doesn't matter
					// Check first term to first term, second term to second
					// term.
					if ((getFirstTerm() == ((Question) o).getFirstTerm())
							&& (getSecondTerm() == ((Question) o)
									.getSecondTerm())) {
						retVal = true;
					}
					// Check first term to second term, second term to first
					// term.
					else if ((getFirstTerm() == ((Question) o).getSecondTerm())
							&& (getSecondTerm() == ((Question) o)
									.getFirstTerm())) {
						retVal = true;
					}
				}
				// Order matters.
				else {
					if ((getFirstTerm() == ((Question) o).getFirstTerm())
							&& (getSecondTerm() == ((Question) o)
									.getSecondTerm())) {
						retVal = true;
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * @return Returns the theType.
	 */
	public Type getTheType() {
		return theType;
	}

	/**
	 * @param theType
	 *            The theType to set.
	 */
	public void setTheType(Type theType) {
		this.theType = theType;
	}

	/**
	 * @return Returns the firstTerm.
	 */
	public int getFirstTerm() {
		return firstTerm;
	}

	/**
	 * @param firstTerm
	 *            The firstTerm to set.
	 */
	public void setFirstTerm(int firstTerm) {
		this.firstTerm = firstTerm;
	}

	/**
	 * @return Returns the secondTerm.
	 */
	public int getSecondTerm() {
		return SecondTerm;
	}

	/**
	 * @param secondTerm
	 *            The secondTerm to set.
	 */
	public void setSecondTerm(int secondTerm) {
		SecondTerm = secondTerm;
	}

	/**
	 * Returns the question Type associated with the given mathematical symbol
	 * 
	 * @param symbol
	 *            the symbol to look up
	 * 
	 * @return the Type associated with symbol
	 */
	public static Type findType(String symbol) {
		Type retVal = null;

		if (symbol.equals("+")) {
			retVal = Type.SUM;
		} else if (symbol.equals("-")) {
			retVal = Type.DIFF;
		} else if (symbol.equals(":")) {
			retVal = Type.POSITION;
		} else if (symbol.equals("x")) {
			retVal = Type.MULT;
		} else if (symbol.equals("\u00F7")) {
			retVal = Type.DIV;
		}

		return retVal;
	}

	/**
	 * @param args -
	 *            Does nothing.
	 */
	public static void main(String[] args) {
		/*
		 * Question q = new Question( Question.Type.SUM, 1, 2 );
		 * System.out.println( q.getTheType()); System.out.println(
		 * q.getFirstTerm() ); System.out.println( q.getSecondTerm());
		 * q.setTheType( Question.Type.DIFF ); q.setFirstTerm( 2 );
		 * q.setFirstTerm( 3 ); System.out.println( q.getTheType() );
		 * System.out.println( q.getFirstTerm()); System.out.println(
		 * q.getSecondTerm());
		 */

		/*
		 * Testing toString() method. Question q2 = new
		 * Question(Question.Type.MULT, 3, 100);
		 * System.out.println(q2.toString());
		 */

		/*
		 * Testing validity of divion problems. Question q3 = new Question(
		 * Question.Type.DIV, 5, 4 ); System.out.println( q3.toString() );
		 */

		// Testing the equals method.
		Question q4 = new Question(Question.Type.DIV, 4, 0);
		Question q5 = new Question(Question.Type.SUM, 1, 4);
		System.out.println(q4.equals(q5));
	}
}
