/**
 * SolveLogic.java
 * 
 * Version:
 * $Id: SolveLogic.java,v 1.6 2007/05/20 18:55:41 bisrael Exp $
 * 
 * Revisions:
 * $Log: SolveLogic.java,v $
 * Revision 1.6  2007/05/20 18:55:41  bisrael
 * Added comments.
 *
 * Revision 1.5  2007/05/20 07:07:44  bisrael
 * Now stops correctly.
 *
 * Revision 1.4  2007/05/17 09:15:01  bisrael
 * Updated for stats.
 *
 * Revision 1.3  2007/05/17 08:12:39  bisrael
 * Now this class just sets everything up.
 *
 * Revision 1.2  2007/05/16 18:32:47  bisrael
 * Changed where it draws and updates the state of chunks.
 *
 * Revision 1.1  2007/05/16 04:45:42  bisrael
 * Logic for the fractal window.
 * Handles the tuple board, which finds all chunk tuples for a certain problem.
 *
 *
 */
package solvelogic;

import edu.rit.tb.TupleBoard;
import edu.rit.tb.PostListener;

import tuple.ProblemTuple;
import tuple.ChunkTuple;
import gui.FractalDisplayGUI;
import gui.FractalDisplayInterface;


/**
 * This class's job is to set the UI, tuple board, etc up properly
 * and handle what happens when the solver threads finish computing
 * a chunk of the problem.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class SolveLogic implements ChunkListener{
	
	/*
	 * Hold a reference to the problem tuple that this solver is trying
	 * to solve.
	 */
	private ProblemTuple problem;
	
	/*
	 * Reference to the tuple board.
	 */
	private TupleBoard myTupleBoard;
	
	/*
	 * The listener for the fractal window.
	 * Only handles when the window is closed.
	 */
	private FractalDisplayInterface winListener;
	
	/*
	 * Created solver window
	 */
	private FractalDisplayGUI solveUI;
	
	/*
	 * The thread that handles choosing new chunks to solve.
	 * Launches solver threads.
	 */
	private ChunkSelector selectChunk;
	
	/*
	 * Synchronized storage for the chunk tuples.
	 */
	private ChunkCollection chunks;
	
	/*
	 * Maximum number of solver threads per problem.
	 */
	private int numOfThreads;
	
	
	/**
	 * Constructor for the solver logic.
	 * 
	 * @param problem - Reference to the problem tuple, used for matching
	 * @param myTupleBoard - Reference to the running tupleboard
	 * @param winListener - Listener that handles window closing
	 */
	public SolveLogic(ProblemTuple problem, TupleBoard myTupleBoard, FractalDisplayInterface winListener){ 
		this.problem = problem;
		this.myTupleBoard = myTupleBoard;
		this.winListener = winListener;
		
		//Statically set the maximum number of solver threads to run
		this.numOfThreads = 1;
	}

	/**
	 * Creates the gui, sets up the chunk collection, starts the chunk selecting thread,
	 * and adds the proper tupleboard listener.
	 * 
	 * @throws Exception - any exception from the tupleboard
	 */
	public synchronized void init() throws Exception{
		//Set up the ui
		solveUI = new FractalDisplayGUI(problem.problemName, problem.authorName, 
				problem.height.intValue(), problem.width.intValue());
		solveUI.setListener(winListener);
		
		//Create the new holder for chunk tuples
		chunks = new ChunkCollection(this.myTupleBoard, this.solveUI, this.problem.width, this.problem);
		
		//Start the thread that will choose new tuples to work on
		selectChunk = new ChunkSelector(this, numOfThreads, problem, chunks);
		selectChunk.start();
		
		//Create the listener to listen for new session tuples
		myTupleBoard.addListener(new PostListener<ChunkTuple>(){
			public void posted(ChunkTuple tuple){
				chunks.addNewChunk(tuple.starty, tuple.startx, tuple);
			}
		}, new ChunkTuple(problem, null, null, null, null, null, null, null));
		
	}
	
	/* (non-Javadoc)
	 * @see solvelogic.ChunkListener#chunkFinished(tuple.ChunkTuple)
	 */
	public void chunkFinished(ChunkTuple finishedTuple){
		//Tell the collection that the chunk has been finished
		chunks.chunkFinished(finishedTuple.starty, finishedTuple.startx, finishedTuple);
		
		//Tell the chunk selector thread that it can choose another chunk
		selectChunk.reduceSolverCount();
	}
	
	/**
	 * Shutdown this thread properly.
	 */
	public void stopRunning(){
		//Dispose of the window
		solveUI.dispose();
		
		//Tell the chunk collection to remove all chunks
		chunks.removeChunks();
		
		//Stop the chunk selector and any solver threads that
		//are still running
		selectChunk.stopThread();
	}
}
