/***
* Name: CreateSimuGraph2
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model CreateSimuGraph2

import "Test - Memorize Experiment - Topology - Graph.gaml"

experiment saveSimu type: gui {
	reflex store when: cycle = 5 {		
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + saveSimulation('graph2.gsim');
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