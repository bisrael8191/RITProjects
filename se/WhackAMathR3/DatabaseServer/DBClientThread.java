/**
 * DBClientThread.java
 * 
 * Version:
 * $Id: DBClientThread.java,v 1.23 2006/11/07 06:07:42 bdi8241 Exp $
 * 
 * Revisions:
 * $Log: DBClientThread.java,v $
 * Revision 1.23  2006/11/07 06:07:42  bdi8241
 * More output deleted.
 *
 * Revision 1.22  2006/11/07 03:31:27  bdi8241
 * Deleted all command line outputs.
 * Deleted the unused deleteTeacher method.
 *
 * Revision 1.21  2006/11/05 01:08:07  bdi8241
 * Uses strings instead of byte[] for password hashes.
 *
 * Revision 1.20  2006/11/04 23:58:44  bdi8241
 * Changed the teacher password hash to a byte[].
 *
 * Revision 1.19  2006/11/03 20:40:10  emm4674
 * *** empty log message ***
 *
 * Revision 1.18  2006/11/02 19:43:09  bdi8241
 * Added a new method putClassNames.
 * Now checks to make sure that there is a default Teacher account "Admin".
 *
 * Revision 1.17  2006/11/01 01:15:32  bdi8241
 * Fixed the putStudentHistory() method, so it now adds new history objects to the end of an ArrayList.
 * All major and minor additions and bugs have been fixed.
 * For the final release, need to remove most print statements and exceptions.
 *
 * Revision 1.16  2006/10/31 21:17:38  bdi8241
 * Fixed the NullPointer exception when trying to getQuestions() that don't exist.
 *
 * Revision 1.15  2006/10/31 20:44:28  bdi8241
 * Filled in all the methods, except deleteTeacher() because it may not be needed.
 * Need to fix getQuestions() Null pointer exception.
 * Need to fix putStudentHistory() to add an object to an ArrayList and write it back to a file.
 *
 * Revision 1.14  2006/10/30 00:20:51  bdi8241
 * Added comments.
 * Added a new method to check a teacher's password.
 *
 * Revision 1.13  2006/10/25 23:55:19  idp3448
 * Finished all methods for R2.
 *
 * Revision 1.12  2006/10/25 23:22:22  idp3448
 * Changed the get methods so they fail nicely.
 *
 * Revision 1.11  2006/10/25 17:47:32  bdi8241
 * Filled in more of the methods.
 *
 * Revision 1.10  2006/10/24 13:42:16  bdi8241
 * Added put/get question methods.
 * I think it will always throw a ClassCastException when trying to type cast ArrayLists, but I think it's just a warning.
 *
 * Revision 1.9  2006/10/23 08:05:10  bdi8241
 * Added the CVS comments at the top of the class
 *
 * Revision 1.8  2006/10/23 07:45:20  bdi8241
 * Minor Changes.
 *
 * Revision 1.7  2006/10/23 07:42:00  bdi8241
 * Minor Changes.
 * 
 * Revision 1.6  2006/10/23 06:44:00  bdi8241
 * Added all the methods into the DBClientThread.
 * First Attempt at creating the database file structure.
 * 
 * Revision 1.5  2006/10/21 01:27:00  bdi8241
 * Cleaned up the code.
 * Now uses one pair of object streams for socket IO and another object stream for local filesystem access.
 * The only problem is that it still doesn't disconnect properly, but it works much better now.
 * 
 * Revision 1.4  2006/10/20 20:13:00  bdi8241
 * Uses bytes to switch modes
 * 
 * Revision 1.3  2006/10/20 18:06:00  bdi8241
 * 
 * Revision 1.2  2006/10/17 15:50:00  bdi8241
 * Minor updates done in class
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
import java.util.ArrayList;
import Administration.MD5Hash;

/**
 * Server side thread that is created for each incoming
 * client connection.
 * 
 * @author Brad Israel
 *
 */
public class DBClientThread extends Thread {
	
	/**
	 * Client socket
	 */
	private Socket dbClient;
	
	/**
	 * Client ID
	 */
    private int clientID = -1;
    
    /**
     * Number of loops to complete before stopping a readObject from the client
     */
    private static final int READ_WAIT = 10;
    
    /**
     * Object output stream for the socket
     */
	ObjectOutputStream soos = null;
	
	/**
	 * Object input stream for the socket
	 */
	ObjectInputStream sois = null;
	
	/**
	 * Object output stream for the local filesystem
	 */
	ObjectOutputStream loos = null;
	
	/**
	 * Object input stream for the local filesystem
	 */
	ObjectInputStream lois = null;
	
	/**
	 * Root database folder of the filesystem
	 */
	String dbRoot = "DatabaseServer/Data";
     
	/**
	 * Configures the client thread
	 * 
	 * @param s the socket to communicate with the client
	 * @param clientID unique client number for the thread
	 */
    public DBClientThread( Socket s, int clientID ) {
    	
        dbClient = s; 
        this.clientID = clientID;
        
    } 
    
