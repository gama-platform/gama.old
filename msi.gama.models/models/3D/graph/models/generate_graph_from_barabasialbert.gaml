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
	graph my_graph ;
	
	init {
		
		 /*
		  * The actual generation of the network. 
		  * Note that for technical reasons, parameters are provided as a gama map.  
		  */
		 set my_graph <- generate_barabasi_albert( [
				"edges_specy"::edgeSpecy,
				"vertices_specy"::nodeSpecy,
				"size"::100,
				"m"::2
			] );
		
		ask edgeSpecy as list{
			set color <- [rnd(100),rnd(100) ,rnd(100)] as rgb;
		}	  
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
		rgb color <- [rnd(255),rnd(255) ,rnd(255)] as rgb;
  
		aspect base {
			let colorValue <- (location.x + location.y);
			draw sphere(1) color: rgb([colorValue, colorValue, colorValue]) at: location; 
		}  		
	}
	
	/*
	 * The specy which will describe edges. 
	 */
	species edgeSpecy  { 
		rgb color;// <- rgb('blue') ; 
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
		display test_display type:opengl {
			species nodeSpecy aspect: base ; 
			species edgeSpecy aspect: base ;
		}	
		
	
	}		
}
