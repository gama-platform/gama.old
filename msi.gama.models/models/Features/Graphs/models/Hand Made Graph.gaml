/**
 *  Hand made graph
 *  Author: Patrick Taillandier
 *  Description: Show how to build a graph from scratch
 */

model hameMadeGraph

global {
	graph the_graph1 ;
	graph the_graph2;
	
	init {
		the_graph1 <- as_edge_graph([edge({10,5}, {20,3}), edge({10,5}, {30,30}),edge({30,30}, {80,35}),edge({80,35}, {40,60}),edge({80,35}, {10,5}), node ({50,50})]);	
		
		the_graph2 <- graph([]);
		//first way to add nodes and edges
		add node: {50,50} to: the_graph2;
		add edge: {10,10}::{90,50} to: the_graph2;
		
		//second way to add nodes and edges
		the_graph2 <- the_graph2 add_node {10,40} ;
		the_graph2 <- the_graph2 add_edge ({35,50}::{50,50}) ;
	}
	
}

species edge_agent {
	aspect default {	
		draw shape color: rgb("black");
	}
}

species node_agent {
	aspect default {	
		draw circle(1) color: rgb("red");
	}
}

experiment create_graph type: gui {
	
	output {
		display graph1 type: opengl{
			graphics "the graph 1" {
				loop e over: the_graph1.edges {
					draw e color: °blue; 
				}
				loop n over: the_graph1.vertices {
					draw circle(2) at: point(n) color: °blue; 
				}
			}
		}
		display graph2 type: opengl{
			graphics "the graph 2" {
				loop e over: the_graph2.edges {
					draw e color: °red; 
				}
				loop n over: the_graph2.vertices {
					draw circle(2) at: point(n) color: °red; 
				}
			}
		}
	}
}
