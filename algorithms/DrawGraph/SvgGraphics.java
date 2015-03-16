
/**
 * SvgGraphics.java
 * 
 * Version:
 * $Id: SvgGraphics.java,v 1.3 2007/11/09 04:56:18 bisrael Exp $
 * 
 * Revisions:
 * $Log: SvgGraphics.java,v $
 * Revision 1.3  2007/11/09 04:56:18  bisrael
 * Added comments.
 *
 * Revision 1.2  2007/11/08 09:40:47  bisrael
 * Added all needed methods, finished functionality.
 *
 * Revision 1.1  2007/11/06 06:25:14  bisrael
 * Initial version
 *
 *
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
* This class is used to build, store, and output an SVG file.
* 
* @author Brad Israel - bdi8241@cs.rit.edu
* 
*/

class SvgGraphics{

	//String to store the xml strings that will
	//be outputted to the svg file.
	private String output;
	
	//Scale of the svg file
	private int scale;
	
	//Radius of the vertices (circles)
	private int r = 10;
	
	/**
	 * Setup the beginning of a svg file.
	 * 
	 * @param numOfVertices - number of vertices in the graph
	 * @param scale - how to scale the final output
	 */
	public SvgGraphics(int numOfVertices, int scale){
		//Set the scale
		this.scale = scale;
		
		//Get the size of the graph (from the paper 2*n-4 x n-2)
		int xSize = (2*numOfVertices - 4);
		int ySize = (numOfVertices - 2);
		
		//Set the starting x and y coordinates (in pixels).
		//The r just moves the graph up and over so that the bottom nodes
		//are fully displayed.
		int startx = r;
		int starty = (scale*ySize) + r;
		
		//Set the initial xml format for SVG
		//Also rotate the picture and move to a place that can be viewed
		output = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
				"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" " +
				"\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n\n" +
				"<svg width=\"100%\" height=\"100%\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">\n\n" +
				"<g transform=\"matrix(1, 0, 0, -1, " + startx + ", " + starty +")\">\n" +
				"<g font-family=\"Verdana\" font-size=\"8\" >\n\n";
	}
	
	/**
	 * Add a new vertex to the svg file.
	 * 
	 * @param x - x grid coordinate (unscaled)
	 * @param y - y grid coordinate (unscaled)
	 * @param vNum - number (name) of the vertex
	 */
	public void addVertex(Integer x, Integer y, Integer vNum){
		//Add the xml for the circle (vertex), place it in the proper place
		//using the entered scale.
		output += "<circle cx=\"" + x*scale + "\" cy=\"" + y*scale + 
				"\" r=\"" + r + "\" stroke=\"black\" stroke-width=\"1\" fill=\"white\"/>\n";
		
		//Draw the vertex number on the node, need to rotate the text around b/c
		//the graph is flipped in the end.
		//Yes this is confusing, but it's only because I don't know what I'm doing in SVG.
		//There's probably a much nicer way of doing this.
		output += "<text x=\"" + (x*scale-6) + "\" y=\"" + (y*scale-4) + "\" fill=\"red\" " +
				"transform=\"translate(" + (x*scale-6) + "," + (y*scale-4) + ") scale(1,-1) " +
				"translate(" + -(x*scale-6) + "," + -(y*scale-4) + ")\" >" + vNum + "</text>\n";
	}
	
	/**
	 * Add a new edge to the svg file.
	 * 
	 * @param x1 - first vertex's x coordinate
	 * @param y1 - first vertex's y coordinate
	 * @param x2 - second vertex's x coordinate
	 * @param y2 - second vertex's y coordinate
	 * @param added - true if the edge was added during triangulation, false if not
	 */
	public void addEdge(Integer x1, Integer y1, Integer x2, Integer y2, boolean added){
		if(!added){
			output += "<line x1=\"" + x1*scale + "\" y1=\"" + y1*scale + "\" x2=\"" 
						+ x2*scale + "\" y2=\"" + y2*scale + "\" style=\"stroke:black; stroke-width:1;\"/>\n";
		}else{
			output += "<line x1=\"" + x1*scale + "\" y1=\"" + y1*scale + "\" x2=\"" 
						+ x2*scale + "\" y2=\"" + y2*scale + "\" style=\"stroke:#d7d7d7; stroke-width:1;\"/>\n";
		}
	}
	
	/**
	 * Close the svg file by adding the needed xml tags.
	 * Based on what tags were used in the constructor.
	 *
	 */
	public void endSvgFile(){
		output += "</g>\n" + 
					"</g>\n" +
					"</svg>\n";
	}
	
	/**
	 * Output the compiled svg string to an SVG file.
	 * 
	 * @param fileName - file name for the final SVG file
	 */
	public void outputSvg(String fileName){
		try {
			FileWriter fw = new FileWriter(new File(fileName));
			fw.write(output);
			fw.flush();
			fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
}