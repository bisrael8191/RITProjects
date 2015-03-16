/**
 * QuestionTest.java
 * 
 * Version:
 * $Id$
 * 
 * Revisions:
 * $Log$
 * Revision 1.1  2006/10/25 21:21:02  jmf8241
 * Initial Revision.
 *
 */

package testing;

import GameLogic.Question;
import junit.framework.TestCase;

/**
 * This is a JUnit test class for the Question class.
 * 
 * @author Justin Field
 */

public class QuestionTest extends TestCase {

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(QuestionTest.class);
    }

    public QuestionTest(String arg0) {
        super(arg0);
    }

    /*
     * Test method for 'GameLogic.Question.toString()'
     */
    public void testToString() {
        Question sumString = new Question( Question.Type.SUM, 1, 2 );
        assertEquals( sumString.toString(), "What is 1 + 2?" );
        
        Question diffString = new Question( Question.Type.DIFF, 2, 1 );
        assertEquals( diffString.toString(), "What is 2 - 1?" );
        
        Question multString = new Question( Question.Type.MULT, 2, 1 );
        assertEquals( multString.toString(), "What is 2 x 1?" );
        
        Question divString = new Question( Question.Type.DIV, 2, 2 );
        assertEquals( divString.toString(), "What is 2 \u00F7 2?" );
        
        Question position1String = new Question( Question.Type.POSITION, 1, 1 );
        assertEquals( position1String.toString(), "<html><center>" + "Whack the mole that has a"
                + "<br>" + "1 in the units place." + "</center></html>");
        
        Question position10String = new Question( Question.Type.POSITION, 1, 10 );
        assertEquals( position10String.toString(), "<html><center>" + "Whack the mole that has a"
                + "<br>" + "1 in the tens place." + "</center></html>");
        
        Question position100String = new Question( Question.Type.POSITION, 1, 100 );
        assertEquals( position100String.toString(), "<html><center>" + "Whack the mole that has a"
                + "<br>" + "1 in the hundreds place." + "</center></html>");
    }

    /*
     * Test method for 'GameLogic.Question.formatQuestion()'
     */
    public void testFormatQuestion() {
        Question sumTest = new Question ( Question.Type.SUM, 1, 1 );
        assertEquals( sumTest.formatQuestion(), "1 + 1 = 2" );
        
        Question diffTest = new Question ( Question.Type.DIFF, 1, 1 );
        assertEquals( diffTest.formatQuestion(), "1 - 1 = 0" );
        
        Question multTest = new Question ( Question.Type.MULT, 1, 1 );
        assertEquals( multTest.formatQuestion(), "1 x 1 = 1");
        
        Question divTest = new Question( Question.Type.DIV, 1, 1 );
        assertEquals( divTest.formatQuestion(), "1 \u00F7 1 = 1" );
        
        Question posTest = new Question( Question.Type.POSITION, 1, 1);
        assertEquals( posTest.formatQuestion(), "1 : 1");
    }

    /*
     * Test method for 'GameLogic.Question.equals(Object)'
     */
    public void testEqualsObject() {
        Question sumQuestion = new Question( Question.Type.SUM, 1, 2 );
        Question diffQuestion = new Question( Question.Type.DIFF, 2,1  );
        assertFalse( sumQuestion.equals( new String("Test") ) );
        assertTrue( sumQuestion.equals( new Question( Question.Type.SUM, 1, 2)));
        assertTrue( sumQuestion.equals( new Question( Question.Type.SUM, 2, 1)));
        assertFalse( sumQuestion.equals( new Question( Question.Type.MULT, 1, 2)));
        assertTrue( diffQuestion.equals( new Question( Question.Type.DIFF, 2, 1)));
        assertFalse( diffQuestion.equals( new Question( Question.Type.DIFF, 1, 2)));
    }

    /*
     * Test method for 'GameLogic.Question.getTheType()'
     */
    public void testGetTheType() {
        Question sumQuestion = new Question( Question.Type.SUM, 1, 1 );
        assertEquals( sumQuestion.getTheType(), Question.Type.SUM );
        assertNotSame( sumQuestion.getTheType(), Question.Type.DIFF );
    }

    /*
     * Test method for 'GameLogic.Question.setTheType(Type)'
     */
    public void testSetTheType() {
        Question sumQuestion = new Question( Question.Type.SUM, 1, 1 );
        assertEquals( sumQuestion.getTheType(), Question.Type.SUM );
        sumQuestion.setTheType( Question.Type.DIFF );
        assertNotSame( sumQuestion.getTheType(), Question.Type.SUM );
        assertEquals( sumQuestion.getTheType(), Question.Type.DIFF );
    }

    /*
     * Test method for 'GameLogic.Question.getFirstTerm()'
     */
    public void testGetFirstTerm() {
        Question testQuestion = new Question( Question.Type.SUM, 1, 2);
        assertEquals( testQuestion.getFirstTerm(), 1 );
    }

    /*
     * Test method for 'GameLogic.Question.setFirstTerm(int)'
     */
    public void testSetFirstTerm() {
        Question tq = new Question( Question.Type.SUM, 1, 2 );
        assertEquals( tq.getFirstTerm(), 1 );
        tq.setFirstTerm( 5 );
        assertNotSame( tq.getFirstTerm(), 1 );
        assertEquals( tq.getFirstTerm(), 5 );
    }

    /*
     * Test method for 'GameLogic.Question.getSecondTerm()'
     */
    public void testGetSecondTerm() {
        Question tq = new Question( Question.Type.SUM, 1, 2 );
        assertEquals( tq.getSecondTerm(), 2 );
    }

    /*
     * Test method for 'GameLogic.Question.setSecondTerm(int)'
     */
    public void testSetSecondTerm() {
        Question tq = new Question ( Question.Type.SUM, 1, 2 );
        assertEquals( tq.getSecondTerm(), 2 );
        tq.setSecondTerm( 10 );
        assertNotSame( tq.getSecondTerm(), 2 );
        assertEquals( tq.getSecondTerm(), 10 );
    }
}
