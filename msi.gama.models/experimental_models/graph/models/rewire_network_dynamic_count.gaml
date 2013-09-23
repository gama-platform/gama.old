/**
 *  rewire_graph
 *  Author: Samuel Thiriot
 *  Description: Shows how to rewire a graph during the simulation. 
 */

model rewire_graph
  
global {
	
	int net_size <- 250 parameter: 'Number of vertices' category: 'initial network' ;
	int net_neighboors <- 4 parameter: 'Number of neighboors' category: 'initial network' ;
	float net_prewire <- 0.0 parameter: 'Rewire probability' category: 'initial network' ;
	int net_rewire_count <- 1 parameter: 'Count of edges to rewire per step' category: 'network evolution' ;
	
	/*
	 * The variable that will store the graph
	 */  
	graph<nodeSpecy,edgeSpecy> my_graph <- generate_watts_strogatz(nodeSpecy, edgeSpecy, net_size, net_prewire,net_neighboors);
			
	
	reflex rewiring{
			//set my_graph <- rewire_n(my_graph, net_rewire_count);
			
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
experiment rewire_graph type: gui {
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
		graphdisplay rewire_graph graph: my_graph lowquality:true ;
	}	
}