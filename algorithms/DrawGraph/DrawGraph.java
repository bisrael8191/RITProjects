
/**
 * DrawGraph.java
 * 
 * Version:
 * $Id: DrawGraph.java,v 1.3 2007/11/09 04:55:48 bisrael Exp $
 * 
 * Revisions:
 * $Log: DrawGraph.java,v $
 * Revision 1.3  2007/11/09 04:55:48  bisrael
 * Added comments.
 *
 * Revision 1.2  2007/11/08 09:35:30  bisrael
 * Added all needed controls to draw the graph.
 *
 * Revision 1.1  2007/11/07 10:42:43  bisrael
 * Main program now controls high level execution for graph drawing.
 *
 *
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;

/**
 * Main program.
 * Tests for planarity, follows the quick and dirty algorithm from class,
 * and outputs either "nonplanar" or a SVG drawing called output.svg.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class DrawGraph {

	/**
	 * @param args - file that contains the graph description
	 */
	public static void main(String[] args) throws Exception{
		/*Configurable variables*/
		//SVG output scale
		//Going lower than 21 will cause smaller graphs to have
		//vertices that touch, so you can't see the edges.
		int scale = 25;
		
		//SVG output file name
		String outputFile = "output.svg";
		
		//Check command line arguments
		if ( args.length != 1 ) {
			System.err.println( "Usage: java TestPlanarity filename" );
			return;
		}
		
		//Get the initial graph from the input file
		Graph<Integer> graph = null;
		
		try {
			graph = new Graph<Integer>( 
					new FileInputStream( args[0] ), Integer.class );
		} catch ( IOException e ) {
			System.err.println( "IOException reading from file:" );
			e.printStackTrace( System.err );
		}
		
		//Check to make sure that the graph was inputted properly
		if ( graph != null ) {
			boolean isPlanar = graph.isPlanar();
			
			//Check to be sure that the initial graph is planar
			if(!isPlanar){
				System.out.println("nonplanar");
			}else{
				System.out.println("planar\nConverting to triangulated planar graph ...");
				
				//Convert planar graph to triangulated planar graph
				Graph<Integer> triangulated = graph.triangulateGraph();

				System.out.println("Finding an outer face ...");
				
				//Get face and build stack (canonical ordering)
				//Part a and b of the algorithm.
				Stack<Integer> canonical = triangulated.findCanonicalOrdering();
				
				//Configure the logic side of the graph drawing algorithm.
				//Map vertices does Steps 1-4 of the embedding algorithm, given
				//the canonical ordering.
				//Once finished there is a Map of vertex to grid point inside of
				//the DrawGraphLogicClass.
				System.out.println("Mapping vertices to grid coordinates ...");
				DrawGraphLogic dgl = new DrawGraphLogic(triangulated, scale, outputFile);
				dgl.mapVertices(canonical);
				
				//Draw the graph
				//Outputs an SVG file called "output.svg"
				System.out.println("Drawing graph ...");
				dgl.drawGraph();
			}
		}
	}

}
