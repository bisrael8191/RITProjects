/*
 * RoundManager.java
 * 
 * Version:
 * 		$Id: RoundManager.java,v 1.22 2006/10/27 20:58:26 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: RoundManager.java,v $
 * 		Revision 1.22  2006/10/27 20:58:26  idp3448
 * 		Added scoring right after question is answered
 * 		
 * 		Revision 1.21  2006/10/27 20:02:59  idp3448
 * 		Added basic accessor methods for JUnit testing.  grr.
 * 		
 * 		Revision 1.20  2006/10/26 05:00:14  idp3448
 * 		Removed main test
 * 		
 * 		Revision 1.19  2006/10/26 04:37:20  exl2878
 * 		Updated to use method GameWindow.getInstance()
 * 		
 * 		Revision 1.18  2006/10/20 05:03:47  idp3448
 * 		Changed problems discussed after peer code review.
 * 		
 * 		Revision 1.17  2006/10/19 04:48:31  idp3448
 * 		Added function call to ensure that the tries remaining sign is below the moles
 * 		
 * 		Revision 1.16  2006/10/10 05:25:53  exl2878
 * 		Implemented "-s" command line argument to disable in-game sounds
 * 		
 * 		Revision 1.15  2006/10/09 02:55:56  idp3448
 * 		Corrected scoring problem
 * 		
 * 		Revision 1.14  2006/10/09 02:23:42  idp3448
 * 		Changed scoring to return -1 if the round is not over.
 * 		
 * 		Revision 1.13  2006/10/07 19:52:34  exl2878
 * 		Added sound effects for when the player whacks a mole
 * 		
 * 		Revision 1.12  2006/10/07 17:57:14  exl2878
 * 		Added end() method and quitButtonPressed data member to prematurely
 * 		end the round when the user clicks the quit button
 * 		
 * 		Revision 1.11  2006/10/07 03:12:43  idp3448
 * 		Added correct answer mole
 * 		
 * 		Revision 1.10  2006/10/06 19:46:13  jmf8241
 * 		Ensured score is not added if time runs out.
 * 		
 * 		Revision 1.9  2006/10/05 23:04:34  idp3448
 * 		Added support for moles underneath the hole overlay image.
 * 		Added an instructions screen.
 * 		Changed component ZOrder
 * 		
 * 		Revision 1.8  2006/10/05 06:49:24  idp3448
 * 		Almost just about ready for R1, all RoundManager and Environment stuff seems to work.
 * 		
 * 		Revision 1.7  2006/10/05 01:53:17  idp3448
 * 		Temporary fix for removing antiquated mouse listeners
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
 * 		Revision 1.3  2006/10/04 00:37:52  idp3448
 * 		*** empty log message ***
 * 		
 * 		Revision 1.2  2006/10/04 00:37:05  idp3448
 * 		Added quick scoring mechanism.
 * 		
 * 		Revision 1.1  2006/10/04 00:05:01  exl2878
 * 		Moved to GameLogic package
 * 		
 * 		Revision 1.3  2006/10/02 01:31:58  idp3448
 * 		Added a short pause between mole poppings
 * 		
 * 		Revision 1.2  2006/10/02 01:20:53  idp3448
 * 		Huge changes include a unit test main method and interaction with the AnswerGenerator and Question classes.  Does not yet respond to mouse clicks.
 * 		
 * 		Revision 1.1  2006/09/23 08:03:52  idp3448
 * 		Initial revision will compile without errors once AnswerGenerator and Question classes have been created.  Minimal functionality creates, places, and starts one set of Creatures.
 * 		
 */

package GameLogic;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import GameUI.Creature;
import GameUI.Environment;
import GameUI.SoundEffect;

/**
 * Takes care of the progression of a single round including
 * responding to mouse events, keeping track of time and incorrect
 * count, and coordinating Creatures.
 * 
 * @author Ian Paterson
 */
public class RoundManager extends Thread implements MouseListener {

	/**
	 * The number of tries allowed per round
	 */
	private final static int DEFAULT_TRIES = 3;
	
	/**
	 * Stores the correct answer for this round
	 */
	private int correct;
	
