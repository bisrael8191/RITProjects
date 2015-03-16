/**
 * GenerateTable.java
 * 
 * Version:
 * $Id:  $
 * 
 * Revisions:
 * $Log:  $
 *
 */
package tmGenerate;

import helperClasses.HelperFunctions;
import helperClasses.LMHash;
import helperClasses.ReductionFunction;
import helperClasses.UnsignedLong;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Generate a rainbow table file and generate rainbow table statistics.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class GenerateTable {

	private int minPwLength;
	private int maxPwLength;
	private String charsetName;
	private String charset;
	private int charsetLength;
	private int chainLength;
	private int rowsPerTable;
	//Row in the table to start at
	private int startAtRow = 0;
	private long keyspace;
	//Output stream to the table file
	private DataOutputStream out;
	private boolean verbose;
	private HelperFunctions help;
	
	/**
	 * Initialize the table generation class.
	 * 
	 * @param minPwLength - min password length
	 * @param maxPwLength - max password length
	 * @param charsetName - custom name for the charset
	 * @param charset - characters allowed in password
	 * @param chainLength - length of chain
	 * @param rowsPerTable - number of chains per table
	 * @param verbose - print verbose messages
	 */
	public GenerateTable(int minPwLength, int maxPwLength, String charsetName, String charset, int chainLength, int rowsPerTable, boolean verbose){
		this.minPwLength = minPwLength;
		this.maxPwLength = maxPwLength;
		this.charsetName = charsetName;
		this.charset = charset;
		this.charsetLength = this.charset.length();
		if(chainLength < 1 || chainLength > 2147483640){
			System.out.println("Chain length value too large, reducing to 67108864");
			this.chainLength = 67108864;
		}else{
			this.chainLength = chainLength;
		}
		
		this.rowsPerTable = rowsPerTable;
		
		//Calculate keyspace
		this.keyspace = 0;
		for(int i = this.maxPwLength; i >= 0; i--){
			keyspace += Math.pow(charsetLength, i);
		}
		
		this.verbose = verbose;
		
		//Initialize helper function class
		help = new HelperFunctions();
	}
	
	/**
	 * Print table statistics and benchmarks.
	 * 
	 * @param basicStats - true to print basic stats, false to print time consuming stats
	 */
	public void printStats(boolean basicStats){
		//Calculate Success per table (from paper, p.3)
		//Emulates this matlab script http://www.antsight.com/zsl/rainbowcrack/note040125.txt
		double pTable=0;
		double [] arr = new double[99999];
		double tt=0;
		arr[1] = keyspace * (1 - Math.exp(-rowsPerTable / keyspace));

		//Calculating Success Step 1
		for (int i = 2; i <= (chainLength-1); i++)
			arr[i]= keyspace * (1 - Math.exp(-arr[i - 1] / keyspace));

		//Calculating Success Step 2
		pTable = 1;
		for (int ii = 1; ii <= (chainLength - 1);ii++)
			pTable *= (1 - arr[ii] / keyspace);
		pTable = 1 - pTable;

		//Calculate Total Success
		int numOfTables = 1;	//Need to move this to an input
		double pTotal = (1 - Math.pow(1 - pTable,numOfTables));

		//Output statistics
		System.out.println("Table Statistics:\n" +
				"Charset Name: " + charsetName + "\n" +
				"Charset: " + charset + "\n" +
				"Charset length: " + charsetLength + "\n" +
				"Password Range: " + minPwLength + " - " + maxPwLength + "\n"+
				"Keyspace: " + keyspace + "\n" +
				"Success Probability (per table): " + pTable * 100+ "%\n" +
				"Success Probability (total): " + pTotal*100 + "%\n" +
				"Disk Space Needed: " + rowsPerTable*16 + "bytes\t" + (rowsPerTable*16)/1048576 + "MB\t" + (rowsPerTable*16)/1073741824 + "GB" + "\n" +
				"Disk Space Needed w/o Rainbow Tables: " + (keyspace*16*maxPwLength) + "bytes\t" + (keyspace*16*maxPwLength)/1048576 + "MB\t" + (keyspace*16*maxPwLength)/1073741824 + "GB" + "\n" +
				"Average Needed Calculations for a Key Search: " + (chainLength*chainLength-1)/2 + "\n" /* t(t-1)/2 */);

		//Do time consuming benchmarks
		if(!basicStats){
			//LM hash function
			LMHash lm = new LMHash();
			//Reduction function
			ReductionFunction reduct = new ReductionFunction(minPwLength, maxPwLength, charset, keyspace);
			//Setup random number generator
			Random rand = new Random(System.currentTimeMillis());
			
			//Time how long it takes to generate 10 chains, then show how long it will take to
			//generate the whole table
			//Set table generation start time
			long startTime = System.currentTimeMillis();
			
			//Loop through all the needed rows (number of chains per table)
			for(int row = 0; row < 11; row++){
			
				//Pick a random keyspace index to begin a chain
				long startPlaintxt = Math.abs(rand.nextLong())%(keyspace-1);
				//Convert it to plaintext 
				String nextPlaintxt = help.KeyspaceIndexToPlain(startPlaintxt, maxPwLength, charset);
				//Temporary storage for LM Hashes, uses special long class b/c long can't handle full 64bit hashes
				UnsignedLong tmpHash = new UnsignedLong();
				
				//Loop through every column (length of the chain)
				for(int col = 0; col < chainLength; col++){
					//Hash the plaintext to a windows LM hash
					tmpHash.setValue(lm.lmHash(nextPlaintxt));
					//Run the reduction function for the specific column in the chain
					nextPlaintxt = reduct.reduce(tmpHash, col);
				}
			}
			long time = (System.currentTimeMillis()-startTime)/1000;
			System.out.println("Benchmark took " + time + " seconds\n" +
					time/10 + " calculations per second\n" +
					(rowsPerTable/10)*time + " total table generation time in seconds");
		}
	}
	
	/**
	 * Start generating the table.
	 */
	public void generateTable(){
		//LM hash function
		LMHash lm = new LMHash();
		//Reduction function
		ReductionFunction reduct = new ReductionFunction(minPwLength, maxPwLength, charset, keyspace);
		//Setup random number generator
		Random rand = new Random(System.currentTimeMillis());
		
		//Figure out if the table has already been started
		//If it has, figure out what row to start generating the table at
		File fName = new File(charsetName + "_" + minPwLength + "-" + maxPwLength 
				+ "_" + chainLength + "x" + rowsPerTable + ".tbl");
		startAtRow = (int)(fName.length()/16);
		try{
			//Output chains to an informatively named table file, append existing file
			out = new DataOutputStream(new FileOutputStream(fName, true));
		}catch(Exception e){
			System.out.println("Can't open table file for output");
			e.printStackTrace();
		}
		
		//Set table generation start time
		long startTime = System.currentTimeMillis();
		
		//Loop through all the needed rows (number of chains per table)
		for(int row = startAtRow; row < rowsPerTable; row++){
		
			//Pick a random keyspace index to begin a chain
			long startPlaintxt = Math.abs(rand.nextLong())%(keyspace-1);
			//Convert it to plaintext 
			String nextPlaintxt = help.KeyspaceIndexToPlain(startPlaintxt, maxPwLength, charset);
			//Temporary storage for LM Hashes, uses special long class b/c long can't handle full 64bit hashes
			UnsignedLong tmpHash = new UnsignedLong();
			
			if(verbose)System.out.println("Starting: " + Long.toHexString(startPlaintxt) + " string: " + nextPlaintxt);
			
			//Loop through every column (length of the chain)
			for(int col = 0; col < chainLength; col++){
				//Hash the plaintext to a windows LM hash
				tmpHash.setValue(lm.lmHash(nextPlaintxt));
				//Run the reduction function for the specific column in the chain
				nextPlaintxt = reduct.reduce(tmpHash, col);
			}
		
			//Store startPlaintxt and nextPlaintxt in table file
			long endChain = help.PlainToKeyspaceIndex(nextPlaintxt, charset);
			if(!writeChainToTable(startPlaintxt, endChain)){
				System.err.println("Output to table file failed");
			}
			
			if(verbose)System.out.println("Final: " + Long.toHexString(endChain) + " string: " + nextPlaintxt);
		}
		
		System.out.println("Table generation took " + (System.currentTimeMillis()-startTime)/1000 + " seconds");
		
		//Do some clean up tasks
		cleanUp();
	}
	
	/**
	 * Write chain to file.
	 * 
	 * @param start - start keyspace index (long hex)
	 * @param end - end keyspace index
	 * @return true if write was successful, false otherwise
	 */
	private Boolean writeChainToTable(long start, long end){
		Boolean retVal = false;

		try{
			out.writeLong(start);
			out.writeLong(end);
			
			retVal = true;
		}catch (Exception e){
			System.err.println ("Error writing to file");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * Clean up any open files.
	 */
	private void cleanUp(){
		try{
			out.close();
		}catch(Exception e){
			
		}
	}
	
}
