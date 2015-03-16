/**
 * 
 */
package GameLogic;

/**
 * @author Justin Field
 *
 */
public class DivisionRemainderException extends RuntimeException {

    String message; // The message to dislay.
    
    /**
     * Creates the exception.
     * 
     * @param theMessage - the message.
     */
    public DivisionRemainderException( String theMessage ){
        super( theMessage );
        message = theMessage;
        // System.out.println( toString() );
    }
    
    public String toString(){
        return message; 
    }
    
    /**
     * @param args - Does nothing
     */
    public static void main(String[] args) throws DivisionRemainderException {
        int x = 4;
        int y = 4;
        DivisionRemainderException qw = new DivisionRemainderException( "Error" );
        if( x % y == 0 ){
            System.out.println( "Problem ok" );
        }
        
        x = 5;
        if ( x % y != 0 ){
            System.out.println( qw.toString() );
            throw qw;
        }
    }
}
