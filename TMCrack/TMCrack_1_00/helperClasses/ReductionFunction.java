/**
 * ReductionFunction.java
 * 
 * Version:
 * $Id:  $
 * 
 * Revisions:
 * $Log:  $
 *
 */
package helperClasses;

/**
 * Reduces hash values to plaintext.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class ReductionFunction {

	private int minPwLength;
	private int maxPwLength;
	private String charset;
	private long keyspace;
	
	/**
	 * Initialize the reduction function.
	 * 
	 * @param minPwLength - min password length
	 * @param maxPwLength - max password length
	 * @param charset - characters allowed in password
	 * @param keyspace - number of password combinations
	 */
	public ReductionFunction(int minPwLength, int maxPwLength, String charset, long keyspace){
		this.minPwLength = minPwLength;
		this.maxPwLength = maxPwLength; //The reduction function is not really set up to do more than 9, convert to hex before xor to fix
		this.charset = charset;
		this.keyspace = keyspace;
	}
	
	/**
	 * Reduce a given hash to a plaintext.
	 * 
	 * @param hash - LM hash
	 * @param col - current position in the chain
	 * @return the reduced plaintext string
	 */
	public String reduce(UnsignedLong hash, int col){
		StringBuilder retVal = new StringBuilder(maxPwLength);
		
		//Xor hash with column
		hash.xor(col);
		
		//Generate new plaintext (mod then divide by charset length)
		for(int i = maxPwLength-1; i >= 0; i--){
			retVal.append(charset.charAt((int)(hash.mod(charset.length()))));
			hash.divide(charset.length());
		}
		
		//Calculate new string length (gives a value from 1 to maxPwLength)
		//This makes sure that all length passwords are in the table somewhere,
		//if you only wanted to search for maxPwLength passwords, comment out.
		long strLen = hash.mod(keyspace)-1;
		long keyspaceMultiples = 0;
		int len = 0;
		while((keyspaceMultiples += Math.pow(charset.length(), len)) < strLen){
			len++;
		}
		
		//Make sure it's not below the minimum passwd length
		if(len < minPwLength){
			len = minPwLength;
		}
		
		//Return the plaintext with variable length
		return retVal.toString().substring(0, len);
	}
}
