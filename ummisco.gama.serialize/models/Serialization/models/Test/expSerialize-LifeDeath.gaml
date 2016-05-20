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
		create sp number: 2;
		create autreSpecies number: 1;
	}
	
	reflex toto {
		write cycle;
	}
	
	reflex create {
		create sp number: 2;
	}
	
	reflex dead  {
		ask one_of(sp) {
			do die;
		}
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

species autreSpecies {}

experiment experimentSerialize type: memorize {
	parameter "speed" var: speed ;
	
	reflex aff {
		write  serializeAgent(self.simulation);
	}
	
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
