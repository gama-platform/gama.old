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

experiment Model4 type: gui {

	list<string> history <- [];

	reflex store { //when: (cycle < 5){	
		write "================ store " + self + " - " + cycle;
		string serial <- serializeAgent(self.simulation);
		add serial to: history;
		write "================ END store " + self + " - " + cycle;			
	}
	
	reflex restore when: (cycle > 4) {
		write "================ restore " + self + " - " + cycle;
		int serial <- unSerializeSimulation(history[0]);
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
