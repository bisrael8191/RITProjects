/**
 * Graph.java
 * 
 * Version:
 * $Id: Graph.java,v 1.4 2007/11/09 04:56:09 bisrael Exp $
 * 
 * Revisions:
 * $Log: Graph.java,v $
 * Revision 1.4  2007/11/09 04:56:09  bisrael
 * Added comments.
 *
 * Revision 1.3  2007/11/08 09:38:29  bisrael
 * Now keeps track of added edges during triangulation.
 *
 * Revision 1.2  2007/11/07 10:40:03  bisrael
 * Added methods to triangulate a planar graph, find an outer face, and get the canonical ordering.
 *
 * Revision 1.1  2007/11/05 02:27:50  bisrael
 * Initial version of the planarity tester from the previous quarter's project.
 *
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


/**
 * This is a class to represent a graph. Note this class is not thread-safe.
 * 
 * Included are basic add/remove/get vertex/edge methods in addition to 
 * some more complex isBipartite, findPath, findCycle, and isPlanar methods.
 * 
 * The getInterlacementGraph and getPieces methods are primarily implemented
 * for the calculation of isPlanar.
 * 
 * @author John Hawley
 * 
 * Added new methods after isPlanar() that are used to draw the planar graph.
 * @author Brad Israel - bdi8241@cs.rit.edu
 *
 */
public class Graph<E> implements Cloneable {

	// Vertex set
	private Set<E> V;
	
	// Edge table
	private Map<E,Set<E>> edgeTable;
	
	// Name of this graph (for debugging/printing purposes)
	private String name;
	
	// Edges added to the graph during triangulation
	// (null if not a triangulated graph)
	private Set<Pair> addedEdges;
	
	/**
	 * Create a new empty graph.
	 *
	 */
	public Graph() {
		V = new HashSet<E>();
		edgeTable = new HashMap<E,Set<E>>();
		addedEdges = new HashSet<Pair>();
	}
	
	/**
	 * Construct a Graph by reading edges from a stream and constructing 
	 * vertices using the Class type given.
	 * 
	 * @param stream	Stream from which to read edges.
	 * @param vertexType	Class type to use for constructing vertices.
	 * @throws IOException	Thrown if there is an IO error reading from stream.
	 * @throws IllegalArgumentException	Thrown if the Class type given has no
	 * 									String constructor.
	 */
	public Graph( InputStream stream, Class<E> vertexType ) throws IOException,
													IllegalArgumentException {
		Constructor<E> vertexConstructor;
		try {
			vertexConstructor = vertexType.getConstructor( String.class );
		} catch ( Exception e ) {
			// Any exceptions here are due to an invalid vertexType given.
			//  This should only ever be NoSuchMethodException, 
			//  SecurityException, or NullPointerException
			throw new IllegalArgumentException("No String constructor found."); 
		}
		
		V = new HashSet<E>();
		edgeTable = new HashMap<E,Set<E>>();
		addedEdges = new HashSet<Pair>();
		
		try {
			BufferedReader reader=
				new BufferedReader(new InputStreamReader(stream));
			String line = reader.readLine();
			while ( line != null ) {
				String[] tokens = line.split("[ \t]");
				if ( tokens.length > 1 ) {
					addEdge( vertexConstructor.newInstance( tokens[0] ), 
							vertexConstructor.newInstance( tokens[1] ) );
				}
				line = reader.readLine();
			}
		} catch ( Exception e ) {
			throw new IllegalArgumentException(
					"Exception thrown constructing vertices: " + e );
		}
	}
	
	/**
	 * Add all the edges from the given graph to this graph.  This effectively
	 * results in this graph containing the union of this graph and the graph
	 * given, minus any vertices of otherGraph with a degree of 0.
	 * 
	 * @param otherGraph The graph whose edges to add to this graph.
	 */
	public void addEdges( Graph<E> otherGraph ) {
		for ( E vertex1 : otherGraph.getEdgeTable().keySet() ) {
			for ( E vertex2 : otherGraph.getEdgeTable().get( vertex1 ) ) {
				addEdge( vertex1, vertex2 );
			}
		}
	}
	
	/**
	 * Add a vertex to this graph's vertex set.
	 * 
	 * @param newVertex Vertex to add to this graph.
	 * @return True iff this graph did not already contain the given vertex.
	 */
	public boolean addVertex( E newVertex ) {
		return V.add( newVertex );
	}
	
	/**
	 * Add an edge to this graph.  If either endpoint is not already a member
	 *  of this graph's vertex set, it will be added.
	 *  
	 * @param vertex1 First endpoint of the edge to be added.
	 * @param vertex2 Other endpoint of the edge to be added.
	 */
	public void addEdge( E vertex1, E vertex2 ) {
		V.add( vertex1 );
		V.add( vertex2 );
		
		Set<E> v1List = edgeTable.get( vertex1 );
		Set<E> v2List = edgeTable.get( vertex2 );
		if ( v1List == null ) {
			v1List = new HashSet<E>();
		}
		if ( v2List == null ) {
			v2List = new HashSet<E>();
		}
		v1List.add( vertex2 );
		v2List.add( vertex1 );
		edgeTable.put( vertex1, v1List );
		edgeTable.put( vertex2, v2List );
	}
	
