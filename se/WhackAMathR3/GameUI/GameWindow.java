/*
 * GameWindow.java
 * 
 * Version:
 * 		$Id: GameWindow.java,v 1.11 2006/11/06 23:53:51 exl2878 Exp $
 * 
 * Revisions:
 * 		$Log: GameWindow.java,v $
 * 		Revision 1.11  2006/11/06 23:53:51  exl2878
 * 		GameWindow now exits application
 * 		
 * 		Revision 1.10  2006/11/06 23:26:26  exl2878
 * 		Closing the GameWindow now closes the database connection
 * 		
 * 		Revision 1.9  2006/10/26 04:33:09  exl2878
 * 		GameWindow now implements the Singleton design pattern
 * 		
 * 		Revision 1.8  2006/10/21 18:29:37  exl2878
 * 		Overloaded setTitle() method to add/replace a subtitle instead of
 * 		replacing the entire title in the title bar
 * 		
 * 		Revision 1.7  2006/10/19 04:47:57  idp3448
 * 		Changed from hiding then displaying window again to using the doLayout() method
 * 		
 * 		Revision 1.6  2006/10/19 04:45:17  idp3448
 * 		Added autoresize functionality to make the window the right size for the environment regardless of the size of the titlebar and other decorations.
 * 		
 * 		Revision 1.5  2006/10/09 22:27:35  emm4674
 * 		made the window unable to be resized
 * 		
 * 		Revision 1.4  2006/10/05 06:49:24  idp3448
 * 		Almost just about ready for R1, all RoundManager and Environment stuff seems to work.
 * 		
 * 		Revision 1.3  2006/10/04 05:19:31  idp3448
 * 		Implemented GameWindow and correct/incorrect overlays.
 * 		
 */

package GameUI;

import java.awt.Dimension;
import javax.swing.JFrame;
import GameUI.Environment;
import DatabaseClient.DBConnector;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * The main window for the game
 * 
 * @author Ian Paterson
 */
public class GameWindow extends JFrame implements WindowListener {
	
	/**
	 * Main title for this window
	 */
	private String mainTitle;
	
	private static GameWindow gameWindow;
	
	private GameWindow(String title) {
		super(title);
		mainTitle = title;
		// Close the program thread when the JFrame is closed.  Will be 
		// modified later to gracefully close any open windows
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		// Set the max size of the window 
		setSize(800,600);
		
		// Don't allow resizing
		setResizable(false);
		
		// Get the size of the screen
		Dimension screen = getToolkit().getScreenSize();
		
		// Position the window in the middle of the screen
		setBounds( (screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2, getWidth(), getHeight() );
		
		// Display the window
		setVisible(true);
		
		// Find the difference in actual interior size from the desired size
		int dw = 764 - getRootPane().getWidth();
		int dh = 560 - getRootPane().getHeight();
		
		// Resize the window according to the desired size
		setSize(getWidth() + dw, getHeight() + dh);
		
		// Finalizes the layout
		doLayout();
		
		// Add window listener
		addWindowListener( this );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
	}
	
	public static GameWindow getInstance() {
		if ( gameWindow == null ) gameWindow = new GameWindow( "Whack Whack Math Attack" );
		return gameWindow;
	}
	
	public void close() {
		DBConnector dbc = DBConnector.getInstance();
		dbc.killConnection();
		System.out.println( "Database is connected: " + dbc.isConnected() );
		dispose();
	}
	
	/**
	 * Adds the Environment to the window
	 * 
	 * @param e the Environment
	 */
	public void setEnvironment(Environment e) {
		add(e);
		
		// Allow the Environment to complete initialization
		e.initializeEnvironment();
	}
	
	/**
	 * Sets the title of the GameWindow.  This method overloads the setTitle()
	 * method in JFrame so that it adds a subtitle to the window bar instead
	 * of completely replacing the game's title.
	 * 
	 * @param	title - the subtitle to use in the title bar
	 */
	public void setTitle( String title ) {
		super.setTitle( title + " - " + mainTitle );
	}

	public void windowOpened(WindowEvent e) {		
	}

	public void windowClosing(WindowEvent e) {
		DBConnector dbc = DBConnector.getInstance();
		dbc.killConnection();
	}

	public void windowClosed(WindowEvent e) {
		DBConnector dbc = DBConnector.getInstance();
		dbc.killConnection();
		System.exit( 0 );
	}

	public void windowIconified(WindowEvent e) {
		// Do nothing
		
	}

	public void windowDeiconified(WindowEvent e) {
		// Do nothing
		
	}

	public void windowActivated(WindowEvent e) {
		// Do nothing		
	}

	public void windowDeactivated(WindowEvent e) {
		// Do nothing
	}
	
}
