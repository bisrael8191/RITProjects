package Administration;

import java.security.*;

/**
 * Class that converts plaintext strings to MD5 hashed strings.
 * Used to generate and check Teacher passwords.
 * 
 * @author Brad Israel
 *
 */

public class MD5Hash {
    
    MessageDigest md = null;
    
    /**
     * Default constructor.
     * Sets up the message digest algorithm.
     *
     */
    public MD5Hash(){
        //Create a Message Digest from a Factory method
        try{
            md = MessageDigest.getInstance("MD5");
        } catch( NoSuchAlgorithmException nsae ){
            System.out.println( "MD5 not found" );
        }
    }
    
    /**
     * Converts a string to a hash
     * 
     * @param password plaintext password
     * @return a hashed value of the plaintext password
     */
    public String getHash( String password ){
        String retVal = null;
        
        //Turn the password string into bytes
        byte[] hash = null;
        try{
        	hash = password.getBytes( "UTF-8" );
        } catch( Exception uee ){
        	System.out.println( "String could not be encoded as UTF-8" );
        }
        
        //Update the digest with the password
        md.update( hash );
        
        //Hash the password
        retVal = new String( md.digest() );
        
        //Reset the hash for next use
        md.reset();
        
        return retVal;
    }
}

