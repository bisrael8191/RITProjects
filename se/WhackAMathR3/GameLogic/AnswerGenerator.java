
/**
 * AnswerGenerator.java
 * 
 * Version:
 * $Id: AnswerGenerator.java,v 1.5 2006/10/25 17:03:42 jmf8241 Exp $
 * 
 * Revision:
 * $Log: AnswerGenerator.java,v $
 * Revision 1.5  2006/10/25 17:03:42  jmf8241
 * Added fix that aviods problems when looking for zeros
 *
 * Revision 1.4  2006/10/16 03:25:15  jmf8241
 * Added support for Multiplication and Division
 *
 * Revision 1.3  2006/10/06 20:02:29  jmf8241
 * Changed to some constants.
 *
 * Revision 1.2  2006/10/04 21:54:02  jmf8241
 * Changed 1,2,3 to 1,10,100.
 *
 * Revision 1.1  2006/10/04 00:13:09  exl2878
 * Moved to GameLogic package
 *
 * Revision 1.3  2006/09/29 20:48:32  jmf8241
 * Some minor changes.
 *
 * Revision 1.2  2006/09/29 19:42:02  jmf8241
 * Functional.  Has been tested.  Still some TODO tags present.
 *
 * Revision 1.1  2006/09/29 19:30:54  jmf8241
 * Initial Revision.  Don't use yet.
 *
 * 
 */
package GameLogic;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane; //  For input errors.

/**
 * This class generates answers to a question.
 * 
 * @author Justin Field
 */
public class AnswerGenerator {

    private static final int UNITS = 1;
    private static final int TENS = 10;
    private static final int HUNDREDS = 100;
    
