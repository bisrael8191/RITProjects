import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 * FormatData.java
 * 
 * Version:
 * $Id:  $
 * 
 * Revisions:
 * $Log:  $
 *
 */

/**
 * Creates a normalized, formatted data file for the neural network.
 * Should use the KDD data as input.
 *
 * @author Brad Israel, Jon Ludwig
 *
 */
public class FormatData {

	public static void format(String filename) throws IOException{
		BufferedReader in;
		PrintWriter out;
		Random rand = new Random();
		String line = null;
		String[] tokens;
		float[] max = null;
		float f;
		ArrayList<String> selectedData = new ArrayList<String>();

		//No symbolic fields
		float[] usefulFields = {1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

		int attackCount = 0;
		int unattackCount = 0;
		int lineCount = 0;

		//Get max value for all number fields, and specify
		//the amount of attacks needed.
		in = new BufferedReader(new FileReader(filename));
		while ((line = in.readLine()) != null && (attackCount < 8000)) {
				tokens = line.split(",");

				if (max == null) {
					max = new float[tokens.length];
					for (int i = 0; i < max.length; i++) max[i] = 1;
				}

				for (int i = 0; i < tokens.length-2; i++) {
					if(usefulFields[i] == 1){
						f = Float.parseFloat(tokens[i]);
						if (f > max[i])
							max[i] = f;
					}
				}
				String tok = String.valueOf(tokens[tokens.length-1]);
				if(tok.equals("smurf.") || tok.equals("neptune.") || tok.equals("warezclient.") || tok.equals("portsweep.") || tok.equals("satan.")){
					attackCount++;
				}else if(!tok.equals("normal.")){
					unattackCount++;
				}
				selectedData.add(line);
		}

		//Normalize and print out the data, with attack/normal as 1/0 on the end
		out = new PrintWriter(new FileOutputStream("FormattedData.txt"));
		for(String pline : selectedData){
			// split
			tokens = pline.split(",");

			StringBuffer outputLine = new StringBuffer();
			//Combine all useful fields into a string for formatted output file
			for (int i = 0; i < tokens.length-2; i++) {
				if(usefulFields[i] == 1){
					f = Float.parseFloat(tokens[i]);
					f /= max[i];
					outputLine.append(String.valueOf(f) + ";");
				}
			}

			outputLine.append((String.valueOf(tokens[tokens.length-1]).equals("normal.") ? "0.0" : "1.0") + "\n");
			out.write(outputLine.toString());
		}
		
		System.out.println("Attack count: " + attackCount + "\nUnknown attack count: " + unattackCount);
		in.close();
		out.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{

		format(args[0]);


	}

}
