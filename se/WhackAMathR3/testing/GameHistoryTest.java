/*
 * GameHistoryTest.java
 *
 * Version:
 *     $Id: GameHistoryTest.java,v 1.2 2006/11/04 15:42:11 exl2878 Exp $
 *
 * Revisions:
 *     $Log: GameHistoryTest.java,v $
 *     Revision 1.2  2006/11/04 15:42:11  exl2878
 *     Updated GameHistory constructor calls to pass in a gameType argument
 *
 *     Revision 1.1  2006/10/21 17:20:51  exl2878
 *     Initial revision
 *
 */
package testing;

import junit.framework.TestCase;
import GameLogic.GameHistory;
import GameLogic.GameManager;

/**
 * JUnit tests for GameHistory class 
 *
 * @author Eric Lutley	exl2878@rit.edu
 */
public class GameHistoryTest extends TestCase {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(GameHistoryTest.class);
	}

	public GameHistoryTest(String name) {
		super(name);
	}

	/**
	 * Test method for default constructor
	 */
	public void testGameHistory() {
		GameHistory gh = new GameHistory(GameManager.MIX);
		assertNotNull( gh.getDateString() );
		System.out.println( "Game played " + gh.getDateString() );
		assertEquals( gh.getScore(), 0 );
		assertEquals( gh.getNumCorrectOnFirstTry(), 0 );
		assertEquals( gh.getQuestionsCorrect(), 0 );
		assertEquals( gh.getQuestionsTotal(), 0 );
	}


	/**
	 * Test method for 'GameLogic.GameHistory.addCorrectAnswer(true)'
	 */
	public void testAddCorrectAnswerOnFirstTry() {
		GameHistory gh = new GameHistory(GameManager.MIX);
		gh.addCorrectAnswer( true );
		assertEquals( gh.getNumCorrectOnFirstTry(), 1 );
		assertEquals( gh.getQuestionsCorrect(), 1 );
		assertEquals( gh.getQuestionsTotal(), 1 );
	}
	
	/**
	 * Test method for 'GameLogic.GameHistory.addCorrectAnswer(false)'
	 */
	public void testAddCorrectAnswerNotOnFirstTry() {
		GameHistory gh = new GameHistory(GameManager.MIX);
		gh.addCorrectAnswer( false );
		assertEquals( gh.getNumCorrectOnFirstTry(), 0 );
		assertEquals( gh.getQuestionsCorrect(), 1 );
		assertEquals( gh.getQuestionsTotal(), 1 );
	}

	/**
	 * Test method for 'GameLogic.GameHistory.addIncorrectAnswer()'
	 */
	public void testAddIncorrectAnswer() {
		GameHistory gh = new GameHistory(GameManager.MIX);
		gh.addIncorrectAnswer();
		assertEquals( gh.getNumCorrectOnFirstTry(), 0 );
		assertEquals( gh.getQuestionsCorrect(), 0 );
		assertEquals( gh.getQuestionsTotal(), 1 );
	}

	/**
	 * Test method for 'GameLogic.GameHistory.increaseScore(int)'
	 */
	public void testIncreaseScore() {
		GameHistory gh = new GameHistory(GameManager.MIX);
		assertEquals("Score is 0", gh.getScore(), 0 );
		gh.increaseScore( 1 );
		assertEquals("Score is 1", gh.getScore(), 1 );
		gh.increaseScore( 2 );
		assertEquals("Score is 3", gh.getScore(), 3 );
	}

}
