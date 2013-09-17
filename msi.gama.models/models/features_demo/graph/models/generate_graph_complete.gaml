/**
 *  generate_graph_complete
 *  Author: bgaudou
 *  Description: Example of generation of complete graph with and without standard circle layout
 */

model generategraphcomplete

global {
	int net_size <- 5;  

	/*
	 * The variable that will store the graph 
	 */   
 	graph my_graph <- generate_complete_graph(nodeSpecy,edgeSpecy,net_size);
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
			draw circle(1) color: color ;
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

experiment generategraphcomplete type: gui {
	parameter 'Number of vertices' var: net_size <- 5 category: 'network' ;	
	
	output {
		display mon_graph  {
			species nodeSpecy aspect: base ; 
			species edgeSpecy aspect: base ;
		}

		graphdisplay monNom2 graph: my_graph lowquality:true;	
	}
}
