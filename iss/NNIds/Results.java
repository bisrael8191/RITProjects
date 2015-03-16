import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Results.java
 * 
 * Version:
 * $Id:  $
 * 
 * Revisions:
 * $Log:  $
 *
 */

/**
 * Compares the datasets to the output from the neural network
 * to compile the results.
 *
 * @author Brad Israel, Jon Ludwig
 *
 */
public class Results {

	public Results(){

	}

	public String calculateResults(String nnOutput, String dataFile){
		String retVal = null;
		int numOfEntries = 0;
		int numOfAttacks = 0;
		int numCorrectlyClassified = 0;
		int numOfFalsePos = 0;
		int numOfFalseNeg = 0;

		try{
			String[] tokens;
			BufferedReader nnIn = new BufferedReader(new FileReader(nnOutput));
			BufferedReader dataIn = new BufferedReader(new FileReader(dataFile));
			String nnLine = null;
			String dataLine = null;
			float nnResult;


			while((nnLine = nnIn.readLine()) != null){
				dataLine = dataIn.readLine();
				tokens = dataLine.split(";");

				numOfEntries++;

				//If the entry should be normal traffic, else attack
				if(String.valueOf(tokens[tokens.length-1]).equals("0.0")){
					nnResult = Float.valueOf(nnLine);
					if(nnResult < Float.valueOf("0.5")){
						numCorrectlyClassified++;
					}else{
						numOfFalsePos++;
					}
				}else{
					numOfAttacks++;

					nnResult = Float.valueOf(nnLine);
					if(nnResult >= Float.valueOf("0.5")){
						numCorrectlyClassified++;
					}else{
						numOfFalseNeg++;
					}
				}

			}
		}catch(Exception e){
			e.printStackTrace();
		}

		retVal = "Total Entries: " + numOfEntries +
		"\nNumber of attacks: " + numOfAttacks +
		"\nCorrectly Classified: " + numCorrectlyClassified +
		"\nFalse Positives: " + numOfFalsePos +
		"\nFalse Negatives: " + numOfFalseNeg;

		return retVal;
	}
}
