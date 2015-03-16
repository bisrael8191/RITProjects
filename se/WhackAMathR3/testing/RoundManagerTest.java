/*
 * GameManagerTest.java
 * 
 * Version:
 * 		$Id: RoundManagerTest.java,v 1.1 2006/10/27 20:03:13 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: RoundManagerTest.java,v $
 * 		Revision 1.1  2006/10/27 20:03:13  idp3448
 * 		Initial revision
 * 		
 */

package testing;

import GameLogic.Question;
import GameLogic.RoundManager;
import GameUI.Environment;
import GameUI.MoleEnvironment;
import junit.framework.TestCase;

/**
 * Tests the RoundManager class.  Since this class is the
 * primary interface to the GameUI, it is nearly impossible
 * to accurately test all of the methods with 100% certainty.
 * 
 *  Furthermore, the class was written with events triggered
 *  by the mouse and it does not make sense to make methods
 *  which emulate mouse click events just to gratify JUnit.
 * 
 * @author Ian Paterson
 */
public class RoundManagerTest extends TestCase {

	/**
	 * Runs the tests
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(RoundManagerTest.class);
	}

	/**
	 * Tests whether the constructor correctly sets the 
	 * question display
	 */
	public void testConstructorSetsQuestionDisplay() {
		Environment e = new MoleEnvironment();
		Question q = new Question(Question.Type.SUM, 4, 2);
		RoundManager r = new RoundManager(q, e);
		
		assertEquals("Question text should read: What is 4 + 2?", "What is 4 + 2?", e.getQuestion());
	}

	/**
	 * Tests that the score is not returned before the round
	 * ends.
	 */
	public void testGetScoreBeforeRoundEnds() {
		Environment e = new MoleEnvironment();
		Question q = new Question(Question.Type.SUM, 4, 2);
		RoundManager r = new RoundManager(q, e);
		
		assertEquals("Score should be: -1", -1, r.getScore());
	}
}
