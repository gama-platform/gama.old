/**
 *  loadgraphfromfile
 *  Author: Samuel Thiriot
 *  Description: Shows how to generate a scale-free graph using a Barabasi-Albert scale-free generator. 
 * Nothing "moves" into this first model.
 */

model loadgraphfromfile
  
global {
	
	int net_size <- 250 parameter: 'Number of vertices' category: 'initial network' ;
	int net_neighboors <- 4 parameter: 'Number of neighboors' category: 'initial network' ;
	float net_prewire <- 0.0 parameter: 'Rewire probability' category: 'initial network' ;
	float net_prewire_dynamic <- 0.01 parameter: 'Rewire probability dynamic' category: 'network evolution' ;
	
	/*
	 * The variable that will store the graph
	 */  
	var mongraphe type:graph;
	
	init {
		
		 /*
		  * The actual generation of the network. 
		  * Note that for technical reasons, parameters are provided as a gama map.  
		  */
		set mongraphe <- generate_watts_strogatz( [
				"edges_species"::edgeSpecy,
				"vertices_specy"::nodeSpecy,
				"size"::net_size,
				"p"::net_prewire,
				"k"::net_neighboors
			] );
			  
	 }
	 
	
	  reflex {
			set mongraphe <- rewire(mongraphe, net_prewire_dynamic);
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
			draw shape: circle size:3 color: color ;
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
	graphdisplay monNom2 graph: mongraphe lowquality:true {
		 
	}
	
}