/**
* Name: Dynamic of the vegetation (grid)
* Author:
* Description: Second part of the tutorial : Predator Prey
* Tags: grid
*/

model prey_predator

global { }

grid vegetation_cell width: 2 height: 2 neighbors: 4 {
	float maxFood <- 1.0 ;
	rgb color <- rnd_color(255) update: rnd_color(255);
}

experiment prey_predator type: memorize keep_seed: true {
	
	list<string> history <- [];

	reflex store { //when: (cycle < 5){	
		write "================ store " + self + " - " + cycle;
		string serial <- serializeAgent(self.simulation);
		add serial to: history;
		write serial;
		write "================ END store " + self + " - " + cycle;		
		//write serializeSimulation(cycle);
	}
	
	output {
		display main_display {
			grid vegetation_cell lines: #black ;
		}
	}
}

 