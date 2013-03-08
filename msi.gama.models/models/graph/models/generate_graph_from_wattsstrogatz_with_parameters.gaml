/**
 *  generate_graph_wattsstrogatz
 *  Author: Samuel Thiriot
 *  Description: Shows how to generate a scale-free graph using a Wattstrogatz generator. 
 * Nothing "moves" into this first model.
 */

model generate_graph_wattsstrogatz
  
global {
	
	int net_size <- 250 parameter: 'Number of vertices' category: 'network' ;
	int net_neighboors <- 4 parameter: 'Number of neighboors' category: 'network' ;
	float net_prewire <- 0.08 parameter: 'Rewire probability' category: 'network' ;
	
	/*
	 * The variable that will store the graph
	 */  
	graph my_graph;
	
	init {
		
		 /*
		  * The actual generation of the network. 
		  * Note that for technical reasons, parameters are provided as a gama map.  
		  */
		set my_graph <- generate_watts_strogatz( [
				"edges_specy"::edgeSpecy,
				"vertices_specy"::nodeSpecy,
				"size"::net_size,
				"p"::net_prewire,
				"k"::net_neighboors
			] );	  
	 }
}

environment;

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
			draw color: color ;
			
		}
		
	}
}
experiment generate_graph type: gui {
	output {
		
		/*
		 * This first display is the classical GAMA display: 
		 * agents are represented according to their aspects (shapes)
		 * and location. This provides a spatialized view of the network.
		 * 
		 */
		display test_display refresh_every: 1 {
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