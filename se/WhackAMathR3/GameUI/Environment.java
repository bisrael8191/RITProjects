/**
 * Environment.java
 * 
 * Version:
 * 		$Id: Environment.java,v 1.28 2006/11/07 05:00:07 exl2878 Exp $
 * 
 * Revisions:
 * 		$Log: Environment.java,v $
 * 		Revision 1.28  2006/11/07 05:00:07  exl2878
 * 		Levels now start at 1 instead of 0
 * 		
 * 		Revision 1.27  2006/11/07 04:24:07  idp3448
 * 		Created Animator class and used it to create a frame-animated instruction screen.
 * 		
 * 		Revision 1.26  2006/11/06 17:01:01  idp3448
 * 		Added 'distinct mole' capabilities so no two similarly-clad moles appear at once!
 * 		
 * 		Revision 1.25  2006/11/06 03:47:02  idp3448
 * 		Added removeQuitListener
 * 		
 * 		Revision 1.24  2006/11/06 03:44:05  idp3448
 * 		Added Bonus Round capabilities
 * 		
 * 		Revision 1.23  2006/11/06 03:27:58  idp3448
 * 		Added level overlay
 * 		
 * 		Revision 1.22  2006/11/05 04:57:03  idp3448
 * 		Added level overlays
 * 		
 * 		Revision 1.21  2006/10/27 20:58:09  idp3448
 * 		Added addToScore method
 * 		
 * 		Revision 1.20  2006/10/27 20:02:59  idp3448
 * 		Added basic accessor methods for JUnit testing.  grr.
 * 		
 * 		Revision 1.19  2006/10/26 14:01:37  idp3448
 * 		Added simple level calculator
 * 		
 * 		Revision 1.18  2006/10/26 05:25:30  idp3448
 * 		Set lower limits on speed
 * 		
 * 		Revision 1.17  2006/10/26 03:13:11  idp3448
 * 		Removed old GlassPane listeners
 * 		
 * 		Revision 1.16  2006/10/26 01:30:42  idp3448
 * 		Added level support
 * 		
 * 		Revision 1.15  2006/10/21 18:23:41  idp3448
 * 		Added removeCursor() to get rid of the custom cursor
 * 		
 * 		Revision 1.14  2006/10/20 05:04:51  idp3448
 * 		Added setTimerDuration()
 * 		
 * 		Revision 1.13  2006/10/19 04:21:25  idp3448
 * 		Fixed occasional problem of tries remaining sign appearing on top of a mole.
 * 		
 * 		Revision 1.12  2006/10/09 03:03:15  idp3448
 * 		Larger score display
 * 		
 * 		Revision 1.11  2006/10/06 01:57:44  idp3448
 * 		Added quit button functionality
 * 		
 * 		Revision 1.10  2006/10/05 23:21:40  idp3448
 * 		Changed bad method names
 * 		
 * 		Revision 1.9  2006/10/05 23:04:34  idp3448
 * 		Added support for moles underneath the hole overlay image.
 * 		Added an instructions screen.
 * 		Changed component ZOrder
 * 		
 * 		Revision 1.8  2006/10/05 19:27:31  idp3448
 * 		Added instructions and listener for mole game
 * 		
 * 		Revision 1.7  2006/10/05 06:49:24  idp3448
 * 		Almost just about ready for R1, all RoundManager and Environment stuff seems to work.
 * 		
 * 		Revision 1.6  2006/10/04 05:19:31  idp3448
 * 		Implemented GameWindow and correct/incorrect overlays.
 * 		
 * 		Revision 1.5  2006/10/04 02:15:40  idp3448
 * 		Added round score (tries remaining) functionality.
 * 		
 * 		Revision 1.4  2006/10/04 01:26:35  idp3448
 * 		Implemented scoring system and GUI elements.
 * 		
 * 		Revision 1.3  2006/10/04 00:37:05  idp3448
 * 		Added quick scoring mechanism.
 * 		
 * 		Revision 1.2  2006/10/02 01:18:11  idp3448
 * 		Added major changes which make Environment the root UI interaction interface.  Environment now handles the timer, question, and all other UI elements.
 * 		
 * 		Revision 1.1  2006/09/23 08:09:41  idp3448
 * 		Initial revision, manages most of the things an environment needs to know about itself, but still more work needed.
 * 		
 */

package GameUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * An abstract implementation of standard Environmental concerns
 * 
 * @author Ian Paterson
 */
public abstract class Environment extends BackgroundPanel implements MouseListener {

	/**
	 * The maximum time on the timer (level 1 time)
	 */
	private final int BASE_TIMER_DURATION = 30;
	
	/**
	 * The unique set of holes for the environment
	 */
	ArrayList<Hole> holes;
	
	/**
	 * The special cursor used in the game
	 */
	private GameCursor cursor;	
	
	/**
	 * The box containing the question
	 */
	private SmoothLabel questionPanel;
	
	/**
	 * The screen overlay which appears for correct answers
	 */
	private ImageLabel correctOverlay;
	
	/**
	 * The screen overlay which appears for incorrect answers
	 */	
	private ImageLabel incorrectOverlay;
	
	/**
	 * The instructions screen
	 */
	protected Animator instructions;	
	
	/**
	 * The graphical timer for the round
	 */
	private GameTimer timer;	
	
	/**
	 * The duration of the timer
	 */
	private int timerDuration;	
	
	/**
	 * The current level of the game
	 */
	protected int level;
	
	/**
	 * The int indicating the bonus round
	 */
	public final static int BONUS_ROUND = -1;
	
	/**
	 * The current score of the game
	 */
	private int score;	
	
	/**
	 * The number of creatures 
	 */
	protected int numCreatures;
	
	/**
	 * The background behind the timer bar
	 */
	private ImageLabel timerbar;	

	/**
	 * The quit button
	 */
	private ImageLabel quit;	

	/**
	 * The background behind the timer bar
	 */
	protected SmoothLabel triesPanel;	

	/**
	 * Displays the user's score
	 */
	private ScorePanel scorePanel;	
	
	/**
	 * Constructs the background for the scene 
	 * 
	 * @param bkg the background for the environment
	 */
	public Environment(ImageIcon bkg) {
		super(bkg);
		
		holes = new ArrayList<Hole>();
		numCreatures = 0;
		
		// Create the cursor and place it in the environment
		cursor = new GameCursor();
		add(cursor);
		
		timer = new GameTimer(1);
		add(timer);
		
		// Allows user to quit the game
		quit = new ImageLabel("images/button_quit.png");
		quit.setPosition(660, 529);
		add(quit);
		
		// Create the timerbar which goes behind the animated timer
		timerbar = new ImageLabel("images/timerbar.png");
		timerbar.setPosition(50,6);
		add(timerbar);
		
		// Create the question panel with text smoothing
		questionPanel = new SmoothLabel("images/question.png");
		questionPanel.setPosition(35,36);
		
		// Change the font settings
		questionPanel.setForeground(new Color(0,102,255));
		questionPanel.setFont(new Font("Trebuchet MS", Font.PLAIN, 42));
		
		add(questionPanel);
		
		// Create the score panel
		scorePanel = new ScorePanel();
		scorePanel.setPosition(-25,526);
		
		add(scorePanel);
		
		// The message that appears after clicking a correct answer
		correctOverlay = new ImageLabel("images/overlay_correct.png");
		correctOverlay.setVisible(false);
		add(correctOverlay);
		
		// The message that appears after clicking an incorrect answer
		incorrectOverlay = new ImageLabel("images/overlay_incorrect.png");
		incorrectOverlay.setVisible(false);
		add(incorrectOverlay);				
		
		// Order the overlay of the components to ensure that the consistent
		// elements always stay on top
		setComponentZOrder(quit, 2);
		setComponentZOrder(incorrectOverlay, 4);
		setComponentZOrder(correctOverlay, 4);
		setComponentZOrder(scorePanel, 6);
		
		score = 0;
	}
		