	/**
	 * Set the name of this graph
	 * 
	 * @param name The name to set this graph's name to.
	 */
	public void setName( String name ) {
		this.name = new String( name );
	}
	
	/**
	 * Obtain a string representation of this graph, simply return the graph's
	 *  name.  To print the edge table of this graph, use printEdgeTable()
	 *  
	 *  @return A String containing the name of this graph.
	 */
	public String toString() {
		return name;
	}
	
	/**
	 * Print this graph's edge table to the outputstream given. 
	 * 
	 * @param stream Stream to which to print this graph's edge table.
	 * @throws IOException Thrown if there is an IO error writing to stream.
	 */
	public void printEdgeTable(OutputStream stream) throws IOException {
		PrintWriter writer = new PrintWriter( stream );
		for( E vertex : V ) {
			String line = vertex + " | ";
			for ( E neighbor : edgeTable.get( vertex ) ) {
				line = line + neighbor + " "; 
			}
			writer.println( line );
		}
		writer.flush();
	}
	
	/**
	 * Simple accessor to this graph's vertex set.
	 * 
	 * @return This graph's vertex set.
	 */
	public Set<E> getVertexSet() { return V; }
	
	/**
	 * Simple accessor this graph's edge table.
	 * 
	 * @return This graph's edge table.
	 */
	public Map<E,Set<E>> getEdgeTable() { return edgeTable; }; 
	
	/**
	 * Get the neighbors of a given vertex in this graph. Note the returned
	 *  Set is a copy made from the edge table of this graph, and any
	 *  modifications made to it will not affect this graph in any way.
	 * 
	 * @param vertex Vertex whose neighbors to get.
	 * @return A Set containing the neighbors of the given vertex.
	 */
	public Set<E> getNeighbors( E vertex ) {
		return new HashSet<E>(edgeTable.get( vertex ));
	}
	
	/**
	 * Get the number of edges in this graph.
	 * 
	 * @return The number of edges in this graph.
	 */
	public int getEdgeCount() {
		int retval = 0;
		// Loop over all the edge lists in edgeTable
		for ( Set<E> edges : edgeTable.values() ) {
			retval += edges.size();
		}
		
		// Every edge will have been counted twice because retval currently
		//  represents the sum of all the degrees of the vertices in this graph,
		//  so we need to divide by 2 to get the number of edges.
		retval = retval / 2;
		
		return retval;
	}
	
	/**
	 * Remove a vertex and any incident edges from this graph.
	 * 
	 * @param vertex Vertex to remove from this graph.
	 */
	public void removeVertex( E vertex ) {
		V.remove( vertex );
		edgeTable.remove( vertex );
		for( Set<E> edges : edgeTable.values() ) {
			edges.remove( vertex );
		}
	}
	
	/**
	 * Remove an edge from this graph.  If the graph does not contain the edge
	 *  given, then no action will be performed.
	 *  
	 * @param vertex1 First endpoint of the edge to remove.
	 * @param vertex2 Second endpoint of the edge to remove.
	 */
	public void removeEdge( E vertex1, E vertex2 ) {
		if ( edgeTable.containsKey( vertex1 ) ) { 
			edgeTable.get( vertex1 ).remove( vertex2 );
		}
		
		if ( edgeTable.containsKey( vertex2 ) ) {
			edgeTable.get( vertex2 ).remove( vertex1 );
		}
	}
	
	/**
	 * Create a copy of this graph.  This is used when graphs need to
	 * temporarily have vertices or edges removed.  Rather than remember which
	 * vertices or edges were removed and then re-adding them, a copy can be
	 * created and the original graph can remain unmodified.
	 * 	
	 * @return A graph that contains the same vertices and edges as this one.
	 */
	public Graph<E> clone() {
		Graph<E> retval = new Graph<E>();
		retval.V = new HashSet<E>( V );
		retval.edgeTable = new HashMap<E,Set<E>>();
		for ( E key : edgeTable.keySet() ) {
			if ( edgeTable.get( key ) != null ) {
				retval.edgeTable.put( key, 
						new HashSet<E>( edgeTable.get( key )));
			}
		}
		retval.addedEdges = new HashSet<Pair>(addedEdges);
		return retval;
	}
	
	/**
	 * Find a Path in this Graph between two vertices, based ona simple BFS
	 *  algorithm.
	 * 
	 * @param vertex1 One endpoint of the path to find
	 * @param vertex2 The other endpoint of the path to find.
	 * @return A subgraph containing only a path between vertex1 and vertex2,
	 * 			or null if no such path exists.
	 */
	public Graph<E> findPath( E vertex1, E vertex2 ) {
		Graph<E> retval = null;
		
		Map<E,E> parentMap = new HashMap<E,E>();
		Queue<E> vertexQueue = new LinkedList<E>();
	
		parentMap.put( vertex1, null );
		vertexQueue.offer( vertex1 );

		while ( retval == null && !vertexQueue.isEmpty() ) {
			E node = vertexQueue.poll();
			
			if ( node.equals( vertex2 ) ) {
				// We've found a path, create retval and we're done.
				retval = new Graph<E>();
				
				while ( parentMap.get( node ) != null ) {
					retval.addEdge( node, parentMap.get(node) );
					node = parentMap.get(node);
				}
	
			} else {
				// Iterate over neighbors, if we find one we haven't
				//  already seen, enqueue it and remember its parent.
				for( E i : getNeighbors( node ) ) {
					if ( !parentMap.containsKey( i ) ) {
						parentMap.put( i, node );
						vertexQueue.offer( i );
					}
				}
			}
		}
		
		return retval;
	}
	
