/***
* Name: CreateSimuGraph2
* Author: Benoit Gaudou
* Description: Loads a simulation from a file
* Tags: serialization, load_file
***/

model CreateSimuGraph2

import "Test - Memorize Experiment - Topology - Graph.gaml"

experiment saveSimu type: gui {
	reflex store when: cycle = 5 {		
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + save_simulation('graph2.gsim');
		write "================ RESTORE + self " + " - " + cycle ;			
	}	
	output {
		display main_display {
			species road aspect: geom;
			species people aspect: base;						
		}
	}	
}

experiment reloadSimu type: gui {
	
	init {
		create simulation from: saved_simulation_file("graph2.gsim");	
		write "init simulation at step " + simulation.cycle;
	}
	
	output {
		display main_display {
			species road aspect: geom;
			species people aspect: base;						
		}
	}	
}

experiment reloadSingleSimu type: gui {
	
	action _init_ {
		create simulation from: saved_simulation_file("graph2.gsim");	
	}

	output {
		display main_display {
			species road aspect: geom;
			species people aspect: base;						
		}
	}	
}