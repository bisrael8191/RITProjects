/**
 * ProblemTuple.java
 * 
 * Version:
 * $Id: ProblemTuple.java,v 1.7 2007/05/20 19:13:24 bisrael Exp $
 * 
 * Revisions:
 * $Log: ProblemTuple.java,v $
 * Revision 1.7  2007/05/20 19:13:24  bisrael
 * Added comments.
 *
 * Revision 1.6  2007/05/20 17:37:06  bisrael
 * Added code to print out the tuple properly.
 *
 * Revision 1.5  2007/05/20 07:08:40  bisrael
 * Updated to try and solve the tuple matching problem.
 *
 * Revision 1.4  2007/05/15 05:54:37  bisrael
 * Deleted the constructor object because it isn't serializable.
 * Added x and y pixel offsets.
 *
 * Revision 1.3  2007/05/14 09:12:23  bisrael
 * Finalized problem tuple.
 *
 * Revision 1.2  2007/05/13 17:42:12  jhays
 * *** empty log message ***
 *
 * Revision 1.1  2007/05/02 05:41:23  bisrael
 * OMG it works!
 *
 *
 */
package tuple;

import edu.rit.tb.Tuple;

/**
 * Tuple that holds the problem class and information
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class ProblemTuple extends Tuple {

	/*
	 * Name of the problem
	 */
	public String problemName;
	
	/*
	 * Name of the author
	 */
	public String authorName;
	
	/*
	 * Class that implements FractalImplementation
	 */
	public Class problemClass;
	
	/*
	 * Constructor arguments for the class
	 */
	public Object[] constArgs;
	
	/*
	 * Height of the problem
	 */
	public Integer height;
	
	/*
	 * Width of the problem
	 */
	public Integer width;
	
	/*
	 * Complex plane x center
	 */
	public Double xcenter;
	
	/*
	 * Complex plane y center
	 */
	public Double ycenter;
	
	/*
	 * 2d image plane x center
	 */
	public Double xcenterPx;
	
	/*
	 * 2d image plane y center
	 */
	public Double ycenterPx;
	
	/*
	 * Pixels per unit
	 */
	public Double resolution;
	
	/**
	 * Constructor for the problem tuple
	 * 
	 * @param problemName - name of the proble
	 * @param authorName - name of the author
	 * @param problemClass - implemented class
	 * @param constArgs - arguments for the constructor
	 * @param height - problem height
	 * @param width - problem width
	 * @param xcenter - complex x center
	 * @param ycenter - complex y center
	 * @param xcenterPx - image x center
	 * @param ycenterPx - image y center
	 * @param resolution - pixels per unit
	 */
	public ProblemTuple(String problemName, String authorName, Class problemClass, 
			Object[] constArgs, Integer height, Integer width, Double xcenter, 
			Double ycenter, Double xcenterPx, Double ycenterPx, Double resolution){
		this.problemName = problemName;
		this.authorName = authorName;
		this.problemClass = problemClass;
		this.constArgs = constArgs;
		this.height = height;
		this.width = width;
		this.xcenter = xcenter;
		this.ycenter = ycenter;
		this.xcenterPx = xcenterPx;
		this.ycenterPx = ycenterPx;
		this.resolution = resolution;
	}
	
	/* (non-Javadoc)
	 * @see edu.rit.tb.Tuple#equals(java.lang.Object)
	 * 
	 * Check if one tuple is equal to another
	 * 
	 * @param obj - tuple to compare
	 */
	public boolean equals(Object obj){
		return obj instanceof ProblemTuple &&
		this.problemName != null &&
		this.problemName.equals(((ProblemTuple)obj).problemName) &&
		this.problemClass != null &&
		this.problemClass.equals(((ProblemTuple)obj).problemClass) &&
		this.height != null &&
		this.height.equals(((ProblemTuple)obj).height) &&
		this.width != null &&
		this.width.equals(((ProblemTuple)obj).width);
	}
	
	/**
	 * Returns a string version of this tuple.
	 *
	 * @return  String version.
	 */
	protected String computeStringVersion(){
		StringBuilder buf = new StringBuilder();
		buf.append ("ProblemTuple(problemName=");
		buf.append (problemName);
		buf.append (",authorName=");
		buf.append (authorName);
		buf.append (",problemClass=");
		buf.append (problemClass.toString());
		buf.append (",constArgs=");
		for(int i = 0; i < constArgs.length; i++) buf.append(constArgs[i]+",");
		buf.append (height);
		buf.append (",height=");
		buf.append (height);
		buf.append (",width=");
		buf.append (width);
		buf.append (')');
		return buf.toString();
	}
}
