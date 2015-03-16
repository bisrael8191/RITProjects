/**
 * DatabaseTest.java
 * 
 * Version: 
 * $Log: DatabaseTest.java,v $
 * Revision 1.5  2006/11/07 07:44:30  bdi8241
 * Updated all tests so that all new R3 methods are being tested.
 *
 * Revision 1.4  2006/11/04 15:42:11  exl2878
 * Updated GameHistory constructor calls to pass in a gameType argument
 *
 * Revision 1.3  2006/11/03 21:36:22  jmf8241
 * Now runs the server if the test is run as a Java Application.
 * Server still needs to be run manually if run as a JUnit Test.
 *
 * Revision 1.2  2006/11/01 01:22:08  bdi8241
 * Commented out a test so the code compiles properly.
 * Will fix before final release.
 *
 * Revision 1.1  2006/10/30 07:56:04  bdi8241
 * Initial revision.
 * R2 Tests.
 *
 * 
 * Revisions:
 * $Version: $
 * 
 */
package testing;

import DatabaseClient.DBConnector;
import DatabaseServer.DatabaseServer;
import GameLogic.Student;
import GameLogic.GameHistory;
import GameLogic.GameManager;
import GameLogic.Question;
import Administration.MD5Hash;
import junit.framework.TestCase;
import java.util.*;

/**
 * Tests the database functionality.
 * The test needs to have a database server running on the localhost, port 8191.
 * The database server can be run with the following command
 *  "java DatabaseServer/DatabaseServer"
 * The server needs to be run manually only if you try and run the test 
 *  as a JUnit test in Eclipse.
 * 
 * @author Brad Israel
 *
 */
public class DatabaseTest extends TestCase {

	/**
	 * DBConnector for this test class
	 */
	static DBConnector dbc = null;
	
	/**
	 * Runs the tests.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
        
        //Start the server.
        //Only works if test is run as a Java Application.
        new Thread() {
            public void run() {
                new DatabaseServer();
            }
        }.start();
        
        //JUnit test runner
		junit.textui.TestRunner.run(DatabaseTest.class);
	}
	
	/**
	 * Default constructor.
	 * Passes variable to superclass.
	 * 
	 * @param name the test name
	 */
	public DatabaseTest(String name) {
		super(name);
	
		//Create the database connection thread
		dbc = DBConnector.getInstance( null );
    }
	
	/**
	 * Test to make sure that there is a connection to the database.
	 *
	 */
	public void testDBConnection() {
		assertTrue( dbc.isConnected() );
	}
	
	/**
	 * Add a new class and teacher
	 * 
	 */
	public void testAddNewClass(){
		ArrayList<String> classNames = new ArrayList<String>();
		classNames.add("Test Class");
		assertTrue( dbc.putClassNames(classNames) );
		
		assertTrue( dbc.addTeacher( "Test Class", new MD5Hash().getHash( "Test Class" ) ) );
		
	}
	
	/**
	 * Check if the class is setup
	 * 
	 */
	public void testIsClassSetup(){
		assertTrue( dbc.isClassSetup("Test Class") );
		assertFalse( dbc.isClassSetup( "Fake Class" ) );
	}
	
	/**
	 * Get class names
	 * 
	 */
	public void testGetClassNames(){
		assertNotNull( dbc.getClassNames() );
	}
	
	/**
	 * Check teacher password
	 * 
	 */
	public void testCheckPassword(){
		assertTrue( dbc.checkTeacherPassword( "Test Class", new MD5Hash().getHash( "Test Class" ) ) );
		assertFalse( dbc.checkTeacherPassword("Fake Class", new MD5Hash().getHash( "Fake Class" ) ) );
	}
	
	/**
	 * Try to get a class list from a class that doesn't exist.
	 * Should fail nicely and return a null object.
	 *
	 */
	public void testGetAClasslistThatDoesntExist() {
		assertNull( dbc.getClassList( "Fake Class" ) );
	}
	
	/**
	 * Create a new class list and send it to the database.
	 * The class name for testing is "Test Class".
	 *
	 */
	public void testAddStudentsToAClass() {
		ArrayList<String> classList = new ArrayList<String>();
		classList.add("Bob");
		classList.add("Bill");
		classList.add("Sally");
		assertTrue( dbc.putClassList( "Test Class", classList ) );
	}
	
	/**
	 * Get the list of students back from the database.
	 *
	 */
	public void testGetClassList() {
		ArrayList<String> classList = new ArrayList<String>();
		classList.add("Bob");
		classList.add("Bill");
		classList.add("Sally");
		
		assertNotNull( dbc.getClassList( "Test Class" ) );
		assertEquals( "Classlist has Bob, Bill, and Sally as students", 
				classList, dbc.getClassList( "Test Class" ) );
	}

	/**
	 * Try to get the student summary for a student that doesn't exist.
	 * Should fail nicely and return null.
	 *
	 */
	public void testGetStudentSummaryThatDoesntExist() {
		assertNull( dbc.getStudentSummary( "Fake Class", "Fake Student" ) );
	}
	
