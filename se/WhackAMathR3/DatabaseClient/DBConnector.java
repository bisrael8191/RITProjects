/**
 * DBConnector.java
 * 
 * Version:
 * $Id: DBConnector.java,v 1.30 2006/11/07 08:45:12 exl2878 Exp $
 * 
 * Revisions:
 * $Log: DBConnector.java,v $
 * Revision 1.30  2006/11/07 08:45:12  exl2878
 * Changed how getInstance( ipAddress ) works
 *
 * Revision 1.29  2006/11/07 06:02:57  exl2878
 * getInstance now returns a null connection if the database is not
 * connected
 *
 * Revision 1.28  2006/11/07 05:47:46  exl2878
 * Added getServerAddress() method
 *
 * Revision 1.27  2006/11/07 03:31:16  bdi8241
 * Deleted all command line outputs.
 * Deleted the unused deleteTeacher method.
 *
 * Revision 1.26  2006/11/05 01:07:55  bdi8241
 * Uses strings instead of byte[] for password hashes.
 *
 * Revision 1.25  2006/11/04 23:58:58  bdi8241
 * Changed the teacher password hash to a byte[].
 *
 * Revision 1.24  2006/11/02 20:11:59  emm4674
 * added new getInstance method which accepts an ip to connect to
 *
 * Revision 1.23  2006/11/02 19:41:52  bdi8241
 * Added a new method putClassNames.
 *
 * Revision 1.22  2006/11/01 01:16:30  bdi8241
 * Fixed the getStudentHistory() method, so it now gets an ArrayList of history objects from the server.
 * All major and minor additions and bugs have been fixed.
 * For the final release, need to remove most print statements and exceptions.
 *
 * Revision 1.21  2006/10/31 21:17:19  bdi8241
 * Fixed the NullPointer exception when trying to getQuestions() that don't exist.
 *
 * Revision 1.20  2006/10/31 20:41:17  bdi8241
 * Filled in all the methods, except deleteTeacher() because it may not be needed.
 * Need to fix getQuestions() NullPointer exception.
 * Need to fix getStudentHistory() to return an ArrayList.
 *
 * Revision 1.19  2006/10/30 00:54:59  bdi8241
 * Added comments.
 * Added a new method to check a teacher's password.
 *
 * Revision 1.18  2006/10/28 16:23:45  exl2878
 * Original connection is now closed before it is removed when a new connection
 * is made
 *
 * Revision 1.17  2006/10/28 16:22:15  exl2878
 * connectToDB() method can now be used to connect to a database even if
 * it is already connected to a different database.  In that case, the original
 * connection is ended and a new connection is made.
 * 
 * Revision 1.16  2006/10/26 06:00:28  exl2878
 * Modified class to use singleton design pattern
 *
 * Revision 1.15  2006/10/26 05:56:34  bdi8241
 * Fixed a dumb mistake in getStudentHistory.
 *
 * Revision 1.14  2006/10/26 00:55:43  idp3448
 * Minor Changes
 *
 * Revision 1.13  2006/10/25 23:55:27  idp3448
 * Finished all methods for R2.
 *
 * Revision 1.12  2006/10/25 23:21:08  idp3448
 * Changed the get methods so they don't lock the program.
 *
 * Revision 1.11  2006/10/25 17:49:18  bdi8241
 * Added more methods.
 * Still trying to make it handle not getting the correct object or not getting any object from the server.
 *
 * Revision 1.10  2006/10/24 13:43:52  bdi8241
 * Added put/get question methods.
 *
 * Revision 1.9  2006/10/23 08:04:43  bdi8241
 * Added the CVS comments at the top of the class
 *
 * Revision 1.8  2006/10/23 07:44:56  bdi8241
 * Minor Changes.
 * 
 * Revision 1.7  2006/10/23 07:42:00  bdi8241
 * Minor Changes.
 * 
 * Revision 1.6  2006/10/23 06:45:00  bdi8241
 * Updated to send the proper strings to the server, telling it where to create new files.
 * 
 * Revision 1.5  2006/10/21 01:25:00  bdi8241
 * Cleaned up the code.
 * Now uses one pair of object streams.
 * 
 * Revision 1.4  2006/10/20 20:13:00  bdi8241
 * Uses bytes to switch modes
 * 
 * Revision 1.3  2006/10/20 18:06:00  bdi8241
 * 
 * Revision 1.2  2006/10/17 15:50:00  bdi8241
 * Minor updates done in class
 * 
 * Revision 1.1  2006/10/17 05:36:00  bdi8241
 * Client thread that connects to the database over a socket.
 * A class to test the client.
 *
 * 
 */

