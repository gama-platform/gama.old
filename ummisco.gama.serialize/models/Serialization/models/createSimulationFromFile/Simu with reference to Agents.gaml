/**
* Name: Model1
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model Model1

global {
	
	people p;
	
	init {
		create people number: 1;
		p <- first(people);
	}
}

species people {
	int t; 
}

experiment Model1 type: gui {

	reflex store { 
		write "================ store " + self + " - " + cycle;
		string serial <- serializeAgent(self.simulation);
		write serial;
		write "================ END store " + self + " - " + cycle;		
	}
	
	output {
		
	}
}


experiment saveSimu type: gui {
	reflex store when: cycle = 5 {		
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + saveSimulation('simpleSimu.gsim');
		write "================ RESTORE + self " + " - " + cycle ;		
		write serializeAgent(simulation);	
	}	
}

experiment reloadSimu type: gui {
	
//	init {
//		create simulation from: saved_simulation_file("simpleSimu.gsim");	
//		write "init simulation at step " + simulation.cycle;
//	}	
}

experiment reloadSingleSimu type: gui {
	
	action _init_ {
		create simulation from: saved_simulation_file("simpleSimu.gsim");	
		write "init simulation at step " + simulation.cycle;		
	}
}