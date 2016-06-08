/**
* Name: experimentSerialize
* Author: bgaudou
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model lifeDeath

global {
	int speed <- 1;
	
	init {
		create autreSpecies number: 2;
		create sp number: 1 {
			autreAgent <- one_of(autreSpecies);
		}		
	}
	
	reflex dead when: cycle = 3 {
		ask sp {do die;}
	}
}

species sp {
	autreSpecies autreAgent;
	
	reflex moveSP {
		location <- location + {speed,0,0};
	}
	
	aspect circle {
		draw circle(1) border: #black;
	}
}

species autreSpecies {}

experiment experimentSerialize type: memorize {
	parameter "speed" var: speed ;
	
//	reflex aff {
//		write  serializeAgent(self.simulation);
//	}
	
	output {
		display display1 {
			species sp aspect: circle;
		}
		
		display d2 {
			chart "loc" type: series {
//				data "loc" value: first(sp).location.x color: #blue;
			}
		}
	}
}
