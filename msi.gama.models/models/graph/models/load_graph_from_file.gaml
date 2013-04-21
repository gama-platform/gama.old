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
	/* 
	graph my_graph <- load_graph_from_file("../includes/ProteinSimple.anUnknownExtention", 
				nodeSpecy,
				edgeSpecy
		 );
		*/ 
	graph my_graph <- load_graph_from_file("gml","../doc/test.gml", 
				nodeSpecy,
				edgeSpecy
		 );
		 
	init {
		/*	  
		my_graph  <- generate_watts_strogatz(
				nodeSpecy,
				edgeSpecy,
				200,
				0.05,
				2
		);
		* 
		*/	  
		
		// print the graph in the console
		write my_graph;

	 }
	 
	 reflex dolayout {
	 	
	 	write "layout !";
	 	//my_graph <- layout_offline(my_graph, "fruchtermanreingold", 1000);
	 	
	 	//my_graph <- layout_offline(my_graph, "fruchtermanreingold", 1000);
	 	// forcedirected fruchtermanreingold circle radialtree
	 	my_graph <- layout(my_graph, "radialtree", 500);
	 	
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
		int age;
		aspect base { 
			draw circle(1) color: color ;
		} 
		 		
	}
	
	/*
	 * The specy which will describe edges. 
	 */
	species edgeSpecy  { 
		rgb color <- rgb('blue') ; 
		float strengh;
		aspect base {
			draw shape color:color;
			
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
		display test_display refresh_every: 1 type:opengl {
			species nodeSpecy aspect: base ; 
			species edgeSpecy aspect: base ;
		}
		
		/*
		 * This display provides another look on the network,
		 * without spatiality.
		 */
		//graphdisplay monNom2 graph: my_graph lowquality:true;
	}
}