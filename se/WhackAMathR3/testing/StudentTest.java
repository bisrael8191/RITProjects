/*
 * StudentTest.java
 *
 * Version:
 *     $Id: StudentTest.java,v 1.2 2006/11/04 15:42:11 exl2878 Exp $
 *
 * Revisions:
 *     $Log: StudentTest.java,v $
 *     Revision 1.2  2006/11/04 15:42:11  exl2878
 *     Updated GameHistory constructor calls to pass in a gameType argument
 *
 *     Revision 1.1  2006/10/30 15:06:46  exl2878
 *     Initial Revision
 *
 */
package testing;

import GameLogic.GameHistory;
import GameLogic.Student;
import GameLogic.GameManager;
import junit.framework.TestCase;

/**
 * JUnit test class for the Student class.
 *
 * @author Eric Lutley
 */
public class StudentTest extends TestCase {

	/**
	 * Main method for JUnit StudentTest class
	 * @param args
	 */
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(StudentTest.class);
	}

	/**
	 * Test the constructor that only takes a student name as an argument.
	 */
	public void testNameOnlyConstructor() {
		Student s = new Student( "Jimmy" );
		assertEquals( s.getMyName(), "Jimmy" );
		assertEquals( s.getMyClass(), "" );
		assertEquals( "Score is 0", s.getScore(), 0 );
		assertEquals( "Games played is 0", s.getNumGames(), 0 );
		assertEquals( "Questions correct is 0", s.getNumCorrect(), 0 );
		assertEquals( "Questions correct on first try is 0", s.getNumCorrectFirst(), 0 );
		assertEquals( "Total number of questions is 0", s.getTotalQuestions(), 0 );
		assertEquals( "Percent correct is 0.0", s.getPercentCorrect(), 0.0 );
	}
	
	/**
	 * Test the constructor that takes a student and class name as arguments.
	 */
	public void testNameAndClassConstructor() {
		Student s = new Student( "Jimmy", "Class" );
		assertEquals( s.getMyName(), "Jimmy" );
		assertEquals( s.getMyClass(), "Class" );
		assertEquals( "Score is 0", s.getScore(), 0 );
		assertEquals( "Games played is 0", s.getNumGames(), 0 );
		assertEquals( "Questions correct is 0", s.getNumCorrect(), 0 );
		assertEquals( "Questions correct on first try is 0", s.getNumCorrectFirst(), 0 );
		assertEquals( "Total number of questions is 0", s.getTotalQuestions(), 0 );
		assertEquals( "Percent correct is 0.0", s.getPercentCorrect(), 0.0 );
	}
	
	/**
	 * Test updating the student object when a student gets an answer correct 
	 * on his first try.
	 */
	public void testUpdateStatsQuestionCorrectFirstTry() {
		Student s = new Student( "Jimmy" );
		GameHistory hist = new GameHistory(GameManager.MIX);
		hist.addCorrectAnswer( true );
		hist.increaseScore( 3 );
		s.updateStats( hist );
		// Check student object
		assertEquals( s.getMyName(), "Jimmy" );
		assertEquals( s.getMyClass(), "" );
		assertEquals( "Score is 3", s.getScore(), 3 );
		assertEquals( "Games played is 1", s.getNumGames(), 1 );
		assertEquals( "Questions correct is 1", s.getNumCorrect(), 1 );
		assertEquals( "Questions correct on first try is 1", s.getNumCorrectFirst(), 1 );
		assertEquals( "Total number of questions is 1", s.getTotalQuestions(), 1 );
		assertEquals( "Percent correct is 100.0", s.getPercentCorrect(), 100.0 );
	}
	
	/**
	 * Test updating the student object when a student gets an answer correct 
	 * on his second try.
	 */
	public void testUpdateStatsQuestionCorrectSecondTry() {
		Student s = new Student( "Jimmy" );
		GameHistory hist = new GameHistory(GameManager.MIX);
		hist.addCorrectAnswer( false );
		hist.increaseScore( 2 );
		s.updateStats( hist );
		// Check student object
		assertEquals( s.getMyName(), "Jimmy" );
		assertEquals( s.getMyClass(), "" );
		assertEquals( "Score is 2", s.getScore(), 2 );
		assertEquals( "Games played is 1", s.getNumGames(), 1 );
		assertEquals( "Questions correct is 1", s.getNumCorrect(), 1 );
		assertEquals( "Questions correct on first try is 0", s.getNumCorrectFirst(), 0 );
		assertEquals( "Total number of questions is 1", s.getTotalQuestions(), 1 );
		assertEquals( "Percent correct is 100.0", s.getPercentCorrect(), 100.0 );
	}
	
	/**
	 * Test updating the student object when a student gets an answer wrong.
	 */
	public void testUpdateStatsQuestionWrong() {
		Student s = new Student( "Jimmy" );
		GameHistory hist = new GameHistory(GameManager.MIX);
		hist.addIncorrectAnswer();
		s.updateStats( hist );
		// Check student object
		assertEquals( s.getMyName(), "Jimmy" );
		assertEquals( s.getMyClass(), "" );
		assertEquals( "Score is 0", s.getScore(), 0 );
		assertEquals( "Games played is 1", s.getNumGames(), 1 );
		assertEquals( "Questions correct is 0", s.getNumCorrect(), 0 );
		assertEquals( "Questions correct on first try is 0", s.getNumCorrectFirst(), 0 );
		assertEquals( "Total number of questions is 1", s.getTotalQuestions(), 1 );
		assertEquals( "Percent correct is 0.0", s.getPercentCorrect(), 0.0 );
	}
	
	/**
	 * Test updating the student object after the student has played two 
	 * different games.
	 */
	public void testUpdateStatsForTwoGames() {
		Student s = new Student( "Jimmy" );
		// First Game
		GameHistory game1 = new GameHistory(GameManager.MIX);
		game1.addCorrectAnswer( true );
		game1.increaseScore( 3 );
		s.updateStats( game1 );
		// Second game
		GameHistory game2 = new GameHistory(GameManager.MIX);
		game2.addIncorrectAnswer();
		game2.increaseScore( 0 );
		s.updateStats( game2 );
		// Check student object
		assertEquals( s.getMyName(), "Jimmy" );
		assertEquals( s.getMyClass(), "" );
		assertEquals( "Score is 3", s.getScore(), 3 );
		assertEquals( "Games played is 2", s.getNumGames(), 2 );
		assertEquals( "Questions correct is 1", s.getNumCorrect(), 1 );
		assertEquals( "Questions correct on first try is 1", s.getNumCorrectFirst(), 1 );
		assertEquals( "Total number of questions is 2", s.getTotalQuestions(), 2 );
		assertEquals( "Percent correct is 50.0", s.getPercentCorrect(), 50.0 );
	}
	
} // StudentTest
