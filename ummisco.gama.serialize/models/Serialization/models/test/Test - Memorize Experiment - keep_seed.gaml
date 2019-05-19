/**
* Name: Model1
* Author: Benoit Gaudou
* Description: Loads a simulation from a file
* Tags: serialization, load_file
*/

model Model4

global {
	int toot <- 0;
	string s <- "test";
	
	init {
		create people number: 1;
		create toto number: 2;
	}
	
	reflex t {
		toot <- toot +10;
	}
}

species people {
	int t;
	list<int> lo <- [1,2,3];
	
	reflex move {
		float r <- rnd(5.0);
		write "" + cycle + " - "  + r  ;
		location <- {location.x + r, location.y};
	}
	
	aspect default {
		draw circle(1) color: #red;
	}
}

species toto {
	aspect default {
		draw circle(1) color: #blue;
	}
}

experiment Model4_not_keep_seed type: memorize keep_seed: false {

	output {
		display d {
			species people aspect: default;
			species toto aspect: default;
		}	
		
		display c {
			chart "t" {
				data "location" value: first(people).location.x;
			}
		}	
	}
}

experiment Model4_keep_seed type: memorize keep_seed: true {

	output {
		display d {
			species people aspect: default;
			species toto aspect: default;
		}	
		
		display c {
			chart "t" {
				data "location" value: first(people).location.x;
			}
		}	
	}
}