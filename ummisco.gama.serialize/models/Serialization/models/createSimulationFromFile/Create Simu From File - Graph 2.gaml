/***
* Name: CreateSimuGraph2
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model CreateSimuGraph2

import "../Memorize Experiment - Topology - Graph - 2 - Moving agents.gaml"


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
