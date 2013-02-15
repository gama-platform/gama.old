model Graph

import 'bug.gaml'


global{
		
	int distance parameter: 'Distance' min: 1 <- 10 category: 'Model';	
		
	reflex updateNode{
		ask node as list{
			do die;
		}
		ask bug as list{
			let target <-self;
			create node{
				set self.target<-target;
				set self.location<-target.location;
			}
		}
	}
	reflex updateEdge{
		ask edge as list{
			do die;
		}
		ask bug as list{
			let neighbours <- (self neighbours_at (distance));
			ask neighbours as list{      
		      if(self is bug){
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
	
	species node {
		bug target;
		aspect basic{
		  draw sphere(1) color: rgb('blue');
		}
   }
   
   species edge{
   	node src;
   	node dest;	
   	aspect basic{
   		draw line( [ src.location, dest.location] ) color:rgb('blue');
   	}
   }	
}

experiment basicGraph type: gui {
	output {
	    display graph_plus_bug type:opengl ambiant_light:0.2{
	    	species bug aspect:basic;
	        species node aspect: basic z:0.1;
	        species edge aspect:basic z:0.1;
	    }
	} 
}
