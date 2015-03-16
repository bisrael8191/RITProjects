package gui;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

import javax.imageio.ImageIO;

import java.awt.image.*;
import java.io.IOException;


/**
 * This class is a window that displays a fractal that is being worked on or finished 
 * 
 * @author Jacob
 *
 */


public class FractalDisplayGUI extends JFrame implements ActionListener, WindowListener{

	//The JPanel where the fractal will be displayed, piece by pieve
	ImagePanel fractalView;
	
	//Program bar
	JProgressBar progress;
	
	//The Save Image button
	JButton saveImageButton;
	
	//The image of the fractal
	BufferedImage fractalImage;
	
	//The scroll Pane
	JScrollPane fractalScrolling;
	
	//JLabel that tells how long this problem has been worked on
	JLabel problemTime;
	JLabel totalTime;
	JLabel averageTime;
	
	
	//Holds the start time
	long startTime;

	//The GUI interfcae
	FractalDisplayInterface GUIinterface;
	
	//Hold the problem name
	String problemName;
	
	
	public FractalDisplayGUI(String problemName, String author, int height, int width){
		super(problemName + " -- Created by " + author);
		this.problemName = problemName;
		setVisible(true);
		setLayout(new BorderLayout());
		setSize(700, 500);
		
		fractalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		fractalImage.flush();
		
		Container c = getContentPane();
		
		fractalView = new ImagePanel(width, height);
		fractalView.setBackground(new Color(0,0,0));
		
		fractalScrolling = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		fractalScrolling.setViewportView(fractalView);
		fractalScrolling.setEnabled(true);
		fractalScrolling.setVisible(true);
		
		fractalScrolling.getHorizontalScrollBar().setEnabled(true);
		fractalScrolling.getHorizontalScrollBar().setVisible(true);
		fractalScrolling.getHorizontalScrollBar().setMaximum(width);
		
		fractalScrolling.getVerticalScrollBar().setEnabled(true);
		fractalScrolling.getVerticalScrollBar().setVisible(true);
		fractalScrolling.getVerticalScrollBar().setMinimum(height);
		
		
		c.add(fractalScrolling, BorderLayout.CENTER);
		
		//Create Progress bar
		
		JPanel botPanel = new JPanel();
		
		saveImageButton = new JButton("Save Image");
		saveImageButton.addActionListener(this);
		botPanel.add(saveImageButton);
		
		progress = new JProgressBar();
		progress.setValue(0);
		progress.setStringPainted(true);
		botPanel.add(progress);
		
		JPanel statPanel = new JPanel();
		statPanel.setLayout(new BoxLayout(statPanel,BoxLayout.Y_AXIS));
		problemTime = new JLabel("Time worked On(ms):         " + 0);
		totalTime =   new JLabel("Total Computation Time(ms): " + 0);
		averageTime = new JLabel("Average time per Part(ms):  " + 0);
		
		statPanel.add(problemTime);
		statPanel.add(totalTime);
		statPanel.add(averageTime);
		
		
		
		botPanel.add(statPanel);
		
		
		addWindowListener(this);
		
		
		c.add(botPanel, BorderLayout.SOUTH);
		startTime = System.currentTimeMillis();
		
		repaint();
		
		
		
	}
	
	/*
	 * Action Performed
	 */
	public void actionPerformed(ActionEvent e){
	
		if(e.getActionCommand() == "Save Image"){
			
		    JFileChooser chooser = new JFileChooser();
		    ExtensionFileFilter filter = new ExtensionFileFilter();
		    filter.addExtension("PNG");
		    filter.setDescription("PNG files");
		    chooser.setFileFilter(filter);
		    
		    
		    int returnVal = chooser.showSaveDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       System.out.println("You chose to save the image to the file: " +
		            chooser.getSelectedFile().getName());
		       
		       
		       
		       //Save the image
		       try{
		    	   ImageIO.write(fractalImage, "PNG", chooser.getSelectedFile());
		       }
		       catch(IOException ep){
		    	   
		    	   System.err.println(ep);
		    	   JOptionPane.showMessageDialog(this, "Unable to write to file.", "Error", JOptionPane.ERROR_MESSAGE);
		       }
		    
		       
		    }
		}
	}
	
	
	/**
	 * Close the window
	 */
	public void closeSolverWindow(){
		
	}
	
	/**
	 * A chunk was completed, add this image to the final image
	 */
	public void addImage(BufferedImage im, int x, int y){
		
		Graphics g = fractalImage.getGraphics();
		
		g.drawImage(im,x,y,this);
		
		fractalView.setImage(fractalImage);
		
		repaint();
	}
	
	/**
	 * Updates the statistics
	 */
	public void updateStats(int percentDone, long totalCompTime, long avgChunkTime){
		
		progress.setValue(percentDone);
		progress.setString(percentDone + "%");
		problemTime.setText("Time worked On(ms):         " + (System.currentTimeMillis() - startTime));
		totalTime.setText("Total Computation Time(ms): " + totalCompTime);
		averageTime.setText("Average time per Part(ms):  " + avgChunkTime);

		repaint();
	}
	
	/*
	 * Sets the listener for this window
	 */
	public void setListener(FractalDisplayInterface GUIinterface){
		this.GUIinterface = GUIinterface;
	}
	
	public void windowActivated(WindowEvent e){
	}

	public void windowClosed(WindowEvent e){
	}
		
   
	public void windowClosing(WindowEvent e){
		
		GUIinterface.windowClosed(problemName);
		
	}
    
	public void windowDeactivated(WindowEvent e){
		
	}
    
	public void windowDeiconified(WindowEvent e){
		
	}
  
	public void windowIconified(WindowEvent e){
		
	}
   
	public void windowOpened(WindowEvent e) {
		
	}
	

	
	
}