	/**
	 * Find a cycle in this graph, return null if no cycle is found.  This 
	 *  method uses a simple BFS algorithm to find a cycle.  The algorithm
	 *  starts traversing from an arbitrary vertex and returns the first cycle
	 *  it finds, often resulting in the return of smaller rather than larger
	 *  cycles. 
	 * 
	 * @return A subgraph of this graph containing only a single cycle, or null
	 *         if no cycle could be found. 
	 */
	@SuppressWarnings("unchecked")
	public Graph<E> findCycle() {
		// Don't bother checking if we don't have enough vertices!
		if ( V.size() < 3 ) return null;
		
		// Queue for BFS.
		Queue<E> vertexQueue = new LinkedList<E>();
		// Map of vertices to their parent
		Map<E, E> parentMap = new HashMap<E,E>();
		
		// Start with any vertex
		vertexQueue.offer( (E)V.toArray()[0] );
		parentMap.put( (E)V.toArray()[0], null );
		
		// Stop when we find a cycle, queue not necessarily empty.  vertex1 will
		//  be set to the vertex that was found on a cycle, or null if no cycle
		//  could be found.
		E vertex1 = null;
		E vertex1OtherParent = null;
		while ( !vertexQueue.isEmpty() && (vertex1 == null) ) {
			E vertex = vertexQueue.poll();
			for( E child : getNeighbors( vertex ) ) {
				if ( child.equals( parentMap.get( vertex ) ) ) {
					// Do nothing, ignore this vertex's parent, it isn't a child
				} else if ( parentMap.containsKey( child ) && 
						(parentMap.get(child) != null) ) {
					// If we've already seen this child, then it's in a cycle;
					//  this will cause the BFS to stop after this vertex.
					vertex1 = child;
					vertex1OtherParent = vertex;
					// Break out of the for-each loop over children, the while
					//  loop will exit because vertex1 is no longer null.
					break;
				} else {
					parentMap.put( child, vertex );
					vertexQueue.offer( child );
				}
			}
		}
		
		// If we didn't find any cycle, return null.
		if ( vertex1 == null ) return null;
		
		// Build a set of vertices that were seen between the root and vertex1
		Set<E> vertex1SeenVertices = new HashSet<E>();
		// Start with vertex1's parent so we don't find vertex1 again.
		E vertex2 = parentMap.get( vertex1 );
		while ( vertex2 != null ) {
			vertex1SeenVertices.add( vertex2 );
			vertex2 = parentMap.get( vertex2 );
		}
		
		// Build the cycle as we traverse.
		Graph<E> retval = new Graph<E>();
		retval.addEdge( vertex1, vertex1OtherParent );
		
		// Now find which vertex besides vertex1 is on the cycle we found.
		vertex2 = vertex1OtherParent;
		boolean done = false;
		while ( !done && (vertex2 != null) ) {
			if ( vertex1SeenVertices.contains( vertex2 ) ) {
				done = true;
			} else {
				retval.addEdge( vertex2, parentMap.get( vertex2 ) );
				vertex2 = parentMap.get( vertex2 );
			}
		}
		
		// This should never happen, as the two paths should have at least the
		//  root node of the BFS in common, but just in case, fail fast here.
		if ( vertex2 == null ) return null;
		
		// Now traverse up the other path from vertex1 to vertex2 again, adding
		//  edges to retval this time (since we know where to stop).
		while ( (vertex1 != null) && (vertex1 != vertex2) ) {
			retval.addEdge( vertex1, parentMap.get( vertex1 ) );
			vertex1 = parentMap.get( vertex1 );
		}
		
		return retval;
	}
	