	/**
	 * Stores the incorrect answers for this round
	 */
	private ArrayList<Integer> incorrect;
	
	/**
	 * Stores the correct answer for this round
	 */
	private Creature correctCreature;
	
	/**
	 * Stores the incorrect answers for this round
	 */
	private ArrayList<Creature> incorrectCreatures;
		
	/**
	 * Manages the Environment-specific features
	 */
	private Environment environment;	
	
	/**
	 * Stores the user's score for this round
	 */
	private volatile int score;
	
	/**
	 * Stores whether an answer has been selected
	 */
	private boolean answerSelected;	
	
	/**
	 * Stores whether the round is over or not
	 */
	private boolean roundComplete; 
	
	/**
	 * Indicates whether the quit button was pressed.  Its value is altered by the end() method,
	 * which is called by GameManager.
	 */
	private volatile boolean quitButtonPressed;
	
	/**
	 * Configures and begins the round
	 * 
	 * @param q the question for this round
	 */
	public RoundManager(Question q, Environment e) {
	
		// Get the correct answer from the question
		correct = AnswerGenerator.getCorrectAnswer(q);
		
		// Get the incorrect answer array from the question
		incorrect = AnswerGenerator.getIncorrectAnswers(q, correct, 1);
		
		// Initialize a new array of incorrect Creatures
		incorrectCreatures = new ArrayList<Creature>();
		
		// Set the default tries
		score = DEFAULT_TRIES;
		
		// The round is not complete
		roundComplete = false;
		
		// The quit button has not been pressed
		quitButtonPressed = false;
		
		// Assignt the environment
		environment = e;	
		
		// Set the question display
		environment.setQuestion(q.toString());
		
		// Set the tries remaining display
		environment.setTriesRemaining(score);
		
		// TODO Remove MouseListener fix once it is handled by GameManager
		if (environment.getMouseListeners().length > 0)
			environment.removeMouseListener(environment.getMouseListeners()[0]);

		environment.addMouseListener(this);
	}

	/**
	 * Causes all visible Creatures to hurry up
	 */
	public void hurryAllCreatures() {
		// Hide the correct answer mole
		correctCreature.hurryUp();
		
		//  Make any remaining Creatures hurry up
		Iterator<Creature> i = incorrectCreatures.iterator();
		while (i.hasNext()) {
			i.next().hurryUp();
		}
	}
	
	/**
	 * Decreases the score by one if it is above 0
	 */
	private void lowerScore() {
		// Don't let the score become negative
		if (score > 0) {
			score--;
			
			// Update the displayed tries remaining
			environment.setTriesRemaining(score);
		}
	}
	
	/**
	 * Returns the score for the round if the round is complete
	 * 
	 * @return the score for the round
	 */
	public int getScore() {
		// Only return the score if the round is complete
		if (roundComplete) 
			return score;
		else
			return -1;
	}	
	