	/**
	 * Sets up the cursor once the 
	 */
	public void initializeEnvironment() {
		JFrame win = ((JFrame)getTopLevelAncestor());
		
		// Make the glass pane visible so that it could be used
		win.getGlassPane().setVisible(true);
		
		// Add the necessary listeners
		win.getGlassPane().addMouseMotionListener(cursor);
		win.getGlassPane().addMouseListener(cursor);
		
		// Hide the cursor so the fun one can show through
		Toolkit tk = win.getGlassPane().getToolkit();
		Cursor hiddenCursor = tk.createCustomCursor(tk.getImage("images/blank.png"), new Point(0,0), "");
		win.getGlassPane().setCursor(hiddenCursor);		
	}
	
	/**
	 * Removes the customized cursor and replaces it with the
	 * default.
	 */
	public void removeCursor() {
		JFrame win = ((JFrame)getTopLevelAncestor());
		
		// Make the glass pane invisible
		win.getGlassPane().setVisible(false);
		
		win.getGlassPane().removeMouseListener(cursor);
		win.getGlassPane().removeMouseMotionListener(cursor);
		
		// Remove the mallet
		remove(cursor);
		
		// Reset the cursor to default
		win.getGlassPane().setCursor(Cursor.getDefaultCursor());		
	}
	
	/**
	 * Adds a listener to the quit button
	 * 
	 * @param l adds a listener to determine when the user quits
	 */
	public void addQuitListener(MouseListener l) {
		quit.addMouseListener(l);
	}
	
	/**
	 * Removes the listener from the quit button
	 */
	public void removeQuitListener() {
		quit.removeMouseListener(quit.getMouseListeners()[0]);
	}	
	
	/**
	 * Clears and resets the timer
	 * 
	 * @param s 
	 */
	public void resetTimer() {
		// Stop the timer thread
		timer.clear();
		
		// Remove the timer form the GUI
		remove(timer);
		
		// Make a new timer
		timer = new GameTimer(timerDuration);
		
		// Add it to the GUI
		add(timer);
		
		// Set it at the appropriate ZOrder
		setComponentZOrder(timer, 3);
		
		// Start the timer
		new Thread(timer).start();
	}
	
	/**
	 * Sets the duration of the timer
	 * 
	 * @param s the number of seconds
	 */
	public void setTimerDuration(int s) {
		timerDuration = s;
	}
	
	/**
	 * Sets the current level at which Creatures will be created and shows
	 * the proper level overlay.
	 * 
	 * @param l the level (starting at 0)
	 */
	public void setLevel(int l) {
		// Set the timer to the base time less 5 second for each level
		timerDuration = BASE_TIMER_DURATION - 5 * (l - 1);
		
		// Set a lower limit
		if (timerDuration < 5) {
			timerDuration = 5;
		}
		
		level = l;
		
		// Show the bonus overlay at the proper time
		if (level == BONUS_ROUND)
			instructions.setIcon(new ImageIcon("images/levelbonus.png"));
		// Otherwise show the appropriate level overlay
		else
			instructions.setIcon(new ImageIcon("images/level" + level + ".png"));
		
		instructions.setVisible(true);
				
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		instructions.setVisible(false);
	}
	
	/**
	 * Sets the current question
	 * 
	 * @param q the question in displayable form
	 */
	public void setQuestion(String q) {
		questionPanel.setText(q);
	}
	
	/**
	 * Returns the current question
	 * 
	 * @return the question string
	 */
	public String getQuestion() {
		return questionPanel.getText();
	}	
	
	/**
	 * Sets the number of tries remaining
	 * 
	 * @param i the number of tries remaining
	 */
	public void setTriesRemaining(int i) {
		triesPanel.setText("" + i);
	}
	
	/**
	 * Sets the user's running score
	 * 
	 * @param s the score
	 */
	public void setScore(int s) {
		score = s;		
		scorePanel.setScore(s);
	}
	
