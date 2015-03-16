/*
 * BonusMole.java
 * 
 * Version: 
 * 		$Id: BonusMole.java,v 1.2 2006/11/06 17:01:01 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: BonusMole.java,v $
 * 		Revision 1.2  2006/11/06 17:01:01  idp3448
 * 		Added 'distinct mole' capabilities so no two similarly-clad moles appear at once!
 * 		
 * 		Revision 1.1  2006/11/06 03:44:05  idp3448
 * 		Added Bonus Round capabilities
 * 		
 */

package GameUI;

import javax.swing.ImageIcon;

/**
 * Creates a Mole with a little character
 * 
 * @author Ian Paterson
 */
public class BonusMole extends Mole {
	
	/**
	 * The number of mole character images
	 */
	public static final int NUM_CHARACTERS = 12;
	
	/**
	 * Generates a Mole with some character
	 */
	public BonusMole() {
		super();
	}
	
	/**
	 * Sets the character of the mole
	 * 
	 * @param moleCharacter the number of the mole's character image
	 */
	public void setCharacter(int moleCharacter) {
		setIcon(new ImageIcon("images/bonus" + moleCharacter + ".png"));
	}

}
