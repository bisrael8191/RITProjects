/**
 * Pair.java
 * 
 * Version:
 * $Id: Pair.java,v 1.3 2007/11/08 09:39:51 bisrael Exp $
 * 
 * Revisions:
 * $Log: Pair.java,v $
 * Revision 1.3  2007/11/08 09:39:51  bisrael
 * Added overloading methods for equals, toString, and hashCode, so that you can use this object in a set.
 *
 * Revision 1.2  2007/11/07 11:38:45  bisrael
 * Added constructor that takes in x and y and a toString method.
 *
 * Revision 1.1  2007/11/07 10:40:58  bisrael
 * Added class to hold grid coordinates.
 *
 *
 */

/**
 * Holds the X,Y grid coordinates for a vertex.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class Pair {

	private Integer x;
	private Integer y;
	
	public Pair(){
		this.x = null;
		this.y = null;
	}
	
	public Pair(Integer x, Integer y){
		this.x = x;
		this.y = y;
	}
	
	public Integer getX(){
		return x;
	}
	
	public Integer getY(){
		return y;
	}
	
	public void setX(Integer x){
		this.x = x;
	}
	
	public void setY(Integer y){
		this.y = y;
	}
	
	public String toString(){
		return "("+ x + ", " + y + ")";
	}
	
	public boolean equals(Object obj){
		return obj instanceof Pair &&
		this.x != null &&
		this.x.equals((((Pair)obj).getX())) &&
		this.y != null &&
		this.y.equals((((Pair)obj).getY()));
	}
	
	public int hashCode() {
		return this.x.hashCode()+this.y.hashCode();
	}
}