package DatabaseClient;

import java.io.*;
import java.net.*;
import java.util.*;
import GameLogic.Question;
import GameLogic.Student;
import GameLogic.GameHistory;

/**
 * Client side interface that connects to the server.
 * 
 * @author Brad Israel
 *
 */
public class DBConnector extends Thread {

	/**
	 * Socket that connects to the server
	 */
	private Socket s = null;
	
	/**
	 * Port that the server is listening on
	 */
	private static final int PORT = 8191;
	
	/**
	 * True if connected to the server.
	 * False if the connection has been lost.
	 */
	private boolean isConnected = false;
	
	/**
	 * Object output stream for sending objects to the server
	 */
	private ObjectOutputStream oos = null;
	
	/**
	 * Object input stream for reading objects from the server
	 */
	private ObjectInputStream ois = null;  
	
	/**
	 * Instance of the database connector
	 */
    private static DBConnector dbc;
    
    /**
     * Number of loops to complete before stopping a readObject from the server
     */
    private static final int READ_WAIT = 10;
	
    /**
     * Default constructor.
     * Never used.
     *
     */
    private DBConnector() {	
    	
    }
	
    /**
     * Main Constructor to use.
     * 
     * @param ipAddress IP address to connect to ( can also be hostname )
     */
	private DBConnector( String ipAddress ){
		connectToDB( ipAddress );
		
	}
	
	/**
	 * Returns the instance of the database connector
	 * 
	 * @return the database connector instance
	 */
	public static DBConnector getInstance() {
		return dbc;
	}
	
	/**
	 * Returns the instance of the database connector
	 *
	 * @param ipAddress the remove ip to connect to
	 * 
	 * @return the database connector instance
	 */
	public static DBConnector getInstance(String ipAddress) {
		if ( dbc == null ) 
			dbc = new DBConnector( ipAddress );
		else
			dbc.connectToDB( ipAddress );
		return dbc;
	}
	
	public String getServerAddress() {
		return s.getInetAddress().getHostName();
	}
	
	/**
	 * Method needs to be here so the class can be run as a thread
	 */
	public void run(){
		
	}
	
	/**
	 * Make a connection to the database
	 * 
	 * @param ipAddress IP address that the server is listening on
	 */
	public void connectToDB( String ipAddress ){
		
		//Create the socket connection to the server
        try {
        	//Do nothing if the socket connection is already established
        	if ( s != null && s.getInetAddress().equals(
					InetAddress.getByName( ipAddress ) ) ) {
    			return;
    		} else if ( s != null ) {
        		killConnection();
    		}
        	s = new Socket( ipAddress, PORT);
            
        } catch(UnknownHostException uhe) { 
            // Host unreachable 
            //System.out.println("Unknown Host:" + ipAddress ); 
            //System.out.flush();
            s = null; 
        } catch(IOException ioe) { 
            // Cannot connect to port on given host 
            //System.out.println("Cant connect to server at " + PORT + ". Make sure it is running.");
            s = null; 
        } catch (Exception e) {
        	s = null;
        }
         
        if(s == null) {
            isConnected = false;
        } else {
        	isConnected = true;
        	try { 
                //Create the streams to send and receive information
        		oos = new ObjectOutputStream( s.getOutputStream() );
        		ois = new ObjectInputStream( s.getInputStream() );
            } catch(Exception ioe) { 
                isConnected = false;
            }
        }
        
	}
	