    /**
     * Starts the thread in an infinite loop, waiting for client commands.
     * 
     */
    public void run() {
    	
    	//Display new client information
    	//Just for debugging
    	//System.out.println( "Accepted Client: ID - " + clientID + ", Address - " + dbClient.getInetAddress() ); 
    	
    	//Setup the default administrator folders and password.
    	//The default username is "Admin" and the default password is "Admin".
    	boolean adminIsSetup = setupDefaultAdmin();
    	
    	if( adminIsSetup ) {
    		//Try to setup the I/O streams
    		boolean sstreamsIsSetup = false;
    		try{
    			soos = new ObjectOutputStream( dbClient.getOutputStream() );
    			sois = new ObjectInputStream( dbClient.getInputStream() );
    			sstreamsIsSetup = true;
    		} catch (StreamCorruptedException sce) {
    			/*System.out.println("Stream Corrupted Exception" + sce.getMessage() + 
    					". Please restart server.");*/
    		} catch (OptionalDataException ode) {
    			/*System.out.println("Optional Data Exception" + ode.getMessage() + 
    					". Please restart server.");*/
    		} catch (IOException ioe) {
    			/*System.out.println("I/O exception creating stream" + ioe.getMessage() + 
    					". Please restart server.");*/
    		}

    		//If the I/O streams were set up properly, wait for a command from the client.
    		if( sstreamsIsSetup ){
    			String clientCommand;

    			//While the client is connected
    			while( dbClient.isConnected() ){

    				//Read a command in the form of a string
    				clientCommand = null;
    				clientCommand = (String)readFromClient();

    				//If the client wants to get the class list
    				//The print statements are only for testing, comment out for R3
    				if( clientCommand != null && clientCommand.equals( "getClassList" ) ){
    					if( getClassList() ){
    						//System.out.println( "Class list was sent to client successfully");
    					} else {
    						writeToClient( new String( "nack" ) );
    						//System.out.println( "Class list failed to send to client");
    					}
    					//If the client wants to overwrite the class list
    				} else if( clientCommand != null && clientCommand.equals( "putClassList" ) ) {
    					if( putClassList() ){
    						writeToClient( new String( "ack" ) );
    						//System.out.println( "Class list was written successfully" );
    					} else {
    						//System.out.println( "Class list failed to write to file" );
    					}
    					//If the client wants to get a list of questions
    				} else if( clientCommand != null && clientCommand.equals( "getQuestions" ) ) {
    					if( getQuestions() ){
    						//System.out.println( "Questions were successfully sent to client" );
    					} else {
    						writeToClient( new String( "nack" ) );
    						//System.out.println( "Questions failed to send to client" );
    					}
    					//If the client wants to add new questions
    				} else if( clientCommand != null && clientCommand.equals( "putQuestions" ) ) {
    					if( putQuestions() ){
    						writeToClient( new String( "ack" ) );
    						//System.out.println( "Questions were written successfully" );
    					} else {
    						//System.out.println( "Questions failed to write to file" );
    					}
    					//If the client wants to get a student summary
    				} else if( clientCommand != null && clientCommand.equals( "getStudentSummary" ) ) {
    					if( getStudentSummary() ){
    						//System.out.println( "Summary was successfully sent to client" );
    					} else {
    						writeToClient( new String( "nack" ) );
    						//System.out.println( "Summary failed to send to client" );
    					}
    					//If the client wants to update a student summary
    				} else if( clientCommand != null && clientCommand.equals( "putStudentSummary" ) ) {
    					if( putStudentSummary() ){
    						writeToClient( new String( "ack" ) );
    						//System.out.println( "Summary was written successfully" );
    					} else {
    						//System.out.println( "Summary failed to write to file" );
    					}
    					//If the client wants to get the student's complete history
    				} else if( clientCommand != null && clientCommand.equals( "getStudentHistory" ) ) {
    					if( getStudentHistory() ){
    						//System.out.println( "History was successfully sent to client" );
    					} else {
    						writeToClient( new String( "nack" ) );
    						//System.out.println( "History failed to send to client" );
    					}
    					//If the client wants to update the student's history
    				} else if( clientCommand != null && clientCommand.equals( "putStudentHistory" ) ) {
    					if( putStudentHistory() ){
    						writeToClient( new String( "ack" ) );
    						//System.out.println( "History was written successfully" );
    					} else {
    						//System.out.println( "History failed to write to file" );
    					}
    					//If the client wants to delet a student
    				} else if( clientCommand != null && clientCommand.equals( "deleteStudent" ) ) {
    					if( deleteStudent() ){
    						writeToClient( new String( "ack" ) );
    						//System.out.println( "Student was deleted successfully" );
    					} else {
    						writeToClient( new String( "nack" ) );
    						//System.out.println( "Student failed to be deleted" );
    					}
    					//If the client wants to delete a class
    				} else if( clientCommand != null && clientCommand.equals( "deleteClass" ) ) {
    					if( deleteClass() ){
    						writeToClient( new String( "ack" ) );
    						//System.out.println( "Class was deleted successfully" );
    					} else {
    						writeToClient( new String( "nack" ) );
    						//System.out.println( "Class failed to be delete" );
    					}
    					//If the client wants to get a list of class names
    				} else if( clientCommand != null && clientCommand.equals( "getClassNames" ) ) {
    					if( getClassNames() ){
    						//System.out.println( "Class names were written to client successfully" );
    					} else {
    						writeToClient( new String( "nack" ) );
    						//System.out.println( "Class names failed to send to client" );
    					}
    					//If the client wants to get a list of class names
    				} else if( clientCommand != null && clientCommand.equals( "putClassNames" ) ) {
    					if( putClassNames() ){
    						writeToClient( new String( "ack" ) );
    						//System.out.println( "Class names were written successfully" );
    					} else {
    						writeToClient( new String( "nack" ) );
    						//System.out.println( "Class names failed to write" );
    					}
    					//If the client wants to add a teacher or change a teacher's password
    				} else if( clientCommand != null && clientCommand.equals( "addTeacher" ) ) {
    					if( addTeacher() ){
    						writeToClient( new String( "ack" ) );
    						//System.out.println( "Teacher was added successfully" );
    					} else {
    						//System.out.println( "Teacher failed to be added" );
    					}
    					//If the client wants to check a teacher's password for correctness
    				} else if( clientCommand != null && clientCommand.equals( "checkTeacherPassword" ) ) {
    					if( checkTeacherPassword() ){
    						writeToClient( new String( "ack" ) );
    						//System.out.println( "Teachers password was correct" );
    					} else {
    						writeToClient( new String( "nack" ) );
    						//System.out.println( "Teachers password was incorrect" );
    					}
    					//If the client wants to check to make sure a class is setup properly
    				} else if( clientCommand != null && clientCommand.equals( "isClassSetup" ) ) {
    					if( isClassSetup() ){
    						writeToClient( new String( "ack" ) );
    						//System.out.println( "Class is setup" );
    					} else {
    						writeToClient( new String( "nack" ) );
    						//System.out.println( "Class isn't setup" );
    					}
    					//If the client wants to terminate the connection
    				} else if ( clientCommand != null && clientCommand.equals( "quit" ) ){
    					break;
    				} 
    			}
    		
    			//Kill the connection when the client is no longer connected
    			killConnection();

    		}
    	}
    }
    
