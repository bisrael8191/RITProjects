/**
 * HelperFunctions.java
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
 * Basic functions used throughout the project.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class HelperFunctions {

	/**
	 * Empty constructor to initialize helper functions.
	 */
	public HelperFunctions(){
		
	}
	
	/**
	 * Get the plaintext (alpha-numeric) for a given keyspace index.
	 * 
	 * @param index - keyspace index
	 * @param maxPwLength - maximum possible password length
	 * @param charset - characters allowed in password
	 * @return plaintext string
	 */
	public String KeyspaceIndexToPlain(long index, int maxPwLength, String charset){
		StringBuilder plain = new StringBuilder(maxPwLength);
		do{ 
			int charsetIndex = (int)(index % (charset.length()));
			if(charsetIndex > 0){
				plain.append(charset.charAt(charsetIndex-1));
				index /= charset.length();
			}else{
				plain.append(charset.charAt(charset.length()-1));
				index /= charset.length();
			}
		}while(index > 0);
		
		return plain.toString();
	}
	
	/**
	 * Get the keyspace index number for a give plaintext value.
	 * 
	 * @param plain - plaintext value
	 * @param charset - characters allowed in password
	 * @return keyspace index
	 */
	public long PlainToKeyspaceIndex(String plain, String charset){
		long keyspaceIndex = 0;
		
		for(int i = 0; i < plain.length(); i++){
			keyspaceIndex += (charset.indexOf(plain.charAt(i))+1) * Math.pow(charset.length(), plain.length()-1-i);
		}
		
		return keyspaceIndex;
	}
	
	/**
	 * Converts an array of bytes to a hex string.
	 * 
	 * @param bytes - array of bytes
	 * @return string representing those bytes
	 */
	public String toHexString(byte [] bytes) {
		StringBuffer hexStr = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			int hex = (0xff & bytes[i]);
			String tmp = Integer.toHexString(hex);
			tmp = (tmp.length() == 1) ? "0" + tmp : tmp;
			hexStr.append(tmp);
		}
		return hexStr.toString();
	}
}
