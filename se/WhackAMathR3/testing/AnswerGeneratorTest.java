/**
 * AnswerGeneratorTest.java
 * 
 * Version: 
 * $Log$
 * Revision 1.2  2006/10/30 03:51:37  jmf8241
 * Completed for R2
 *
 * Revision 1.1  2006/10/27 20:56:04  jmf8241
 * Initial Revision.
 *
 * 
 * Revisions:
 * $Version: $
 */

package testing;

import java.util.ArrayList;

import junit.framework.TestCase;
import GameLogic.Question;
import GameLogic.AnswerGenerator;

public class AnswerGeneratorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AnswerGeneratorTest.class);
    }

    public AnswerGeneratorTest(String arg0) {
        super(arg0);
    }

    /*
     * Test method for 'GameLogic.AnswerGenerator.getCorrectAnswer(Question)'
     */
    public void testGetCorrectAnswer() {
        Question t1 = new Question( Question.Type.SUM, 2, 3 );
        Question t2 = new Question( Question.Type.DIFF, 2, 2 );
        Question t3 = new Question( Question.Type.MULT, 3, 3);
        Question t4 = new Question( Question.Type.DIV, 4, 4);
        Question t5 = new Question( Question.Type.POSITION, 1, 1);
        
        assertEquals( AnswerGenerator.getCorrectAnswer( t1 ), 5 );
        assertEquals( AnswerGenerator.getCorrectAnswer( t2 ), 0);
        assertEquals( AnswerGenerator.getCorrectAnswer( t3 ), 9);
        assertEquals( AnswerGenerator.getCorrectAnswer( t4 ), 1 );
        
        /*
         *  Test the position questions by converting the answer to a String,
         *  parsing the answer and then subtracting the numbers from the total 
         *  answer.  i.e. subtract 800 from 801, answer remaining should be 1.
         */
        Integer i1 = AnswerGenerator.getCorrectAnswer( t5 );
        String aString = i1.toString();
        char c1 = aString.charAt(0);
        char c2 = aString.charAt(1);
        int hundreds = 0;
        int tens = 0;
        int hundTens;   // Hundreds + tens
        
        try{
            hundreds = Integer.parseInt( c1 + "" );
            tens = Integer.parseInt( c2 + "" );
        }
        catch( NumberFormatException e ){
            System.err.println( "Error" );
        }
        hundTens = (hundreds * 100) + (tens * 10);
        i1 = i1 - hundTens;
        assertEquals( (int)i1, 1 );
    }

    /*
     * Test method for 'GameLogic.AnswerGenerator.getIncorrectAnswers(Question, int, int)'
     */
    public void testGetIncorrectAnswers() {
        Question t1 = new Question( Question.Type.SUM, 1, 3 );
        Question t2 = new Question( Question.Type.DIFF, 3, 1);
        Question t3 = new Question( Question.Type.MULT, 3, 1);
        Question t4 = new Question( Question.Type.DIV, 3, 3);
        Question t5 = new Question( Question.Type.POSITION, 1, 1);
        
        /*
         * Test Sum.
         */
        ArrayList answers = AnswerGenerator.getIncorrectAnswers( t1, 
                AnswerGenerator.getCorrectAnswer(t1), 1 );
        
        for( int i = 0; i < answers.size(); i++ ){
            assertNotSame( answers.get(i), AnswerGenerator.getCorrectAnswer(t1));
        }
        
        /*
         * Test Diff.
         */
        answers = AnswerGenerator.getIncorrectAnswers( t2, 
                AnswerGenerator.getCorrectAnswer(t2), 1 );
        
        for( int i = 0; i < answers.size(); i++ ){
            assertNotSame( answers.get(i), AnswerGenerator.getCorrectAnswer(t2));
        }
        
        /*
         * Test Mult.
         */
        answers = AnswerGenerator.getIncorrectAnswers( t3, 
                AnswerGenerator.getCorrectAnswer(t3), 1 );
        
        for( int i = 0; i < answers.size(); i++ ){
            assertNotSame( answers.get(i), AnswerGenerator.getCorrectAnswer(t3));
        }
        
        /*
         * Test Div.
         */
        answers = AnswerGenerator.getIncorrectAnswers( t4, 
                AnswerGenerator.getCorrectAnswer(t4), 1 );
        
        for( int i = 0; i < answers.size(); i++ ){
            assertNotSame( answers.get(i), AnswerGenerator.getCorrectAnswer(t4));
        }
        
        /*
         * Test Pos.
         */
        answers = AnswerGenerator.getIncorrectAnswers( t5, 
                AnswerGenerator.getCorrectAnswer( t5 ), 1 );
        for( int i = 0; i < answers.size(); i++ ){
        String aString = answers.get(i).toString();
        char c1 = aString.charAt(0);
        char c2 = aString.charAt(1);
        int hundreds = 0;
        int tens = 0;
        int hundTens;   // Hundreds + tens
        Integer currentAnswers = (Integer) answers.get(i);
        
        try{
            hundreds = Integer.parseInt( c1 + "" );
            tens = Integer.parseInt( c2 + "" );
        }
        catch( NumberFormatException e ){
            System.err.println( "Error" );
        }
        hundTens = (hundreds * 100) + (tens * 10);
        currentAnswers = currentAnswers - hundTens;
        assertNotSame( currentAnswers, 1 );
        }
    }
}
