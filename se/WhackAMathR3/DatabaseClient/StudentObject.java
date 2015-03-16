/**
 * 
 */
package DatabaseClient;

import java.io.*;

/**
 * @author Brad Israel
 *
 */
public class StudentObject implements Serializable {
	public String sName;
	public String sCorrect;
	public String sTotal;
	
	public StudentObject(String name, String correct, String total){
		sName = name;
		sCorrect = correct;
		sTotal = total;
	}
}
