/*
 * GameManager.java
 *
 * Version:
 *     $Id: GameManager.java,v 1.28 2006/11/06 13:49:00 exl2878 Exp $
 *
 * Revisions:
 *     $Log: GameManager.java,v $
 *     Revision 1.28  2006/11/06 13:49:00  exl2878
 *     Readjusted game length to 20 rounds, changed level threshold to 55 points
 *
 *     Revision 1.27  2006/11/06 13:45:14  exl2878
 *     Added bonus round and revised logic for quitting from a game session
 *
 *     Revision 1.26  2006/11/06 03:33:25  exl2878
 *     GameManager sets the player's level in the environment with setLevel()
 *
 *     Revision 1.25  2006/11/06 02:02:59  exl2878
 *     Fixed problem with student report not refreshing in window
 *
 *     Revision 1.24  2006/11/05 00:31:59  exl2878
 *     Added level support
 *
 *     Revision 1.23  2006/11/04 16:11:51  exl2878
 *     GameManager now gets custom questions from the database
 *
 *     Revision 1.22  2006/11/04 15:42:11  exl2878
 *     Updated GameHistory constructor calls to pass in a gameType argument
 *
 *     Revision 1.21  2006/11/01 03:15:25  emm4674
 *     changed the student report construction to reflect the new constructor
 *
 *     Revision 1.20  2006/10/28 16:20:27  exl2878
 *     Game sessions now last GAME_LENGTH number of rounds, which is
 *     currently 20 rounds
 *
 *     Revision 1.19  2006/10/27 03:10:43  exl2878
 *     Position questions will only appear in mix games
 *
 *     Revision 1.18  2006/10/26 06:57:40  jmf8241
 *     Added support for division in mixed.
 *
 *     Revision 1.17  2006/10/26 05:01:07  exl2878
 *     Added level changes every 20 points
 *
 *     Revision 1.16  2006/10/26 04:15:34  exl2878
 *     Fixed order of arguments passed to database connector methods
 *
 *     Revision 1.15  2006/10/26 03:54:31  exl2878
 *     Student and GameHistory objects are saved at the end of the game
 *
 *     Revision 1.14  2006/10/25 02:38:41  exl2878
 *     Fixed createQuestion bug, although multiplication and division games
 *     seem to like the number 11
 *
 *     Revision 1.13  2006/10/25 00:15:52  exl2878
 *     Game manager now accepts a student object for its constructor,
 *     as well as a constant to select the game type
 *
 *     Revision 1.12  2006/10/21 18:48:03  exl2878
 *     Student Report is now created in GameWinow after the student clicks
 *     on the Quit button
 *
 *     Revision 1.11  2006/10/21 05:42:44  emm4674
 *     attempts to create the student report in the same window; not working
 *
 *     Revision 1.10  2006/10/20 05:03:26  idp3448
 *     Added changes due to new GameTimer methods
 *
 *     Revision 1.9  2006/10/10 05:25:53  exl2878
 *     Implemented "-s" command line argument to disable in-game sounds
 *
 *     Revision 1.8  2006/10/09 03:15:36  jmf8241
 *     Fixed bug that was causing incorrect questions to be added to the correct question count.
 *
 *     Revision 1.7  2006/10/09 02:36:17  exl2878
 *     Resets timer so the program will terminate if the user quits
 *     while an overlay is being displayed
 *
 *     Revision 1.6  2006/10/08 23:33:33  exl2878
 *     Now creates all three different question types
 *
 *     Revision 1.5  2006/10/07 17:50:04  exl2878
 *     GameManager now ends the round, closes the window, and displays the
 *     student's report when the quit button is clicked.
 *
 *     Revision 1.4  2006/10/06 20:03:56  exl2878
 *     Creates student report once the student has finished playing
 *
 *     Revision 1.3  2006/10/06 18:40:14  exl2878
 *     Game records and saves player score between rounds
 *
 *     Revision 1.2  2006/10/05 13:26:13  exl2878
 *     GameManager now keeps track of the student's score
 *
 *     Revision 1.1  2006/10/04 00:13:17  exl2878
 *     Initial revision
 *
 */

