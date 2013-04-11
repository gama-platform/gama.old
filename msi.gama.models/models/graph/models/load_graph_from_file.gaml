/**
 *  loadgraphfromfile
 *  Author: Samuel Thiriot
 *  Description: Shows how to load a graph from a file and how to display it. Nothing "moves" into this first model.
 */

model loadgraphfromfile

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
			draw color ;
			
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