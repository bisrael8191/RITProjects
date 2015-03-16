/**
 * Databaserver.java
 * 
 * Version:
 * $Id: DatabaseServer.java,v 1.9 2006/11/07 06:35:43 bdi8241 Exp $
 * 
 * Revisions:
 * $Log: DatabaseServer.java,v $
 * Revision 1.9  2006/11/07 06:35:43  bdi8241
 * Added a "how to stop server" print out.
 *
 * Revision 1.8  2006/11/07 03:34:51  bdi8241
 * Deleted any output that is not necessary.
 * When the server is running it should only display the ip address and a "Listening" message.
 *
 * Revision 1.7  2006/11/02 19:42:14  bdi8241
 * Now displays external IP address.
 *
 * Revision 1.6  2006/10/30 00:20:17  bdi8241
 * Added comments
 *
 * Revision 1.5  2006/10/23 08:05:10  bdi8241
 * Added the CVS comments at the top of the class
 *
 * Revision 1.4  2006/10/23 07:43:55  bdi8241
 * Minor Changes.
 * 
 * Revision 1.3  2006/10/23 07:42:00  bdi8241
 * Minor Changes.
 * 
 * Revision 1.2  2006/10/20 18:06:00  bdi8241
 *
 * Revision 1.1  2006/10/17 05:37:00  bdi8241
 * Multithreaded server for the database.
 * Still trying to work out some problems with the Object IO Streams.
 *
 * 
 */

package DatabaseServer;

import java.net.*; 
import java.io.*;

/**
 * Main Database class. 
 * Runs the server and creates new threads for every incoming connection.
 * Runs on port 8191.
 * 
 * @author Brad Israel
 *
 */
public class DatabaseServer {

	/**
	 * Port that the server will listen on
	 */
	private static final int PORT = 8191;
	
	/**
	 * Assigns a client number to each thread
	 */
	private static int CLIENT_ID = 0;
	
	/**
	 * The server socket
	 */
	ServerSocket dbServer;
	
	/**
	 * Configures and starts the server.
	 * 
	 */
	public DatabaseServer() {
		
		//Try to create the server socket
		try { 
            dbServer = new ServerSocket( PORT ); 
        } catch( IOException ioe ) { 
            System.out.println( "Could not create server socket on " + PORT + 
            		". Make sure that it is not already running." ); 
            System.exit(-1); 
        } 
         
        //Print out server information
        try{
        	System.out.println( "The IP Address for the server is " + 
        			InetAddress.getLocalHost().getHostAddress() );
        	System.out.println( "Listening for clients ..." );
        	System.out.println( "\nPress Control-C to stop the server.");
        } catch( UnknownHostException uhe ){
        	System.out.println( "Could not find the IP Address, " + 
        			", please make sure that the server is running. " );
        }
        
        while( true ) {
        	
        	try {
        		//Accept incoming connections	
                Socket clientSocket = dbServer.accept();
                
                //Start a new client thread with a unique number
                DBClientThread cliThread = new DBClientThread( clientSocket, CLIENT_ID++ );
                cliThread.start();
        	} catch( IOException ioe ) { 
                System.out.println( "Creating a new client thread failed. Please " +
                		"restart the server."); 
            }
        	
        }
        
	}
	
	/**
	 * Main method to start the server. Won't take any arguments.
	 */
	public static void main(String[] args) {
		
		new DatabaseServer();
	
	}
}
