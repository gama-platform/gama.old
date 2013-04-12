/**
 *  remove_node
 *  Author: Samuel Thiriot
 *  Description: Use of remove_node_from operator. 
 * Nothing "moves" into this first model.
 */

model remove_node
  
global {
	
	int net_size <- 250 parameter: 'Number of vertices' category: 'network' ;
	int net_neighboors <- 4 parameter: 'Number of neighboors' category: 'network' ;
	float net_prewire <- 0.08 parameter: 'Rewire probability' category: 'network' ;
	
	/*
	 * The variable that will store the graph
	 */  
	graph my_graph <- generate_watts_strogatz(nodeSpecy,edgeSpecy,net_size,net_prewire,net_neighboors);	
		
	
	init {
		
		write my_graph;
		  
	 }
}

environment {
	
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
		 		
		reflex removing_node when: flip(0.1) {
			set my_graph <- remove_node_from(self, my_graph);
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
experiment remove_node type: gui {
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
		graphdisplay graph graph: my_graph lowquality:true ;
	}
}
