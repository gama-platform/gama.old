model Graph

import 'bug.gaml'


global{
	graph myGraph;
	int distance parameter: 'Distance' min: 1 <- 10 category: 'Model';
			
   /*  reflex updateGraph{
    	set myGraph <- list(node) as_distance_graph distance;
    }*/

	reflex updateEdge{
		ask edge as list{
			do die;
		}
		ask node as list{
			let neighbours <- (self neighbours_at (distance));
			ask neighbours as list{      
		      if(self is node){
		      	let src <-self;
		      	let dest <- myself;
		      	create edge{
		      		set self.src <- src;
		      		set self.dest <- dest;
		      	}
		      }
		    }  
		}
	}
	
}

entities{
	
	species node mirrors: list(bug){
		point location <- target.location value: target.location;
		aspect base{
		  draw sphere(1) color: rgb('blue');
		}
   }
   
   species edge{
   	node src;
   	node dest;	
   	aspect base{
   		draw line( [ src.location, dest.location] ) color:rgb('blue');
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
