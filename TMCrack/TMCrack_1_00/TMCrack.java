import helperClasses.UnsignedLong;

import java.io.File;

import tmCrack.CrackHash;
import tmGenerate.GenerateTable;

/**
 * TMCrack.java
 * 
 * Version:
 * $Id:  $
 * 
 * Revisions:
 * $Log:  $
 *
 */

/**
 * Main class that helps users generate and search rainbow tables.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class TMCrack {

	public static void main(String[] args) {
		int minPwLength = 1;
		int maxPwLength = 7;
		String charsetName = "alpha-numeric";
		String charset = "abcdefghijklmnopqrstuvwxyz0123456789";
		int chainLength = 21000;	//These are typical values for real-life alpha-numeric tables
		int rowsPerTable = 8000000;
		boolean verbose = false;
		boolean printStats = true;
		boolean benchmark = false;
		boolean generateTable = true;
		String unknownHash = null;
		File tableFilename = null;
		
		//This is the hash Windows uses to signify an unused second part of the 32 byte hash.
		UnsignedLong emptyHash = new UnsignedLong("AAD3B435B51404EE");
		
		//Figure out if using full command line options or
		//want help by using the menu
		if(args[0].equals("-generate")){
			if(args.length == 10){
				minPwLength = Integer.parseInt(args[1]);
				maxPwLength = Integer.parseInt(args[2]);
				charsetName = args[3];
				chainLength = Integer.parseInt(args[4]);
				rowsPerTable = Integer.parseInt(args[5]);
				verbose = Boolean.parseBoolean(args[6]);
				printStats = Boolean.parseBoolean(args[7]);
				benchmark = Boolean.parseBoolean(args[8]);
				generateTable = Boolean.parseBoolean(args[9]);
			}else{
				usage();
				System.exit(0);
			}
		}else if(args[0].equals("-search")){
			if(args.length == 8){
				unknownHash = args[1];
				tableFilename = new File(args[2]);
				minPwLength = Integer.parseInt(args[3]);
				maxPwLength = Integer.parseInt(args[4]);
				chainLength = Integer.parseInt(args[5]);
				rowsPerTable = Integer.parseInt(args[6]);
				verbose = Boolean.parseBoolean(args[7]);
			}else{
				usage();
				System.exit(0);
			}
		}else{
			usage();
			System.exit(0);
		}
		
		if(args[0].equals("-generate")){
//			System.out.println(args[0]+ " " + minPwLength + " " + maxPwLength + " " + charsetName + " " + chainLength + " " + rowsPerTable + " " + verbose + " " + printStats + " " + benchmark + " " + generateTable);
			GenerateTable gen = new GenerateTable(minPwLength, maxPwLength, charsetName, charset, chainLength, rowsPerTable, verbose);
			if(printStats){
				gen.printStats(true);
			}
			if(benchmark){
				gen.printStats(false);
			}
			if(generateTable){
				gen.generateTable();
			}
		}else if(args[0].equals("-search")){
//			System.out.println(args[0] + " " + unknownHash + " " + tableFilename + " " + minPwLength + " " + maxPwLength + " " + chainLength + " " + rowsPerTable + " " + verbose);
			//Split the 32 byte hash string into 2 16 byte hash strings
			//because LM hashes are 2 16 byte hashes concatenated together.
			UnsignedLong hash1 = new UnsignedLong(unknownHash.substring(0, 16));
			UnsignedLong hash2 = new UnsignedLong(unknownHash.substring(16, 32));
			
			if(!emptyHash.equals(hash2)){
				System.out.println("Loaded 2 hashes: " + hash1 + " and " + hash2);
			}else{
				System.out.println("Loaded 1 hash: " + hash1);
			}
			
			//Try and crack hash1
			CrackHash crackHash1 = new CrackHash(hash1, tableFilename, minPwLength, maxPwLength, charset, chainLength, rowsPerTable, verbose);
			crackHash1.crackHash();
			
			//If hash2 isn't empty, try and crack it
			if(!emptyHash.equals(hash2)){
				CrackHash crackHash2 = new CrackHash(hash2, tableFilename, minPwLength, maxPwLength, charset, chainLength, rowsPerTable, verbose);
				crackHash2.crackHash();
			}
		}
		
	}
	
	private static void usage(){
		System.out.println("Table Generation Usage:TMCrack -generate minPwLength maxPwLength charsetName chainLength rowsPerTable verbose printStats benchmark generateTable\n" +
				"\tminPwLength - minimum password length. Must be greater than 0.\n" +
				"\tmaxPwLength - maximum password length. Must be less than or equal to 7.\n" +
				"\tcharsetName - custom name to append to the table name.\n" +
				"\tchainLength - length of each rainbow chain.\n" +
				"\trowsPerTable - number of chains per table.\n" +
				"\tverbose - 1 to print verbose messages, 0 to print normal messages.\n" +
				"\tprintStats - 1 to print table statistics, 0 to disable.\n" +
				"\tbenchmark - 1 to run time consuming benchmarks to show how long it will take to generate table, 0 to disable.\n" +
				"\tgenerateTable - 1 to actually generate table, 0 to just print the statistics");
		System.out.println("\n");
		System.out.println("Table Searching Usage:TMCrack -search unknownHash tableFilename minPwLength maxPwLength chainLength rowsPerTable verbose\n" +
				"\tNote: all table parameters must be the same that were used to generate the table\n" +
				"\tunknownHash - 32 byte Windows LM Hash. Must be exactly 32 characters.\n" +
				"\ttableFilename - path to the table file. No spaces.\n" +
				"\tminPwLength - minimum password length. Must be greater than 0.\n" +
				"\tmaxPwLength - maximum password length. Must be less than or equal to 7.\n" +
				"\tcharsetName - custom name to append to the table name.\n" +
				"\tchainLength - length of each rainbow chain.\n" +
				"\trowsPerTable - number of chains per table.\n" +
				"\tverbose - 1 to print verbose messages, 0 to print normal messages.\n");
	}

}
