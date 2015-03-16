/*
 * MoleEnvironment.java
 * 
 * Version:
 * 		$Id: MoleEnvironment.java,v 1.16 2006/11/07 04:24:07 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: MoleEnvironment.java,v $
 * 		Revision 1.16  2006/11/07 04:24:07  idp3448
 * 		Created Animator class and used it to create a frame-animated instruction screen.
 * 		
 * 		Revision 1.15  2006/11/06 17:01:01  idp3448
 * 		Added 'distinct mole' capabilities so no two similarly-clad moles appear at once!
 * 		
 * 		Revision 1.14  2006/11/06 03:44:05  idp3448
 * 		Added Bonus Round capabilities
 * 		
 * 		Revision 1.13  2006/10/26 01:30:42  idp3448
 * 		Added level support
 * 		
 * 		Revision 1.12  2006/10/19 04:21:25  idp3448
 * 		Fixed occasional problem of tries remaining sign appearing on top of a mole.
 * 		
 * 		Revision 1.11  2006/10/10 05:25:53  exl2878
 * 		Implemented "-s" command line argument to disable in-game sounds
 * 		
 * 		Revision 1.10  2006/10/09 01:22:43  idp3448
 * 		Added try catch when changing the ZOrder of the tries panel to avoid curious illegal argument execeptions
 * 		
 * 		Revision 1.9  2006/10/07 19:53:26  exl2878
 * 		Added sound effect for when the player whacks the tries remaining sign
 * 		
 * 		Revision 1.8  2006/10/05 23:04:33  idp3448
 * 		Added support for moles underneath the hole overlay image.
 * 		Added an instructions screen.
 * 		Changed component ZOrder
 * 		
 * 		Revision 1.7  2006/10/05 19:27:31  idp3448
 * 		Added instructions and listener for mole game
 * 		
 * 		Revision 1.6  2006/10/05 06:49:24  idp3448
 * 		Almost just about ready for R1, all RoundManager and Environment stuff seems to work.
 * 		
 * 		Revision 1.5  2006/10/04 05:19:31  idp3448
 * 		Implemented GameWindow and correct/incorrect overlays.
 * 		
 * 		Revision 1.4  2006/10/04 02:15:40  idp3448
 * 		Added round score (tries remaining) functionality.
 * 		
 * 		Revision 1.3  2006/10/04 01:26:35  idp3448
 * 		Implemented scoring system and GUI elements.
 * 		
 * 		Revision 1.2  2006/10/02 01:19:58  idp3448
 * 		Added the positioning of the holes so Moles can pop up correctly.
 * 		
 * 		Revision 1.1  2006/09/23 08:04:30  idp3448
 * 		Initial revision, this class will define everything which makes a MoleEnvironment unique.
 * 		
 */

package GameUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import GameLogic.GameManager;

/**
 * Creates an environment based on the classic Mole theme.
 * 
 * @author Ian Paterson
 */
public class MoleEnvironment extends Environment implements MouseListener {

	/**
	 * The background image for the MoleEnvironment
	 */
	private static final String BACKGROUND_IMG = "images/background_mole.png";
	
	/**
	 * The overlay which hides moles popping up in the rear 5 holes
	 */
	private HoleOverlay rearOverlay;
	
	/**
	 * The overlay which hides moles popping up in the front hole
	 */
	private HoleOverlay frontOverlay;
	
	/**
	 * The front hole, used for Z-ordering
	 */
	private Hole frontHole;
	
	/**
	 * The bonus moles which have not been shown yet
	 */
	private ArrayList<Integer> bonusMolesAvailable;
	
	/**
	 * Creates the classic Mole Environment
	 * 
	 * @param bkg
	 */
	public MoleEnvironment() {
		// Create the background
		super(new ImageIcon(BACKGROUND_IMG));

		instructions = new Animator("images/instructions_mole_1.png", 2000);
		add(instructions);
		instructions.addMouseListener(this);
		setComponentZOrder(instructions, 1);
		
		(new Thread(instructions)).start();
		
		// Create the rear hole overlay
		rearOverlay = new HoleOverlay("images/overlay_rear_mole.png");
		rearOverlay.setPosition(77, 401);
		add(rearOverlay);
		
		// Create the front hole overlay
		frontOverlay = new HoleOverlay("images/overlay_front_mole.png");
		frontOverlay.setPosition(310, 538);
		add(frontOverlay);		
		
		// Create the tries remaining panel
		triesPanel = new SmoothLabel("images/triesRemaining_mole.png");
		triesPanel.setPosition(120,245);
		
		// Change the font settings for triesPanel
		triesPanel.setForeground(new Color(215,200,170));
		try {
			// Create a new font from the "Crud" font
			Font.createFont(Font.TRUETYPE_FONT, new File("fonts/tries_mole.ttf"));
			
			// Set the font to the newly created "Crud" font
			triesPanel.setFont(new Font("Crud", Font.PLAIN, 44));
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		add(triesPanel);			
		triesPanel.addMouseListener( this );
		// Holes represent the exact pixel location of every mole when it is up
		holes.add(new Hole(91,320));
		holes.add(new Hole(214,275));
		holes.add(new Hole(328,232));
		holes.add(new Hole(442,275));
		holes.add(new Hole(560,320));
		
		// We'll need this hole position later in addCreature
		frontHole = new Hole(332,375);
		holes.add(frontHole);
		
		// Order the overlay of the components to allow room between
		// the front and rear overlays for the frontmost mole
		setComponentZOrder(rearOverlay, 9);
		setComponentZOrder(frontOverlay, 7);
	}

	/**
	 * Adds a Creature in an open hole
	 */
	public Creature addCreature(int n) {		
		Creature c;
		
		// If it's bonus time, use fun BonusMoles
		if (level == BONUS_ROUND) {
			// If we have started a new round, recreate the list
			if (numCreatures == 0)
				resetBonusMolesAvailable();
			
			// Create a new BonusMole
			c = new BonusMole();
			
			c.setSpeed(5);
			
			// Set the character of each mole differently.
			((BonusMole)c).setCharacter(bonusMolesAvailable.remove(0).intValue());
		}
		else {
			// Create a new mole
			c = new Mole();
			
			// Set the speed of the mole according to the level
			c.setSpeed(level);
		}
		
		// Find an empty hole for said new mole
		Hole h = getOpenHole();
		
		// Pretend like the mole is in the hole
		h.occupy();
		
		// Propagate the environment's mouse listener to the mole
		c.addMouseListener(this.getMouseListeners()[0]);
		
		// Position the mole according to the hole
		c.setPosition(h);
		
		// Add the Mole to the Environment
		add(c);
		
		// If the current hole is the frontHole, force a higher ZOrder
		if (h.equals(frontHole)) {
			setComponentZOrder(c, 8);
		}
		
		// Set the mole's number to n
		c.setNumber(n);
		
		numCreatures++;
		
		return c;
	}
	
	/**
	 * Fired when the tries remaining sign is hit
	 */
	public void mouseReleased( MouseEvent e ) {
		if ( GameManager.playSound() && e.getSource().equals( triesPanel ) ) {
			SoundEffect.hitSign();
		}
	}
	
	/**
	 * Sets up the bonusMolesAvailable ArrayList
	 */
	private void resetBonusMolesAvailable() {
		bonusMolesAvailable = new ArrayList<Integer>();
		
		// Add all of the Integers which represent the moles
		for(int i = 1; i <= BonusMole.NUM_CHARACTERS; i++) {
			bonusMolesAvailable.add(new Integer(i));
		}
		
		// Randomize the order
		Collections.shuffle(bonusMolesAvailable);
	}
}