    /**
     * Read an object from the client
     * 
     * @return a generic object that can be typecasted or just stored
     */
    private Object readFromClient(){
    	Object retVal = null;
    	
    		try{
    			retVal = sois.readObject();
    		} catch( Exception e ){
    			//There will always be an exception until the client sends an Object
    			// to the server. Don't print anything out here.
    		}
    	try{
    		sois.reset();
    	} catch( IOException ioe ){
    		
    	}
    	
		return retVal;
    }
    
    /**
     * Write an object to the client
     * 
     * @param send the object to send
     * @return true if there were no exceptions, false if it failed
     */
    private boolean writeToClient( Object send ){
    	boolean retVal = false;
    	try{
			soos.writeObject( send );
			soos.flush();
			retVal = true;
		} catch( IOException ioe ) {
			//Will error if fileNotFoundException, don't print anything here
			//System.out.println( "Failed to send object to client" );
			//ioe.printStackTrace();
		}
		return retVal;
    }
    
    /**
     * Close the connection and clean up the object streams
     *
     */
	private void killConnection() {
		try{
			if( soos != null ) {
				soos.close();
			}
			if( sois != null ) {
				sois.close();
			}
			if( loos != null ){
				loos.close();
			}
			if( lois != null ){
				lois.close();
			}
			dbClient.close();
		} catch( Exception e ) {
			//System.out.println( "Failed to kill socket connection" );
		}
	}
	
	/**
	 * Used to delete class and student folders
	 * 
	 * @param dir directory to delete
	 * @return true if directory and all contents were deleted, false if it failed
	 */
	private static boolean deleteDir( File dir ) {
		//If the file is a directory
		if( dir.isDirectory() ) {
			
			//Get a list of all files in it
			String[] children = dir.list();
			
			//For all the children, try to recursively delete them
			for( int i = 0; i < children.length; i++ ) {
				boolean success = deleteDir( new File(dir, children[i] ) );
				
				//Return false if anything failed to delete
				if( !success ) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
	
	/**
	 * Make sure that the default Admin login is setup, but don't overwrite it
	 *  if they changed the password. They can use addTeacher( "Admin", "Admin", "passwd" )
	 *  to change the password from the client side.
	 *  
	 * @return true if the folder is setup already or was created, false if it failed
	 */
	private boolean setupDefaultAdmin(){
		boolean retVal = false;
		
		//Default password as a MD5 hash
		String adminPw = "Admin";
		
		MD5Hash create = new MD5Hash();
		
		//Create the new folders if they don't exist
    	boolean mkdirStatus = false;
    	boolean alreadySetup = false;
    	File adminDir = new File( dbRoot + "/Classes/Admin" );
    	if( adminDir.exists() ){
			mkdirStatus = true;
			alreadySetup = true;
		} else {
			mkdirStatus = adminDir.mkdirs();
		}
		if( mkdirStatus && !alreadySetup ){
			
			//Try to create the local object stream
			boolean loosIsSetup = false;
			try{
				loos = new ObjectOutputStream( new FileOutputStream( adminDir + "/passwd" ) );
				loosIsSetup = true;
			} catch( IOException ioe ) {
				//Will return false if theres an exception
				//System.out.println( "Failed to setup local object output stream" );
				//ioe.printStackTrace();
			}

			//If the object stream is setup properly, write the class list
			if( loosIsSetup ) {
				try{
					loos.writeObject( create.getHash( adminPw ) );
					loos.flush();
					retVal = true;
				} catch( IOException ioe ) {
					//Will return false if theres an exception
					//System.out.println( "Failed to write class list to file" );
					//ioe.printStackTrace();
				}
			}
			
		//The admin folder is already setup, return true
		} else if( alreadySetup ) {
			retVal = true;
		}
		
		return retVal;
	}
	
	/**
	 * Sends a list of students in a class to the clients
	 * 
	 * @return true if list was sent successfully, false if it failed
	 */
	private boolean getClassList(){
		boolean retVal = false;
		
		//Tell the client that command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received successfully
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}
			
				if( wroteAck ){
					
					//Try to setup the local filesystem stream
					boolean loisIsSetup = false;
					try{
						lois = new ObjectInputStream( 
								new FileInputStream( dbRoot + "/Classes/" + className + "/classlist" ) );
						loisIsSetup = true;
					} catch( IOException ioe ) {
						//Will return false if theres an exception
						//System.out.println( "Failed to setup local object input stream" );
						//ioe.printStackTrace();
					}

					//If the local stream was setup correctly, read the class list from the file
					if( loisIsSetup ){
						Object classList = null;
						try{
							classList = lois.readObject();
						} catch( Exception e ){
							//FileNotFoundException, returns false
							//System.out.println( "Error reading class list from local file" );
						}
						//If there is a class list, send it to the client
						if( classList != null ){
							if( writeToClient( classList ) ){
								retVal = true;
							}
						}
					}
				}
			}
		}
		
		//Close the local stream
		try{
			if( lois!= null ){
				lois.close();
			}
			lois = null;
		} catch( IOException ioe ) {
			//System.out.println( "loos failed to close" );
			lois = null;
		}
		
		return retVal;
	}
	