	/**
	 * Partition this graph into pieces with respect to the given cycle.
	 * 
	 * @param cycle	The cycle to be used to partition this graph into pieces.  
	 * 				cycle <b>must</b> be a subgraph of this graph. 
	 * 
	 * @return A Collection of Graphs representing the Pieces of this graph
	 * 			when partitioned with respect to the cycle given.  Note that 
	 * 			these pieces contain the attachment vertices and edges, and as
	 * 			such, these pieces are not biconnected.
	 */
	public Collection<Graph<E>> getPieces(Graph<E> cycle) {
		Collection<Graph<E>> retval = null;

		// This isn't a reliable check that cycle is a subgraph of this graph,
		//  but it's at least something.
		if ( !V.containsAll( cycle.getVertexSet() ) ) { 
			return retval; 
		}
		
		retval = new LinkedList<Graph<E>>();
		// Queue used while BFS traversing each piece.
		Queue<E> vertexQueue = new LinkedList<E>();
		// Set of vertices that have already been used by some piece.
		Set<E> usedVertices = new HashSet<E>();
		// Map from a vertex to all other vertices with which it is a chord.
		Map<E, Set<E>> chords = new HashMap<E, Set<E>>();
		
		for ( E i : cycle.getVertexSet() ) {
			for ( E neighbor : getNeighbors( i ) ) {
				// If neighbor is not a neighbor of i in cycle and it hasn't 
				//  been used in a piece already, then we might need to make a 
				//  new piece for it.
				if ( !cycle.getNeighbors( i ).contains( neighbor ) &&
						!usedVertices.contains( neighbor ) ) {
					if ( !cycle.getVertexSet().contains( neighbor ) ) {
						// If the neighbor is not on the cycle, this isn't a 
						//  chord, so enqueue neighbor for BFS.
						vertexQueue.offer( neighbor );
						usedVertices.add( neighbor );
					} else if ( !chords.containsKey(i) || 
							!chords.get( i ).contains( neighbor ) ) {
						// This is a chord that we haven't seen yet, make sure 
						//  we don't count it twice.
						if ( !chords.containsKey(i) ) {
							chords.put( i, new HashSet<E>() );
						}
						if ( !chords.containsKey(neighbor) ) {
							chords.put( neighbor, new HashSet<E>() );
						}
						chords.get(i).add( neighbor );
						chords.get(neighbor).add( i );
					} else {
						// We found a chord that we've already found before,
						//  so don't add it to retval.  Continue on to the
						//  next neighbor in getNeighbors(i);
						continue;
					}
					
					// Build the new piece
					Graph<E> piece = new Graph<E>();
					piece.setName( "Piece " + (retval.size() + 1) );
					piece.addEdge( i, neighbor );
					
					// If this wasn't a chord, then vertexQueue will have 
					//  neighbor in it, and we'll BFS out from there.
					while ( !vertexQueue.isEmpty() ) {
						E node = vertexQueue.poll();
						for ( E nodeNeighbor : getNeighbors( node ) ) {
							piece.addEdge( node, nodeNeighbor );
							// Don't traverse past the cycle, don't enqueue
							//  vertices we've already seen.
							if ( !usedVertices.contains( nodeNeighbor ) &&
								!cycle.getVertexSet().contains(nodeNeighbor)) {
								vertexQueue.offer( nodeNeighbor );
								usedVertices.add( nodeNeighbor );
							}
						}
					}
					
					retval.add( piece );
				}
						
			}
		}
		
		// Note that the pieces returned here contain the attachment vertices
		//  and edges.
		return retval;
	}

	/**
	 * Test whether this graph is bipartite or not.  This method uses a simple
	 * BFS coloring algorithm to determine whether this graph is bipartite. 
	 * 
	 * @return True iff this graph is bipartite.
	 */
	public boolean isBipartite() {
		// Any time a vertex is enqueued in vertexQueue, it MUST be added as a
		//  key to colorMap. A NullPointerException will be thrown eventually
		//  if this contract is not upheld (this is NOT failfast).
		Map<E, Integer> colorMap = new HashMap<E, Integer>();
		Queue<E> vertexQueue = new LinkedList<E>();

		// Keep looping until we've colored all the vertices.  This loop will
		//  only execute once as long as this graph is connected.
		while ( colorMap.keySet().size() != V.size() ) {
			// Find a vertex we haven't colored.
			for( E vertex : V ) {
				if ( !colorMap.keySet().contains( vertex ) ) {
					// We've found an uncolored vertex, so color it, enqueue it 
					// for BFS, and break out of the for-each loop over V.
					colorMap.put( vertex, 0 );
					vertexQueue.add( vertex );
					break;
				}
			}
			
			// Simple BFS to color all the connected vertices starting with the
			//  vertex we just found.
			while ( !vertexQueue.isEmpty() ) {
				E vertex = vertexQueue.poll();
				Integer newColor = (colorMap.get( vertex ) + 1) % 2;
				
				for ( E neighbor : edgeTable.get( vertex ) ) {
					if ( !colorMap.containsKey( neighbor ) ) {
						// We haven't colored this neighbor yet, so color it and
						//  enqueue it to continue the BFS.
						colorMap.put( neighbor, newColor );
						vertexQueue.offer( neighbor );
					} else if ( !newColor.equals( colorMap.get( neighbor ) ) ) {
						// We have colored neighbor already, but the color
						//  doesn't match the color we're trying to color it
						//  now, so the graph isn't bipartite.
						return false;
					}
					// Otherwise, we've already colored neighbor, and we don't
					//  have any conflict, so just keep going (don't enqueue
					//  neighbor because it was already colored).
				} // for all the neighbors of the current vertex.
			} // while there's a vertex enqueued for BFS.
		} // while there is some uncolored vertex

		// If we colored all the vertices with no conflicts, then this graph
		//  is bipartite, return true.
		return true;
	}

	/**
	 * Get the interlacement graph of the pieces of this graph created with 
	 * respect to cycle.  This version of this method calculates the pieces
	 * itself and calls the version that takes the pieces as a parameter.
	 * 
	 * @param cycle The cycle to use for partitioning this graph into pieces
	 * 				and determining the interlacement graph of those pieces.
	 * @return A graph whose vertices are the pieces of this graph, which are
	 * 			graphs themselves.  Pieces are adjacent iff they are interlaced
	 * 			in the partitioning of this graph.
	 */
	public Graph<Graph<E>> getInterlacementGraph( Graph<E> cycle ) {
		return getInterlacementGraph( cycle, getPieces( cycle ) );
	}
	
