/**
 * SolverThread.java
 * 
 * Version:
 * $Id: SolverThread.java,v 1.6 2007/05/20 18:56:04 bisrael Exp $
 * 
 * Revisions:
 * $Log: SolverThread.java,v $
 * Revision 1.6  2007/05/20 18:56:04  bisrael
 * Added comments.
 *
 * Revision 1.5  2007/05/20 07:08:02  bisrael
 * Now stops correctly.
 *
 * Revision 1.4  2007/05/17 09:14:37  bisrael
 * Now keeps track of time for statistics.
 *
 * Revision 1.3  2007/05/17 08:17:50  bisrael
 * Goes through every pixel for an image and gets the proper color.
 * Then creates the new tuple and passes it back to the listener.
 * Also checks if the chunk has already been completed and stops if it has.
 *
 * Revision 1.2  2007/05/16 18:33:14  bisrael
 * Changed how the data is stored.
 *
 * Revision 1.1  2007/05/16 04:39:36  bisrael
 * Created the solver thread that solves chunks and returns the data to the listener.
 *
 *
 */
package solvelogic;


import tuple.ProblemTuple;
import tuple.ChunkTuple;
import tools.FractalImplementation;

/**
 * This thread class does the actual solving of the chunk.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class SolverThread extends Thread {

	/*
	 * Reference to the listener for returned data
	 */
	private ChunkListener myListener;
	
	/*
	 * Reference to the problem tuple
	 */
	private ProblemTuple problem;
	
	/*
	 * The chunk tuple to start solving
	 */
	private ChunkTuple chunk;
	
	/*
	 * Reference to the instance of the FractalImplementation class
	 */
	private FractalImplementation newFrac;
	
	/*
	 * Reference to the chunk collection
	 */
	private ChunkCollection chunkCollection;
	
	/*
	 * Keep track of the starting time of computation
	 */
	private long startTime;
	
	/*
	 * Keep track of the ending time of computation
	 */
	private long endTime;
	
	/*
	 * Variable checked to see if the thread should stop running
	 */
	private boolean isRunning;
	
	/**
	 * Constructor for the solver thread.
	 * 
	 * @param listener - listener for returned data
	 * @param problem - problem tuple
	 * @param newFrac - instance of the FractalImplementation class
	 * @param chunk - chunk tuple that is being solved
	 * @param chunkCollection - reference to the chunk collection
	 */
	public SolverThread(ChunkListener listener, ProblemTuple problem, 
			FractalImplementation newFrac, ChunkTuple chunk, ChunkCollection chunkCollection){
		this.isRunning = true;
		this.myListener = listener;
		this.problem = problem;
		this.chunk = chunk;
		this.newFrac = newFrac;
		this.chunkCollection = chunkCollection;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 * 
	 * Run the thread and solve the chunk. Loops until interrupted by the chunk being
	 * finished by another thread/computer, stopped by the user, or finished.
	 */
	public void run(){
		//Storage for the data
		Integer[][] computedFractal = new Integer[chunk.height][chunk.width];
		
		//For each pixel in the chunk, calculate the picture color
		//Check every row to see if the chunk has been completed
		startTime = System.currentTimeMillis();
		boolean interrupted = false;
		for(int row = chunk.starty, dataRow = 0; row < (chunk.starty+chunk.height) 
		&& dataRow < chunk.height; row++, dataRow++){
			//Check to see if the thread should be stopped
			if(!isRunning) return;
			
			//Check to see if the chunk has been finished by someone else
			if(chunkCollection.isChunkFinished(chunk.starty, chunk.startx)){
				interrupted = true;
				break;
			}
			
			//Get the x coordinate
			double x = problem.ycenter + (problem.ycenterPx-row) / problem.resolution;
			
			for(int col = chunk.startx, dataCol = 0; col < (chunk.startx+chunk.width) 
			&& dataCol < chunk.width; col++, dataCol++){
				//Get the y coordinate
				double y = problem.xcenter + (problem.xcenterPx+col) / problem.resolution;
				//Compute the data for this pixel
				computedFractal[dataRow][dataCol] = newFrac.getPixelColor(x, y).getRGB();
			}
		}
		endTime = System.currentTimeMillis();
		
		//If the calculations were not interrupted,
		//create the new chunk tuple and pass it to the listener
		if(!interrupted){
			myListener.chunkFinished(new ChunkTuple(chunk.problem, chunk.startx, chunk.starty, 
				chunk.height, chunk.width, computedFractal, new Boolean(true), new Long(endTime-startTime)));
			return;
		}else{
			//System.out.println("Chunk calculations interrupted");
		}
	}
	
	/**
	 * Stop running this thread if the user stops working on a problem.
	 */
	public void stopThread(){
		isRunning = false;
	}
}
