package GameLogic;

import java.io.Serializable;

/**
 * Holds data on a single student; a collection of getters and setters.
 * 
 * @author Eric M
 */
public class Student implements Serializable {

	/** */
	private String myName;

	/** the name of the class this student is in */
	private String myClass;

	private int score;
	
	private int gamesPlayed;
	
	private int numCorrect;
	
	private int numCorrectFirstTry;
	
	private int numTotal;
	
	private int[] levels;
	
	/**
	 * 
	 */
	public Student(String myName) {
		this(myName, "");
	}

	/**
	 * 
	 */
	public Student(String myName, String myClass) {
		this.myName = myName;
		this.myClass = myClass;
		score = 0;
		gamesPlayed = 0;
		numCorrect = 0;
		numCorrectFirstTry = 0;
		numTotal = 0;
		levels = new int[3];
		for ( int i: levels ) i = 1;
	}

	public String toString() {
		return "[name: " + getMyName() + ", class: " + getMyClass()
				+ ", num games: " + getNumGames() + ", score:" + getScore()
				+ ", num correct: " + getNumCorrect() + ", total questions: "
				+ getTotalQuestions() + ", percent: " + getPercentCorrect()
				+ "%, num correct first: " + getNumCorrectFirst() + " ]";
	}

	// Accessors
	
	public int getLevel( int type ) {
		return levels[ type ];
	}
	
	public String getMyName() {
		return myName;
	}

	public String getMyClass() {
		return myClass;
	}

	public int getNumGames() {
		return gamesPlayed;
	}

	public int getScore() {
		return score;
	}

	public int getNumCorrect() {
		return numCorrect;
	}

	public int getTotalQuestions() {
		return numTotal;
	}

	public int getNumCorrectFirst() {
		return numCorrectFirstTry;
	}

	public double getPercentCorrect() {
		return (getTotalQuestions() == 0) ? 0
				: (100.0 * getNumCorrect() / getTotalQuestions());
	}

	// Mutators
	public void updateStats( GameHistory hist ) {
		score += hist.getScore();
		gamesPlayed += 1;
		numCorrect += hist.getQuestionsCorrect();
		numCorrectFirstTry += hist.getNumCorrectOnFirstTry();
		numTotal += hist.getQuestionsTotal();
		if ( hist.getScore() > GameManager.MIN_SCORE_TO_ADVANCE )
			levels[ hist.getGameType() ] += 1;
	}
}// Student
