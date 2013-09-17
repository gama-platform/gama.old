/**
 *  generate_graph_wattsstrogatz
 *  Author: Samuel Thiriot
 *  Description: Shows how to generate a scale-free graph using a Wattstrogatz generator. 
 * Nothing "moves" into this first model.
 */

model generate_graph_wattsstrogatz
  
global {
	
	int net_size <- 250;
	int net_neighboors <- 4;
	float net_prewire <- 0.08;
	
	/*
	 * The variable that will store the graph
	 */  
	graph my_graph <- generate_watts_strogatz(
				nodeSpecy,
				edgeSpecy,
				net_size,
				net_prewire,
				net_neighboors
		);	  
	
	init {		
		write "" + my_graph;		
	 }
}

entities {

	/*
	 * The specy which will describe nodes. 
	 * Note that these agents will be implicitely
	 * initialized with default x,y random locations.
	 */
	species nodeSpecy  {
		rgb color <- rgb('black') ;  
		aspect base { 
			draw circle(3) color: color ;
		}		
	}
	
	/*
	 * The specy which will describe edges. 
	 */
	species edgeSpecy  { 
		rgb color <- rgb('blue') ; 
		
		aspect base {
			draw shape color: color ;		
		}		
	}
}
experiment generate_graph type: gui {
	
	parameter 'Number of vertices' var: net_size <- 250 category: 'network' ;
	parameter 'Number of neighboors' var: net_neighboors <- 4 category: 'network' ;
	parameter 'Rewire probability'  var: net_prewire <- 0.08 category: 'network' ;
	
	output {
		
		/*
		 * This first display is the classical GAMA display: 
		 * agents are represented according to their aspects (shapes)
		 * and location. This provides a spatialized view of the network.
		 * 
		 */
		display test_display  {
			species nodeSpecy aspect: base ; 
			species edgeSpecy aspect: base ;
		}
		
		/*
		 * This display provides another look on the network,
		 * without spatiality.
		 */
		graphdisplay monNom2 graph: my_graph lowquality:true;	
	}
		
}