package GameLogic;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import DatabaseClient.DBConnector;
import javax.swing.JOptionPane;
import GameUI.Environment;
import GameUI.GameWindow;
import GameUI.MoleEnvironment;
import GameUI.StudentReport;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Game manager controls the actual game element of the program.
 * 
 * @author Eric Lutley exl2878@rit.edu
 */
public class GameManager extends Thread implements MouseListener {

	/**
	 * Constant for selecting addition, subtraction and unit places..
	 */
	public static final int ADD_SUBT = 0;
	
	/**
	 * Constant for selecting multiplication, division and unit places.
	 */
	public static final int MULT_DIVIDE = 1;
	
	/**
	 * Constant for selecting all 5 question types.
	 */
	public static final int MIX = 2;
	
	/**
	 * The maximum number of questions asked per game session.
	 */
	public static final int GAME_LENGTH = 20;
	
	public static final int MIN_SCORE_TO_ADVANCE = 55;
	
	/**
	 * The student currently playing the game
	 */
	private Student student;

	/**
	 * The game environment
	 */
	private Environment gameEnv;

	/**
	 * Statistics of the game the student is currently playing.
	 */
	private GameHistory gameStats;

	/**
	 * The GameWindow where all the action takes place
	 */
	private GameWindow window;

	/**
	 * The RoundManager for the currently playing game
	 */
	private RoundManager round = null;
	
	private QuestionGenerator questionGenerator;
	
	/**
	 * The type of questions to create.
	 */
	private int gameType;

	/**
	 * Indicates that the user pressed the quit button
	 */
	private volatile boolean quitButtonPressed = false;

	/**
	 * Should the game play sounds?
	 */
	private static DBConnector database;

	// The following two data members are used to generate the different
	// question types when the MIX gametype is selected.
	private final Question.Type type[] = { Question.Type.SUM,Question.Type.DIFF,
				Question.Type.MULT, Question.Type.DIV, Question.Type.POSITION };

	private int typeIndex = 0;
	
	private ArrayList<Question> qList;


	/**
	 * Constructor for a GameManager object. GameManager runs the game, although
	 * how exactly it does that is yet to be determined. Right now the
	 * constructor also starts the game, but that will probably be changed at a
	 * later point.
	 * 
	 * @param name -
	 *            name of the student who is playing the game
	 * @param env -
	 *            the game environment to use
	 */
	public GameManager(Student student, GameWindow w, int type, DBConnector dbc) {
		// Assert that the game type is valid
		assert ( type >= ADD_SUBT && type <= MIX );
		
		// Member variables
		this.student = student;
		gameStats = new GameHistory( type );
		gameEnv = new MoleEnvironment();
		gameEnv.addQuitListener(this);
		questionGenerator = QuestionGenerator.getQuestionGenerator();
		gameType = type;
		// TODO Base timer duration on level
		gameEnv.setTimerDuration(20);
		window = w;
		w.setEnvironment(gameEnv);
		database = dbc;
		// Get custom questions from the databasem and shuffle them
		qList = dbc.getQuestions( student.getMyClass(), gameType );
		if ( qList.size() > 0 ) {
			Collections.shuffle( qList );
			// Send question list to QuestionGenerator so it does not duplicate them
			questionGenerator.addToProblemSet( qList );
		}
		try {
			while (gameEnv.showingInstructions())
				sleep(50);
			start();
		} catch (InterruptedException ie) {
			System.err.println("GameManager: " + ie.getStackTrace());
		}
	}