	/**
	 * Create a new student summary.
	 *
	 */
	public void testAddStudentsSummary() {
		Student bob = new Student( "Bob", "Test Class" );
		GameHistory round = new GameHistory(GameManager.MIX);
		for( int i = 0; i < 15; i++ ){
			round.addCorrectAnswer(true);
			round.increaseScore(3);
			round.addIncorrectAnswer();
		}
		bob.updateStats( round );
		
		assertTrue( dbc.putStudentSummary( "Test Class", "Bob", bob ) );
	}
	
	/**
	 * Get the student summary that was submitted to the database.
	 *
	 */
	public void testGetStudentSummary() {
		Student ret = dbc.getStudentSummary( "Test Class", "Bob" );
		assertNotNull( ret );
		System.out.println( "testStudentSummary:\n" + ret );
	}
	
	/**
	 * Get the student from the database, "play" another round, 
	 * update the summary in the database, get the updated summary.
	 *
	 */
	public void testUpdateStudentSummary(){
		Student ret = dbc.getStudentSummary( "Test Class", "Bob" );
		GameHistory round = new GameHistory(GameManager.MIX);
		for( int i = 0; i < 15; i++ ){
			round.addCorrectAnswer(true);
			round.increaseScore(3);
			round.addIncorrectAnswer();
		}
		ret.updateStats( round );
		
		assertTrue( dbc.putStudentSummary( "Test Class", "Bob", ret ) );
		
		ret = dbc.getStudentSummary( "Test Class", "Bob" );
		assertNotNull( ret );
		System.out.println( "testUpdateStudentSummary:\n" + ret );
		
	}
	
	/**
	 * Tries to get a student history that doesn't exist.
	 * Should fail nicely and return null.
	 *
	 */
	public void testGetStudentHistoryThatDoesntExist(){
		assertNull( dbc.getStudentHistory( "Fake Class", "Fake Student" ) );
	}
	
	/**
	 * Add a new game history for the student "Bob".
	 *
	 */
	public void testPutStudentHistory(){
		GameHistory bobHistory = new GameHistory(GameManager.MIX);
		for( int i = 0; i < 15; i++ ){
			bobHistory.addCorrectAnswer(true);
			bobHistory.increaseScore(3);
			bobHistory.addIncorrectAnswer();
		}
		
		assertTrue( dbc.putStudentHistory( "Test Class", "Bob", bobHistory ) );
	}
	
	/**
	 * Get the student history object from the database.
	 *
	 */
	public void testGetStudentHistoryTwo(){
		ArrayList<GameHistory> ret = dbc.getStudentHistory( "Test Class", "Bob" );
		assertNotNull( ret );
		System.out.println( "testGetStudentHistory:\n" + ret );
	}
	
	/**
	 * Add another new game history for the student "Bob".
	 *
	 */
	public void testPutStudentHistoryTwo(){
		GameHistory bobHistory = new GameHistory(GameManager.MIX);
		for( int i = 0; i < 15; i++ ){
			bobHistory.addCorrectAnswer(true);
			bobHistory.increaseScore(3);
			bobHistory.addIncorrectAnswer();
		}
		
		assertTrue( dbc.putStudentHistory( "Test Class", "Bob", bobHistory ) );
	}
	
	/**
	 * Get both student history objects from the database.
	 *
	 */
	public void testGetStudentHistory(){
		ArrayList<GameHistory> ret = dbc.getStudentHistory( "Test Class", "Bob" );
		assertNotNull( ret );
		System.out.println( "testGetStudentHistory:\n" + ret );
	}
	
	/**
	 * Add new questions to the database.
	 * 
	 */
	public void testPutQuestions(){
		ArrayList<Question> addsub = new ArrayList<Question>();
		addsub.add( new Question( Question.Type.SUM, 1, 2 ) );
		addsub.add( new Question( Question.Type.SUM, 3, 4 ) );
		addsub.add( new Question( Question.Type.DIFF, 7, 2 ) );
		addsub.add( new Question( Question.Type.DIFF, 6, 2 ) );
		assertTrue( dbc.putQuestions( "Test Class", 0, addsub ) );
	}
	
	/**
	 * Get the questions back from the database.
	 * 
	 */
	public void testGetQuestions(){
		assertNotNull( dbc.getQuestions( "Test Class", 0 ) );
	}
	
	/**
	 * Try to get questions that don't exist
	 * 
	 */
	public void testGetQuestionsThatDontExist(){
		assertNull( dbc.getQuestions( "Fake Class", 0 ) );
	}
	
	/**
	 * Delete a Student.
	 * 
	 */
	public void testDeleteStudent(){
		assertTrue( dbc.deleteStudent( "Test Class", "Bob" ) );
	}
	
	/**
	 * Delete a class.
	 * 
	 */
	public void testDeleteClass(){
		assertTrue( dbc.deleteClass( "Test Class" ) );
		assertFalse( dbc.isClassSetup( "Test Class" ) );
	}
	
	/**
	 * Kill the connection after the tests have run.
	 * Always returns true.
	 *
	 */
	public void testKillConnection() {
		dbc.killConnection();
		assertTrue(true);
	}
}
