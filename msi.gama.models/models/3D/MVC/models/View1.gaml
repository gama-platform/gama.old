model View1

import 'model.gaml'


global{
	int distance parameter: 'Distance' min: 1 <- 20 category: 'Model';	
}

entities{
	
	species node mirrors: list(bug) parent: graph_node edge_species:edge {
		point location <- target.location update: target.location;
		
		//Update the interaction between node
		//two node are connected if their euclidian distance is smaller than a given value.
		bool related_to(node other){
			using topology(target){
				return (target distance_to other.target) < distance;
			}
		}
		aspect base{
		  draw sphere(1) color: rgb('green');
		}
   }
   
   species edge parent:base_edge{	
   	rgb color;

    
   	aspect base{
   		draw shape color: rgb("green");
   	}
   	
   	aspect dynamic{
   	  float val <- 255.0 *  (shape.perimeter / distance);
   	  set  color <- color hsb_to_rgb ([val,1.0,1.0]);
   	  draw shape + 0.1 color:color border: color ;	
   	}
   }	
}

experiment view1 type: gui {
	output {
	    display view1 type:opengl ambient_light:100 {
	        species node aspect: base z:0 position: {0,0,0};
	        species edge aspect: base z:0 position: {0,0,0};
	    }
	} 
}
