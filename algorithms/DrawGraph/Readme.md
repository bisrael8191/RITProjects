Project 1 - Drawing planar graphs
Brad Israel
4005.800

Extract the jar
Compile: javac DrawGraph.java
Run: java DrawGraph inputFile

Files:
DrawGraph.java - main file, controls execution.
Graph.java - All graph manipulation and testing methods.
DrawGraphLogic.java - Maps the vertices to grid coordinates, given the triangulated graphs canonical ordering.
SvgGraphics.java - Basic class to help adding vertices and edges to an SVG file.

Notes:
You can adjust the scale of the drawing by changing the scale variable in DrawGraph.java and recompiling.
If you make the scale to small, < 20, the nodes in smaller graphs will touch and you may not be able to 
see the connecting edges. Just increase the scale to fix it. On larger graphs some edges might pass behind
other nodes making it difficult to tell if the edge stopped at the node or passed behind it. Increasing the 
scale should also fix this.