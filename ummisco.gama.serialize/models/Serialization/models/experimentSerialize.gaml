/**
* Name: experimentSerialize
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model experimentSerialize

global {
	int cycleSq ;
	int speed <- 1;
	
	init {
		create sp number: 2;
	}
	
	reflex toto {
		write cycle;
		cycleSq <- cycle * cycle;
	}
}

species sp {
	reflex moveSP {
		location <- location + {speed,0,0};
	}
	
	aspect circle {
		draw circle(1) border: #black;
	}
}

experiment experimentSerialize type: memorize {
	parameter "speed" var: speed ;
	
	output {
		display display1 {
			species sp aspect: circle;
		}
		
		display d2 {
			chart "loc" type: series {
				data "loc" value: first(sp).location.x color: #blue;
			}
		}
	}
}