	/**
	 * Kill the connection to the database and clean up the streams
	 *
	 */
	public void killConnection() {
		
		//Send the quit command to the server
		writeToServer( new String( "quit" ) );
		
		//Close the streams and socket, set isConnected to false
		try{
			oos.close();
			ois.close();
			s.close();
			isConnected = false;
		} catch( Exception e ) {
			//System.out.println( "Failed to kill socket connection" );
		}
		
	}
	
	/**
	 * If the class is connected to the server
	 * 
	 * @return true if the connection is still alive, false if it was closed
	 */
	public boolean isConnected(){
		return isConnected;
	}
	
	/**
	 * Read an object that was sent from the server.
	 * The objects need to be typecasted back to their original class.
	 * 
	 * @return the object that was sent from the server
	 */
	private Object readFromServer(){
		Object retVal = null;
		
		//Try to read an object
		try{
			retVal = ois.readObject();
		} catch( Exception e ){
			//There will always be an exception until the client sends an Object
			// to the server. Don't print anything out here.
		}
		
		//Reset the stream, so that the next object will come through properly
    	try{
    		ois.reset();
    	} catch( IOException ioe ){
    		
    	}
    	
		return retVal;
    }
	
	/**
	 * Sends an object to the server
	 * 
	 * @param send object to send
	 * @return true if the object was sent successfully, false if it failed
	 */
	private boolean writeToServer( Object send ){
    	boolean retVal = false;
    	
    	//Try to send the object
    	try{
			oos.writeObject( send );
			oos.flush();
			retVal = true;
		} catch( IOException ioe ) {
			retVal = false;
		}
		
		return retVal;
    }
	
