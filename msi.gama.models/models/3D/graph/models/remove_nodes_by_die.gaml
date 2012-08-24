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
		 set my_graph <- load_graph_from_dgs_old( [
				"filename"::"../includes/BarabasiGenerated.dgs", 
				"edges_specy"::edgeSpecy,
				"vertices_specy"::nodeSpecy
			] );
			
		/*ask nodeSpecy as list{
			write self.name;
			//set self.shape <- self.shape add_z 1;
			loop i from: 0 to: length(self.shape.points) - 1{ 
				set shape <- shape add_z_pt {i,1};
			}
			
		}*/	
	  
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
		geometry shape <- geometry (point([location.x,location.y])) ;
		//geometry shape <- circle (1) ;  
		aspect base {
			draw shape: geometry color: color  ; 
		} 
		
	}

	
	/*
	 * The specy which will describe edges. 
	 * Note that these edges are automatically plugged in nodes so they are represented as lines.
	 */
	species edgeSpecy  { 
		rgb color <- rgb('blue') ; 
		
		aspect base {
			draw color: color ;	
		}
		 		
		/*
		 * This reflex kills 5% of edge agents per time step.
		 */
		reflex dying when: flip(0.05) {
			do die;
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
		display test_display refresh_every: 1 type:opengl {
			species nodeSpecy aspect: base ; 
			species edgeSpecy aspect: base ;
		}
		
		
	}
}