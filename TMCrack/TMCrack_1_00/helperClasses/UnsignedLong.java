/**
 * UnsignedLong.java
 * 
 * Version:
 * $Id:  $
 * 
 * Revisions:
 * $Log:  $
 *
 */
package helperClasses;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * This class is here because Java sucks and doesn't provide
 * a full 64bit long and BigInteger is overkill for this application.
 * 
 * This class assumes that it will always get a 64 bit string 
 * as input, doesn't provide any checks.
 * 
 * If you are re-implementing this in a better language, like c, this whole
 * class can be replaced by using "unsigned long" statements.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class UnsignedLong {

	private Long lower;
	private Long upper;
	NumberFormat formatter = new DecimalFormat("0000000000000000");
	
	public UnsignedLong(){
		lower = Long.parseLong("0", 16);
		upper = Long.parseLong("0", 16);
	}
	
	public UnsignedLong(Long number){
		String strNum = formatter.format(Long.parseLong(number.toString(), 16));
		
		lower = Long.parseLong(strNum.substring(strNum.length()-1-7, strNum.length()), 16);
		upper = Long.parseLong(strNum.substring(0, 8), 16);
	}
	
	public UnsignedLong(String number){
		lower = Long.parseLong("0", 16);
		upper = Long.parseLong("0", 16);
		lower = Long.parseLong(number.substring(number.length()-1-7, number.length()), 16);
		upper = Long.parseLong(number.substring(0, 8), 16);
	}
	
	public void setValue(String number){
		lower = Long.parseLong(number.substring(number.length()-1-7, number.length()), 16);
		upper = Long.parseLong(number.substring(0, 8), 16);
	}
	
	public int getLower(){
		return lower.intValue();
	}
	
	public void setLower(int low){
		lower = Long.parseLong(Integer.toHexString(low), 16);
	}
	
	public int getUpper(){
		return upper.intValue();
	}
	
	public void setUpper(int up){
		upper = Long.parseLong(Integer.toHexString(up), 16);
	}
	
	public void xor(int col){
		if(col > 2147483640){
			System.out.println("Unable to xor because Chain Length value is too large ... shouldn't get here");
			return;
		}
		lower ^= col;
	}
	
	public void divide(int div){
		BigInteger tmp = new BigInteger(Long.toHexString(upper) + Long.toHexString(lower), 16);
		tmp = tmp.divide(BigInteger.valueOf(div));
		if(tmp.longValue() > 0){
			String newVal = formatter.format(tmp.longValue());
			setValue(newVal);
		}else{
			setValue("0000000000000000");
		}
	}
	
	public long mod(long mod){
		BigInteger tmp = new BigInteger(Long.toHexString(upper) + Long.toHexString(lower), 16);
		tmp = tmp.mod(BigInteger.valueOf(mod));
		return Integer.parseInt(tmp.toString(10));
	}
	
	public String toString(){
		return Long.toHexString(upper) + Long.toHexString(lower);
	}
	
	public boolean equals(Object obj){
		return obj instanceof UnsignedLong &&
		this.upper != null &&
		this.upper.equals(((UnsignedLong)obj).upper) &&
		this.lower != null &&
		this.lower.equals(((UnsignedLong)obj).lower);
	}
}
