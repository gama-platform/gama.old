model Graph

import 'bug.gaml'

global{
	
}

entities{
	species node {
		aspect basic{
		  draw sphere(1) color: rgb('blue');
		}
   }	
}

experiment basicGraph type: gui {
	output {
	    display graph type:opengl{
	        species node aspect: basic;
	    }
	} 
}
