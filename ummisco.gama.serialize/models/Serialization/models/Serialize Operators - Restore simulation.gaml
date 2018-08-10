/**
* Name: Model1
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model Model4

global {
	int toot <- 0;
	string s <- "test";
	
	init {
		create people number: 1;
		create toto number: 2;
		write "Run the model.";
		write "Every 4 steps, the simulation is restored to its initial step.";
		write "You will thus observe the red agent going to the right side of the display and, every 4 steps, moving back to its initial location.";
	}
	
	reflex t {
		write toot;
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

experiment Model4 type: gui {

	string saved_step <- "";

	init {
		saved_step <- serializeAgent(self.simulation);
	}
	
	reflex restore when: (cycle = 4) {
		write "================ restore " + self + " - " + cycle;
		write "Restore from: ";		
		write saved_step;
		int serial <- restoreSimulation(saved_step);
		write "================ END restore " + self + " - " + cycle;			
	}

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
