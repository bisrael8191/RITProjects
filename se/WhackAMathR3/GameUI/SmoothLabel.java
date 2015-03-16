/*
 * SmoothLabel.java
 * 
 * Version:
 * 		$Id: SmoothLabel.java,v 1.2 2006/09/17 16:26:28 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: SmoothLabel.java,v $
 * 		Revision 1.2  2006/09/17 16:26:28  idp3448
 * 		Updated member functions.
 * 		
 * 		Revision 1.1  2006/09/16 19:25:59  idp3448
 * 		Improved commenting.
 * 		
 */

package GameUI;
 
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A basic JLabel with text antialiasing added
 * 
 * @author Ian Paterson
 */
public class SmoothLabel extends ImageLabel {
	
	/**
	 * Constructor with ImageIcon argument
	 * 
	 * @param img the ImageIcon to use on the label
	 */
	public SmoothLabel(String img) {
		super(img);
	}
	
	/**
	 * Ensures that text is painted with antialiasing enabled
	 */
    protected void paintComponent(Graphics g) {
    	// Convert to Graphics2D to allow antialising 
        Graphics2D g2 = (Graphics2D)g;
        
        // Antialias it
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        super.paintComponent(g);
    }
}