	/**
	 * Read an "ack" string from the server.
	 * Used to make sure that the server receives all objects and commands correctly
	 * 
	 * @return true if an "ack" was received, false if a "nack" was received or nothing was received
	 */
	private boolean readAckFromServer(){
		boolean retVal = false;
		
		//Try to read an "ack" or "nack" from the server
		int loop = 0;
		boolean readLoop = true;
		while( readLoop ){
			String ack = (String)readFromServer();
			if( ack.equals( "ack" ) ) {
				retVal = true;
				readLoop = false;
			} else if( ack.equals( "nack" ) ){
				retVal = false;
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
		
		return retVal;
	}
	
	/**
	 * Get a list of students in a class
	 * 
	 * @param className name of the class
	 * @return an ArrayList of strings, where each string is a student name
	 */
	public ArrayList<String> getClassList( String className ){
		ArrayList<String> retVal = null;
		
		//Send the mode or command to the server
		boolean sentMode = false;
		if( writeToServer( new String( "getClassList" ) ) ){
			sentMode = true;
		}
		
		if( sentMode ){
			
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ) {
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){
						
						//Try to read the list of students from the server
						int loop = 0;
						boolean readLoop = true;
						while( readLoop ){
							try{
								retVal = (ArrayList<String>)readFromServer();
							} catch( ClassCastException cce ){
								//If something other than an ArrayList is received from server
								//kill the loop
								readLoop = false;
							}
							if( retVal != null ) {
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
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Sent an updated class list to the server
	 * 
	 * @param className name of the class
	 * @param classList ArrayList of student names
	 * @return true if the list was sent to the server successfully, false if it failed
	 */
	public boolean putClassList( String className, ArrayList<String> classList ) {
		boolean retVal = false;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "putClassList" ) ) ) {
			sentMode = true;
		}
		
		if( sentMode ) {
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ){
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){
							
							//Write the class list to the server
							writeToServer( classList );
							
							//Read an "ack" if the server wrote the class list successfully
							retVal = readAckFromServer();
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Get a list of custom questions related to a specific game
	 * 
	 * @param className name of the class
	 * @param gameNumber 0 - Add/Subtract game, 1 - Multiply/Divide game, 2 - Units game
	 * @return an ArrayList of question objects
	 */
	public ArrayList<Question> getQuestions( String className, int gameNumber ){
		ArrayList<Question> retVal = null;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "getQuestions" ) ) ){
			sentMode = true;
		}
		
		if( sentMode ){
			
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ) {
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){
						
						boolean sentGameName = false;
						String game = null;
						if( gameNumber == 0 ){
							game = "AddSub";
						} else if( gameNumber == 1 ){
							game = "MultDivide";
						} else if( gameNumber == 2 ){
							game = "Unit";
						}
						if( game != null ){
							
							//Send the game name to the server
							if( writeToServer( game ) ){
								sentGameName = true;
							}
						}
						
						if( sentGameName ){
							
							//Read an "ack" if the server received the game name
							// and the server-side local streams are setup properly
							readAck = readAckFromServer();
							if( readAck ){
							
								//Try and read the list of questions from the server
								int loop = 0;
								boolean readLoop = true;
								while( readLoop ){
									try{
										retVal = (ArrayList<Question>)readFromServer();
									} catch( ClassCastException cce ) {
										//If something other than an ArrayList of questions is received from server
										//System.out.println( "Cast exception thrown when trying to convert object to questions" );
										readLoop = false;
									}
									if( retVal != null ) {
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
							}
						}
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Add custom questions for a game to the database
	 * 
	 * @param className name of the class
	 * @param gameNumber 0 - Add/Subtract game, 1 - Multiply/Divide game, 2 - Units game
	 * @param questions an ArrayList of question objects
	 * @return true if the questions were sent successfully, false if it failed
	 */
	public boolean putQuestions( String className, int gameNumber, ArrayList<Question> questions ){
		boolean retVal = false;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "putQuestions" ) ) ) {
			sentMode = true;
		}
		
		if( sentMode ) {
			
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ){
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name 
					readAck = readAckFromServer();
					if( readAck ){
				
						boolean sentGameName = false;
						String game = null;
						if( gameNumber == 0 ){
							game = "AddSub";
						} else if( gameNumber == 1 ){
							game = "MultDivide";
						} else if( gameNumber == 2 ){
							game = "Unit";
						}
						if( game != null ){
							//Send the game name to the server
							if( writeToServer( game ) ){
								sentGameName = true;
							}
						}

						if( sentGameName ){
							
							//Read an "ack" if the server received the game name
							readAck = readAckFromServer();
							if( readAck ){
								
								//Send the ArrayList of question objects to the server
								writeToServer( questions );
								
								//Read an "ack" if the server wrote the questions to the DB successfully
								retVal = readAckFromServer();
							}
						}
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Get the student summary for a student.
	 * The student summary contains the number of correctly answered questions and
	 *  the total number of questions answered.
	 *  
	 * @param className name of the class
	 * @param studentName name of the student
	 * @return the student object, which is the student summary
	 */
	public Student getStudentSummary( String className, String studentName ){
		Student retVal = null;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "getStudentSummary" ) ) ){
			sentMode = true;
		}
		
		if( sentMode ){
			
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ) {
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){
						
						//Send the student's name to the server
						boolean sentStudentName = false;
						if( writeToServer( studentName ) ){
							sentStudentName = true;
						}
						
						if( sentStudentName ){
							
							//Read an "ack" if the server received the student's name and 
							// setup the object streams on the server side
							readAck = readAckFromServer();
							if( readAck ){
								
								//Try to read the student object from the server
								int loop = 0;
								boolean readLoop = true;
								while( readLoop ){
									try{
										retVal = (Student)readFromServer();
									} catch( ClassCastException cce ) {
										//If something other than an ArrayList of questions is received from server
										//System.out.println( "Cast exception thrown when trying to convert object to Student" );
										readLoop = false;
									}
									if( retVal != null ) {
										readLoop = false;
									} else {
										try{
											Thread.sleep(500);
										} catch( InterruptedException ie ) {}
										loop++;
									}
									if( loop == READ_WAIT ){
										readLoop = false;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Updates a student summary in the database
	 * 
	 * @param className name of the class
	 * @param studentName name of the student
	 * @param studentSummary Student object
	 * @return true if the summary was written successfully, false if it faileds
	 */
	public boolean putStudentSummary( String className, String studentName, Student studentSummary ){
		boolean retVal = false;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "putStudentSummary" ) ) ) {
			sentMode = true;
		}
		
		if( sentMode ) {
			
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ){
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){

						//Send the student's name
						boolean sentStudentName = false;
						if( writeToServer( studentName ) ){
							sentStudentName = true;
						}

						if( sentStudentName ){

							//Read an "ack" if the server received the student's name
							readAck = readAckFromServer();
							if( readAck ){
								
								//Send the student's summary to the server
								writeToServer( studentSummary );
								
								//Read an "ack" if the server wrote the summary successfully
								retVal = readAckFromServer();
							}
						}
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Gets the student history from the server
	 * 
	 * @param className name of the class
	 * @param studentName name of the student
	 * @return a GameHistory object
	 */
	public ArrayList<GameHistory> getStudentHistory( String className, String studentName ){
		ArrayList<GameHistory> retVal = null;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "getStudentHistory" ) ) ){
			sentMode = true;
		}
		
		if( sentMode ){
			
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ) {
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){
						
						//Send the student's name to the server
						boolean sentStudentName = false;
						if( writeToServer( studentName ) ){
							sentStudentName = true;
						}
						
						if( sentStudentName ){
							
							//Read an "ack" if the server received the student's name and 
							// setup the object streams on the server side
							readAck = readAckFromServer();
							if( readAck ){
								
								//Read the student's game history from the server
								int loop = 0;
								boolean readLoop = true;
								while( readLoop ){
									try{
										retVal = (ArrayList<GameHistory>)readFromServer();
									} catch( ClassCastException cce ) {
										//If something other than an ArrayList of game history objects is received from server
										//System.out.println( "Cast exception thrown when trying to convert object to game history" );
										readLoop = false;
									}
									if( retVal != null ) {
										readLoop = false;
									} else {
										try{
											Thread.sleep(500);
										} catch( InterruptedException ie ) {}
										loop++;
									}
									if( loop == READ_WAIT ){
										readLoop = false;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Add a new student game history to the database
	 * 
	 * @param className name of the class
	 * @param studentName name of the student
	 * @param studentHistory a GameHistory object for the student's game session
	 * @return true if the server wrote the new game history, false if it failed
	 */
	public boolean putStudentHistory( String className, String studentName, GameHistory studentHistory ){
		boolean retVal = false;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "putStudentHistory" ) ) ) {
			sentMode = true;
		}
		
		if( sentMode ) {
			
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ){
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){

						//Send the student's name to the server
						boolean sentStudentName = false;
						if( writeToServer( studentName ) ){
							sentStudentName = true;
						}

						if( sentStudentName ){

							//Read an "ack" if the server received the student's name
							readAck = readAckFromServer();
							if( readAck ){
								
								//Send the new history object to the server
								writeToServer( studentHistory );
								
								//Read an "ack" if the server wrote the history to the database
								retVal = readAckFromServer();
							}
						}
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Delete a student's folder from the database
	 * 
	 * @param className name of the class
	 * @param studentName name of the student
	 * @return true if the student was deleted, false if it failed
	 */
	public boolean deleteStudent( String className, String studentName ){
		boolean retVal = false;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "deleteStudent" ) ) ) {
			sentMode = true;
		}
		
		if( sentMode ) {
			
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ){
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){
						
						//Send the student name to the server
						boolean sentStudentName = false;
						if( writeToServer( studentName ) ){
							sentStudentName = true;
						}
						if( sentStudentName ){
							//Read an "ack" if the server received the student name
							readAck = readAckFromServer();
							if( readAck ){
								
								//Read an "ack" if the server deleted the student
								retVal = readAckFromServer();
							}
						}
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Delete a class folder from the database
	 * 
	 * @param className name of the class
	 * @return true if the class was deleted, false if it failed
	 */
	public boolean deleteClass( String className ){
		boolean retVal = false;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "deleteClass" ) ) ) {
			sentMode = true;
		}
		
		if( sentMode ) {
			
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ){
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){
						
						//Read an "ack" if the server deleted the class
						retVal = readAckFromServer();
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Get a list of all the class names
	 * 
	 * @return an ArrayList of class names
	 */
	public ArrayList<String> getClassNames(){
		ArrayList<String> retVal = null;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "getClassNames" ) ) ){
			sentMode = true;
		}
		
		if( sentMode ){
			
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ) {
				
				//Read the list of classes from the server
				int loop = 0;
				boolean readLoop = true;
				while( readLoop ){
					try{
						retVal = (ArrayList<String>)readFromServer();
					} catch( ClassCastException cce ){
						//If something other than an ArrayList of strings is received from server
						readLoop = false;
					}
					if( retVal != null ) {
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
			}
		}
		
		return retVal;
	}
	
	/**
	 * Send a list of all the class names to the server
	 * 
	 * @param classNames the list of class names
	 * @return true if the class names were written, false if it failed
	 */
	public boolean putClassNames( ArrayList<String> classNames ){
		boolean retVal = false;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "putClassNames" ) ) ) {
			sentMode = true;
		}
		
		if( sentMode ) {
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ){
							
				//Write the class list to the server
				writeToServer( classNames );

				//Read an "ack" if the server wrote the class list successfully
				retVal = readAckFromServer();
			}
		}
		
		return retVal;
	}
	
	/**
	 * Add a new teacher to the database.
	 * Can also be used to change the teacher's password.
	 * 
	 * @param className name of the class that they teach
	 * @param passwordHash MD5 hash of their password
	 * @return true if the teacher was added successfully, false if it failed
	 */
	public boolean addTeacher( String className, String passwordHash ){
		boolean retVal = false;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "addTeacher" ) ) ) {
			sentMode = true;
		}
		
