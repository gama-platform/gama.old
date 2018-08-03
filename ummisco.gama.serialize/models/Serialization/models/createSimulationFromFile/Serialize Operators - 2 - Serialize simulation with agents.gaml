/**
* Name: Model1
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model Model1

global {
	init {
	//	create people number: 2;
		
	//	people(0).p <- people(1);
	//	people(1).p <- people(0);
	}
}

species people {
	people p;
}

experiment ModelUnserialize type: gui {

	string save_step <- "";
	
	reflex save when: (cycle = 1) {
		save_step <- serializeAgent(self.simulation);
	}
	
	reflex t {
		write serializeAgent(self.simulation);	
	}
	
	reflex restore when: (cycle = 4) {
		write "================ restore " + self + " - " + cycle;
		int serial <- unSerializeSimulation(save_step);	
		write "================ END restore " + self + " - " + cycle;			
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