	/**
	 * Creates Question objects for the game using QuestionGenerator.
	 * 
	 * @return A Question object that can be passed to RoundManager
	 */
	private Question createQuestion() {
		Question q = null;
		if ( gameType == ADD_SUBT ) {
			if ( gameStats.getQuestionsTotal() % 2 == 0 ) {
				q = questionGenerator.getRandomQuestion( Question.Type.SUM );
			} else {
				q = questionGenerator.getRandomQuestion( Question.Type.DIFF );
			}
		} else if ( gameType == MULT_DIVIDE ) {
			if ( gameStats.getQuestionsTotal() % 2 == 0 ) {
				q = questionGenerator.getRandomQuestion( Question.Type.MULT );
			} else {
				q = questionGenerator.getRandomQuestion( Question.Type.DIV );
			}
		} else if ( gameType == MIX ) {
			q = questionGenerator.getRandomQuestion( type[typeIndex] );
			typeIndex = (typeIndex + 1) % type.length;
		}
	return q;
	}

	/**
	 * This method is the heart of the game. It creates each round and each
	 * question, keeps track of the user's score, and ends the game when the
	 * user selects the quit button.
	 */
	public void run() {
		try {
			gameEnv.setLevel( student.getLevel( gameType ) );
			do {
				Question q = null; 
				if ( qList.size() > 0 )
					q = qList.remove( 0 );
				else
					q = createQuestion();
				round = new RoundManager(q, gameEnv);
				gameEnv.setScore( gameStats.getScore() );
				round.start();
				round.join(); // Wait until the round has ended
				int questionScore = round.getScore();
				if (questionScore != -1 ) {
					gameStats.increaseScore( questionScore );
					if (questionScore > 0)
						gameStats.addCorrectAnswer( questionScore == 3 );
					else
						gameStats.addIncorrectAnswer();
				}
			} while (!quitButtonPressed && gameStats.getQuestionsTotal() < GAME_LENGTH );
			// Check to see if a bonus round should be offered to the player
			if ( !quitButtonPressed && gameStats.getQuestionsTotal() == GAME_LENGTH ) {
				// Add their points for the last question to their score
				gameEnv.setScore( gameStats.getScore() );
				// Change the environment to the BONUS_ROUND environment
				gameEnv.setLevel( Environment.BONUS_ROUND );
				// Create a new question and round, just like normal
				Question q = null; 
				if ( qList.size() > 0 )
					q = qList.remove( 0 );
				else
					q = createQuestion();
				round = new RoundManager( q, gameEnv );
				round.start();
				round.join(); // Wait until the round has ended
				int questionScore = round.getScore();
				// Increase score, but do not increase the number of correct
				// or incorrect answers
				if (questionScore != -1 ) gameStats.increaseScore( questionScore );
				gameEnv.setScore( gameStats.getScore() );
			}
			if ( !quitButtonPressed ) {
				gameEnv.removeCursor();
				gameEnv.removeQuitListener();
				window.remove( gameEnv );
				showStudentReport();
			}
		} catch (InterruptedException ie) {
			System.err.println("WhackAMath: " + ie.getStackTrace());
		}
	}

	public void showStudentReport() {
//		 Update and save the student's profile and game history
		student.updateStats( gameStats );
		database.putStudentSummary( student.getMyClass(), student.getMyName(), student );
		database.putStudentHistory( student.getMyClass(), student.getMyName(), gameStats );
		// Create a student report
		window.add(new StudentReport(student.getMyName(), gameStats.getScore(), 
										gameStats.getQuestionsCorrect(), 
										gameStats.getQuestionsTotal(), 
										gameStats.getNumCorrectOnFirstTry()));
		// refresh the frame
		window.repaint();
		window.validate();
	}
	
	/**
	 * GameManager is only a MouseListener for the quit button, so if this
	 * method is called then the quit button has been pressed.
	 * 
	 * @param e -
	 *            MouseEvent from user clicking on the quit button
	 */
	public void mouseReleased(MouseEvent e) {
		quitButtonPressed = true;
		round.end();
		gameEnv.setTimerDuration(1);
		gameEnv.resetTimer();
		gameEnv.removeCursor();
		gameEnv.removeQuitListener();
		window.remove( gameEnv );
		showStudentReport();
	}

	// Other methods that must be implemetned to satisfy MouseListener
	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Indicates whether sounds were disabled by a command line argument
	 * 
	 * @return false is sounds are disabled, else true
	 */
	public static boolean playSound() {
		return true;
	}
}
