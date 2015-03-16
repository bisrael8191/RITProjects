/**
 * CreateProblem.java
 * 
 * Version:
 * $Id: CreateProblem.java,v 1.9 2007/05/20 19:44:32 bisrael Exp $
 * 
 * Revisions:
 * $Log: CreateProblem.java,v $
 * Revision 1.9  2007/05/20 19:44:32  bisrael
 * Added comments.
 *
 * Revision 1.8  2007/05/20 07:04:54  bisrael
 * Does some checking to make sure that the problem is not named the same as an existing problem.
 *
 * Revision 1.7  2007/05/17 14:35:39  jhays
 * Fixed small bug with chunk splitting.
 *
 * Revision 1.6  2007/05/17 09:16:55  bisrael
 * Updated for stats.
 *
 * Revision 1.5  2007/05/17 08:07:11  bisrael
 * Statically sets how big the chunks should be.
 *
 * Revision 1.4  2007/05/16 18:31:45  bisrael
 * Now splits up the problem into chunks
 *
 * Revision 1.3  2007/05/16 05:04:56  bisrael
 * Now creates a single chunk.
 *
 * Revision 1.2  2007/05/15 05:52:34  bisrael
 * Minor changes.
 *
 * Revision 1.1  2007/05/14 09:09:40  bisrael
 * This class is now the main logic for creating new problems.
 * Will be able to load new classes, take user input, validate it, and then send the
 * proper tuples to the tuple board.
 *
 *
 */
package createlogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Set;

import javax.swing.JOptionPane;

import edu.rit.tb.TupleBoard;

import tuple.ProblemTuple;
import tuple.ChunkTuple;
import gui.CreateProblemGUI;
import gui.CreateProblemInter;

