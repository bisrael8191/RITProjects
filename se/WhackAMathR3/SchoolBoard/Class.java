/**
 * Class.java
 * 
 * Version:
 * $Id$
 * 
 * Revisions:
 * $Log$
 * Revision 1.5  2006/11/06 03:26:55  jmf8241
 * Minor Changes.
 *
 * Revision 1.4  2006/11/06 02:55:58  jmf8241
 * Minor Changes.
 *
 * Revision 1.3  2006/11/03 05:24:22  jmf8241
 * Changed the way you connect to the database.
 *
 * Revision 1.2  2006/11/03 05:23:07  jmf8241
 * Added functionality for gathering class statistics and connecting to the database.
 *
 * Revision 1.1  2006/11/03 04:04:45  jmf8241
 * Moved class.java to here.
 *
 * Revision 1.1  2006/10/31 04:10:14  jmf8241
 * Initial Revision.
 *
 */

package SchoolBoard;

import java.io.Serializable;
import java.util.ArrayList;

import DatabaseClient.DBConnector;
import DatabaseServer.DatabaseServer;
import GameLogic.Student;

/**
 * This class holds data on a class.  It functions similarly to
 * the student class.
 * 
 * @author Justin Field
 */

public class Class implements Serializable {
    
    /**
     * The name of the class.
     */
    private String nameOfClass;
    
    /**
     * The total number of questions correct by a class.
     */
    private int totalNumberCorrect;
    
    /**
     * The total number of games played by a class.
     */
    private int totalGamesPlayed;
    
    /**
     * Holds the students in the class.
     */
    private ArrayList<String> studentNames;
    
    /**
     * Connects to the database connector.
     */
    private DBConnector dbc;
    
    /**
     * The constructor.
     * @param nameOfClass - the name of the class.
     */
    public Class( String nameOfClass, DBConnector dbc ){
        this.nameOfClass = nameOfClass;
        totalNumberCorrect = 0;
        totalGamesPlayed = 0;
        this.dbc = dbc;
        studentNames = dbc.getClassList( nameOfClass );
        gatherStats();
    }
    
    /**
     * Compiles the statistics for the class.
     */
    public void gatherStats(){
        Student studentHistory; // The current Student.
        
        try{
            for( int i = 0; i < studentNames.size(); i++ ){
                studentHistory = dbc.getStudentSummary( nameOfClass, studentNames.get( i ));
                totalNumberCorrect += studentHistory.getNumCorrect(); 
                totalGamesPlayed += studentHistory.getNumGames();
            }
        }
            catch( NullPointerException e ){}
    }
    
    /**
     * @return Returns the nameOfClass.
     */
    public String getNameOfClass() {
        return nameOfClass;
    }

    /**
     * @param nameOfClass The nameOfClass to set.
     */
    public void setNameOfClass(String nameOfClass) {
        this.nameOfClass = nameOfClass;
    }

    /**
     * @return Returns the totalGamesPlayed.
     */
    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    /**
     * @param totalGamesPlayed The totalGamesPlayed to set.
     */
    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    /**
     * @return Returns the totalNumberCorrect.
     */
    public int getTotalNumberCorrect() {
        return totalNumberCorrect;
    }

    /**
     * @param totalNumberCorrect The totalNumberCorrect to set.
     */
    public void setTotalNumberCorrect(int totalNumberCorrect) {
        this.totalNumberCorrect = totalNumberCorrect;
    }
}
