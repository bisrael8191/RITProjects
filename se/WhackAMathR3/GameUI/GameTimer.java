/*
 * GameTimer.java
 * 
 *  Version: 
 *  	$Id: GameTimer.java,v 1.7 2006/10/08 22:11:10 exl2878 Exp $
 *  
 *  Revisions:
 *  	$Log: GameTimer.java,v $
 *  	Revision 1.7  2006/10/08 22:11:10  exl2878
 *  	Added 50ms sleep interval into the run loop to stop timer from using
 *  	100% of the CPU when paused
 *  	
 *  	Revision 1.6  2006/10/06 02:00:36  idp3448
 *  	Minor changes.
 *  	
 *  	Revision 1.5  2006/10/05 16:24:03  idp3448
 *  	Fixed infinite loop problem.
 *  	
 *  	Revision 1.4  2006/10/02 01:18:42  idp3448
 *  	Cleaned up some stupid stuff which was probably added to the code by Omondo UML
 *  	
 *  	Revision 1.3  2006/09/23 08:07:43  idp3448
 *  	The precursor to the timer planned by the group.
 *  	
 *  	Revision 1.2  2006/09/17 03:16:07  idp3448
 *  	Added functionality for pausing, resuming, and stopping the timer.
 *  	
 *  	Revision 1.1  2006/09/16 19:22:27  idp3448
 *  	Added seconds param to constructor which allows the timer to be run for any number of seconds.  Also added a constant WARNING_TIME which defines a percentage of the time after which the bar will display the warning color.  Improved commenting.
 *  	
 */

package GameUI;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The animated timer display.  Runs for the specified amount of time
 * and switches to a warning color when time is running low.
 * 
 * @author Ian Paterson
 * 
 * TODO The timer needs to report when it is finished
 */
public class GameTimer extends JPanel implements Runnable {

	/**
	 * The ratio of the total time after which the timer bar
	 * will display the warning color
	 */
	private final double WARNING_TIME = .25;
	
	/**
	 * The initial size and location of the timer on the screen
	 */
	private final Rectangle INITIAL_BOUNDS = new Rectangle(53,9,661,20);
	
	/**
	 * The total time that the timer should run
	 */
	private int totalTime;
	
	/**
	 * The time between each decrement of the width which must be
	 * calculated based on the width of the bar and the totalTime
	 */
	private int msInterval;	
	
	/**
	 * The left endcap
	 */
	private JLabel timerbarLeft;
	
	/**
	 * The right endcap
	 */
	private JLabel timerbarRight; 
	
	/**
	 * The expanding center of the bar
	 */
	private BackgroundPanel timerbarBkg;

	/**
	 * Specifies if the time has passed the WARNING_TIME
	 */
	private boolean timeIsRunningOut;
	
	/**
	 * Specifies if the timer is running or paused
	 */
	private volatile boolean paused;
	
	/**
	 * Specifies if the timer is alive or terminated
	 */
	private volatile boolean alive;	
	
	/**
	 * Constructs all of the necessary objects to display the timer
	 * 
	 * @param s the number of seconds to run the clock
	 */
	public GameTimer(int s) {
		// Construct the JPanel with a BorderLayout so the center can expand
		super(new BorderLayout());
	
		// Assign the images to their objects
		timerbarLeft = new JLabel(new ImageIcon("images/timerbar_green_l.png"));		
		timerbarRight = new JLabel(new ImageIcon("images/timerbar_green_r.png"));
		timerbarBkg = new BackgroundPanel(new ImageIcon("images/timerbar_green_bkg.png"));
		
		// Transparent background
		setOpaque(false);
		
		// Starting bounds
		setBounds(INITIAL_BOUNDS);
		
		// Add the images to the JPanel
		add(timerbarLeft, BorderLayout.WEST);
		add(timerbarBkg, BorderLayout.CENTER);
		add(timerbarRight, BorderLayout.EAST);
		
		// Convert the totalTime to milliseconds
		totalTime = s * 1000;
		
		timeIsRunningOut = false;
		paused = false;
		alive = true;
		
		// Determine how long each interval should be to achieve this totalTime
		msInterval = Math.round(totalTime / getWidth());
	}
	
	/**
	 * Resets the timer
	 */
	public void reset() {
		setBounds(INITIAL_BOUNDS);
	}
	
	/**
	 * Pauses execution of the timer
	 */
	public void pause() {
		paused = true;
	}	

	/**
	 * Resumes execution of the timer
	 */
	public void resume() {
		paused = false;
	}
	
	/**
	 * Stops the timer thread
	 */
	public void clear() {
		alive = false;
	}
	
	/**
	 * Determines is time is up
	 * 
	 * @return whether the time is up
	 */
	public boolean isTimeUp() {
		return timerbarBkg.getWidth() == 1;
	}
	
	/**
	 * Runs the timer based on the time given in the constructor
	 */
	public void run() {
		while (alive) {
			try{ 
				if (paused) Thread.sleep( 50 ); 
			} catch ( InterruptedException ie ) {
				System.err.println( "GameTimer: "+ ie.getMessage() );
				ie.printStackTrace();
			}
			while(!paused) {
				// If the bar is completely collapsed, time's up!
				if (isTimeUp()) 
					return;
				
				// If the time is below the warning time, change to the warning
				// colors (timeIsRunningOut ensures that this is only done once)
				if (!timeIsRunningOut && getWidth() * msInterval < WARNING_TIME * totalTime) {
					timerbarLeft.setIcon(new ImageIcon("images/timerbar_red_l.png"));
					timerbarRight.setIcon(new ImageIcon("images/timerbar_red_r.png"));
					timerbarBkg.setBackground(new ImageIcon("images/timerbar_red_bkg.png"));
					
					timeIsRunningOut = true;
				}
				
				// Reduce the width by 1
				Rectangle r = new Rectangle(
						getBounds().x,
						getBounds().y,
						getBounds().width - 1,
						getBounds().height
				);
				
				// Set the new bounds
				setBounds(r);
				
				// Reposition everything
				doLayout();
				
				// Repaint everything
				repaint();			
				
				try {
					// Wait the proper amount of time before shortening the bar again
					Thread.sleep(msInterval);
				} catch (InterruptedException e1) {
					System.err.println( "GameTimer: "+ e1.getMessage() );
					e1.printStackTrace();
				}
			}		
		}
	}	
}
