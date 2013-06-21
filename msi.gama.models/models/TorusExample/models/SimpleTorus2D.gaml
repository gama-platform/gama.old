/**
 *  SimpleTorus2D
 *  Author: Patrick Taillandier, Truong Xuan Viet
 *  Description: 
 */

model SimpleTorus2D

global torus: true{
	geometry shape <- square(20);
    init {
        create GeometryObject number: 10;
    }
} 


entities {
    species GeometryObject skills: [moving] {
    	geometry shape <- circle (1); 
    	rgb color <-  rgb (rnd(255),rnd(255),rnd(255));
    	
        reflex basic_move {
        	do wander speed: 1; 
           
        }
        aspect basic {
            draw shape color: color;
        }
    }
}

experiment torusModel type: gui {
	output {
	    display torus_display {
	        species GeometryObject aspect: basic;
	    }
	} 
}
