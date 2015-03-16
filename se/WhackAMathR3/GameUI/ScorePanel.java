/*
 * ScorePanel.java
 * 
 *  Version:
 *  	$Id: ScorePanel.java,v 1.2 2006/10/09 03:03:06 idp3448 Exp $
 *  
 *  Revisions:
 *  	$Log: ScorePanel.java,v $
 *  	Revision 1.2  2006/10/09 03:03:06  idp3448
 *  	Larger score display
 *  	
 *  	Revision 1.1  2006/10/04 01:26:35  idp3448
 *  	Implemented scoring system and GUI elements.
 *  	
 */

package GameUI;

import java.awt.Color;
import java.awt.Font;

/**
 * Creates a panel that displays the user's total score
 * 
 * @author Ian Paterson
 */
public class ScorePanel extends SmoothLabel {

	public ScorePanel() {
		super("images/score.png");
		setForeground(Color.YELLOW);
		setFont(new Font("Trebuchet MS", Font.BOLD, 22));
	}
	
	public void setScore(int i) {
		setText("Your score is: " + i);
	}
	
}
