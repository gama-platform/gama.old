model Graph

import 'bug.gaml'


global{
	graph myGraph;
	float distance parameter: 'Distance' min: 1.0 <- 10.0 category: 'Model';
	
		
	reflex initEdge when: cycle = 1{
     	write "cycle : " + cycle;
     	set myGraph <- as_distance_graph(list(node), map(["distance"::distance, "species":: edge])); 
   	}	
    
     reflex updateEdge when: every(20){ 
     	ask edge as list {do die;}
    	set myGraph <- as_distance_graph(list(node), map(["distance"::distance, "species":: edge])); 
   	}
   	
   //	reflex bug_death when: cycle = 50 { ask bug {if (flip(0.5)) {do die;}}} //ATTENTION, AVEC CE REFLEX CELA PLANTE A CAUSE DE L ATTRIBUT location des agents node :point location <- target.location value: target.location;
   
}
 
entities{
	
	species node mirrors: list(bug) {
		point location <- target.location value: target.location;
		aspect base{
		  int degree <-myGraph = nil ? 0.0 : myGraph degree_of(self);
		   draw sphere(1+ (degree / 5.0)) color: rgb('blue');
		  
		 
		}
   }
   
   species edge{
   	aspect base{
   		float val <- 255.0 *  (shape.perimeter / distance);
   		draw shape + 0.5 color:rgb([val, 255, val]);
   	}

   }	
}

experiment basicGraph type: gui {
	output {
	   display graph_plus_bug type:opengl ambiant_light:0.2{
	    	species bug aspect:base;
	        species node aspect: base z:0.2;
	        species edge aspect:base z:0.2;
	    }
	} 
}
