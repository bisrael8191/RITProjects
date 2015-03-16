/**
 * ComplexNumber.java
 * 
 * Version:
 * $Id: ComplexNumber.java,v 1.2 2007/05/20 18:58:43 bisrael Exp $
 * 
 * Revisions:
 * $Log: ComplexNumber.java,v $
 * Revision 1.2  2007/05/20 18:58:43  bisrael
 * Added comments.
 *
 *
 */
package tools;

/*
 * This simple class represents complex numbers and does simple 
 * mathmatical operations on them.
 */

public class ComplexNumber {
	
	//Real part
	public double a;
	
	//Imaginary part
	public double b;
	
	ComplexNumber(double a, double b){
		this.a = a;
		this.b = b;
	}
	
	//Are they equal?
	public boolean equals(ComplexNumber other){
		return(other.a == a && other.b == b);
	}
	
	//Add two Complex Numbers
	public ComplexNumber add(ComplexNumber other){
		return new ComplexNumber(other.a + a, other.b + b);
	}
	
	//subtract two ComplexNumbers
	public ComplexNumber subtract(ComplexNumber other){
		return new ComplexNumber(a - other.a, b - other.b);
	}
	
	//Multiply two ComplexNumbrs
	public ComplexNumber multiply(ComplexNumber other){
		return new ComplexNumber(a*other.a - b*other.b, b*other.a + a*other.b);
	}
	
	//Absolute Value of a ComplexNumber
	public double dist(){
		return Math.sqrt(a*a + b*b);
	}

}
