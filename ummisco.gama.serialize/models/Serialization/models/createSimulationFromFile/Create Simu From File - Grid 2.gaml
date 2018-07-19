/***
* Name: CreateSimuGrid2
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model CreateSimuGrid2

import "../Memorize Experiment - Topology - Grid - 2.gaml"

experiment saveSimu type: gui {
	reflex store when: cycle = 5 {		
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + saveSimulation('grid2.gsim');
		write "================ RESTORE + self " + " - " + cycle ;			
	}	
	output {
		display main_display {
			grid vegetation_cell lines: #black ;
			species prey aspect: base;						
		}
	}	
}

experiment reloadSimu type: gui {
	
	init {
		create simulation from: saved_simulation_file("grid2.gsim");	
		write "init simulation at step " + simulation.cycle;
	}
	output {
		display main_display {
			grid vegetation_cell lines: #black ;
			species prey aspect: base;					
		}
	}	
}

