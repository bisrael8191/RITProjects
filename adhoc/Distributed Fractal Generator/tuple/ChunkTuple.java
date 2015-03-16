/**
 * ChunkTuple.java
 * 
 * Version:
 * $Id: ChunkTuple.java,v 1.7 2007/05/20 19:13:01 bisrael Exp $
 * 
 * Revisions:
 * $Log: ChunkTuple.java,v $
 * Revision 1.7  2007/05/20 19:13:01  bisrael
 * Added comments.
 *
 * Revision 1.6  2007/05/20 17:37:45  bisrael
 * Added code to print out the tuple properly.
 *
 * Revision 1.5  2007/05/20 07:08:32  bisrael
 * Updated to try and solve the tuple matching problem.
 *
 * Revision 1.4  2007/05/17 09:14:14  bisrael
 * Added in a place for statistics.
 *
 * Revision 1.3  2007/05/16 18:33:23  bisrael
 * Changed how the data is stored.
 *
 * Revision 1.2  2007/05/16 04:37:22  bisrael
 * Updated chunk tuple, now uses a BufferedImage to hold the computed data.
 *
 * Revision 1.1  2007/05/15 06:01:24  bisrael
 * Initial chuck tuple.
 *
 *
 */
package tuple;

import edu.rit.tb.Tuple;

import tuple.ProblemTuple;

/**
 * Tuple that holds the split up problem.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class ChunkTuple extends Tuple {

	/*
	 * Reference to the problem tuple,
	 * used for matching.
	 */
	public ProblemTuple problem;
	
	/*
	 * Starting x coordinate
	 */
	public Integer startx;
	
	/*
	 * Starting y coordinate
	 */
	public Integer starty;
	
	/*
	 * Height of the chunk
	 */
	public Integer height;
	
	/*
	 * Width of the chunk
	 */
	public Integer width;
	
	/*
	 * Computed data
	 */
	public Integer[][] data;
	
	/*
	 * True if the chunk has been computed
	 * False if it hasn't
	 */
	public Boolean finished;
	
	/*
	 * Computation time for the chunk
	 */
	public Long computationTime;
	
	/**
	 * Constructor for the chunk tuple.
	 * 
	 * @param problem - problem tuple, for matching
	 * @param startx - starting x coordinate
	 * @param starty - starting y coordinate
	 * @param height - chunk height
	 * @param width - chunk width
	 * @param data - computed data
	 * @param finished - if the chunk has been computed
	 * @param computationTime - computation time for the chunk
	 */
	public ChunkTuple(ProblemTuple problem, Integer startx, Integer starty, 
			Integer height, Integer width, Integer[][] data, 
			Boolean finished, Long computationTime){
		this.problem = problem;
		this.startx = startx;
		this.starty = starty;
		this.height = height;
		this.width = width;
		this.data = data;
		this.finished = finished;
		this.computationTime = computationTime;
	}
	
	/* (non-Javadoc)
	 * @see edu.rit.tb.Tuple#equals(java.lang.Object)
	 * 
	 * Check if one tuple is equal to another
	 * 
	 * @param obj - tuple to compare
	 */
	public boolean equals(Object obj){
		return obj instanceof ChunkTuple &&
		this.problem != null &&
		this.problem.equals(((ChunkTuple)obj).problem) &&
		this.startx != null &&
		this.startx.equals(((ChunkTuple)obj).startx) &&
		this.starty != null &&
		this.starty.equals(((ChunkTuple)obj).starty) &&
		this.finished != null &&
		this.finished.equals(((ChunkTuple)obj).finished);
	}
	
	/**
	 * Returns a string version of this tuple.
	 *
	 * @return  String version.
	 */
	protected String computeStringVersion(){
		StringBuilder buf = new StringBuilder();
		buf.append ("ChunkTuple(problem=");
		buf.append (problem.computeStringVersion());
		buf.append (",startx=");
		buf.append (startx);
		buf.append (",starty=");
		buf.append (starty);
		buf.append (",finished=");
		buf.append (finished);
		buf.append (')');
		return buf.toString();
	}
	
}
