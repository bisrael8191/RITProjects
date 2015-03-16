
/**
 * DrawGraphLogic.java
 * 
 * Version:
 * $Id: DrawGraphLogic.java,v 1.3 2007/11/09 04:56:00 bisrael Exp $
 * 
 * Revisions:
 * $Log: DrawGraphLogic.java,v $
 * Revision 1.3  2007/11/09 04:56:00  bisrael
 * Added comments.
 *
 * Revision 1.2  2007/11/08 09:37:15  bisrael
 * Fixed some bugs in mapVertices().
 * Completed drawGraph() method.
 *
 * Revision 1.1  2007/11/07 20:49:19  bisrael
 * Finished initial vertex coordinating method.
 *
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * This class does things needed to get the vertex coordinates
 * and output a drawing of the graph, but does not need to be
 * in the Graph class.
 *
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class DrawGraphLogic {

	//Store the triangulated planar graph
	//Used to get neighbors and draw edges
	private Graph<Integer> triangulated = null;
	
	//How many pixels to scale the svg output file
	private int scale;
	
	//Name of the final output file
	private String outputFile;
	
	//Store the grid coordinates for the planar embedding
	private Map<Integer, Pair> vCoordinates;
	
	/**
	 * Initialize the class.
	 * 
	 * @param triangulated - the triangulated planar graph
	 * @param scale - how many pixels to scale the svg
	 * @param outputFile - name of the final svg file
	 */
	public DrawGraphLogic(Graph<Integer> triangulated, int scale, String outputFile){
		this.triangulated = triangulated;
		this.scale = scale;
		this.outputFile = outputFile;
	}
	
	/**
	 * Create a planar embedding from the canonical ordering
	 * of the graph. The grid coordinates for the vertices
	 * are stored in the global variable vCoordinates.
	 * 
	 * @param canonical - a stack representing the canonical ordering
	 */
	public void mapVertices(Stack<Integer> canonical){
		//Store the L(Vk) vertex sets 
		Map<Integer, Set<Integer>> L = new HashMap<Integer, Set<Integer>>();
		
		//Store the vertices on the contour in order
		List<Integer> contour = new ArrayList<Integer>();
		
		//Store the vertex coordinates
		vCoordinates = new HashMap<Integer, Pair>();
		
		//Current vertex from stack
		Integer v = null;
		
		//Set up the base case for v1 (first on the stack)
		Set<Integer> lSet = new HashSet<Integer>();
		v = canonical.pop();						//Get from stack
		contour.add(0, v);						//Add to contour
		lSet.add(v);
		L.put(v, lSet);							//Add to L, the only thing in the set is v
		vCoordinates.put(v, new Pair(0,0));	//Add to the coordinate map at (0,0)
		
		//Set up the base case for v2 (second on the stack)
		lSet = new HashSet<Integer>();
		v = canonical.pop();						//Get from stack
		contour.add(1, v);						//Add to contour
		lSet.add(v);
		L.put(v, lSet);							//Add to L, the only thing in the set is v
		vCoordinates.put(v, new Pair(2,0));	//Add to the coordinate map at (2,0)
		
		//Set up the base case for v3 (third on the stack)
		lSet = new HashSet<Integer>();
		v = canonical.pop();						//Get from stack
		contour.add(1, v);						//Add to contour
		lSet.add(v);
		L.put(v, lSet);							//Add to L, the only thing in the set is v
		vCoordinates.put(v, new Pair(1,1));	//Add to the coordinate map at (1,1)
		
		//Loop until there are no more vertices in the stack
		while(!canonical.isEmpty()){
			//Pop off next vertex
			v = canonical.pop();
			
			//Get v's neighbors
			Set<Integer> neighbors = triangulated.getNeighbors(v);
			
			//The neighbor with the minimum index in contour is Wp
			//and the maximum index is Wq. Wp and Wq are indices in contour.
			Integer wp = null, wq = null;
			for(Integer n : neighbors){
				Integer tmp;
				
				//If wp hasn't been set, set it to the first n
				if(wp == null && (tmp = contour.indexOf(n)) != -1){
					wp = tmp;
				//Else, try and find the minimum index, but
				//don't let it become -1 (index not found)
				}else if((tmp = contour.indexOf(n))!= -1){
					if(wp > tmp){
						wp = tmp;
					}
				}

				//If wp hasn't been set, set it to the first n
				if(wq == null && (tmp = contour.indexOf(n)) != -1){
					wq = tmp;
				//Else, try and find the maximum index
				}else if((tmp = contour.indexOf(n))!= -1){
					if(wq < tmp){
						wq = tmp;
					}
				}
			}
			
			//Step 1. Increase the x coordinate of all needed vertices from the
			//sets in L(wq) to L(m) by 2.
			for(int i = wq; i < contour.size(); i++){
				Set<Integer> currentL = L.get(contour.get(i));	//Get the L(Vi) set of vertices
				for(Integer c : currentL){							//For each vertex in L(Vi)
					Pair newP = vCoordinates.get(c);
					newP.setX(newP.getX()+2);						//Increase x coordinate by 2
					vCoordinates.put(c, newP);
				}
			}
			
			//Step 2. Increase the x coordinate of all needed vertices from the
			//sets in L(wp+1) to L(wq-1) by 1.
			Set<Integer> coveredSet = new HashSet<Integer>();
			for(int i = wp+1; i < wq; i++){
				Set<Integer> currentL = L.get(contour.get(i));	//Get the L(Vi) set of vertices
				for(Integer c : currentL){							//For each vertex in L(Vi)
					Pair newP = vCoordinates.get(c);
					newP.setX(newP.getX()+1);						//Increase x coordinate by 2
					vCoordinates.put(c, newP);
					coveredSet.add(c);								//Add to covered set for use in Step 3.5 and 4
				}
			}
			
			//Step 3. Add next vertex to vCoordinate with the correct x,y pair
			Pair newP = new Pair();
			Pair pairWp = vCoordinates.get(contour.get(wp));
			Pair pairWq = vCoordinates.get(contour.get(wq));
			newP.setX((pairWp.getX() - pairWp.getY() + pairWq.getX() + pairWq.getY())/2);
			newP.setY((-pairWp.getX() + pairWp.getY() + pairWq.getX() + pairWq.getY())/2);
			vCoordinates.put(v, newP);
			
			//Step 3.5. Remove all the covered vertices from the contour and add
			//this new vertex in the correct place.
			contour.removeAll(coveredSet);
			contour.add(wp+1, v);
			
			
			//Step 4. Create the new L(Vi) entry for this vertex and the
			//neighbors that it covers.
			coveredSet.add(v);
			L.put(v, coveredSet);
			
		}
		System.out.println("Vertex coordinates: " + vCoordinates);
	}
	
	public void drawGraph(){
		SvgGraphics embed = new SvgGraphics(vCoordinates.size(), scale);
		
		//Get the set of edges added by triangulation
		Set<Pair> addedEdges = triangulated.getAddedEdges();
		
		//Loop over all the edges, check if they were added by triangulation, then draw them.
		//Draws edges first so that they appear behind the nodes and makes the graph look better.
		for(Map.Entry<Integer, Set<Integer>> entry : triangulated.getEdgeTable().entrySet()) {
			Pair u = vCoordinates.get(entry.getKey());
			for(Integer v : entry.getValue()){
				//The two possible combinations for an edge
				Pair edge1 = new Pair(entry.getKey(), v);
				Pair edge2 = new Pair(v, entry.getKey());
				
				//Get the coordinates for the other vertex in the edge
				Pair vPair = vCoordinates.get(v);
				
				//If the edge was added, draw as a gray edge
				if(addedEdges.contains(edge1) || addedEdges.contains(edge2)){
					embed.addEdge(u.getX(), u.getY(), vPair.getX(), vPair.getY(), true);
				}else{
					embed.addEdge(u.getX(), u.getY(), vPair.getX(), vPair.getY(), false);
				}
			}
		}
		
		//Loop over all the vertices and add them to the SVG output
		for(Map.Entry<Integer, Pair> entry : vCoordinates.entrySet()) {
			Integer key = entry.getKey();
			Pair value = entry.getValue();
			embed.addVertex(value.getX(), value.getY(), key);
		}
		
		//Put the ending tags on the file
		embed.endSvgFile();
		
		//Write the svg to the proper output svg file
		embed.outputSvg(outputFile);
		System.out.println("Graph saved to the file \"" + outputFile + "\"");
	}
	
}
