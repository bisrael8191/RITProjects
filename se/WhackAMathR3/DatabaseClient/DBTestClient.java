 /**
 * DBTestClient.java
 * 
 * Version:
 * $Id: DBTestClient.java,v 1.18 2006/11/04 15:42:11 exl2878 Exp $
 * 
 * Revisions:
 * $Log: DBTestClient.java,v $
 * Revision 1.18  2006/11/04 15:42:11  exl2878
 * Updated GameHistory constructor calls to pass in a gameType argument
 *
 * Revision 1.17  2006/11/01 01:15:45  bdi8241
 * Added more tests.
 *
 * Revision 1.16  2006/10/31 20:42:51  bdi8241
 * Some new tests.
 * This class is no longer used for formal testing, use the database JUnit testing class.
 *
 * Revision 1.15  2006/10/30 00:55:22  bdi8241
 * Added comments.
 *
 * Revision 1.13  2006/10/26 06:01:05  exl2878
 * Updated to use method DBConnector.getInstance()
 *
 * Revision 1.12  2006/10/26 01:34:03  exl2878
 * Changed test class to use Student objects
 *
 * Revision 1.11  2006/10/25 23:21:56  idp3448
 * New Tests
 *
 * Revision 1.10  2006/10/25 17:48:13  bdi8241
 * Added more tests.
 *
 * Revision 1.9  2006/10/24 13:43:24  bdi8241
 * Added more tests and stubs for tests.
 * One of the next revisions of this class needs to convert it to a JUnit test.
 *
 * Revision 1.8  2006/10/23 08:04:43  bdi8241
 * Added the CVS comments at the top of the class
 *
 * Revision 1.7  2006/10/23 07:45:07  bdi8241
 * Minor Changes.
 * 
 * Revision 1.6  2006/10/23 07:42:00  bdi8241
 * Minor Changes.
 * 
 * Revision 1.5  2006/10/23 06:45:00  bdi8241
 * Updated to send the proper strings to the server, telling it where to create new files.
 * 
 * Revision 1.4  2006/10/21 01:25:00  bdi8241
 * Cleaned up the code.
 * Now uses one pair of object streams.
 * 
 * Revision 1.3  2006/10/20 20:13:00  bdi8241
 * Uses bytes to switch modes
 * 
 * Revision 1.2  2006/10/17 15:50:00  bdi8241
 * Minor updates done in class
 * 
 * Revision 1.1  2006/10/17 05:36:00  bdi8241
 * Client thread that connects to the database over a socket.
 * A class to test the client.
 *
 * 
 */

package DatabaseClient;

import java.io.*;
import java.net.*;
import java.util.*;
import GameLogic.Student;
import GameLogic.GameHistory;
import GameLogic.Question;
import GameLogic.GameManager;

/**
 * Test class for the Database.
 * Will use JUnit tests.
 * 
 * @author Brad Israel
 *
 */
public class DBTestClient{