/**
 * Loads any class file that implements tools.FractalImplementation.
 * Sets up the problem and picture parameters, then creates the 
 *  needed tuples.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class CreateProblem implements CreateProblemInter {
	
	/*
	 * File pointer to the class
	 */
	private File newClass = null;
	
	/*
	 * Class loader to load the file into the VM
	 */
	private URLClassLoader localLoader = null;
	
	/*
	 * Name of the class
	 */
	private String name = null;
	
	/*
	 * Class object
	 */
	private Class cl = null;
	
	/*
	 * Class constructors
	 */
	private Constructor[] mainConst = null;
	
	/*
	 * Class constructor parameter types
	 */
	private Class[] params = null;
	
	/*
	 * Entered class constructor arguments
	 */
	private Object[] constArgs = null;
	
	/*
	 * Reference to the tuple board
	 */
	private TupleBoard myTupleBoard;
	
	/*
	 * Create problem GUI
	 */
	private CreateProblemGUI createUI;
	
	/*
	 * Names of problems that are already taken
	 */
	private Set<String> takenNames;
	
	/**
	 * Constructor for the create problem logic.
	 * 
	 * @param newClass - File pointer to the class
	 * @param myTupleBoard - tuple board
	 * @param takenNames - list of taken problem names
	 */
	public CreateProblem(File newClass, TupleBoard myTupleBoard, Set<String> takenNames){
		this.myTupleBoard = myTupleBoard;
		this.takenNames = takenNames;
		
		//Remove the .class file extension
		this.newClass = newClass;
		name = this.newClass.getName().replace(".class", "");
	}
	
	/**
	 * Uses reflections to get information about the loaded class
	 * and then starts the control panel gui.
	 */
	public void init(){
		//Load the class file
		if(loadClass()){
			//Get the types of the constructor args
			if(getConstructorArgs()){
				//Get the readable names of the constructor arguments
				String[] typeNames = new String[params.length];
				for(int i = 0; i < params.length; i++){
					typeNames[i] = params[i].getSimpleName();
				}
				
				//Set up the UI
				createUI = new CreateProblemGUI(this, typeNames);
			}else{
				JOptionPane.showMessageDialog(createUI, 
						"Class could not be loaded, please try again", 
						"", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}else{
			//Show popup error and close this window
			JOptionPane.showMessageDialog(createUI, 
					"Class could not be loaded, please try again", 
					"", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	/* (non-Javadoc)
	 * @see gui.CreateProblemInter#createProblem(java.lang.String, java.lang.String, int, int, double, double, double, java.lang.String[])
	 */
	public void createProblem(String name, String author, int height, int width, 
			double xcenter, double ycenter, double resolution, String[] params) {
		//Check if problem name has already been taken
		if(takenNames.contains(name)){
			JOptionPane.showMessageDialog(createUI, 
					"Problem name already exists, please choose a different name", 
					"", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//Check the constructor arguments
		// try and create an instance of the class to make sure that they work

		ProblemTuple newProb = new ProblemTuple(name, author, cl, (Object[])params, new Integer(height), 
				new Integer(width), new Double(xcenter), new Double(ycenter), 
				new Double(-(width-1)/2), new Double((height-1)/2), new Double(resolution));
		myTupleBoard.post(newProb);
		
		//Create the chunk tuples
		postChunks(newProb);
	}

	/**
	 * Split up the problem and post the needed chunk tuples.
	 * 
	 * @param newProb - new problem tuple
	 */
	private void postChunks(ProblemTuple newProb){
		//Statically set the maximum number of pixels per chunk
		int numOfPxPerChunk = 20000;
		
		//Get the number of chunks
		int num = (newProb.width.intValue()*newProb.height.intValue())/numOfPxPerChunk;
		if(num == 0)
			num = 1;
		
		//Loops through the 2d image plane to figure out where to split
		//up the problem
		double wSize = newProb.width/num;
		for(double x1 = 0; x1 < newProb.width; x1 += wSize){
	
			double x2;
				
			if(x1 + wSize > newProb.width)
				x2 = newProb.width;
			else
				x2 = x1 + wSize;
			
			//Create the new chunk
			ChunkTuple newChunk = new ChunkTuple(newProb, new Integer((int)Math.round(x1)), new Integer(0),
					newProb.height, new Integer((int)Math.round(x2)-(int)Math.round(x1)), 
					null, new Boolean(false), new Long(0));
			//Post the chunk
			myTupleBoard.post(newChunk);
		}
	}
	
	/**
	 * Load the class into the VM using the URLClassLoader.
	 * 
	 * @return true if the class was loaded properly, false if it failed
	 */
	private boolean loadClass(){
		//Start the local loader
		try{
			localLoader = new URLClassLoader(new URL[]{newClass.getParentFile().toURI().toURL()});
		}catch (MalformedURLException mue){
			mue.printStackTrace();
			return false; //Show popup error and close this window
		}
		
		//Try to load the class
		try{
			cl = localLoader.loadClass(name);
		}catch(ClassNotFoundException cnfe){
			cnfe.printStackTrace();
			return false; //Show popup error and close this window
		}
		
		//Check for FractalImplementation interface
		//Just so that a random class isn't loaded
		Type[] implInter = cl.getGenericInterfaces();
		boolean interCheck = false;
		int i = 0;
		while(!interCheck && i < implInter.length){
			if(implInter[i].toString().contains("FractalImplementation")) interCheck = true;
			i++;
		}
		if( !interCheck ){
			System.err.println("Class must implement FractalImplementation");
			return false; //Show popup error and close this window
		}
		
		//Check to make sure that the class isn't null
		if(cl != null) return true;
		else return false;
	}
	
	//Use reflections to get the declared constructor of the loaded class
	// and allow the user to enter the correct arguments
	/**
	 * Gets the types and number of constructor arguments.
	 * 
	 * @return true if the arguments were found properly, false if any errors occur
	 */
	private boolean getConstructorArgs(){
		//Get the declared constructors
		mainConst = cl.getDeclaredConstructors();
		
		//There should be only one constructor
		if(mainConst.length == 1){
			params = mainConst[0].getParameterTypes();
			constArgs = new Object[params.length];	//Initialize the object array
		} else {
			System.err.println("Please load a class with only one implemented constructor");
			return false; //Show popup and close window
		}
		return true;
	}
}
