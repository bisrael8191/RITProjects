/**
 * ChunkListener.java
 * 
 * Version:
 * $Id: ChunkListener.java,v 1.2 2007/05/20 18:54:46 bisrael Exp $
 * 
 * Revisions:
 * $Log: ChunkListener.java,v $
 * Revision 1.2  2007/05/20 18:54:46  bisrael
 * Added comments.
 *
 * Revision 1.1  2007/05/16 05:03:22  bisrael
 * Listener for the solver thread.
 *
 *
 */
package solvelogic;

import tuple.ChunkTuple;

/**
 * Listener for when a solver thread finishes computing
 * a chunk of a fractal
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public interface ChunkListener {

	/**
	 * Called by the solver thread when it has completed computing
	 * a chunk. Will create a new chunk with the data in it and
	 * pass it to this implemented method.
	 * 
	 * @param finishedTuple - the finished tuple
	 */
	public void chunkFinished(ChunkTuple finishedTuple);
}