	/**
	 * Sets the user's running score
	 * 
	 * @param s the score
	 */
	public void addToScore(int s) {
		score += s;		
		scorePanel.setScore(score);		
	}		
	
	/**
	 * Show or hide the correct overlay
	 */
	public void toggleCorrectOverlay() {
		correctOverlay.setVisible(!correctOverlay.isVisible());
	}
	
	/**
	 * Show or hide the incorrect overlay
	 */
	public void toggleIncorrectOverlay() {
		incorrectOverlay.setVisible(!incorrectOverlay.isVisible());
	}	
	
	/**
	 * Allows access to the GameTimer
	 * 
	 * @return the GameTimer
	 */
	public GameTimer getTimer() {
		return timer;
	}
	
	/**
	 * Returns the game cursor
	 * 
	 * @return the game cursor
	 */
	public GameCursor getGameCursor() {
		return cursor;
	}
	
	/**
	 * Adds a creature to an open hole
	 * 
	 * @param n the number associated with the Creature
	 * @return the new Creature
	 */
	public abstract Creature addCreature(int n);
	
	/**
	 * Makes all holes in the environment vacant
	 */
	public void resetHoles() {
		Iterator<Hole> i = holes.iterator();
		
		// Vacate all holes
		while (i.hasNext()) {
			i.next().vacate();
		}
	
		numCreatures = 0;
	}
	
	/**
	 * Finds and returns a random open hole.
	 * 
	 * @return a random open hole
	 */
	protected Hole getOpenHole() {
		// Shuffle the order of the holes to facilitate random assignment
		Collections.shuffle(holes);
		
		Iterator<Hole> i = holes.iterator();
		
		// Return the first open hole
		while (i.hasNext()) {
			Hole h = i.next();
			
			// If the hole is vacant, occupy and return it
			if (!h.isOccupied()) {
				return h;
			}
		}
		
		// No holes left!
		return null;
	}	
	
	/**
	 * Returns the number of holes
	 * 
	 * @return the number of holes
	 */
	public int getNumHoles() {
		return holes.size();
	}
	
	/**
	 * Returns whether or not the user has hidden the instructions
	 * 
	 * @return whether or not the user has hidden the instructions
	 */
	public boolean showingInstructions() {
		return instructions.isVisible();
	}

	/**
	 * Finalizes the positioning of the environment
	 */
	public void finalizePlacement() {
		try {
			// Make sure the triesPanel stays behind everything
			setComponentZOrder(triesPanel, getComponentCount() - 1);
		} catch (IllegalArgumentException e) {
			setComponentZOrder(triesPanel, getComponentCount() - 2);	
		}
	}
	
	/**
	 * Handles mouse events on specific Environment objects
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getSource().equals(instructions)) {
			instructions.setVisible(false);
			instructions.stop();
		}
		else {
			forward(e);
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	/**
	 * Bubbles events along to the item the cursor is actually over,
	 * creating a GlassPane effect.
	 * 
	 * @param e the event object
	 */
    private void forward(MouseEvent e) {
    	// Hide the cursor, otherwise it is always the deepest component and
    	// therefore overflows the stack by calling the events upon itself 
    	// infinitely
    	setVisible(false);
    	
    	// Get the actual intended target of the user
        Component target = SwingUtilities.getDeepestComponentAt(getParent(), e.getX(), e.getY());
        
        // Show the cursor again
        setVisible(true);
        
        // As long as the user wasn't clicking on something that's nothing...
        if(target != null) {
        	// Convert the coords of the point relative to the cursor icon to
        	// the relative point in the target
            Point p = SwingUtilities.convertPoint(this, e.getPoint(), target);
            
            // Send out the event with the new x and y coordinates
            target.dispatchEvent(new MouseEvent(target,
                                                e.getID(),
                                                e.getWhen(),
                                                e.getModifiers(),
                                                p.x, p.y,
                                                e.getClickCount(),
                                                e.isPopupTrigger()));
        }
    }	
	
}
