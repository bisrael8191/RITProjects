/*
 * GameCursor.java
 * 
 * Version:
 * 		$Id: GameCursor.java,v 1.4 2006/10/05 23:04:34 idp3448 Exp $
 * 
 * Revisions:
 * 		$Log: GameCursor.java,v $
 * 		Revision 1.4  2006/10/05 23:04:34  idp3448
 * 		Added support for moles underneath the hole overlay image.
 * 		Added an instructions screen.
 * 		Changed component ZOrder
 * 		
 * 		Revision 1.3  2006/10/05 06:49:24  idp3448
 * 		Almost just about ready for R1, all RoundManager and Environment stuff seems to work.
 * 		
 * 		Revision 1.2  2006/09/23 08:08:56  idp3448
 * 		Implemented constant values.
 * 		
 * 		Revision 1.1  2006/09/16 18:52:51  idp3448
 * 		Added automatic cursor sizing and event bubbling to pass click events on to the actual components that the user intended to click.  Improved commenting.
 * 		
 */

package GameUI;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/**
 * A fun homemade cursor class since Java's cursor-setting abilities are
 * awful and would not provide the needed functionality to make the cursor
 * look super awesome.
 * 
 * Also functions as a GlassPane by bubbling events down to objects below it.
 * 
 * @author Ian Paterson
 */
public class GameCursor extends BackgroundPanel implements MouseMotionListener, MouseListener {
	
	/**
	 * The cursor that displays when the mouse button is not depressed
	 */
	private final static ImageIcon CURSOR_WAITING = new ImageIcon("images/cursor_waiting.png");
	
	/**
	 * The cursor that displays when the user is clicking or dragging
	 */
	private final static ImageIcon CURSOR_CLICKING = new ImageIcon("images/cursor_ready.png");
	
	/**
	 * The amount by which the image must be offset in the x direction in
	 * order to place the actual pointer tip in a logical position with respect
	 * to the image.
	 */
	private final int OFFSET_X = -10;
	
	/**
	 * The amount by which the image must be offset in the y direction in
	 * order to place the actual pointer tip in a logical position with respect
	 * to the image.
	 */	
	private final int OFFSET_Y = -54;	
	
	/**
	 * The current image used for the cursor
	 */
	private ImageIcon cursor;
	
	/**
	 * Generates the cursor, sets the bounds, and adds the listeners
	 *
	 */
	public GameCursor() {
		// Construct the JLabel
		super(CURSOR_WAITING);
		
		cursor = CURSOR_WAITING;

		// Listen for clicks
		addMouseListener(this);
		
		// Set the default bounds with the w and h of the image 
		setBounds(-200, -200, cursor.getIconWidth(), cursor.getIconHeight());
	}
	
	/**
	 * Synonym for mouseMoved in case the user doesn't like moving the mouse
	 * without pressing the button
	 */
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}
	
	/**
	 * Moves the cursor image relative to the cursor itself
	 */
	public void mouseMoved(MouseEvent e) {
		// Changes only the x and y components
		setBounds(e.getX() + OFFSET_X, e.getY() + OFFSET_Y, getBounds().width, getBounds().height);
	}	
	
	/**
	 * When the mouse button goes down, display the clicking cursor then 
	 * bubble down the event
	 */
    public void mousePressed(MouseEvent e)  {
    	// Changes and updates the background
		setBackground(CURSOR_CLICKING);
		
		cursor = CURSOR_CLICKING;
		
		// Bubble down the event
		forward(e); 
	}
    
	/**
	 * When the mouse button goes down, display the waiting cursor then 
	 * bubble down the event
	 */    
    public void mouseReleased(MouseEvent e) {
    	setBackground(CURSOR_WAITING);
    	
    	cursor = CURSOR_WAITING;
    	
    	forward(e); 
    }
    
    // Bubble down the remaining events
    public void mouseClicked(MouseEvent e)  { forward(e); }
	public void mouseEntered(MouseEvent e) {  }
	public void mouseExited(MouseEvent e) {  }
	
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
            p = e.getPoint();
            
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