	/**
	 * Manages the progression of the round
	 */
	public void run() {
		// Reset and start the timer
		environment.resetTimer();		
		
		// Continue until the quit button is pressed, the player loses or the time runs up
		while (!quitButtonPressed && score > 0 && !environment.getTimer().isTimeUp()) {
			// Reset to no answer selected
			answerSelected = false;
			
			// Generate Creatures and assign them to holes
			placeCreatures();				
			
			// Make a new Thread for the correctCreature to run in
			Thread correctThread = new Thread(correctCreature);
			
			// Starts the correct creature's animation
			correctThread.start();
			
			//  Start the incorrect creatures next
			Iterator<Creature> i = incorrectCreatures.iterator();
			while (i.hasNext()) {
				new Thread(i.next()).start();
			}
						
			// Watch out for interruptions while waiting on Threads
			try {
				// Wait until the correctCreature is hidden
				correctThread.join();
				
				// If any other answer was selected, it was incorrect
				if (answerSelected || environment.getTimer().isTimeUp()) {
					
					// If it was hidden because it was hit, correct!
					if (correctCreature.isHit()) {
						// Show the correct overlay
						environment.toggleCorrectOverlay();
						
						// Pause the timer while the overlay is up
						environment.getTimer().pause();
						
						roundComplete = true;
						
						environment.addToScore(getScore());

						// Displays the actual answer
						showCorrectAnswer();
						
						sleep(2000);
						
						hideCorrectAnswer();
						
						// Hide the correct overlay
						environment.toggleCorrectOverlay();
						
						// Terminate the round
						return;
					}
					else {
						// Show the incorrect overlay
						environment.toggleIncorrectOverlay();
						
						// If they haven't lost, only display the overlay
						// for half a second
						if (score > 0 && !environment.getTimer().isTimeUp()) {
							sleep(500);
						}
						// Otherwise pause the timer and show the overlay
						// for two seconds
						else {
							environment.getTimer().pause();
							
							roundComplete = true;
							
							environment.addToScore(getScore());
							
							// Displays the actual answer
							showCorrectAnswer();
							
							sleep(2000);
							
							hideCorrectAnswer();
						}
						
						// Hide the correct overlay 
						environment.toggleIncorrectOverlay();
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            if( environment.getTimer().isTimeUp() ){
                score  = 0;
            }
            
            answerSelected = false;
		}
	}	
		
	/**
	 * Shows the correct Creature after the round has ended
	 */
	private void showCorrectAnswer() {		
		// Position the creature at bottom center of the window
		correctCreature.setPosition(environment.getWidth() / 2 - correctCreature.getWidth() / 2,
					  				environment.getHeight() - correctCreature.getHeight() + 20);
		
		// Add the creature to the top level of the environment
		environment.add(correctCreature);
		environment.setComponentZOrder(correctCreature, 2);
		
		// Show the creature
		correctCreature.setVisible(true);
		
		// Repaint the creature so it stays on top
		correctCreature.repaint();
	}
	
	/**
	 * Hides the correct Creature before starting a new round
	 */
	private void hideCorrectAnswer() {
		environment.remove(correctCreature);
	}	
	
	/**
	 * Places the Creatures in the environment so that the correct creature
	 * is always displayed and the incorrect creatures alternate
	 */
	private void placeCreatures() {
		// Clear any previous incorrectCreatures
		incorrectCreatures.clear();
		
		// Vacate all the holes
		environment.resetHoles();
		
		// Add the correctCreature in any open hole
		correctCreature = environment.addCreature(correct);
		
		// Mix up the order of the answers
		Collections.shuffle(incorrect);
		
		//  Make any remaining Creatures hurry up
		Iterator<Integer> i = incorrect.iterator();
		int j = 0;		
		
		// Generate between 2 and 5 incorrect Creatures
		int numCreatures = (int) (Math.round(Math.random() * 3) + 2);
		
		// Add incorrectCreatures up to numCreatures or until all holes are occupied
		while (i.hasNext() && j < environment.getNumHoles() && j < numCreatures) {
			incorrectCreatures.add(environment.addCreature(i.next()));
			
			j++;
		}		
		
		environment.finalizePlacement();
	}
	
	/**
	 * Immediately ends the round.  This method is used by GameManager when
	 * the user clicks on the quit button.
	 */
	public void end() {
		quitButtonPressed = true;
	}
	
	/**
	 * Fired when the user clicks on a Creature.  This formulates the
	 * user's score for the round based on the number of incorrect guesses.
	 * 
	 * TODO: Solidify scoring 
	 */
	public void mouseReleased(MouseEvent e) {
		// If a Creature was clicked, respond!
		if (!answerSelected && e.getSource().getClass().isInstance(correctCreature)) {
			// An answer has been selected (as opposed to not clicking anything)
			answerSelected = true;
			// If it was not correct, lower the score
			if (!e.getSource().equals(correctCreature)) {
				if ( GameManager.playSound() ) SoundEffect.hitWrongMole();
				lowerScore();
			} else {
				if ( GameManager.playSound() ) SoundEffect.hitCorrectMole();
			}
	
			// Hit the targeted Creature so it knows to go down
			((Creature)e.getSource()).hit();
			
			// Hide all of the other Creatures, too
			hurryAllCreatures();
		}
	}

	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	
}