	/**
	 * Get the interlacement graph of the pieces of this graph created with 
	 * respect to cycle.  This version of this method takes the pieces of this
	 * graph as a parameter, as it is likely the pieces will have been already
	 * calculated before this method is called, in which case they need not be
	 * calculated a second time.  Note that the pieces given MUST have been
	 * calculated with respect to the cycle given, and that this graph and the
	 * pieces MUST not have been modified since the partitioning was calculated.
	 * 
	 * @param cycle The cycle used to partition this graph into the pieces given
	 * @param pieces The pieces of this graph calculated with respect to cycle.
	 * @return A graph whose vertices are the pieces of this graph, which are
	 * 			graphs themselves.  Pieces are adjacent iff they are interlaced
	 * 			in the partitioning of this graph.
	 */
	@SuppressWarnings("unchecked")
	public Graph<Graph<E>> getInterlacementGraph( Graph<E> cycle, 
			Collection<Graph<E>> pieces ) {
		Graph<Graph<E>> retval = null;
		
		// We need to traverse the cycle in order
		LinkedList<E> cycleNodes = new LinkedList<E>();
		cycleNodes.add( (E)cycle.getVertexSet().toArray()[0] );
		int size = 0;
		while ( !cycleNodes.containsAll( cycle.getVertexSet() ) && 
				size < cycleNodes.size() ) { 
			size = cycleNodes.size();
			for( E neighbor : cycle.getNeighbors( cycleNodes.getLast() ) ) {
				if ( !cycleNodes.contains( neighbor ) ) {
					// We found a vertex not already in cycleNodes, so
					//  add it and break out of the for-each over neighbors
					cycleNodes.addLast( neighbor );
					break;
				}
			}
		}
		
		if ( !cycleNodes.containsAll( cycle.getVertexSet() ) ) {
			//System.err.println( "Invalid Cycle:" );
			//try { printEdgeTable( System.err ); } catch (Exception e){}
			System.err.println( "Cycle:" );
			try { cycle.printEdgeTable( System.err ); } catch (Exception e){}
			// We were given an invalid cycle value, return null.
			return retval;
		}

		// Remember the vertex at which we first encountered a given piece, so 
		//  we know when we can stop.  Set to null when the piece is done.
		Map<Graph<E>, E> firstPieceVertex = new HashMap<Graph<E>, E>();
		// Remember how many attachments each piece has so we don't have to
		//  recalculate it over and over.
		Map<Graph<E>, Integer> pieceAttachmentCount = 
			new HashMap<Graph<E>, Integer>();
		// Remember how many of each other piece each piece has seen since its
		//  last attachment.
		Map<Graph<E>, Map<Graph<E>, Integer>> pieceSeenCounts =
			new HashMap<Graph<E>, Map<Graph<E>, Integer>> ();
		// Remember which pairs of pieces are NOT interlaced.
		Map<Graph<E>, Set<Graph<E>>> nonInterlacedPieces = 
			new HashMap<Graph<E>, Set<Graph<E>>>();
				
		// Calculate how many attachments each piece has
		for ( Graph<E> piece : pieces ) {
			for ( E cycleNode :  cycleNodes ) {
				if ( piece.getVertexSet().contains( cycleNode ) ) {
					Integer oldVal = pieceAttachmentCount.get( piece );
					if ( oldVal == null ) {
						oldVal = 1;
					} else {
						oldVal = oldVal + 1;
					}
					pieceAttachmentCount.put( piece, oldVal );
				}
			}
		}
		
		// keep going until we get back to the firstPieceVertex of every piece.
		boolean done = false;
		while (!done) {
			for( E cycleNode : cycleNodes ) {
				if ( done ) {
					// Break out of the for-each loop over cycleNodes if 
					//  we're done.
					break;
				}
				
				// Assume we're done, if there's a non-disabled node, it will
				//  set done to false.
				done = true;
				
				// Set of pieces attached to cycle at vertex cycleNode
				Set<Graph<E>> attachedPieces = new HashSet<Graph<E>>();
				for( Graph<E> piece : pieces ) {
					if ( piece.getVertexSet().contains( cycleNode ) ) {
						// We've found a piece that attaches to cycle
						//  at cycleNode
						attachedPieces.add( piece );
						
						// Create this piece's seenCounts Map if it doesn't
						//  already exist
						if ( !pieceSeenCounts.containsKey( piece ) ) {
							pieceSeenCounts.put( piece, 
								new HashMap<Graph<E>, Integer>() );
						}
					}
				}
				
				// If we haven't seen all the pieces yet, we're not done with
				//  the outer loop.
				if ( pieces.size() != firstPieceVertex.keySet().size() ) {
					done = false;
				}
				
				// Increment the counts of any pieces we've seen and aren't 
				//  done with.  Ignore pieces that we're seeing for the first
				//  time at this vertex, their counts will get set accordingly
				//  at the end of this iteration.
				for( Graph<E> piece : firstPieceVertex.keySet() ) {
					// If we're not done with this piece yet, we need to update
					//  its counts, and we're not done with the outer loop yet.
					if ( firstPieceVertex.get( piece ) != null ) {
						done = false;
						for ( Graph<E> attachedPiece : attachedPieces ) {
							// don't let a piece count itself
							if ( piece == attachedPiece ) continue;
							Integer count = 
								pieceSeenCounts.get(piece).get(attachedPiece);
							if ( count == null ) {
								count = 1;
							} else {
								count = count + 1;
							}
							// Update the piece's seen attachment count.
							pieceSeenCounts.get(piece).put(attachedPiece,count);
						}
					}
				}
				
				// Check the attached pieces to see if there are any that
				//  are non-interlaced
				for( Graph<E> attachedPiece : attachedPieces ) {
					for ( Graph<E> countedPiece : 
						pieceSeenCounts.get( attachedPiece ).keySet() ) {
						// Don't bother checking a piece against itself.
						if ( attachedPiece == countedPiece ) continue;
						
						// If we've seen all the attachments, these two aren't
						//  interlaced.
						if ( pieceAttachmentCount.get( countedPiece ).equals(
								pieceSeenCounts.get(attachedPiece).get(
										countedPiece) ) ) {
							Set pieceNonInterlacedPieces = 
								nonInterlacedPieces.get( attachedPiece );
							if ( pieceNonInterlacedPieces == null ) {
								pieceNonInterlacedPieces = 
									new HashSet<Graph<E>>();
								nonInterlacedPieces.put( attachedPiece,
										pieceNonInterlacedPieces );
							}
							
							pieceNonInterlacedPieces.add( 
									countedPiece );
							
							pieceNonInterlacedPieces = 
								nonInterlacedPieces.get( countedPiece );
							if ( pieceNonInterlacedPieces == null ) {
								pieceNonInterlacedPieces = 
									new HashSet<Graph<E>>();
								nonInterlacedPieces.put( countedPiece,
										pieceNonInterlacedPieces );
							}
							
							pieceNonInterlacedPieces.add( 
									attachedPiece );
						}
					}
				}
				
				// Reset the counts for the pieces that are attached at this
				//  vertex
				for ( Graph<E> attachedPiece : attachedPieces ) {
					// if we haven't seen this piece yet, remember the
					//  vertex at which we first saw it. 
					if ( !firstPieceVertex.containsKey(attachedPiece)) {
						firstPieceVertex.put( attachedPiece, cycleNode );
					} else if ( cycleNode.equals( 
							firstPieceVertex.get( attachedPiece ) ) ) {
						// if we're back to the first vertex for this piece,
						//  then we're done with it.
						firstPieceVertex.put( attachedPiece, null );
					}
					
					// For all pieces
					for ( Graph<E> piece : pieces ) {
						// if it's attached here, start the count at 1, 
						//  otherwise remove it (start at 0).
						if ( attachedPieces.contains( piece ) ) {
							pieceSeenCounts.get(attachedPiece).put(piece, 1);
						} else {
							pieceSeenCounts.get(attachedPiece).remove(piece);
						}
					}
					
				}
			} // Iterate over cycle vertices as cycleNode
		} // while !done
		
		// Create retval
		retval = new Graph<Graph<E>>();
		
		// Check the noninterlacedEdgeTable for pairs of pieces that are not
		//  interlaced.  All other pairs are interlaced.
		for( Graph<E> piece : pieces ) {
			for( Graph<E> otherPiece : pieces ) {
				// Don't even bother checking if a piece is interlaced with
				//  itself...
				if ( piece == otherPiece ) continue;
				
				// If we haven't found these two to not be interlaced, then
				//  they must be interlaced.
				if ( !nonInterlacedPieces.containsKey(piece) ||
						!nonInterlacedPieces.get(piece).contains(
								otherPiece ) ) {
					// Each edge will get added twice, but that's not a problem
					retval.addEdge( piece, otherPiece );
				}
			}
		}
		
		return retval;
	}
	
