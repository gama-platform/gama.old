/**
* Name: experimentSerialize
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model experimentSerialize

global {	
	float speed <- 1.0;
	init {
		create sp number: 2 {
			shape <- cube(cycle + 2);
		}
	}
}

species sp {
	reflex moveSP {
		location <- location + {speed,0,0};
		shape <- cube(cycle + 2);
	}
	
	aspect circle {
		draw shape border: #black;
	}
}

experiment experimentSerialize type: memorize {
	parameter "speed" var: speed;
	
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
