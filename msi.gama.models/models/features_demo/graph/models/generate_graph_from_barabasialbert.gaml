/**
 *  generate_graph_barabasi_albert
 *  Author: Samuel Thiriot
 *  Description: Shows how to generate a scale-free graph using a Barabasi-Albert scale-free generator. 
 * Nothing "moves" into this first model.
 */

model generate_graph_barabasi_albert
 
global {
	
	/*
	 * The variable that will store the graph 
	 */  
	graph my_graph <- generate_barabasi_albert(nodeSpecy,edgeSpecy,100,4);
	
	init {	
		 write "" + my_graph;
	}

	reflex truc {		
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
		graphdisplay monNom2 graph: my_graph lowquality:true ;
			
	}		
}
