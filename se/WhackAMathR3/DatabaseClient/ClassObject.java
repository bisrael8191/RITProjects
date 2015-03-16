/**
 * ClassObject.java
 * 
 * Version:
 * $Id$
 * 
 * Revisions:
 * $Log$
 * Revision 1.2  2006/10/31 03:57:16  jmf8241
 * Added functionality to the class.
 *
 * Revision 1.1  2006/10/31 03:46:43  jmf8241
 * Initial Revision.
 *
 *
 */

package DatabaseClient;

import java.io.Serializable;

public class ClassObject implements Serializable {

    /**
     * The number of games played
     */
    private int totalGamesPlayed;
    
    /**
     * The number of questions answered by a class.
     */
    private int totalQuesCorrect;
    
    /**
     * Constructor for the class.
     * 
     * @param totalGamesPlayed = the total number of games played.
     * @param totalQuesCorrect = the total number of questions answered.
     */
    public ClassObject( int totalGamesPlayed, int totalQuesCorrect){
        this.totalGamesPlayed = totalGamesPlayed;
        this.totalQuesCorrect = totalQuesCorrect;
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
     * @return Returns the totalQuesCorrect.
     */
    public int getTotalQuesCorrect() {
        return totalQuesCorrect;
    }

    /**
     * @param totalQuesCorrect The totalQuesCorrect to set.
     */
    public void setTotalQuesCorrect(int totalQuesCorrect) {
        this.totalQuesCorrect = totalQuesCorrect;
    }
}
