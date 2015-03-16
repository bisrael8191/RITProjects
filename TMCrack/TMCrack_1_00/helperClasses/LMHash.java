/**
 * LMHash.java
 * 
 * Version:
 * $Id:  $
 * 
 * Revisions:
 * $Log:  $
 *
 */

package helperClasses;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * LM hash functions found by the NTLM Authentication Protocol
 *  group. Their webpage can be found here:
 *  http://davenport.sourceforge.net/
 *
 *	I just made some format and class changes.
 *
 * @author - NTLM Authentication Protocol
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class LMHash {

	/**
	 * Blank constructor
	 *
	 */
	public LMHash(){

	}

	/**
	 * Creates the LM Hash of the user's password.
	 *
	 * @param password The password.
	 *
	 * @return The lower half of the LM Hash of the given password.
	 */
	public String lmHash(String password){
//		byte[] lmHash = new byte[16];
		byte[] lmHash = new byte[8];	//Only need one part of the hash
		try{
			byte[] oemPassword = password.toUpperCase().getBytes("US-ASCII");
			int length = Math.min(oemPassword.length, 14);
			byte[] keyBytes = new byte[14];
			System.arraycopy(oemPassword, 0, keyBytes, 0, length);
			Key lowKey = createDESKey(keyBytes, 0);
//			Key highKey = createDESKey(keyBytes, 7);
			byte[] magicConstant = "KGS!@#$%".getBytes("US-ASCII");
			Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
			des.init(Cipher.ENCRYPT_MODE, lowKey);
			byte[] lowHash = des.doFinal(magicConstant);
//			des.init(Cipher.ENCRYPT_MODE, highKey);
//			byte[] highHash = des.doFinal(magicConstant);
			System.arraycopy(lowHash, 0, lmHash, 0, 8);
//			System.arraycopy(highHash, 0, lmHash, 8, 8);
		}catch(Exception e){
			System.out.println("LM Hash function failed: " + e.getMessage());
		}
		
		return toHexString(lmHash);
	} 

	/**
	 * Creates a DES encryption key from the given key material.
	 *
	 * @param bytes A byte array containing the DES key material.
	 * @param offset The offset in the given byte array at which
	 * the 7-byte key material starts.
	 *
	 * @return A DES encryption key created from the key material
	 * starting at the specified offset in the given byte array.
	 */
	private static Key createDESKey(byte[] bytes, int offset) {
		byte[] keyBytes = new byte[7];
		System.arraycopy(bytes, offset, keyBytes, 0, 7);
		byte[] material = new byte[8];
		material[0] = keyBytes[0];
		material[1] = (byte) (keyBytes[0] << 7 | (keyBytes[1] & 0xff) >>> 1);
		material[2] = (byte) (keyBytes[1] << 6 | (keyBytes[2] & 0xff) >>> 2);
		material[3] = (byte) (keyBytes[2] << 5 | (keyBytes[3] & 0xff) >>> 3);
		material[4] = (byte) (keyBytes[3] << 4 | (keyBytes[4] & 0xff) >>> 4);
		material[5] = (byte) (keyBytes[4] << 3 | (keyBytes[5] & 0xff) >>> 5);
		material[6] = (byte) (keyBytes[5] << 2 | (keyBytes[6] & 0xff) >>> 6);
		material[7] = (byte) (keyBytes[6] << 1);
		oddParity(material);
		return new SecretKeySpec(material, "DES");
	}

	/**
	 * Applies odd parity to the given byte array.
	 *
	 * @param bytes The data whose parity bits are to be adjusted for
	 * odd parity.
	 */
	private static void oddParity(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			boolean needsParity = (((b >>> 7) ^ (b >>> 6) ^ (b >>> 5) ^
					(b >>> 4) ^ (b >>> 3) ^ (b >>> 2) ^
					(b >>> 1)) & 0x01) == 0;
			if (needsParity) {
				bytes[i] |= (byte) 0x01;
			} else {
				bytes[i] &= (byte) 0xfe;
			}
		}
	}
	
	/**
	 * Converts an array of bytes to a hex string.
	 *
	 * @param bytes - array of bytes
	 * @return string representing those bytes
	 */
	private String toHexString(byte [] bytes) {
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
