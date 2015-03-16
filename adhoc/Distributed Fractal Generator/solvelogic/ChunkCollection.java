/**
 * ChunkCollection.java
 * 
 * Version:
 * $Id: ChunkCollection.java,v 1.4 2007/05/20 18:54:19 bisrael Exp $
 * 
 * Revisions:
 * $Log: ChunkCollection.java,v $
 * Revision 1.4  2007/05/20 18:54:19  bisrael
 * Added comments.
 *
 * Revision 1.3  2007/05/20 07:06:43  bisrael
 * Now has a stop method that withdraws all of your tuples when exiting a problem.
 *
 * Revision 1.2  2007/05/17 09:16:13  bisrael
 * Now updates the statistics in the UI.
 *
 * Revision 1.1  2007/05/17 08:08:32  bisrael
 * Created a class to hold and keep track of the chunks of a problem.
 * Also solves a synchronization error with threads.
 *
 *
 */
package solvelogic;

import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.awt.image.BufferedImage;
import java.util.Random;

import edu.rit.tb.TupleBoard;

import tuple.ChunkTuple;
import tuple.ProblemTuple;
import gui.FractalDisplayGUI;

/**
 * Holds all chunks received from the tuple board
 * and also has synchronized methods for getting/setting
 * chunks and checking if they're finished.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class ChunkCollection {

	/*
	 * Store the chunks in a map with the key being a
	 * flattened integer of its starting coordinate.
	 * (starting row * total problem width) + starting column
	 */
	private HashMap<Integer, ChunkTuple> chunkStates;
	
	/*
	 * Reference to the tuple board
	 */
	private TupleBoard myTupleBoard;
	
	/*
	 * Reference to the ui
	 */
	private FractalDisplayGUI imageUI;
	
	/*
	 * The total problem width
	 */
	private Integer problemWidth;
	
	/*
	 * Problem tuple to use for matching
	 */
	private ProblemTuple match;
	
	/*
	 * Store the statistic of total computation time for the chunk
	 */
	private long totalCompTime;
	
	/**
	 * Constructor for the chunk collection class.
	 * 
	 * @param myTupleBoard - reference to the tuple board
	 * @param imageUI - reference to the ui
	 * @param problemWidth - total problem width
	 * @param match - problem tuple used for matching
	 */
	public ChunkCollection(TupleBoard myTupleBoard, FractalDisplayGUI imageUI, 
			Integer problemWidth, ProblemTuple match){
		chunkStates = new HashMap<Integer, ChunkTuple>();
		this.myTupleBoard = myTupleBoard;
		this.imageUI = imageUI;
		this.problemWidth = problemWidth;
		this.match = match;
		this.totalCompTime = 0;
	}
	
	/**
	 * Called from the tupleboard listener when a chunk has been posted.
	 * 
	 * @param row - starting row number
	 * @param col - starting column number
	 * @param tuple - chunk tuple
	 */
	public synchronized void addNewChunk(Integer row, Integer col, ChunkTuple tuple){
		//Check to make sure that the problem tuples match
		if(!match.equals(tuple.problem)) return;
		
		//Calculate the key for the map
		Integer tupleId = (row*problemWidth)+col;
		
		//Try and get the old tuple, null if there isn't one
		ChunkTuple oldTuple = chunkStates.get(tupleId);
		
		//If the tuple has an older state
		if(oldTuple != null){
			//If tuple has already been processed, return
			if(oldTuple.equals(tuple)) return;
			
			myTupleBoard.withdraw(oldTuple);	//Withdraw the old tuple
			chunkStates.put(tupleId, tuple);	//Replace the old with the new one
			myTupleBoard.post(tuple);			//Post the new tuple
			
		//If the chunk doesn't have an older state	
		}else{
			chunkStates.put(tupleId, tuple);	//Add it to the map
			myTupleBoard.post(tuple);			//Post the tuple
		}
		
		//If the tuple is finished
		if(tuple.finished && tuple.data != null){
			//Calculate stats
			totalCompTime += tuple.computationTime;
			imageUI.updateStats(percentOfCompletedChunks(), totalCompTime, 
					totalCompTime/totalCompletedChunks());
			
			//Draw it in the fractal window
			BufferedImage chunkImg = new BufferedImage(tuple.height, tuple.width, BufferedImage.TYPE_INT_RGB);
			for(int x = 0; x < tuple.height; x++){
				for( int y = 0; y < tuple.width; y++){
					chunkImg.setRGB(x, y, tuple.data[x][y]);
				}
			}
			imageUI.addImage(chunkImg, tuple.starty, tuple.startx);
		}
	}
	
	/**
	 * Called when the solvers on your machine finish a chunk.
	 * 
	 * @param row - starting row number
	 * @param col - starting column number
	 * @param tuple - chunk tuple
	 */
	public synchronized void chunkFinished(Integer row, Integer col, ChunkTuple tuple){
		myTupleBoard.post(tuple);			//Post the new tuple
	}
	
	/**
	 * Chooses a random unfinished chunk to begin working on.
	 * 
	 * @return - the chunk tuple
	 */
	public synchronized ChunkTuple getRandomUnfinishedChunk(){
		Set<Integer> keySet = chunkStates.keySet();
		Integer[] keys = keySet.toArray(new Integer[chunkStates.size()]); 
		ChunkTuple start = null;
		
		//If stop becomes greater than the number of tuples*2,
		// stop trying to pick an unfinished tuple.
		//Class that calls this will check if returned tuple is null
		// and check to make sure that the problem still has unfinished tuples
		int stop = 0;
		while(start == null && stop < keys.length*2){
			Random chooser = new Random();
			//Choose a random chunk
			start = chunkStates.get(keys[new Integer(chooser.nextInt(keys.length))]);

			//If the chunk has been solved, set it back to null
			// and go through the loop again
			if(start.finished){
				start = null;
			}
			stop++;
		}
		return start;
	}
	
	/**
	 * Check if the chunk you are working on has been finished
	 * 
	 * @param row - starting row number
	 * @param col - starting column number
	 * @return - true if it has been finished, false if it hasn't
	 */
	public synchronized boolean isChunkFinished(Integer row, Integer col){
		//Calculate the map key
		Integer tupleId = (row*problemWidth)+col;
		
		//Try and get the tuple
		ChunkTuple test = chunkStates.get(tupleId);
		
		//If the tuple doesn't exist in the map, return false
		if(test == null) return false;
		//else return the tuple finished value
		else return test.finished;
	}
	
	/**
	 * Get the ratio of completed out of total chunks.
	 * 
	 * @return - the percent of completed chunks
	 */
	public synchronized int percentOfCompletedChunks(){
		Collection<ChunkTuple> values = chunkStates.values();
		int finishedChunks = 0;
		
		//Loop through all the values(chunks) and add one
		//to the finishedChunks variable if it is finished
		for(ChunkTuple tmp : values){
			if(tmp.finished) finishedChunks++;
		}
		//Return the ratio number out of 100
		return (int)((double)finishedChunks/chunkStates.size()*100.0);
	}
	
	/**
	 * Get the number of completed chunks
	 * 
	 * @return - number of completed chunks
	 */
	public synchronized int totalCompletedChunks(){
		Collection<ChunkTuple> values = chunkStates.values();
		int finishedChunks = 0;
		for(ChunkTuple tmp : values){
			if(tmp.finished) finishedChunks++;
		}
		return finishedChunks;
	}
	
	/**
	 * Get the total number of chunk tuples
	 * 
	 * @return - the total number of chunk tuples
	 */
	public synchronized int totalChunks(){
		return chunkStates.size();
	}
	
	/**
	 * Called when the user wants to stop working on a problem.
	 * Withdraw all of your posted tuples.
	 * Clear the map.
	 */
	public void removeChunks(){
		//Withdraw all tuples
		Collection<ChunkTuple> values = chunkStates.values();
		for(ChunkTuple tmp : values){
			myTupleBoard.withdraw(tmp);
		}
		
		//Clear the map of chunk states
		chunkStates.clear();
	}
	
}