		if( sentMode ) {
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ){
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){
							
							//Write the class list to the server
							writeToServer( passwordHash );
							
							//Read an "ack" if the server wrote the password 
							retVal = readAckFromServer();
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Checks the teacher's password when entered
	 * 
	 * @param className name of the class
	 * @param passwordHash enetered password MD5 hash
	 * @return true if the password is correct, false if it is incorrect
	 */
	public boolean checkTeacherPassword( String className, String passwordHash ){
		boolean retVal = false;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "checkTeacherPassword" ) ) ) {
			sentMode = true;
		}
		
		if( sentMode ) {
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ){
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){
							
							//Write the class list to the server
							writeToServer( passwordHash );
							
							//Read an "ack" if the server confirmed the password
							//Reads a "nack" if the password is not correct
							retVal = readAckFromServer();
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Checks the teacher's folder to make sure that there is a password files
	 * 
	 * @param className name of the class
	 * @return true if there is a password file, false if there isn't
	 */
	public boolean isClassSetup( String className ){
		boolean retVal = false;
		
		//Send the mode to the server
		boolean sentMode = false;
		if( writeToServer( new String( "isClassSetup" ) ) ) {
			sentMode = true;
		}
		
		if( sentMode ) {
			//Read an "ack" if the server received the command
			boolean readAck = readAckFromServer();
			if( readAck ){
				
				//Send the class name to the server
				boolean sentClassName = false;
				if( writeToServer( className ) ){
					sentClassName = true;
				}
				if( sentClassName ){
					
					//Read an "ack" if the server received the class name
					readAck = readAckFromServer();
					if( readAck ){
						
						//Read an "ack" if the class is setup
						//Read a "nack" if the class isn't setup
						retVal = readAckFromServer();
					}
				}
			}
		}
		
		return retVal;
	}
}
