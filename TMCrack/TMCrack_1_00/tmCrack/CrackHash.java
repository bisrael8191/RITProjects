/**
 * CrackHash.java
 * 
 * Version:
 * $Id:  $
 * 
 * Revisions:
 * $Log:  $
 *
 */
package tmCrack;

import helperClasses.HelperFunctions;
import helperClasses.LMHash;
import helperClasses.ReductionFunction;
import helperClasses.UnsignedLong;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Searches a table given an unknown Windows LM hash.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class CrackHash {

	//Store the original hash
	private UnsignedLong originalHash;
	
	//Storage for the whole table in memory.
	//The map key is the end point and the start
	//points are stored as the value.
	private Map<Long, HashSet<Long>> table;
	
	//Minimum password length
	private int minPwLength;
	
	//Maximum password length
	private int maxPwLength;
	
	//Keyspace charset
	private String charset;
	
	//Length of a single chain
	private int chainLength;
	
	//How many expected rows are in the table
	private int rowsPerTable;
	
	//Print out verbose messages
	private boolean verbose;
	
	//Number of possible passwords
	private int keyspace;
	
	//LM hash function
	private LMHash lm;
	
	//Reduction function
	private ReductionFunction reduct;
	
	//Holds some helper functions
	private HelperFunctions help;
	
	/**
	 * Initialize the class with all needed values.
	 * 
	 * @param hash - hash to break
	 * @param tblFile - table filename
	 * @param minPwLength - min password length
	 * @param maxPwLength - max password length
	 * @param charset - characters allowed in password
	 * @param chainLength - length of a single chain
	 * @param rowsPerTable - expected number of rows per table
	 * @param verbose - print verbose messages
	 */
	public CrackHash(UnsignedLong hash, File tblFile, int minPwLength, int maxPwLength, String charset, int chainLength, int rowsPerTable, boolean verbose){
		originalHash = hash;
		
		//Read the table into memory
		long diskReadTime = System.currentTimeMillis();
		boolean readTable = readTable(tblFile);
		if(readTable){
			System.out.println("Disk access time: " + (System.currentTimeMillis()-diskReadTime)/1000 + " seconds, table size: " + table.size() );
		}else{
			System.err.println("Error reading table from disk");
		}
		
		this.minPwLength = minPwLength;
		this.maxPwLength = maxPwLength;
		this.charset = charset;
		this.chainLength = chainLength;
		this.rowsPerTable = rowsPerTable;
		this.verbose = verbose;
		
		//Calculate keyspace
		this.keyspace = 0;
		for(int i = this.maxPwLength; i >= 0; i--){
			keyspace += Math.pow(charset.length(), i);
		}
		
		//Setup other classes
		lm = new LMHash();
		reduct = new ReductionFunction(minPwLength, maxPwLength, charset, keyspace);
		help = new HelperFunctions();
	}
	
	/**
	 * Read a table into memory.
	 * 
	 * @param tblFile - table filename
	 * @return true if table read successfully, false otherwise
	 */
	private boolean readTable(File tblFile){
		boolean retVal = false;
		
		table = new HashMap<Long, HashSet<Long>>();
		DataInputStream in = null;
		try{
			in = new DataInputStream(new FileInputStream(tblFile));
			while(true){
				//Need to reverse the order from the file,
				//so the map key is the end of the chain and
				//the map value is the beginning of the chain.
				//Since the ends are not unique(obviously), the 
				//values will be a set of start positions.
				Long start = in.readLong();
				Long end = in.readLong();
				HashSet<Long> values = table.get(end);
				if(values == null){
					values = new HashSet<Long>();
				}
				values.add(start);
				table.put(end, values);
			}
		}catch(EOFException eof){
			retVal = true;	//Hit end of file
		}catch (Exception e){
			System.err.println ("Error reading from file");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * Start cracking the hash.
	 */
	public void crackHash(){
		HashSet<Long> prevSeenEndPoints = new HashSet<Long>();
		
		//Store current time
		long crackTime = System.currentTimeMillis();
		
		//Copy original hash to temporary var
		UnsignedLong newHash = new UnsignedLong(originalHash.toString());
		String nextPlaintxt = null;
		
		//Loop through the possible reductions
		//starting at the end of the chain.
		for(int t = chainLength-1; t >= 0; t--){
			for(int x = t; x < chainLength; x++){
				nextPlaintxt = reduct.reduce(newHash, x);
				String tmp = lm.lmHash(nextPlaintxt);
				newHash.setValue(tmp);
			}
			
			//Check if the reduction is an end point in the table
			long index = help.PlainToKeyspaceIndex(nextPlaintxt, charset);
			if(table.containsKey(index)){
				//If it was an end point, add it to the set,
				//non-unique values will be dropped.
				prevSeenEndPoints.add(index);
			}
		}
		
		if(verbose)System.out.println("Found " + prevSeenEndPoints.size() + " possible chain ends");
		
		//Now regenerate each chain until original hash is found
		String crackedHash = generateChain(prevSeenEndPoints);
		
		//If the password was cracked, output the plaintext and the time
		if(crackedHash != null){
			System.out.println("Hash Cracked! Hash ("+ originalHash + ") = " + nextPlaintxt);
			System.out.println("Took " + (crackTime - System.currentTimeMillis())/1000 + " seconds");
			
		//Else output that cracking failed
		}else{
			System.out.println("Cracking hash (" + originalHash + ") failed");
		}
		
	}
	
	/**
	 * Regenerate the found chains from their start
	 * points to their end points. Check if any hash 
	 * matches the original hash.
	 * 
	 * @param endPoints - Unique set of end points
	 * @return null if password not found, plaintext string if found
	 */
	private String generateChain(HashSet<Long> endPoints){
		String retVal = null;
		
		//Count the number of false alarms
		int fa = 0;
		
		//Loop through each found chain end point
		for(Object end : endPoints){

			//Get the list of start points associated with that end point
			HashSet<Long> startPoints = table.get((Long)end);
			if(startPoints != null){

				//Loop through each start point
				for(Object start : startPoints){
					UnsignedLong newHash = new UnsignedLong();
					String nextPlaintxt = help.KeyspaceIndexToPlain((Long)start, maxPwLength, charset);
					//Loop through every column (length of the chain)
					for(int col = 0; col < chainLength; col++){
						//Hash the plaintext to a windows LM hash
						newHash.setValue(lm.lmHash(nextPlaintxt));

						//Run the reduction function for the specific column in the chain
						nextPlaintxt = reduct.reduce(newHash, col);

						//If hashes match, then hash broken
						//return the plaintext!
						if(newHash.equals(originalHash)){
							if(verbose)System.out.println("PW: " + nextPlaintxt);
							return nextPlaintxt;
						}
					}
				}
			}
			fa++;	//Increase false alarm count
			if(verbose)System.out.println("End point (" + Long.toHexString((Long)end) + ") was false alarm number " + fa);
		}
		return retVal;
	}
	
	
//	public void generateChain(long start){
//		LMHash lm = new LMHash();
//		ReductionFunction reduct = new ReductionFunction(minPwLength, maxPwLength, charset, keyspace);
//		HelperFunctions help = new HelperFunctions();
//		
////		System.out.println("hash: 4733115097298669 reduces to " + reduct.reduce(new UnsignedLong("4733115097298669"), 2100));
////		UnsignedLong testHash = new UnsignedLong("a804a05a5d39e806");
//		UnsignedLong testHash = new UnsignedLong("4825639297121403");
//		UnsignedLong newHash = new UnsignedLong(testHash.toString());
//		String nextPlaintxt = null;/*help.KeyspaceIndexToPlain(start, maxPwLength, charset);*/
//		long endofchain = Long.parseLong("17f28", 16);
////		System.out.println("Starting: " + Long.toHexString(start) + " string: " + nextPlaintxt);
//		for(int t = chainLength-1; t >= 0; t--){
//			for(int x = t; x < chainLength; x++){
//				nextPlaintxt = reduct.reduce(newHash, x);
//				String tmp = lm.lmHash(nextPlaintxt);
//				newHash.setValue(tmp);
//			}
////			System.out.println(newHash);
//			if(help.PlainToKeyspaceIndex(nextPlaintxt, charset) == endofchain){
//				System.out.println("Matches end of chain: " + t + "\t" + newHash + " -> " + nextPlaintxt);
//			}
//		}
////		System.out.println("Final: " + help.PlainToKeyspaceIndex(nextPlaintxt, charset) + " string: " + nextPlaintxt);
//		
////		UnsignedLong newHash = new UnsignedLong();
////		String nextPlaintxt = help.KeyspaceIndexToPlain(start, maxPwLength, charset);
////		System.out.println("Starting: " + Long.toHexString(start) + " string: " + nextPlaintxt);
//		newHash = new UnsignedLong();
//		nextPlaintxt = help.KeyspaceIndexToPlain(start, maxPwLength, charset);
//		//Loop through every column (length of the chain)
//		for(int col = 0; col < chainLength; col++){
//			//Hash the plaintext to a windows LM hash
//			newHash.setValue(lm.lmHash(nextPlaintxt));
//			
//			//Run the reduction function for the specific column in the chain
//			nextPlaintxt = reduct.reduce(newHash, col);
//			if(newHash.equals(testHash)){
//				System.out.println("Password found: " + nextPlaintxt);
//			}
//			System.out.println(newHash + " -> " + nextPlaintxt);
//		}
//		System.out.println("Final: " + help.PlainToKeyspaceIndex(nextPlaintxt, charset) + " string: " + nextPlaintxt);
//	}
	
}
