/**
* Name: experimentSerialize
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model experimentSerialize

global {	
	init {
		create sp number: 1 {
			shape <- cube(cycle + 2);
		}
	}
}

species sp {
	reflex moveSP {
		location <- any_location_in(world.shape);
	}
	
	aspect circle {
		draw shape border: #black;
	}
}

experiment experimentSerialize type: memorize {	
	output {
		display display1 type: opengl {
			species sp aspect: circle;
		}
		
		display d2 {
			chart "loc" type: series {
				data "loc" value: first(sp).location.x color: #blue;
			}
		}
	}
}
