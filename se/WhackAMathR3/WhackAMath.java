/*
 * WhackAMath.java
 * 
 * Version:
 * 		$Id: WhackAMath.java,v 1.17 2006/11/07 07:25:56 exl2878 Exp $
 * 
 * Revisions:
 * 		$Log: WhackAMath.java,v $
 * 		Revision 1.17  2006/11/07 07:25:56  exl2878
 * 		Game no longer starts a server on the localhost
 * 		
 * 		Revision 1.16  2006/11/04 23:58:34  exl2878
 * 		Configuration file is now used instead of Windows Registry
 * 		
 * 		Revision 1.15  2006/11/03 21:50:08  jmf8241
 * 		Now tries to setup the IP and class name before launching the login window.
 * 		
 * 		Revision 1.14  2006/11/03 04:42:12  exl2878
 * 		Altered WhackAMath to check registry instead of config file
 * 		
 * 		Revision 1.13  2006/11/02 20:14:27  emm4674
 * 		checks for config file before loading/connecting to anything - launches a dialog to get information from the user if it's not setup yet
 * 		
 * 		Revision 1.12  2006/11/01 03:15:56  emm4674
 * 		updated to reflect the changes in login constructor
 * 		
 * 		Revision 1.11  2006/10/28 16:25:24  exl2878
 * 		Removed call to dbc.start()
 * 		
 * 		Revision 1.10  2006/10/26 06:10:10  exl2878
 * 		DBConnector thread is now started in main method
 * 		
 * 		Revision 1.9  2006/10/26 06:01:39  exl2878
 * 		Updated to use method DBConnector.getInstance()
 * 		
 * 		Revision 1.8  2006/10/26 04:33:48  exl2878
 * 		Updated to use method GameWindow.getInstance()
 * 		
 * 		Revision 1.7  2006/10/26 00:13:02  emm4674
 * 		Creates a database server on the local machine, and forces the game to connect to it
 * 		
 * 		Revision 1.6  2006/10/25 02:08:48  exl2878
 * 		Removed command line parameters and added LoginPanel
 * 		
 * 		Revision 1.5  2006/10/10 05:25:53  exl2878
 * 		Implemented "-s" command line argument to disable in-game sounds
 * 		
 * 		Revision 1.4  2006/10/07 18:01:50  exl2878
 * 		Updated main method to correctly run the game
 * 		
 * 		Revision 1.3  2006/09/16 18:25:24  idp3448
 * 		Added commenting.
 * 		
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import DatabaseClient.DBConnector;
import DatabaseServer.DatabaseServer;
import Login.LoginPanel;
import Login.SetupInputDialog;

/**
 * Drives the GUI
 * 
 * @author Ian Paterson
 */
public class WhackAMath {

	public static String CONFIG_FILE = "config.ini";
	
	/**
	 * Main method for the game Whack Whack Math Attack
	 * 
	 * @param args -
	 *            command line parameters, not used
	 */
	public static void main(String[] args) {
		if (args.length != 0) {
			System.err.println("Usage: java WhackAMath");
		} else {
			// wrap a thread around this so we have a way to block execution
			// later on
			Thread t = new Thread() {
				public void run() {
					// check to see if we've been set up on this computer yet
					File f = new File(CONFIG_FILE);
					// if the file doesn't exist, prompt for data and create it
					if (!f.exists()) {
						new SetupInputDialog( f);
					}// if
				}// run()
			};// new Thread()

			// start running the thread
			t.start();
			try {
				// wait until thread is finished running
				t.join();
			} catch (InterruptedException e) {
			}// try-catch

			// read data from the database
			String className = "";
			String ipAddress = null;
			BufferedReader in = null;
			boolean setup = false;
			File f = new File( CONFIG_FILE );
			while ( !setup ) {
			try {
				in = new BufferedReader(new FileReader(CONFIG_FILE));

				className = in.readLine();
				ipAddress = in.readLine();

				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}// try-catch
			
//			 Note: will wait here for a while if the wrong ip is sent
            DBConnector dbc = DBConnector.getInstance( ipAddress );
            if( dbc.isConnected() ){
                if( dbc.isClassSetup( className ) ){
                    new LoginPanel(className);
                    setup = true;
                } else {
                    //Call the setup dialog to change the class name
                    new SetupInputDialog( f );
                }
            } else {
                //Call the setup dialog to change the IP address
                new SetupInputDialog( f );
            }
			}
		}
	}
}
