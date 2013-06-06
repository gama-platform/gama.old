model Graph

import 'bug.gaml'


global{
	int distance parameter: 'Distance' min: 1 <- 25 category: 'Model';	
}

entities{
	
	/*
	 * The species node mirrors the bug species.
	 */
	species node mirrors: list(bug) parent: graph_node edge_species:edge {
		point location <- target.location update: target.location;
		
		//Update the interaction between node
		//two node are connected if their euclidian distance is smaller than a given value.
		bool related_to(node other){
			using topology(target){
				return (target.location distance_to other.target.location) < distance;
			}
		}
		aspect base{
		  draw sphere(2) color: rgb('green');
		}
		aspect dynamic{
		  int degree <-(my_graph) degree_of(self);
		  draw sphere(1+ (degree / 5.0)) color: rgb('blue');
		}
		aspect dynamic3D{
		  int degree <-(my_graph) degree_of(self);
		  set shape <- sphere(1+ (degree / 5.0));
		  set location <- location add_z degree;
		  draw shape color: rgb('blue'); 
		}

   }
   
   species edge parent:base_edge{	
   	rgb color;

    
   	aspect base{
   		draw shape color: rgb("green");
   	}
   	
   	aspect dynamic{
   	  set shape <- geometry (line ([{self.source.location.x,self.source.location.y,self.source.location.z},{self.target.location.x,self.target.location.y,self.target.location.z}]));	
   	  float val <- 255.0 *  (shape.perimeter / distance);
   	  set  color <- color hsb_to_rgb ([val,1.0,1.0]);
   	  draw shape + 0.1 color:color border: color ;
   	}
   	
   	aspect dynamic3D{	
   		
   	  float val <- 255.0 *  (shape.perimeter / distance);
   	  set shape <-geometry (line ([{self.source.location.x,self.source.location.y,self.source.location.z},{self.target.location.x,self.target.location.y,self.target.location.z}]));
   	  set  color <- color hsb_to_rgb ([val,1.0,1.0]);
   	  draw shape  color:color border: color ;	
   	}
   }	
}

experiment basicGraph type: gui {
	output {

	    
	    display graph_plus_bug type:opengl ambient_light:0.2 {
	    	species bug aspect:base;
	        species node aspect: base z:0 position: {125,0,0};
	        species edge aspect: base z:0 position: {125,0,0};
	        species node aspect: dynamic z:0 position: {250,0,0};
	        species edge aspect: dynamic z:0 position: {250,0,0};
	    }
	    
	    
	    //FIXME: When this display is on the shape of the node are modified (z value !=0)
	    /*display graph_3D type:opengl  ambient_light:100 {
	        species node aspect: dynamic3D;
	        species edge aspect: dynamic3D;
	    }*/
	    
	    display graph_plus_bug_layered type: opengl ambient_light: 0.2 {
			species bug aspect: base;
			species node aspect: base z: 0.2;
			species edge aspect: base z: 0.2;
			species node aspect: dynamic z: 0.4;
			species edge aspect: dynamic z: 0.4; 
		}
	} 
}
