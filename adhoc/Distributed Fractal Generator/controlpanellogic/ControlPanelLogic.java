/**
 * ControlPanelLogic.java
 * 
 * Version:
 * $Id: ControlPanelLogic.java,v 1.6 2007/05/20 19:26:53 bisrael Exp $
 * 
 * Revisions:
 * $Log: ControlPanelLogic.java,v $
 * Revision 1.6  2007/05/20 19:26:53  bisrael
 * Added comments.
 *
 * Revision 1.5  2007/05/20 07:04:03  bisrael
 * The stop button now works.
 *
 * Revision 1.4  2007/05/16 18:31:24  bisrael
 * Adds " -- Running" to running problems
 *
 * Revision 1.3  2007/05/16 05:09:10  bisrael
 * Now starts threads to choose chunks to work on and to solve chunks.
 * Handles the finished tuple when the solver thread returns it.
 *
 * Revision 1.2  2007/05/15 05:52:11  bisrael
 * Minor changes.
 *
 * Revision 1.1  2007/05/14 09:07:52  bisrael
 * Created a logic class for the control panel.
 * This class also creates the tuple board that both the problem creator and solver use.
 *
 *
 */
package controlpanellogic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import edu.rit.tb.PostWithdrawListener;
import edu.rit.tb.TupleBoard;

import gui.ControlPanelInter;
import gui.ControlPanelGUI;
import gui.FractalDisplayInterface;
import createlogic.CreateProblem;
import solvelogic.SolveLogic;
import tuple.ProblemTuple;

/**
 * Sets up the control panel ui and handles any user actions.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class ControlPanelLogic implements ControlPanelInter, FractalDisplayInterface {

	/*
	 * Control panel gui
	 */
	private ControlPanelGUI ctrlPnl;
	
	/*
	 * Started tuple board
	 */
	private TupleBoard myTupleBoard;
	
	/*
	 * List of problem tuples
	 */
	private ArrayList<ProblemTuple> problems;
	
	/*
	 * Map of problem names and if they are running
	 */
	private HashMap<String, Boolean> probDisplay;
	
	/*
	 * Map of problem names and references to their
	 * solver logic classes
	 */
	private HashMap<String, SolveLogic> runningSolvers;
	
	/**
	 * Constructor for the control panel.
	 * Doesn't need to do anything.
	 */
	public ControlPanelLogic(){
		
	}
	
	/**
	 * Starts the control panel gui, initializes the tuple board and listeners, and
	 * initializes the storage of problems.
	 * 
	 * @throws Exception - any exception from the tuple board
	 */
	public synchronized void init() throws Exception{
		//Start the control panel gui
		ctrlPnl = new ControlPanelGUI(this);
		
		//Create the tuple board
		myTupleBoard = new TupleBoard();
		
		//Initialize the problem list
		problems = new ArrayList<ProblemTuple>();
		probDisplay = new HashMap<String, Boolean>();
		
		//Initialize the list of running solvers
		runningSolvers = new HashMap<String, SolveLogic>();
		
		//Create the listener to listen for new problem tuples
		// and also listen for when problem tuples are withdrawn
		myTupleBoard.addListener(new PostWithdrawListener<ProblemTuple>(){
			public void posted(ProblemTuple tuple){
				newProblemFound(tuple);
			}
			public void withdrawn(ProblemTuple tuple){
				problemClosed(tuple);
			}
		}, new ProblemTuple(null, null, null, null, null, null, null, null, null, null, null));
	}
	
	/* (non-Javadoc)
	 * @see gui.ControlPanelInter#createNew(java.io.File)
	 */
	public void createNew(File classFile) {
		//Load the create new problem window
		new CreateProblem(classFile, myTupleBoard, probDisplay.keySet()).init();
	}

	/* (non-Javadoc)
	 * @see gui.ControlPanelInter#quit()
	 */
	public void quit() {
		//exit the program
		System.exit(0);
	}

	/* (non-Javadoc)
	 * @see gui.ControlPanelInter#start(int)
	 */
	public void start(String problemName) {
		//Find the problem tuple based on problem name
		ProblemTuple tuple = null;
		for( ProblemTuple temp : problems){
			if(temp.problemName.equals(problemName)){
				tuple = temp;
				break;
			}
		}
		
		if(tuple != null){
			try{
				//Start the solver
				SolveLogic newSolver = new SolveLogic(tuple, myTupleBoard, this);
				newSolver.init();
				
				//Keep track of the running solver windows
				runningSolvers.put(tuple.problemName, newSolver);
				
				//Set the problem to the running state in the ui
				probDisplay.put(tuple.problemName, true);

				//Tell the ui to update the list
				ctrlPnl.setProblemList(probDisplay);
				
				//Post the problem tuple that you are now working on, for persistence
				myTupleBoard.post(tuple);
			}catch(Exception e){
				System.out.println("Error starting to work on a problem: " );
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see gui.ControlPanelInter#stop(int)
	 */
	public void stop(String problemName) {
		//Find the problem tuple based on problem name
		ProblemTuple tuple = null;
		for( ProblemTuple temp : problems){
			if(temp.problemName.equals(problemName)){
				tuple = temp;
				break;
			}
		}
		
		if(tuple != null){
			try{
				//Stop the solver
				runningSolvers.get(problemName).stopRunning();
				
				//Set the state to not running
				probDisplay.put(problemName, false);
				
				//Tell the ui to update the list
				ctrlPnl.setProblemList(probDisplay);
				
				//Withdraw your posted problem tuple
				myTupleBoard.withdraw(tuple);
			}catch(Exception e){
				System.out.println("Error stopping a problem: " + e.getMessage());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see gui.FractalDisplayInterface#windowClosed(java.lang.String)
	 */
	public void windowClosed(String fractalName){
		//Stop running the problem
		stop(fractalName);
	}
	
	/**
	 * Called from the tuple board post listener when
	 * a new problem tuple has been posted.
	 * 
	 * @param tuple - new problem tuple
	 */
	public void newProblemFound(ProblemTuple tuple){
		//Add every problem tuple to the array of tuples
		problems.add(tuple);
		
		//If the problem is a new problem
		// add it to the list that is displayed in the control panel
		// default the working var to false
		if(!probDisplay.containsKey(tuple.problemName)){
			probDisplay.put(tuple.problemName, false);
			
			//Tell the ui to update the list
			ctrlPnl.setProblemList(probDisplay);
		}
	}
	
	/**
	 * Called from the tuple board withdraw listener when
	 * a problem tuple is withdrawn.
	 * 
	 * @param tuple - withdrawn problem tuple
	 */
	public void problemClosed(ProblemTuple tuple){
		//Remove an instance of the withdrawn tuple
		problems.remove(tuple);
			
		//If the array doesn't contain any other instances,
		//remove the problem from the control panel list
		if(!problems.contains(tuple)){
			probDisplay.remove(tuple.problemName);
			ctrlPnl.setProblemList(probDisplay);
		}
	}

}
