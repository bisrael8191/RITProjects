package GameUI;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;

public class HoleOverlay extends ImageLabel implements MouseListener {

	public HoleOverlay(String img) {
		super(img);
		
		addMouseListener(this);
	}

	/**
	 * Handles mouse events on specific Environment objects
	 */
	public void mouseReleased(MouseEvent e) {
		forward(e);
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}

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
    	
    	((Environment)getParent()).getGameCursor().setVisible(false);
    
    	// Get the actual intended target of the user
        Component target = SwingUtilities.getDeepestComponentAt(getParent(), 
        														e.getX(), 
        														e.getY());
        
        ((Environment)getParent()).getGameCursor().setVisible(true);
        
        // Show the cursor again
        setVisible(true);
        
        // As long as the user wasn't clicking on something that's nothing...
        if(target != null) {            
            // Send out the event with the new x and y coordinates
            target.dispatchEvent(new MouseEvent(target,
                                                e.getID(),
                                                e.getWhen(),
                                                e.getModifiers(),
                                                e.getX(), e.getY(),
                                                e.getClickCount(),
                                                e.isPopupTrigger()));
        }
    }	
	
}