	static DBConnector db = null;
	public DBTestClient(){
		db = DBConnector.getInstance();
		db.start();
		
		if( db.isConnected() )
			System.out.println( "Connected to server" );
		else
			System.out.println( "Not connected to server" );
		
		//Test 1 - Create a new class
		
		//Test 2 - Create a new student
		
		//Test 3 - Write class list to database, then read it back from the database
		ArrayList<String> classList = new ArrayList<String>();
		classList.add("Bob");
		classList.add("Bill");
		classList.add("Sally");
		
		if( db.putClassList( "Class One", classList ) ){
			System.out.println("Wrote class list to DB");
			printArrayListOfStrings( db.getClassList( "Class One" ) );
			
		} else {
			System.out.println( "Could not write class list to DB" ); 
		}
		
		System.out.println( db.getClassList( "Class Two" ) );

		//Test 4 - Write questions to the database, then read them back
		//One set for each game
		/*ArrayList<Question> addsub = new ArrayList<Question>();
		addsub.add( new Question( Question.Type.SUM, 1, 2 ) );
		addsub.add( new Question( Question.Type.SUM, 3, 4 ) );
		addsub.add( new Question( Question.Type.DIFF, 7, 2 ) );
		addsub.add( new Question( Question.Type.DIFF, 6, 2 ) );
		
		if( db.putQuestions( "Class One", 0, addsub ) ){
			System.out.println( "Wrote AddSub Problems to DB" );
			printArrayListOfQuestions( db.getQuestions( "Class One", 0 ) );
		} else {
			System.out.println( "Could not write AddSub Problems to DB" );
		}
		
		ArrayList<Question> nullTest = db.getQuestions( "Class Two", 0 );
		if( nullTest != null ){
			printArrayListOfQuestions( nullTest );
		} else {
			System.out.println( "There were no questions returned" );
		}*/
		
		
		/*ArrayList<Question> multdivide = new ArrayList<Question>();
		for( int i = 0; i < 100; i++ ){
			multdivide.add( new Question( Question.Type.MULT, 1, 2 ) );
			multdivide.add( new Question( Question.Type.MULT, 3, 4 ) );
			multdivide.add( new Question( Question.Type.MULT, 1, 2 ) );
			multdivide.add( new Question( Question.Type.MULT, 3, 4 ) );
			multdivide.add( new Question( Question.Type.MULT, 1, 2 ) );
			multdivide.add( new Question( Question.Type.MULT, 3, 4 ) );
			multdivide.add( new Question( Question.Type.MULT, 1, 2 ) );
			multdivide.add( new Question( Question.Type.MULT, 3, 4 ) );
			multdivide.add( new Question( Question.Type.MULT, 1, 2 ) );
			multdivide.add( new Question( Question.Type.MULT, 3, 4 ) );
			multdivide.add( new Question( Question.Type.MULT, 1, 2 ) );
			multdivide.add( new Question( Question.Type.MULT, 3, 4 ) );
			multdivide.add( new Question( Question.Type.MULT, i, i ) );
		}
		
		if( db.putQuestions( "Class One", 1, multdivide ) ){
			System.out.println( "Wrote MultDivide Problems to DB" );
			printArrayListOfQuestions( db.getQuestions( "Class One", 1 ) );
		} else {
			System.out.println( "Could not write MultDivide Problems to DB" );
		}*/
		
		/*ArrayList<Question> unit = new ArrayList<Question>();
		unit.add( new Question( Question.Type.POSITION, 333, 1 ) );
		unit.add( new Question( Question.Type.POSITION, 444, 10 ) );
		unit.add( new Question( Question.Type.POSITION, 555, 100 ) );
		unit.add( new Question( Question.Type.POSITION, 666, 10 ) );
		
		if( db.putQuestions( "Class One", 2, unit ) ){
			System.out.println( "Wrote Unit Problems to DB" );
			printArrayListOfQuestions( db.getQuestions( "Class One", 2 ) );
		} else {
			System.out.println( "Could not write Unit Problems to DB" );
		}*/
		
		
		//Test 5 - Write a student summary, then read it back
		/*Student bob = new Student( "Bob", "Class One" );
		Student ret = null;
		if( db.putStudentSummary( "Class One", "Bob", bob ) ){
			ret = db.getStudentSummary( "Class One", "Bob"  );
			System.out.println( ret );
		}*/
		
		/*if( ( ret = db.getStudentSummary("Class One", "Sally" ) ) != null ){
			System.out.println( "Sally is a Student" );
		} else {
			System.out.println( "Sally isn't a Student" );
		}*/
		
		//Test 6 - Write three student history objects to the database, 
		//Then read the entire list back
		GameHistory gameOne = new GameHistory(GameManager.MIX);
		for( int i = 0; i < 10; i++ ){
			gameOne.addCorrectAnswer(true);
			gameOne.addIncorrectAnswer();
			gameOne.increaseScore(3);
		}
		GameHistory gameTwo = new GameHistory(GameManager.MIX);
		for( int i = 0; i < 10; i++ ){
			gameTwo.addCorrectAnswer(true);
			gameTwo.addIncorrectAnswer();
			gameTwo.increaseScore(3);
		}
		GameHistory gameThree = new GameHistory(GameManager.MIX);
		for( int i = 0; i < 10; i++ ){
			gameThree.addCorrectAnswer(true);
			gameThree.addIncorrectAnswer();
			gameThree.increaseScore(3);
		}
		
		if( db.putStudentHistory("Class One", "Bob", gameOne ) ){
			System.out.println( "Game one history written to DB" );
			
			ArrayList<GameHistory> ret = db.getStudentHistory("Class One", "Bob" );
			if( ret != null ){
				System.out.println( "Game one returned from server" );
				printArrayListOfHistory( ret );
			} else {
				System.out.println( "Ret was null" );
			}
			
		} else {
			System.out.println( "Game one history failed to write to DB" );
		}
		
		if( db.putStudentHistory("Class One", "Bob", gameTwo ) ){
			System.out.println( "Game two history written to DB" );
			
			ArrayList<GameHistory> ret = db.getStudentHistory("Class One", "Bob" );
			if( ret != null ){
				System.out.println( "Game two returned from server" );
				printArrayListOfHistory( ret );
			} else {
				System.out.println( "Ret was null" );
			}
			
		} else {
			System.out.println( "Game two history failed to write to DB" );
		}
		
		if( db.putStudentHistory("Class One", "Bob", gameThree ) ){
			System.out.println( "Game three history written to DB" );
			
			ArrayList<GameHistory> ret = db.getStudentHistory("Class One", "Bob" );
			if( ret != null ){
				System.out.println( "Game three returned from server" );
				printArrayListOfHistory( ret );
			} else {
				System.out.println( "Ret was null" );
			}
			
		} else {
			System.out.println( "Game three history failed to write to DB" );
		}
		
		if( db.getStudentHistory( "Class Two", "Bob" ) == null ){
			System.out.println( "History doesn't exist" );
		} else {
			System.out.println( "History does exist, but it shouldn't" );
		}
		
		//Test 7 - Delete a Student
		/*if( db.deleteStudent( "Class One", "Bob" ) ){
			System.out.println("Student deleted" );
		} else {
			System.out.println("Student not deleted");
		}*/
		
		
		//Test 8 - Delete a class
		/*if( db.deleteClass( "Class One" ) ){
			System.out.println("Class deleted" );
		} else {
			System.out.println("Class not deleted");
		}*/
		
		//Test 9 - Add teachers password file
		/*if( db.addTeacher("Class One", "My Hash" ) ) {
			System.out.println( "Teachers password added to DB" );
		} else {
			System.out.println( "Teachers password failed to write" );
		}*/
	
		//Test 10 - Check if the class is setup
		/*if( db.isClassSetup( "Class One" ) ){
			System.out.println( "Class One is setup" );
		} else {
			System.out.println( "Class One isn't setup" );
		}
		if( db.isClassSetup( "Class Two" ) ){
			System.out.println( "Class Two is setup" );
		} else {
			System.out.println( "Class Two isn't setup" );
		}*/
		
		//Test 11 - Check teacher password
		/*if( db.checkTeacherPassword( "Class One", "My Hash") ){
			System.out.println( "Teachers password was correct" );
		} else {
			System.out.println( "Teachers password was incorrect" );
		}
		if( db.checkTeacherPassword( "Class One", "My Incorrect Hash") ){
			System.out.println( "Teachers password was correct" );
		} else {
			System.out.println( "Teachers password was incorrect" );
		}*/
		
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		db.killConnection();
		System.out.println("Connection Killed");
	}
	
	private void printArrayListOfStrings( ArrayList<String> list ){
		System.out.println( "Printing an ArrayList of Strings:" );
		for (Iterator iter = list.iterator(); iter.hasNext();) {
		    System.out.println((String)(iter.next()));
		}
	}
	
	private void printArrayListOfQuestions( ArrayList<Question> list ){
		System.out.println( "Printing an ArrayList of Question objects:" );
		for (Iterator iter = list.iterator(); iter.hasNext();) {
		    System.out.println((Question)(iter.next()));
		}
	}
	
	private void printArrayListOfHistory( ArrayList<GameHistory> list ){
		System.out.println( "Printing an ArrayList of History objects:" );
		for (Iterator iter = list.iterator(); iter.hasNext();) {
		    System.out.println((GameHistory)(iter.next()));
		}
	}
	

	public static void main(String[] args) {
		DBTestClient test = new DBTestClient();
	}
}