    /**
     * Generates the correct answer to the question.
     * @param q - the question.
     */
    public static int getCorrectAnswer( Question q ){
        int answer = -1; // The correct answer.
        Random digit = new Random(); // Creates answer for POSITION problems.
        
        if ( q.getTheType() == Question.Type.SUM ){
            answer = q.getFirstTerm() + q.getSecondTerm();
        }
        else if ( q.getTheType() == Question.Type.DIFF ){
            answer = q.getFirstTerm() - q.getSecondTerm();
        }
        else if ( q.getTheType() == Question.Type.MULT ){
            answer = q.getFirstTerm() * q.getSecondTerm();
        }
        else if ( q.getTheType() == Question.Type.DIV ){
            answer = q.getFirstTerm() / q.getSecondTerm();
        }
        else if( q.getTheType() == Question.Type.POSITION ){
            if( q.getSecondTerm() == UNITS ){
                answer = (( 100 * digit.nextInt( 10 )) 
                        + ( 10 * digit.nextInt( 10 )) 
                        + ( 1 * q.getFirstTerm())); 
            }
            else if( q.getSecondTerm() == TENS){
                answer = (( 100 * digit.nextInt( 10 )) 
                        + ( 10 * q.getFirstTerm()) 
                        + ( 1 * digit.nextInt( 10 ))); 
            }
            else if ( q.getSecondTerm() == HUNDREDS ){
                answer = (( 100 * q.getFirstTerm()) 
                        + ( 10 * digit.nextInt( 10 )) 
                        + ( 1 * digit.nextInt( 10 ))); 
            }
            else{
                JOptionPane.showMessageDialog(null, 
                        "Bad Input!", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Bad Input!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        return answer;
    }
    
    /**
     * Generates an array list of incorrect answers.
     * @param q - the question used to generate answers.
     * @param correctAnswer - the correctAnswer
     * @param level - the level you are currently playing at.
     */
    public static ArrayList<Integer> getIncorrectAnswers( Question q,  
            int correctAnswer, int level ){
        // Used to confirm answers do not collide and is returned.
        ArrayList<Integer> incorrectAnswers = new ArrayList<Integer>();
        int currentAnswer; // The current answer.
        Random generateAnswers; // Generates answers
        int temp = 0; // Used for temporary storage.
        Integer tempInteger; // Used for casting.
        String sCurrentAnswer; // A String representation of the current answer.
        String sCorrectAnswer; // A String represenetation of the correct answer.
        
        generateAnswers = new Random();
        
        while( incorrectAnswers.size() < 5 ){
          
            /* 
             * Generates incorrect answers for sum and product.
             */
            if( (q.getTheType() == Question.Type.SUM) 
                    || (q.getTheType() == Question.Type.DIFF) 
                    || (q.getTheType() == Question.Type.MULT)
                    || (q.getTheType() == Question.Type.DIV)){
            
                /*
                 * If correctAnswer is less than 5 avoid negatives by simply 
                 * adding values 1 - 5 to correct answers.
                 */
                if(correctAnswer < 5 ){
                    for( int i = 1; i < 6; i++ ){
                        incorrectAnswers.add( correctAnswer + i);
                    }
                }
                
                /*
                 * If number is five or greater then either subtract five, add 
                 * two and subtract three, add three and subtract two, or add 
                 * five to get the correct answer.
                 */
                else{
                    temp = generateAnswers.nextInt(4);
                    
                    if( temp == 0 ){
                        for( int i = 0; i < 5; i++ ){
                            incorrectAnswers.add(correctAnswer - (i + 1));
                        }
                    }
                    
                    else if( temp == 1 ){
                        for( int i = 0; i < 2; i++ ){
                            incorrectAnswers.add( correctAnswer + (i + 1) );
                        }
                        for( int i = 0; i < 3; i++ ){
                            incorrectAnswers.add( correctAnswer - ( i + 1 ) );
                        }
                    }
                    
                    else if ( temp == 2 ){
                        for( int i = 0; i < 2; i++ ){
                            incorrectAnswers.add( correctAnswer - ( i + 1 ) );
                        }
                        for( int i = 0; i < 3; i++ ){
                            incorrectAnswers.add( correctAnswer + ( i + 1 ) );
                        }
                    }
                    else{
                        for( int i = 0; i < 5; i++ ){
                            incorrectAnswers.add( correctAnswer + (i + 1) );
                        }
                    }
                }
            }
       
            /* If the question is of type POSITION we must ensure we do not
             * create multiple correct answers.
             */
            
            else if( q.getTheType() == Question.Type.POSITION ){
                currentAnswer = ( 100 * generateAnswers.nextInt( 10 ) 
                        + (10 * generateAnswers.nextInt( 10 )) 
                        + (1 * generateAnswers.nextInt( 10 )));
                
                /* 
                 * Ensure that there are not multiple correct answers by turning
                 * both answers into Strings, and comparing the correct decimal
                 * position.
                 */
                
                tempInteger = new Integer( currentAnswer );
                sCurrentAnswer = tempInteger.toString();
                tempInteger = new Integer( correctAnswer );
                sCorrectAnswer = tempInteger.toString();
                
                /* 
                 * Ensure the String is the correct length.
                 * Fill extra places with letters as they are not used anywhere
                 * else
                 */
                
                if( sCurrentAnswer.length() == 1 ){
                    sCurrentAnswer = "0" + "0" + sCurrentAnswer;
                }
                else if ( sCurrentAnswer.length() == 2 ){
                    sCurrentAnswer = "0" + sCurrentAnswer; 
                }
                
                if( sCorrectAnswer.length() == 1 ){
                    sCorrectAnswer = "0" + "0" + sCorrectAnswer;
                }
                else if( sCorrectAnswer.length() == 2 ){
                    sCorrectAnswer = "0" + sCorrectAnswer;
                }
                             
                /**
                 * Checks to make sure there are not multiple correct answers.
                 */
                if( q.getSecondTerm() == UNITS ){
                    if ( sCurrentAnswer.charAt( 2 ) 
                            != sCorrectAnswer.charAt(2) ){
                        incorrectAnswers.add( currentAnswer );
                    }
                }
                
                else if( q.getSecondTerm() == TENS ){
                    if ( sCurrentAnswer.charAt( 1 ) 
                            != sCorrectAnswer.charAt(1) ){
                        incorrectAnswers.add( currentAnswer );
                    }
                }
                
                else if ( q.getSecondTerm() == HUNDREDS ){
                    if ( sCurrentAnswer.charAt( 0 ) 
                            != sCorrectAnswer.charAt(0) ){
                        incorrectAnswers.add( currentAnswer );
                    }
                }       
            }
        }
        return incorrectAnswers;
    }
    
    
    /**
     * @param args - Does nothing.
     */
    public static void main(String[] args) {
        int correctAnswer = 0;
        ArrayList<Integer> incorrectAnswers = new ArrayList<Integer>();
        Question q = new Question( Question.Type.POSITION, 0, 10 );
        correctAnswer = AnswerGenerator.getCorrectAnswer( q );
        System.out.println( "Correct Answer = " + correctAnswer );
        for( int j = 0; j < 100; j++ ){
        incorrectAnswers = AnswerGenerator.getIncorrectAnswers( q, 
                correctAnswer, 1 );
        for( int i = 0; i < incorrectAnswers.size(); i++ ){
            System.out.println( "Incorrect Answer " + (i + 1) + " " +
                    incorrectAnswers.get(i));
        }
        }
    }
}