	/**
	 * Test whether this graph is planar or not.  This method partitions this
	 * graph into pieces with respect to a cycle that is calculated locally, 
	 * checks the interlacement graph of those pieces, and then recursively
	 * checks each piece for planarity.  The interlacement graph is checked for
	 * bipartite-ness before the pieces are recursively checked for planarity
	 * to reduce the number of recursive calls that are made unnecessarily.
	 * Note that this method only works on biconnected graphs.
	 * 
	 * @return True iff this graph is planar.
	 */
	@SuppressWarnings("unchecked")
	public boolean isPlanar() {
		// Check the number of edges first just to be sure.
		if ( getEdgeCount() > ( (3 * V.size()) - 6) ) return false;

		// Find a cycle.  There should be one since this should be a biconnected
		//  graph.  If there isn't, then we'll assume this graph is at least
		//  connected, in which case it must be a tree and therefore is planar.
		Graph<E> cycle = findCycle();
		// Acyclic connected graphs (trees) are always planar 
		if ( cycle == null ) return true;
		
		// Partition this graph into pieces with respect to the cycle we found.
		Collection<Graph<E>> pieces = getPieces( cycle );
		if ( pieces.size() == 0 ) {
			// If we have no pieces, this graph is trivially planar.
			return true;
		} else if ( pieces.size() == 1 ) {
			// We only have one piece, lets try to find a separating cycle;
			//  that is, one that generates at least 2 pieces.  If the new
			//  cycle still only generates one piece, then this graph is planar
			//  because the single piece we've found must be a path with only
			//  two attachments.
			Graph<E> piece = (Graph<E>)pieces.toArray()[0];
			
			// Find an attachment of piece that attaches it to cycle.
			E firstAttachment = null;
			for ( E vertex : cycle.getVertexSet() ) {
				if ( piece.getVertexSet().contains( vertex ) ) {
					firstAttachment = vertex;
					// Exit the for-each loop over cycle's vertices because
					//  we found an attachment of piece.
					break;
				}
			}

			// We need to traverse the cycle in order
			ArrayList<E> cycleNodes = 
				new ArrayList<E>(cycle.getVertexSet().size());
			cycleNodes.add( (E)cycle.getVertexSet().toArray()[0] );
			int size = 0;
			while ( !cycleNodes.containsAll( cycle.getVertexSet() ) && 
					size < cycleNodes.size() ) { 
				size = cycleNodes.size();
				for( E neighbor : cycle.getNeighbors( 
						cycleNodes.get( cycleNodes.size() - 1 ) ) ) {
					if ( !cycleNodes.contains( neighbor ) ) {
						// We found a vertex not already in cycleNodes, so
						//  add it and break out of the for-each over neighbors
						cycleNodes.add( neighbor );
						break;
					}
				}
			}
			
			// Reset size so we can use it in calculations below.
			size = cycleNodes.size(); 
			
			// Find a separating cycle given a single piece.
			Graph<E> newCycle = null;
			E secondAttachment = null;
			int firstAttachmentIndex = cycleNodes.indexOf( firstAttachment );
			for ( int i = (firstAttachmentIndex + 1) % size;
				i != firstAttachmentIndex; i = (i + 1)%size) {
				if ( (newCycle == null) && 
						piece.getVertexSet().contains( cycleNodes.get(i) ) ) {
					// If newCycle is null and we've found a second attachment,
					//  then we need to create newCycle and start building it.
					newCycle = new Graph<E>();
					secondAttachment = cycleNodes.get(i);
				}
				
				// Remove any vertices of newCycle from piece so that later,
				//  when we try to find a path through piece, we ensure we
				//  don't find a path that includes any vertices of newCycle
				//  besides attachment1 and attachment 2.
				if ( !cycleNodes.get(i).equals( secondAttachment ) ) {
					piece.removeVertex( cycleNodes.get(i) );
				}
				
				// If we're building the new cycle, add this edge to it.
				if ( newCycle != null ) {
					newCycle.addEdge( cycleNodes.get(i), 
							cycleNodes.get( (i+1) % size ) );
				}
			}
			
			if ( newCycle == null ) {
				// If there was only 1 attachment, then we aren't dealing with
				//  a biconnected graph for some reason, and this graph is
				//  planar iff the single singly-connected piece is planar.
				return piece.isPlanar();
			}
			
			// Add the path through the piece to get the new cycle.
			newCycle.addEdges( piece.findPath( firstAttachment,
												secondAttachment ) );
			
			// Get the new pieces with respect to the new cycle so we can do
			//  recursive calculations below if we need to.
			cycle = newCycle;
			pieces = getPieces( cycle );
		} // pieces.size() == 1
		
		// If we have more than 1 piece now, then we have more work to do, 
		//  otherwise this graph is planar (default return value).
		if ( pieces.size() > 1 ) {
			if ( !getInterlacementGraph( cycle, pieces ).isBipartite() ) {
				// If the interlacement graph isn't bipartite, then this graph
				//  must be non-planar.
				return false;
			}
			
			// Check all the pieces we found.  If any of the pieces are non-
			//  planar, then this graph is non-planar.  Note that the algorithm
			//  is recursively called on the graph comprised of the piece AND
			//  the separating cycle used in order to maintain biconnectedness.
			for ( Graph<E> piece : pieces ) {
				Graph<E> newGraph = cycle.clone();
				newGraph.addEdges( piece );
				if ( !newGraph.isPlanar() ) {
					// We've found a non-planar subgraph, this graph must be 
					//  non-planar.
					return false;
				}
			}
		}
		
		// We either have fewer than 2 pieces, or the pieces are all planar
		//  and the interlacement graph is bipartite, so we know this graph
		//  is planar.
		return true;
	}
	
