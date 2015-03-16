import java.io.File;

import org.joone.engine.FullSynapse;
import org.joone.engine.LinearLayer;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.SigmoidLayer;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.FileInputSynapse;
import org.joone.io.FileOutputSynapse;
import org.joone.net.NeuralNet;


/**
 * NNTrain.java
 * 
 * Version:
 * $Id:  $
 * 
 * Revisions:
 * $Log:  $
 *
 */

/**
 * Uses the Joone engine to create a neural network,
 * train it, and run it on the formatted real data.
 * 
 * This class requires the Joone libraries to be in your 
 * classpath. Joone can be downloaded from:
 * http://www.jooneworld.com/download.html
 *
 * @author Brad Israel, Jon Ludwig
 *
 */
public class NNIds implements NeuralNetListener{

	private static String inputData = "FormattedData_Mixed_8000known_824unknown.txt";
	private static String realData = "FormattedData_All.txt";
	private static String finalOutputFile = "finalOutput.txt";
	private NeuralNet nnet = null;
	private LinearLayer input = null;
	private SigmoidLayer hidden1 = null;
	private SigmoidLayer hidden2 = null;
	private SigmoidLayer output = null;
	private FileInputSynapse inputStream = null;
	private FileInputSynapse desiredInput = null;
	private int numDataCols, numInputLines;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NNIds train = new NNIds();
		
		//Change the inputData file to change the training data
		//and change the number of lines in the file, otherwise
		//it will go into an infinite loop and crash.
		train.initNN(inputData, 33, 22866);
		train.train();
		train.runOnData();
	}

	/**
	 * Create the neural network.
	 * 
	 * @param inputFile - Training data file
	 * @param numDataCols - How many columns are considered test data
	 * @param numInputLines - Number of lines in the training data file
	 */
	public void initNN(String inputFile, int numDataCols, int numInputLines) {
		this.numDataCols = numDataCols;
		this.numInputLines = numInputLines;

		/*
		 * First, creates the three Layers
		 */
		input = new LinearLayer();
		hidden1 = new SigmoidLayer();
		hidden2 = new SigmoidLayer();
		output = new SigmoidLayer();
		input.setLayerName("input");
		hidden1.setLayerName("hidden1");
		hidden2.setLayerName("hidden2");
		output.setLayerName("output");

		/* sets their dimensions */
		input.setRows(numDataCols);
		hidden1.setRows(numDataCols/2);
		hidden2.setRows(numDataCols/4);
		output.setRows(1);

		/*
		 * Now create the two Synapses
		 */
		FullSynapse synapse_IH = new FullSynapse(); /* input -> hidden1 conn. */
		FullSynapse synapse_HH = new FullSynapse(); /* hidden1 -> hidden2 conn. */
		FullSynapse synapse_HO = new FullSynapse(); /* hidden2 -> output conn. */

		synapse_IH.setName("IH");
		synapse_IH.setName("HH");
		synapse_HO.setName("HO");
		/*
		 * Connect the input layer whit the hidden layer
		 */
		input.addOutputSynapse(synapse_IH);
		hidden1.addInputSynapse(synapse_IH);
		/*
		 * Connect the hidden layers
		 */
		hidden1.addOutputSynapse(synapse_HH);
		hidden2.addInputSynapse(synapse_HH);
		/*
		 * Connect the hidden layer whit the output layer
		 */
		hidden2.addOutputSynapse(synapse_HO);
		output.addInputSynapse(synapse_HO);

		inputStream = new FileInputSynapse();
		input.addInputSynapse(inputStream);

		desiredInput = new FileInputSynapse();

		TeachingSynapse trainer = new TeachingSynapse();

		/* Setting of the file containing the desired responses,
        provided by a FileInputSynapse */
		trainer.setDesired(desiredInput);

		//Create neural network and add the different layers
		nnet = new NeuralNet();
		nnet.addLayer(input, NeuralNet.INPUT_LAYER);
		nnet.addLayer(hidden1, NeuralNet.HIDDEN_LAYER);
		nnet.addLayer(hidden2, NeuralNet.HIDDEN_LAYER);
		nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
		nnet.setTeacher(trainer);

		/* Connects the Teacher to the last layer of the net */
		output.addOutputSynapse(trainer);


		/* The application registers itself as monitor's listener
		 * so it can receive the notifications of termination from
		 * the net.
		 */
		nnet.addNeuralNetListener(this);

	}

	/**
	 * Train the network using the specified training data.
	 */
	public void train(){
		/* This is the file that contains the input data */
		inputStream.setInputFile(new File(inputData));

		/* The first set of columns contain the input values */
		inputStream.setAdvancedColumnSelector("1-" + numDataCols);

		/* This is the file that contains the answer data, same as input */
		desiredInput.setInputFile(new File(inputData));

		/* The output values are on the last column of the file */
		desiredInput.setAdvancedColumnSelector("" + (numDataCols+1));


		//Gets the Monitor object and set the learning parameters
		Monitor monitor = nnet.getMonitor();
		monitor.setLearningRate(0.5);
		monitor.setMomentum(0.2);

		monitor.setTrainingPatterns(numInputLines); /* # of rows (patterns) contained in the input file */
		monitor.setTotCicles(100); /* How many times the net must be trained on the input patterns */
		monitor.setLearning(true); /* The net must be trained */

		long initms = System.currentTimeMillis();
		nnet.getMonitor().setSingleThreadMode(true);
		nnet.go(true); /* The net starts the training job */
		System.out.println("Total time= "+(System.currentTimeMillis() - initms)+" ms");
		
	}

	/**
	 * Run the trained network on the full set of data
	 * and print the results.
	 */
	public void runOnData(){
		/* This is the file that contains the input data */
		inputStream.setInputFile(new File(realData));

		/* The first set of columns contain the input values */
		inputStream.setAdvancedColumnSelector("1-" + numDataCols);

		//Get monitor and set up network to be in
		//interrogation mode.
		Monitor monitor = nnet.getMonitor();
		monitor.setTrainingPatterns(494021);
		monitor.setTotCicles(1);
		monitor.setLearning(false);

		FileOutputSynapse foutput=new FileOutputSynapse();

		// set the output synapse to write the output of the net
		foutput.setFileName(finalOutputFile);

		if(nnet!=null) {
			nnet.addOutputSynapse(foutput);
			nnet.getMonitor().setSingleThreadMode(true);
			nnet.go(true);
		}
		
		//Print the results
		Results printRes = new Results();
		System.out.println(printRes.calculateResults(finalOutputFile, realData));
	}

	//All methods below are here because this class implements the
	//NeuralNetListener class.
	public void cicleTerminated(NeuralNetEvent e) {
	}

	public void errorChanged(NeuralNetEvent e) {
		Monitor mon = (Monitor)e.getSource();
		if (mon.getCurrentCicle() % 100 == 0)
			System.out.println("Epoch: "+(mon.getTotCicles()-mon.getCurrentCicle())+" RMSE:"+mon.getGlobalError());
	}

	public void netStarted(NeuralNetEvent e) {
		Monitor mon = (Monitor)e.getSource();
		System.out.print("Network started for ");
		if (mon.isLearning())
			System.out.println("training.");
		else
			System.out.println("real data.");
	}

	public void netStopped(NeuralNetEvent e) {
		Monitor mon = (Monitor)e.getSource();
		System.out.println("Network stopped. Last RMSE="+mon.getGlobalError());
	}

	public void netStoppedError(NeuralNetEvent e, String error) {
		System.out.println("Network stopped due the following error: "+error);
	}
}
