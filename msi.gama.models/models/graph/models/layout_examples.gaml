/**
 *  layoutexamples
 *  Author: Samuel Thiriot
 *  Description: examples of graph layouts
 */

model layoutexamples

/*
 * Sometimes the space in the model is not mapped with an actual, spatial space;
 * in this case, a modeler could be interest in doing a layout of a graph of
 * agents.
 * 
 * This is weird, anyway. 
 * 
 * In this example:
 * - init creates a network placed randomly on the display,
 * - step runs a layout
 * you can change the layout in the reflex below to observe various results
 */

global {
	
	/*
	 * The variable that will store the graph
	 */  
	graph my_graph;
		 
	init {
			  
		my_graph  <- generate_watts_strogatz(
				nodeSpecy,
				edgeSpecy,
				200,
				0.05,
				2
		);	  
		
		// print the graph in the console
		write my_graph;

	 }
	 
	 reflex dolayout {
	 	
	 	write "layout !";
	 	
	 	// several layouts are available:
	 	// - forcedirected 
	 	// - fruchtermanreingold 
	 	// - circle 
	 	// - radialtree
	 	// - random
	 	// - squarifiedtreemap
	 	my_graph <- layout(my_graph, "fruchtermanreingold", 100);
	 	
	 }
	  
}

environment ;

entities {

	/*
	 * The specy which will describe nodes. 
	 * Note that these agents will be implicitely
	 * initialized with default x,y random locations.
	 */
	species nodeSpecy  {
		rgb color <- rgb('black') ;  
		aspect base { 
			draw circle(1) color: color ;
		} 
		 		
	}
	
	/*
	 * The specy which will describe edges. 
	 */
	species edgeSpecy  { 
		rgb color <- rgb('blue') ; 
		
		aspect base {
			draw shape color:color;
			
		}
		
	}
}

experiment load_graph type: gui {
	output {
		
		/*
		 * This first display is the classical GAMA display: 
		 * agents are represented according to their aspects (shapes)
		 * and location. This provides a spatialized view of the network.
		 * 
		 */
		display test_display refresh_every: 1 type:opengl {
			species nodeSpecy aspect: base ; 
			species edgeSpecy aspect: base ;
		}
		
		/*
		 * This display provides another look on the network,
		 * without spatiality.
		 */
		//graphdisplay monNom2 graph: my_graph lowquality:true;
	}
}