	/**
	 * Converts the graph into a triangulated graph,
	 * while maintaining its planarity.
	 * 
	 * @return A triangulated (maximally) planar graph
	 */
	public Graph<E> triangulateGraph() {
		Graph<E> retVal = this.clone();
		Object[] vertices = retVal.getVertexSet().toArray();

		long start = System.currentTimeMillis();
		
		//Go through every (u,v) pair of vertices
		for(int i = 0; i < vertices.length-1; i++){
			E u = (E)vertices[i];
			Set<E> neighbors = retVal.getNeighbors(u);
			
			for(int k = i+1; k < vertices.length; k++){
				//Check if adding any more edges will violate the Euler rule
				//If it does, retVal is triangulated, so return it
				if ( retVal.getEdgeCount()+1 > ( (3 * (retVal.getVertexSet().size())) - 6) ){ 
					System.out.println("Triangulation Run Time: " + (System.currentTimeMillis()-start) + "ms");
					return retVal;
				}
				
				E v = (E)vertices[k];
				
				//If the edge doesn't already exist
				if(!neighbors.contains(v)){
					//Add the edge
					retVal.addEdge(u, v);
					
					//If the new edge makes the graph nonplanar, remove it
					if(!retVal.isPlanar()){
						retVal.removeEdge(u, v);
					//Else add the edge to the triangulated graphs addedEdges set,
					//used when drawing to change the edge colors.
					}else{
						retVal.addedEdges.add(new Pair((Integer)u, (Integer)v));
					}
				}
			}
		}
		
		//Will never get here, will always exit when checking the Euler rule
		return retVal;
	}
	
