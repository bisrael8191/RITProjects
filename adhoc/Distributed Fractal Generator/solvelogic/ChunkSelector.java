/**
 * ChunkSelector.java
 * 
 * Version:
 * $Id: ChunkSelector.java,v 1.6 2007/05/20 18:55:11 bisrael Exp $
 * 
 * Revisions:
 * $Log: ChunkSelector.java,v $
 * Revision 1.6  2007/05/20 18:55:11  bisrael
 * Added comments.
 *
 * Revision 1.5  2007/05/20 07:07:14  bisrael
 * Now stops correctly.
 *
 * Revision 1.4  2007/05/17 09:15:37  bisrael
 * Checks to make sure that the new chunk isnt null.
 *
 * Revision 1.3  2007/05/17 08:10:12  bisrael
 * Uses the ChunkCollection to do what it was trying to do before, but now it works and is synchronized.
 *
 * Revision 1.2  2007/05/16 18:32:24  bisrael
 * Tries to synchronize the array of chunks, not working properly yet.
 *
 * Revision 1.1  2007/05/16 05:00:22  bisrael
 * Created a thread that will continually run until stopped. It's job is to select new chunks to work on and start/keep track of the solver threads.
 *
 *
 */
package solvelogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import tuple.ChunkTuple;
import tuple.ProblemTuple;
import tools.FractalImplementation;

/**
 * This is a thread that will be running as long as the problem
 * hasn't been stopped. It chooses which chunk of the problem to 
 * work on and then sends the chunk to one or more solver threads.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class ChunkSelector extends Thread {

	/*
	 * Variable used to tell this thread whether or not
	 * to keep running
	 */
	private boolean isRunning;
	
	/*
	 * Reference to the synchronized chunk collection
	 */
	private ChunkCollection chunks;
	
	/*
	 * Reference to the listener that handles data from the solver threads
	 */
	private ChunkListener myListener;
	
	/*
	 * Maximum number of solver threads to run
	 */
	private int numOfThreads;
	
	/*
	 * Number of solver threads running
	 */
	private int solversRunning;
	
	/*
	 * Array of solver threads
	 */
	private ArrayList<SolverThread> solvers;
	
	/*
	 * Reference to the problem tuple
	 */
	private ProblemTuple problem;
	
	/*
	 * Reference to the class that implements FractalImplementation
	 */
	private FractalImplementation newFrac;
	
	/**
	 * Constructor for the chunk selector thread.
	 * 
	 * @param listener - listener for returned data
	 * @param numOfThreads - maximum number of threads
	 * @param problem - problem tuple
	 * @param chunks - reference to a chunk collection
	 */
	public ChunkSelector(ChunkListener listener, int numOfThreads, ProblemTuple problem, 
			ChunkCollection chunks){
		isRunning = true;
		myListener = listener;
		this.numOfThreads = numOfThreads;
		solversRunning = 0;
		this.problem = problem;
		this.chunks = chunks;
		solvers = new ArrayList<SolverThread>();
		
		//Get an instance of the class, cast to a FractalImplementation
		newFrac = getFractalClass();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 * 
	 * Continues to run until there are no more chunks or the
	 * user decides to stop working on a problem.
	 */
	public void run(){
		
		while(isRunning){
			//Wait for a little bit
			try{
				this.sleep(100);
			}catch(InterruptedException ie){
				ie.printStackTrace();
			}
			
			//If there are still uncomputed tuples
			if(chunks.percentOfCompletedChunks() != 100){
				//If there are not too many threads running
				if(solversRunning < numOfThreads){
					//Get a random chunk and send it to a solver thread
					ChunkTuple unsolved = chunks.getRandomUnfinishedChunk();
					if(unsolved != null){
						//Start the solver thread
						SolverThread newSolver = new SolverThread(myListener, problem, 
								newFrac, unsolved, chunks);
						//Keep track of the running solver threads
						solvers.add(newSolver);
						newSolver.start();
						solversRunning++;
					}
				}
			}
		}
	}
	
	/**
	 * Called when the user wants to stop working on the problem.
	 * Shuts itself down and any solver threads that are still running.
	 */
	public void stopThread(){
		for(int i = 0; i < solvers.size(); i++){
			solvers.get(i).stopThread();
		}
		isRunning = false;
	}
	
	/**
	 * Called when data has been returned from a solver thread.
	 * Allows the run method to choose another chunk to work on.
	 */
	public void reduceSolverCount(){
		if(!(solversRunning <= 0)) solversRunning--;
	}
	
	/**
	 * Get an instance of the class inside of the problem tuple,
	 * cast to a FractalImplementation.
	 * 
	 * @return - the proper FractalImplementation class
	 */
	private FractalImplementation getFractalClass(){
		FractalImplementation ret = null;
		
		Object probInstance = null;
		
		//Find the correct constructor
		Constructor[] mainConst = problem.problemClass.getDeclaredConstructors();
		
		if( mainConst.length == 1){
			//Try and create a new instance of the problem
			//and cast it to a FractalImplementation
			try{
				probInstance = mainConst[0].newInstance(problem.constArgs);
				ret = (FractalImplementation)probInstance;
			}catch(InvocationTargetException ite){
				ite.printStackTrace();
			}catch(IllegalAccessException iae){
				iae.printStackTrace();
			}catch (InstantiationException ie){
				ie.printStackTrace();
			}catch(ClassCastException cce){
				cce.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return ret;
	}
}