	/**
	 * Updates the names of the students in a class
	 * 
	 * @return true if the class list was written successfully, false if it fails
	 */
	private boolean putClassList(){
		boolean retVal = false;
		
		//Tell the client that command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received successfully
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}

				if( wroteAck ){
					
					//Read the class list from the client
					Object classList = null;
					int loop = 0;
					boolean readObject = false;
					boolean readLoop = true;
					while( readLoop ){
						classList = readFromClient();
						if( classList != null ) {
							readObject = true;
							readLoop = false;
						} else {
							try{
								Thread.sleep(500);
							} catch( InterruptedException ie ) {

							}
							loop++;
						}
						if( loop == READ_WAIT ){
							break;
						}
					}

					boolean loosIsSetup = false;
					if( readObject ){
						
						//Create any directories that don't exist
						boolean mkdirStatus = false;
						File dirs = new File( dbRoot + "/Classes/" + className );
						if( dirs.exists() ){
							mkdirStatus = true;
						} else {
							mkdirStatus = dirs.mkdirs();
						}
						if( mkdirStatus ){
							
							//Try to create the local object stream
							try{
								loos = new ObjectOutputStream( new FileOutputStream( dirs + "/classlist" ) );
								loosIsSetup = true;
							} catch( IOException ioe ) {
								//Will return false if theres an exception
								//System.out.println( "Failed to setup local object output stream" );
								//ioe.printStackTrace();
							}

							//If the object stream is setup properly, write the class list
							if( loosIsSetup ) {
								try{
									loos.writeObject( classList );
									loos.flush();
									retVal = true;
								} catch( IOException ioe ) {
									//Will return false if theres an exception
									//System.out.println( "Failed to write class list to file" );
									//ioe.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		
		//Close the local object stream
		try{
			if( loos!= null ){
				loos.close();
			}
			loos = null;
		} catch( IOException ioe ) {
			loos = null;
		}
		
		return retVal;
	}
	
	/**
	 * Send a list of custom questions to the client
	 *  
	 * @return true if the questions were sent to the client, false if it failed
	 */
	private boolean getQuestions(){
		boolean retVal = false;
		
		//Tell the client that the command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}
			
				if( wroteAck ){
					
					//Read the name of the game from the client
					String gameName = (String)readFromClient();
					
					//If some type of game name was received
					if( gameName != null ){
						//Try to set up the local object streams
						boolean loisIsSetup = false;
						try{
							lois = new ObjectInputStream( 
									new FileInputStream( dbRoot + 
											"/Classes/" + className + "/Problems/" + gameName ) );
							loisIsSetup = true;
						} catch( IOException ioe ) {
							//System.out.println( "Failed to setup local object input stream" );
							//ioe.printStackTrace();
						}

						if( loisIsSetup ){
							//Tell the client that the game name was received
							// and the local stream is setup
							wroteAck = false;
							if( writeToClient( new String( "ack" ) ) ){
								wroteAck = true;
							}
					
							if( wroteAck ) {
						
								//Try to read the object from the file
								Object questions = null;
								try{
									questions = lois.readObject();
								} catch( Exception e ){
									//Will return false if theres an exception
									//System.out.println( "Error reading questions from local file" );
								}
								if( questions != null ){
									//Write the questions to the client
									if( writeToClient( questions ) ){
										retVal = true;
									}
								}
							}
						}
					}
				}
			}
		}
		
		//Close the local object streams
		try{
			if( lois!= null ){
				lois.close();
			}
			lois = null;
		} catch( IOException ioe ) {
			lois = null;
		}
		return retVal;
	}
	
	/**
	 * Add new questions to the database
	 * 
	 * @return true if the questions were added successfully, false if it failed
	 */
	private boolean putQuestions(){
		boolean retVal = false;
		
		//Tell the client that the command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}
			
				if( wroteAck ){
					
					//Read the game name from the client
					String gameName = (String)readFromClient();
					
					//Tell the client that the game name was received
					wroteAck = false;
					if( gameName != null ){
						if( writeToClient( new String( "ack" ) ) ){
							wroteAck = true;
						}
					}
					
					if( wroteAck ) {
					
						//Get a list of questions from the client
						Object questions = null;
						int loop = 0;
						boolean readObject = false;
						boolean readLoop = true;
						while( readLoop ){
							questions = readFromClient();
							if( questions != null ) {
								readObject = true;
								readLoop = false;
							} else {
								try{
									Thread.sleep(500);
								} catch( InterruptedException ie ) {
									//Will return false if theres an exception
								}
								loop++;
							}
							if( loop == READ_WAIT ){
								break;
							}
						}

						boolean loosIsSetup = false;
						if( readObject ){
							
							//Create any directories that don't exist
							boolean mkdirStatus = false;
							File dirs = new File( dbRoot + "/Classes/" + className + "/Problems");
							if( dirs.exists() ){
								mkdirStatus = true;
							} else {
								mkdirStatus = dirs.mkdirs();
							}
							if( mkdirStatus ){
								
								//Try to setup the local object streams
								try{
									loos = new ObjectOutputStream( new FileOutputStream( dirs + "/" + gameName ) );
									loosIsSetup = true;
								} catch( IOException ioe ) {
									//Will return false if theres an exception
									//System.out.println( "Failed to setup local object output stream" );
									//ioe.printStackTrace();
								}

								if( loosIsSetup ) {
									
									//Write the questions to the file
									try{
										loos.writeObject( questions );
										loos.flush();
										retVal = true;
									} catch( IOException ioe ) {
										//System.out.println( "Failed to write questions to file" );
										//ioe.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}
		
		//Close the local object stream
		try{
			if( loos!= null ){
				loos.close();
			}
			loos = null;
		} catch( IOException ioe ) {
			loos = null;
		}
		
		return retVal;
	}
	
	/**
	 * Send a student's summary to the client
	 * 
	 * @return true if the summary was sent, false if it failed
	 */
	private boolean getStudentSummary(){
		boolean retVal = false;
		
		//Tell the client that the command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}
			
				if( wroteAck ){
					
					//Read the student's name from the client
					String studentName = (String)readFromClient();
					if( studentName != null ) {
						
						//Try to setup the local object streams
						boolean loisIsSetup = false;
						try{
							lois = new ObjectInputStream( 
									new FileInputStream( dbRoot + 
											"/Classes/" + className + "/Students/" + studentName + "/Summary") );
							loisIsSetup = true;
						} catch( IOException ioe ) {
							//Will return false if theres an exception
							//System.out.println( "Failed to setup local object input stream" );
							//ioe.printStackTrace();
						}
						
						if( loisIsSetup ){
							
							//Tell the client that the local object stream was setup and the summary exists
							wroteAck = false;
							if( writeToClient( new String( "ack" ) ) ){
								wroteAck = true;
							}
							
							if( wroteAck ) {
								
								//Read the summary from the file
								Object sSummary = null;
								try{
									sSummary = lois.readObject();
								} catch( Exception e ){
									//FileNotFoundException, returns false
									//System.out.println( "Error reading student summary from local file" );
								}
								
								if( sSummary != null ){
									//Send the summary to the client
									if( writeToClient( sSummary ) ){
										retVal = true;
									}
								}
							}
						} 
					}
				}
			}
		}
		
		//Close the local object streams
		try{
			if( lois!= null ){
				lois.close();
			}
			lois = null;
		} catch( IOException ioe ) {
			lois = null;
		}
		
		return retVal;
	}
	
	/**
	 * Update a students summary
	 * 
	 * @return true if the summary was updated, false if it failed
	 */
	private boolean putStudentSummary(){
		boolean retVal = false;
		
		//Tell the client that the command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}
			
				if( wroteAck ){
					
					//Read the student's name from the client
					String studentName = (String)readFromClient();
					
					//Tell the client that the student's name was received
					wroteAck = false;
					if( studentName != null ){
						if( writeToClient( new String( "ack" ) ) ){
							wroteAck = true;
						}
					}
					
					if( wroteAck ) {
					
						//Read the student summary from the client
						Object sSummary = null;
						int loop = 0;
						boolean readObject = false;
						boolean readLoop = true;
						while( readLoop ){
							sSummary = readFromClient();
							if( sSummary != null ) {
								readObject = true;
								readLoop = false;
							} else {
								try{
									Thread.sleep(500);
								} catch( InterruptedException ie ) {

								}
								loop++;
							}
							if( loop == READ_WAIT ){
								break;
							}
						}

						boolean loosIsSetup = false;
						if( readObject ){
							
							//Create any directories that don't exist
							boolean mkdirStatus = false;
							File dirs = new File( dbRoot + "/Classes/" + className + "/Students/" + studentName );
							if( dirs.exists() ){
								mkdirStatus = true;
							} else {
								mkdirStatus = dirs.mkdirs();
							}
							if( mkdirStatus ){
								
								//Try to create the local object streams
								try{
									loos = new ObjectOutputStream( new FileOutputStream( dirs + "/Summary" ) );
									loosIsSetup = true;
								} catch( IOException ioe ) {
									//Will return false if theres an exception
									//System.out.println( "Failed to setup local object output stream" );
									//ioe.printStackTrace();
								}

								if( loosIsSetup ) {
									
									//Write the summary to the file
									try{
										loos.writeObject( sSummary );
										loos.flush();
										retVal = true;
									} catch( IOException ioe ) {
										//Will return false if theres an exception
										//System.out.println( "Failed to write questions to file" );
										//ioe.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}
		
		//Close the local object stream
		try{
			if( loos!= null ){
				loos.close();
			}
			loos = null;
		} catch( IOException ioe ) {
			loos = null;
		}
		
		return retVal;
	}
	
	/**
	 * Sends the student's history to the client
	 * 
	 * @return true if the history was sent to the client, false if it failed
	 */
	private boolean getStudentHistory(){
		boolean retVal = false;
		
		//Tell the client that the command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}
			
				if( wroteAck ){
					
					//Read the student's name from the client
					String studentName = (String)readFromClient();
					if( studentName != null ) {
						
						//Try to setup the local object streams
						boolean loisIsSetup = false;
						try{
							lois = new ObjectInputStream( 
									new FileInputStream( dbRoot + 
											"/Classes/" + className + "/Students/" + studentName + "/History") );
							loisIsSetup = true;
						} catch( IOException ioe ) {
							//FileNotFoundException, will return false if theres an exception
							//System.out.println( "Failed to setup local object input stream" );
							//ioe.printStackTrace();
						}
						
						if( loisIsSetup ){
							
							//Tell the client that the local stream was setup properly and the file exists
							wroteAck = false;
							if( writeToClient( new String( "ack" ) ) ){
								wroteAck = true;
							}
							
							if( wroteAck ) {
								
								//Read the history object from the file
								Object sHistory = null;
								try{
									sHistory = lois.readObject();
								} catch( Exception e ){
									//System.out.println( "Error reading student history from local file" );
								}
								if( sHistory != null ){
									
									//Send the history object to the client
									if( writeToClient( sHistory ) ){
										retVal = true;
									}
								}
							}
						} 
					}
				}
			}
		}
		
		//Close the local object stream
		try{
			if( lois!= null ){
				lois.close();
			}
			lois = null;
		} catch( IOException ioe ) {
			lois = null;
		}
		
		return retVal;
	}
	
	/**
	 * Update a student's history.
	 * If other histories already exist, add it to the end of the list.
	 * 
	 * @return true if the history was updated successfully, false if it failed
	 */
	private boolean putStudentHistory(){
		boolean retVal = false;
		
		//Tell the client that the command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}
			
				if( wroteAck ){
					
					//Read the student's name from the client
					String studentName = (String)readFromClient();
					
					//Tell the client that the student's name was received
					wroteAck = false;
					if( studentName != null ){
						if( writeToClient( new String( "ack" ) ) ){
							wroteAck = true;
						}
					}
					
					if( wroteAck ) {
					
						//Read the history object from the client
						Object sHistory = null;
						int loop = 0;
						boolean readObject = false;
						boolean readLoop = true;
						while( readLoop ){
							sHistory = readFromClient();
							if( sHistory != null ) {
								readObject = true;
								readLoop = false;
							} else {
								try{
									Thread.sleep(500);
								} catch( InterruptedException ie ) {

								}
								loop++;
							}
							if( loop == READ_WAIT ){
								break;
							}
						}

						
						if( readObject ){
							
							//Create any directories that don't exist
							//Also find out if the history file already exists
							boolean mkdirStatus = false;
							boolean historyFileExists = false;
							File dirs = new File( dbRoot + "/Classes/" + className + "/Students/" + studentName );
							if( dirs.exists() ){
								mkdirStatus = true;
								if( new File( dirs + "/History").exists() ){
									historyFileExists = true;
								}
							} else {
								mkdirStatus = dirs.mkdirs();
							}
							
							if( mkdirStatus ){
								
									ArrayList<Object> allHistory = null;
									
									//If the history file previously existed
									if( historyFileExists ){
										
										//Try to create the local object input stream
										boolean loisIsSetup = false;
										try{
											lois = new ObjectInputStream( new FileInputStream( dirs + "/History" ) );
											loisIsSetup = true;
										} catch( IOException ioe ) {
											//System.out.println( "Failed to setup local object output or input stream" );
											//ioe.printStackTrace();
										}
										
										if( loisIsSetup ) {
											//Read the ArrayList of objects from the file
											//If it fails, it will be null
											try{
												allHistory = (ArrayList<Object>)lois.readObject();
											} catch( IOException ioe ){
												//ioe.printStackTrace();
											} catch( ClassCastException cce ){
												//cce.printStackTrace();
											} catch( ClassNotFoundException cnfe ){
												//cnfe.printStackTrace();
											}

											//If it's not null
											if( allHistory != null ){

												//Add the new history object to the ArrayList
												allHistory.add( sHistory );

												//Try to create the local object output stream
												boolean loosIsSetup = false;
												try{
													loos = new ObjectOutputStream( new FileOutputStream( dirs + "/History" ) );
													loosIsSetup = true;
												} catch( IOException ioe ) {
													//System.out.println( "Failed to setup local object output or input stream" );
													//ioe.printStackTrace();
												}

												if( loosIsSetup ) {
													//Write the ArrayList of history objects back to the file
													try{
														loos.writeObject( allHistory );
														loos.flush();
														retVal = true;
													} catch( IOException ioe ) {
														//System.out.println( "Failed to write history ArrayList to file" );
														//ioe.printStackTrace();
													}
												}
											}
										}
										
									//Else create a new history file
									} else {

										//Create the ArrayList and add the object
										allHistory = new ArrayList<Object>();
										allHistory.add( sHistory );

										//Try to create the local object streams
										boolean loosIsSetup = false;
										try{
											loos = new ObjectOutputStream( new FileOutputStream( dirs + "/History" ) );
											loosIsSetup = true;
										} catch( IOException ioe ) {
											//System.out.println( "Failed to setup local object output or input stream" );
											//ioe.printStackTrace();
										}

										if( loosIsSetup ) {
											//Write the ArrayList of history objects back to the file
											try{
												loos.writeObject( allHistory );
												loos.flush();
												retVal = true;
											} catch( IOException ioe ) {
												//System.out.println( "Failed to write history ArrayList to file" );
												//ioe.printStackTrace();
											}
										}
									}
								}
							}
						}
					}
				}
			}
		
		//Close the local object streams
		try{
			if( loos!= null ){
				loos.close();
			}
			loos = null;
			if( lois!= null ){
				lois.close();
			}
			lois = null;
		} catch( IOException ioe ) {
			loos = null;
			lois = null;
		}
		
		return retVal;
	}
	
	/**
	 * Delete a student's folder and its contents from a class
	 *  
	 * @return true if the student was deleted, false if it failed
	 */
	private boolean deleteStudent(){
		boolean retVal = false;
		
		//Tell the client that the command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}
			
				if( wroteAck ){
					
					//Read the class name from the client
					String studentName = (String)readFromClient();
					
					//Tell the client that the class name was received
					wroteAck = false;
					if( studentName != null ){
						if( writeToClient( new String( "ack" ) ) ){
							wroteAck = true;
						}
					
						if( wroteAck ){
					
							//Find out if the class folder exists
							boolean dirStatus = false;
							File dirs = new File( dbRoot + "/Classes/" + className + "/Students/" + studentName );
							if( dirs.exists() ){
								dirStatus = true;
							} else {
								dirStatus = false;
							}
							if( dirStatus ){
								//Delete the dir
								retVal = deleteDir( dirs );
							}
						}
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Delete a class folder and its contents
	 * 
	 * @return true if the class was deleted, false if it failed
	 */
	private boolean deleteClass(){
		boolean retVal = false;
		
		//Tell the client that the command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}
			
				if( wroteAck ){
					//Find out if the class folder exists
					boolean dirStatus = false;
					File dirs = new File( dbRoot + "/Classes/" + className );
					if( dirs.exists() ){
						dirStatus = true;
					} else {
						dirStatus = false;
					}
					if( dirStatus ){
						//Delete the dir
						retVal = deleteDir( dirs );
					}
				}
			}
		}
		
		return retVal;
	}
	
	
	
	/**
	 * Sends a list of class names to the client
	 * 
	 * @return true if the list of class names were sent, false if it failed
	 */
	private boolean getClassNames(){
		boolean retVal = false;
		
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			boolean loisIsSetup = false;
			try{
				lois = new ObjectInputStream( 
						new FileInputStream( dbRoot + "/Classes/classNames" ) );
				loisIsSetup = true;
			} catch( IOException ioe ) {
				//FileNotFoundException, return false
				//System.out.println( "Failed to setup local object input stream" );
				//ioe.printStackTrace();
			}

			if( loisIsSetup ){
				Object classList = null;
				try{
					classList = lois.readObject();
				} catch( Exception e ){
					//Will return false if theres an exception
					//System.out.println( "Error reading class list from local file" );
				}
				if( classList != null ){
					if( writeToClient( classList ) ){
						retVal = true;
					}
				}
			}
		}
		
		try{
			if( lois!= null ){
				lois.close();
			}
			lois = null;
		} catch( IOException ioe ) {
			lois = null;
		}
		
		return retVal;
	}
	
	/**
	 * Updates list of class names from client
	 * 
	 * @return true if the new list of class names was written successfully, false if it failed
	 */
	private boolean putClassNames(){
		boolean retVal = false;
		
		//Tell the client that command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class names from the client
			Object classNames = null;
			int loop = 0;
			boolean readObject = false;
			boolean readLoop = true;
			while( readLoop ){
				classNames = readFromClient();
				if( classNames != null ) {
					readObject = true;
					readLoop = false;
				} else {
					try{
						Thread.sleep(500);
					} catch( InterruptedException ie ) {

					}
					loop++;
				}
				if( loop == READ_WAIT ){
					break;
				}
			}

			boolean loosIsSetup = false;
			if( readObject ){

				//Create any directories that don't exist
				boolean mkdirStatus = false;
				File dirs = new File( dbRoot + "/Classes" );
				if( dirs.exists() ){
					mkdirStatus = true;
				} else {
					mkdirStatus = dirs.mkdirs();
				}
				if( mkdirStatus ){

					//Try to create the local object stream
					try{
						loos = new ObjectOutputStream( new FileOutputStream( dirs + "/classNames" ) );
						loosIsSetup = true;
					} catch( IOException ioe ) {
						//Will return false if theres an exception
						//System.out.println( "Failed to setup local object output stream" );
						//ioe.printStackTrace();
					}

					//If the object stream is setup properly, write the class list
					if( loosIsSetup ) {
						try{
							loos.writeObject( classNames );
							loos.flush();
							retVal = true;
						} catch( IOException ioe ) {
							//Will return false if theres an exception
							//System.out.println( "Failed to write class list to file" );
							//ioe.printStackTrace();
						}
					}
				}
			}
		}
		
		//Close the local object stream
		try{
			if( loos!= null ){
				loos.close();
			}
			loos = null;
		} catch( IOException ioe ) {
			loos = null;
		}
		
		return retVal;
	}
	
	/**
	 * Add a new teacher to the password file.
	 * Can also be used to change a teacher's password
	 * 
	 * @return true if the teacher was added successfully, false if it failed
	 */
	private boolean addTeacher(){
		boolean retVal = false;
		
		//Tell the client that command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received successfully
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}

				if( wroteAck ){
					
					//Read the password hash from the client
					Object passwd = null;
					int loop = 0;
					boolean readObject = false;
					boolean readLoop = true;
					while( readLoop ){
						passwd = readFromClient();
						if( passwd != null ) {
							readObject = true;
							readLoop = false;
						} else {
							try{
								Thread.sleep(500);
							} catch( InterruptedException ie ) {

							}
							loop++;
						}
						if( loop == READ_WAIT ){
							break;
						}
					}

					boolean loosIsSetup = false;
					if( readObject ){
						
						//Create any directories that don't exist
						boolean mkdirStatus = false;
						File dirs = new File( dbRoot + "/Classes/" + className );
						if( dirs.exists() ){
							mkdirStatus = true;
						} else {
							mkdirStatus = dirs.mkdirs();
						}
						if( mkdirStatus ){
							
							//Try to create the local object stream
							try{
								loos = new ObjectOutputStream( new FileOutputStream( dirs + "/passwd" ) );
								loosIsSetup = true;
							} catch( IOException ioe ) {
								//Will return false if theres an exception
								//System.out.println( "Failed to setup local object output stream" );
								//ioe.printStackTrace();
							}

							//If the object stream is setup properly, write the class list
							if( loosIsSetup ) {
								try{
									loos.writeObject( passwd );
									loos.flush();
									retVal = true;
								} catch( IOException ioe ) {
									//Will return false if theres an exception
									//System.out.println( "Failed to write password to file" );
									//ioe.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		
		//Close the local object stream
		try{
			if( loos!= null ){
				loos.close();
			}
			loos = null;
		} catch( IOException ioe ) {
			loos = null;
		}
		
		return retVal;
	}
	
	/**
	 * Checks the teacher's password against the current password
	 * 
	 * @return true if the teacher's password was correct, false if the password was incorrect
	 */
	private boolean checkTeacherPassword(){
		boolean retVal = false;
		
		//Tell the client that command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received successfully
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}

				if( wroteAck ){
					
					//Read the password hash from the client
					String passwd = null;
					int loop = 0;
					boolean readObject = false;
					boolean readLoop = true;
					while( readLoop ){
						passwd = (String)readFromClient();
						if( passwd != null ) {
							readObject = true;
							readLoop = false;
						} else {
							try{
								Thread.sleep(500);
							} catch( InterruptedException ie ) {

							}
							loop++;
						}
						if( loop == READ_WAIT ){
							break;
						}
					}

					boolean loisIsSetup = false;
					if( readObject ){
						
						//Try to create the local object stream
						try{
							lois = new ObjectInputStream( new FileInputStream( dbRoot + "/Classes/" + className + "/passwd" ) );
							loisIsSetup = true;
						} catch( IOException ioe ) {
							//Will return false if theres an exception
							//System.out.println( "Failed to setup local object input stream" );
							//ioe.printStackTrace();
						}

						//If the object stream is setup properly, write the class list
						//If there are any exceptions it will return false
						if( loisIsSetup ) {
							try{
								if( passwd.equals( (String)lois.readObject() ) ){
									retVal = true;
								}
							} catch( IOException ioe ) {
								//System.out.println( "Failed to write password to file" );
								//ioe.printStackTrace();
							} catch( ClassNotFoundException cnfe ){
								//cnfe.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		//Close the local object stream
		try{
			if( lois!= null ){
				lois.close();
			}
			lois = null;
		} catch( IOException ioe ) {
			lois = null;
		}
		
		return retVal;
	}
	
	/**
	 * Checks the teacher's folder to make sure that there is a password files
	 * 
	 * @return true if there is a password file, false if there isn't
	 */
	private boolean isClassSetup(){
		boolean retVal = false;
		
		//Tell the client that command was received successfully
		boolean wroteAck = false;
		if( writeToClient( new String( "ack" ) ) ){
			wroteAck = true;
		}

		if( wroteAck ) {
			
			//Read the class name from the client
			String className = (String)readFromClient();
			
			//Tell the client that the class name was received successfully
			wroteAck = false;
			if( className != null ){
				if( writeToClient( new String( "ack" ) ) ){
					wroteAck = true;
				}

				//Check if the directory exists
				File pwfile = new File( dbRoot + "/Classes/" + className + "/passwd" );
				if( pwfile.exists() ){
					retVal = true;
				}
			}
		}

		return retVal;
	}
	
}