	/**
	 * Do parts A and B of the algorithm.
	 * A: Find an outer face
	 * B: Compute the canonical ordering
	 * 
	 * @return Stack of vertices in canonical order. v1 and v2 should be popped off first.
	 *
	 */
	public Stack<Integer> findCanonicalOrdering(){
		Stack<Integer> canonical = new Stack<Integer>();
		
		//Create a tmpGraph to remove vertices from
		Graph<E> tmpGraph = clone();
		
		//Find an initial outer face
		List<E> outerFace = tmpGraph.findFace();
		
		System.out.println("Outer face [v1, v2, vn]: " + outerFace);
		
		if(outerFace == null || outerFace.size() < 3){
			System.out.println("Error finding canoncial order");
		}
		
		//Pick 2 vertices
		E v1 = outerFace.get(0); 
		E v2 = outerFace.get(1);
		
		System.out.println("Finding the canonical ordering ...");
		
		int i = 0;
		//While there are more than just v1 and v2 on the outer face
		while(outerFace.size() != 2){
			E v = outerFace.get(i);
			
			//If v is v1 or v2, move to next outer vertex
			if(v == v1 || v == v2){
				i++;
			}else{
				//Check if the vertex is incident to a chord.
				//I.E edges > 2 to other vertices on the outerFace
				int edges = 0;
				Set<E> neighbors = tmpGraph.getNeighbors(v);
				for(E n : neighbors){
					//Increase edge count if n is on the outer face
					if(outerFace.contains(n)) edges++;
					
					//If edges is 3 or more, it's part of a chord,
					//stop checking neighbors
					if(edges > 2) break;
				}
				
				//If vertex is not part of a chord,
				//vertex v can be removed.
				if(edges == 2){
					//Add v to stack
					canonical.push((Integer)v);
					
					//Remove v from outerFace
					outerFace.remove(v);
					
					//Add v's neighbors to outerFace
					Set<E> tmp = tmpGraph.getNeighbors(v);
					for(E add : tmp){
						if(!outerFace.contains(add)) outerFace.add(add);
					}
					
					//Remove v from graph
					tmpGraph.removeVertex(v);
					
					//Reset i to try and remove a new outerFace vertex
					i = 0;
					
				}else{
					//Move to next vertex
					i++;
				}
			}
			
		}
		
		//Push the initial face vertices on the stack,
		//will be popped off first
		canonical.push((Integer)v2);
		canonical.push((Integer)v1);
		
		System.out.println("Canonical Ordering [vn, .. , v2, v1]: " + canonical);
		
		return canonical;
	}

	/**
	 * Return the set of added edges for the triangulated graph.
	 * 
	 * @return Set of added edges (as a Pair object)
	 */
	public Set<Pair> getAddedEdges(){
		return addedEdges;
	}
	
	/**
	 * Go through every (v,u,w) distinct vertices that create 
	 * a 3-cycle, then if the cycle is not a separating cycle,
	 * it can be used in the drawing algorithm.
	 * 
	 * @return the set of vertices that create a non-separating outer 3-cycle
	 */
	private List<E> findFace(){
		List<E> retVal = new ArrayList<E>();
		
		//For each vertex
		for(E v : getVertexSet()){
			
			//For each vertex's neighbor
			Set<E> vNeighbors = getNeighbors(v);
			for(E n : vNeighbors){
				
				//For each neighbor's neighbor, try to find a 3-cycle
				Set<E> nNeighbors = getNeighbors(n);
				for(E s : nNeighbors){
					
					//If s is a common neighbor to both v and n, check if separating cycle
					if(vNeighbors.contains(s) && nNeighbors.contains(s)){
						
						//Create a temp graph to represent the cycle
						Graph<E> tmp = new Graph<E>();
						tmp.addEdge(v, n);
						tmp.addEdge(v, s);
						tmp.addEdge(n, s);
						
						//Get the pieces that the graph is split into based on the cycle
						Collection<Graph<E>> pieces = getPieces(tmp);
						//If it is less than 2 pieces, then 3-cycle is not
						//a separating cycle
						if ( pieces.size() < 2 ) {
							//Return the 3 vertices on the cycle
							retVal.addAll(tmp.getVertexSet());
							return retVal;
						}
					}
				}
			}
		}
		//If it gets here than no non-separating 3-cycle was found,
		//which should never happen, return null.
		return null;
	}
	
}

