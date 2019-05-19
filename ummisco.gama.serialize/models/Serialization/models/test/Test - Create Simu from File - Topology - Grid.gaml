/***
* Name: CreateSimufromFileTopologyGrid1
* Author: Benoit Gaudou
* Description: Loads a simulation from a file
* Tags: serialization, load_file
***/

model CreateSimufromFileTopologyGrid1

import "Test - Memorize Experiment - Topology - Grid.gaml"

experiment saveSimu type: gui {
		
	reflex store when: cycle = 5 {		
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + save_simulation('grid2Simu.gsim');
		write "================ RESTORE + self " + " - " + cycle ;		
	}	
	
	output {
		display main_display {
			grid vegetation_cell lines: #black ;
			species prey aspect: base;
		}
	}	
}

experiment reloadSimu type: gui  {
		
	init {
		create simulation from: saved_simulation_file("grid2Simu.gsim");	
		write "init simulation at step " + simulation.cycle;
	}	

	output {
		display main_display {
			grid vegetation_cell lines: #black ;
			species prey aspect: base;
			
		}
	}	
}

experiment reloadSingleSimu type: gui {
	
	action _init_ {
		create simulation from: saved_simulation_file("grid2Simu.gsim");	
		write "init simulation at step " + simulation.cycle;		
	}
	
	output {
		display main_display {
			grid vegetation_cell lines: #black ;
			species prey aspect: base;
			
		}
	}	
}