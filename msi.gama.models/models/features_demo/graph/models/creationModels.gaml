/**
 *  creationModels
 *  Author: Samuel Thiriot
 *  Description: 
 */

model creationModels


global {
	
	int popsize <- 25 parameter: 'Number of individuals' category: 'network' ;
	
	graph net_friendship <- generate_watts_strogatz(humans, friendship, popsize, 0.3, 2);
			
	init {
		
		write "" + net_friendship ;
	
		create humans number: 10;
	}
	
	
	  
}


entities {

	species humans {
		
		rgb color <- rgb('black') ;  
		
		aspect basic { 
			draw circle(6) color: color ;
		} 
	}
	
	species friendship  { 
		rgb color <- rgb('blue') ; 
		
		aspect basic {
			draw circle(4) color: color ;
			
		}
	
	}
}

experiment load_graph type: gui {
	
	output {
		
		
		display test_display refresh_every: 1 {
			species humans aspect: basic ; 
			species friendship aspect: basic ;
		}
		
		/*
		 * This display provides another look on the network,
		 * without spatiality.
		 */
		graphdisplay monNom2 graph: net_friendship lowquality:true;
	}
}