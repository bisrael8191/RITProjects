/*
 * QuestionGenerator.java
 *
 * Version:
 *     $Id: QuestionGenerator.java,v 1.4 2006/11/07 06:18:25 exl2878 Exp $
 *
 * Revisions:
 *     $Log: QuestionGenerator.java,v $
 *     Revision 1.4  2006/11/07 06:18:25  exl2878
 *     Slightly reduced the difficulty of the questions asked
 *
 *     Revision 1.3  2006/11/04 16:12:54  exl2878
 *     Added "addToProblemSet" method to QuestionGenerator so that it would
 *     not randomly duplicate custom problems specified by the teacher
 *
 *     Revision 1.2  2006/10/26 03:35:37  exl2878
 *     Now implements the Singleton design pattern
 *
 *     Revision 1.1  2006/10/24 01:51:09  exl2878
 *     Initial revision
 *
 */
package GameLogic;

import GameLogic.Question.Type;
import java.util.Random;
import java.util.HashSet;
import java.util.List;

/**
 * Randomly generates questions.  This class implements the Singleton
 * design pattern.
 *
 * @author Eric Lutley
 */
public class QuestionGenerator {

	private Random random;
	private HashSet<Question> questionSet;
	private static QuestionGenerator qGen;
	
	/**
	 * Initialize the random number generator and the questionSet.
	 */
	private QuestionGenerator() {
		random = new Random();
		questionSet = new HashSet<Question>();
	}
	
	public static QuestionGenerator getQuestionGenerator() {
		if ( qGen == null ) qGen = new QuestionGenerator();
		return qGen;
	}
	
	/**
	 * Adds a list of problems to QuestionGenerator's internal
	 * question set.  Problems added to this question set will not
	 * be duplicated by QuestionGenerator.getRandomQuestion().
	 *
	 * @param qList - list of questions to add to problem set
	 */
	public void addToProblemSet( List<Question> qList ) {
		for( Question q : qList ) questionSet.add( q );
	}
	
	/**
	 * Creates a random math problem of the specified type.
	 *
	 * @param type - the type of math problem to randomly create
	 * @return    a random math problem of the specified type
	 */
	public Question getRandomQuestion( Type type ) {
		Question retVal = null;
		do {
			if ( type != Question.Type.POSITION ) {
				int first, second;
				if ( type == Question.Type.SUM || type == Question.Type.DIFF ) {
					first = random.nextInt( 15 );
					second = random.nextInt( 15 );
				} else if ( type == Question.Type.MULT ) {
					first = random.nextInt( 9 ) + 1;
					second = random.nextInt( 9 ) + 1;
				} else {
					second = first = random.nextInt( 11 ) + 1;
					first = second * ( random.nextInt( 11 ) + 1 );
				}
				if ( type == Question.Type.DIFF && first < second ) {
					int temp = first;
					first = second;
					second = temp;
				}
				try {
					retVal = new Question( type, first, second );
				} catch( DivisionRemainderException dre ) {
					retVal = null;
				}
			} else {
				int place = (int)Math.pow( 10, random.nextInt( 3 ) );
				int digit = random.nextInt( 10 );
				retVal = new Question( type, digit, place );
			}
		} while ( retVal == null || questionSet.contains( retVal ) );
		questionSet.add( retVal );
		return retVal;
	}
}
