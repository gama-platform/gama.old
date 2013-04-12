/**
 *  node_die
 *  Author: Samuel Thiriot
 * Description: In this demo model, a graph is loaded and associated with species; 
 * then edges are randomly removed (die). This shows how the graph changes as the corresponding
 * agents die, and how it's display changes in the graph display.
 */

model node_die

global {
	
	/* 
	 * The variable that will store the graph
	 */  
	graph my_graph;
	
	init {
		
		 /*
		  * The actual loading of the network. 
		  * Note that for technical reasons, parameters are provided as a gama map.  
		  */
		 my_graph <- load_graph_from_file(
		 		"dgs_old",
				"../includes/BarabasiGenerated.dgs", 
				nodeSpecy,
				edgeSpecy	
		);
		
		write my_graph;
			  
	 }
	  
}

environment;

entities {

	/*
	 * The specy which will describe nodes. 
	 * Note that these agents will be implicitely
	 * initialized with default x,y random locations.
	 */
	species nodeSpecy {
		rgb color <- rgb('black') ;   
		aspect base { 
			draw circle(3) color: color ;
		} 
		
	}
	
	/*
	 * The specy which will describe edges. 
	 * Note that these edges are automatically plugged in nodes so they are represented as lines.
	 */
	species edgeSpecy  { 
		rgb color <- rgb('blue') ; 
		
		aspect base {
			draw shape color: color ;
			
		}
		 		
		/*
		 * This reflex kills 5% of edge agents per time step.
		 */
		reflex dying when: flip(0.05) {
			do die;
			write my_graph;
		}
		
	}
}
experiment node_die type: gui {
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
		 * without spatiality. Thus we do not see the nodes moving 
		 * in the space as in the first display.
		 */
		graphdisplay monNom2 graph: my_graph lowquality:true ;
		
	}
}