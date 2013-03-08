/**
 *  SimpleTorus2D
 *  Author: Patrick Taillandier, Truong Xuan Viet
 *  Description: 
 */

model SimpleTorus2D

global {
    init {
        create GeometryObject number: 10;
    }
} 

environment width: 20 height: 20 torus: true {
    
}

entities {
    species GeometryObject skills: [moving] {
    	//var shape type: geometry init: polygon([{1,2}, {2,1},{2,-1},{-1,-1},{-1, 1}, {1,2}]); 
    	var shape type: geometry init: circle (1);// at_location {1,1}; 
    	
        reflex basic_move {
        	do wander speed: 3;
            /*let place type: stupid_cell <- (location as stupid_cell);
            let destination type: stupid_cell <- one_of ((place neighbours_at 4) where empty(each.agents));
            if (destination != nil) {
                set location <- destination.location;
            }*/
            
            
        }
        aspect basic {
            //draw geometry: circle color: rgb('red') size: 1;
            draw shape color: rgb ([255, 0, 0]);
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
