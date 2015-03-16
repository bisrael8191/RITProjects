import java.awt.Color;

import edu.rit.color.HSB;
import tools.FractalImplementation;


public class myMandelbrotFractal implements FractalImplementation {

	
	
	// Table of hues.
	static float[] huetable;
	
	int maxIter;
	double breakOut;
	
	
	public myMandelbrotFractal(String max, String bOut){
		
		
		this.maxIter = Integer.parseInt(max);
		this.breakOut = Double.parseDouble(bOut);
		
		// Create table of hues for different iteration counts.
		huetable = new float [maxIter+1];
		for (int i = 0; i < maxIter; ++ i)
			{
			huetable[i] = 
				/*hue*/ (float)Math.pow( (double)i/((double)maxIter) ,0.6);
	
			}
		huetable[maxIter] = 1.0f;
		
	}
	
	
	public Color getPixelColor(double x, double y) {
		
		int i = 0;
		double aold = 0.0;
		double bold = 0.0;
		double a = 0.0;
		double b = 0.0;
		double zmagsqr = 0.0;
		while (i < maxIter && zmagsqr <= breakOut)
			{
			++ i;
			a = aold*aold - bold*bold + x;
			b = 2.0*aold*bold + y;
			zmagsqr = a*a + b*b;
			aold = a;
			bold = b;
			}

		// Record number of iterations for pixel.
	/*	if(i == maxIter){
			return Color.getHSBColor(huetable[i], 1.0f, 1.0f);
		}
		else{
			return Color.getHSBColor(huetable[i], 1.0f, 0.0f);
		}*/
		return Color.getHSBColor(huetable[i], 1.0f, 1.0f);
		
		
		
		
	}

}
