/*
 * Animator.java
 * 
 * Version:
 * 		$Id: Animator.java,v 1.2 2006/11/07 04:27:10 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: Animator.java,v $
 * 		Revision 1.2  2006/11/07 04:27:10  idp3448
 * 		Created class to animate ImageLabels with a sequence of images.
 * 		
 */

package GameUI;

import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.swing.ImageIcon;

/**
 * Animates a sequence of frames based on a specified constant delay
 * 
 * @author Ian Paterson
 */
public class Animator extends ImageLabel implements Runnable {

	/**
	 * The current image path without frame number or extension
	 */
	private String image;
	
	/**
	 * The current extension
	 */
	private String extension;	
	
	/**
	 * The current frame
	 */
	private int frame;
	
	/**
	 * The delay between frames in ms
	 */
	private int delay;
	
	/**
	 * Allows the animation to be stopped
	 */
	private volatile boolean done;
	
	/**
	 * Create a new Animator with a starting image and a delay
	 * 
	 * @param img the image filepath
	 * @param ms the millisecond delay between frames
	 */
	public Animator(String img, int ms) {
		super(img);
		
		// Get the path and image name
		image = img.replaceAll("\\d+\\..*$", "");
		
		// Get the digit
		frame = Integer.parseInt(img.replaceAll("[^\\d]", ""));
		
		// Get the extension
		extension = img.replaceAll("^.*\\.", ".");
		
		delay = ms;
		done = false;
	}
	
	/**
	 * Stops the animation
	 */
	public void stop() {
		done = true;
	}

	/**
	 * Animates the sequence
	 */
	public void run() {
		// Run until no more images remain
		while (!done) {
			try {
				// Just check if the file exists
				new FileReader(image + frame + extension);
				
				// Set the new image
				setIcon(new ImageIcon(image + frame + extension));
				
				frame++;
				
				// Sleep for the specified delay
				(new Thread(this)).sleep(delay);
			} catch (FileNotFoundException e) {
				done = true;
				
				// Set the new image
				setIcon(new ImageIcon(image + (frame - 1) + extension));
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

}
