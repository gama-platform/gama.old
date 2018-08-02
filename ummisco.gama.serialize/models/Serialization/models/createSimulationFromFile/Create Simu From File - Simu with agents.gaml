/***
* Name: CreateSimuFromFileSimu
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model CreateSimuFromFileSimu

import "../Serialize Operators - 2 - Serialize simulation with agents.gaml"


experiment saveSimu type: gui {
	reflex store when: cycle = 5 {		
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + saveSimulation('simpleSimu.gsim');
		write "================ RESTORE + self " + " - " + cycle ;		
		write serializeAgent(simulation);	
	}	
}

experiment reloadSimu type: gui {
	
	init {
		create simulation from: saved_simulation_file("simpleSimu.gsim");	
		write "init simulation at step " + simulation.cycle;
	}	
}

experiment reloadSingleSimu type: gui {
	
	action _init_ {
		create simulation from: saved_simulation_file("simpleSimu.gsim");	
		write "init simulation at step " + simulation.cycle;		
	}
}