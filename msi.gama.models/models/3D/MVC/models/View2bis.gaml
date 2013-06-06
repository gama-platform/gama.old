model View2

import 'View1.gaml'

global{
	
}

entities{
	
	/*
	 * The species node mirrors the bug species.
	 */
	species node2 mirrors: list(node){
		point location <- target.location update: target.location;
		aspect dynamic{
		int degree <-2;//graph_node(target).my_graph degree_of(target);
		  draw sphere(2) color: rgb('blue');
		}
   }
   
   species edge2 mirrors:list(edge){	
   	rgb color;
   	aspect dynamic{
   	  float val <- 255.0 *  (shape.perimeter / distance);
   	  set  color <- color hsb_to_rgb ([val,1.0,1.0]);
   	  draw shape + 0.1 color:color border: color ;	
   	}
   }	
}

experiment view2 type: gui {
	output {
	    display view2 type:opengl ambient_light:100 {        
	        species node2 aspect: dynamic z:0;
	        species edge2 aspect: dynamic z:0;
	    }
	} 
}
