/**
* Name: Model1
* Author: Benoit Gaudou
* Description: Loads a simulation from a file
* Tags: serialization, load_file
*/

model Model1

global {
	init {
		create people number: 4;
		
		people(0).p <- people(1);
		people(1).p <- people(0);
	}
}

species people {
	people p;
	
	aspect default {
		draw circle(1) color: rnd_color(255);
	}
}

experiment ModelUnserialize type: gui {

	string save_step <- "";
	
	reflex save when: (cycle = 1) {
		save_step <- serialize_agent(self.simulation);
	}
	
	reflex t {
		write serialize_agent(self.simulation);	
	}
	
	reflex restore when: (cycle = 4) {
		write "================ restore " + self + " - " + cycle;
		int serial <- restore_simulation(save_step);	
		write "================ END restore " + self + " - " + cycle;			
	}

}

experiment saveSimu type: gui {
	reflex store when: cycle = 5 {		
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + save_simulation('simpleSimu.gsim');
		write "================ RESTORE + self " + " - " + cycle ;		
		write serialize_agent(simulation);	
	}	
}

experiment reloadSimu type: gui {

	init {
		create simulation from: saved_simulation_file("simpleSimu.gsim");	
		write "init simulation at step " + simulation.cycle;
	}	
	
	output {
		display d {
			species people;
		}
	}		
}

experiment reloadSingleSimu type: gui {
	
	action _init_ {
		create simulation from: saved_simulation_file("simpleSimu.gsim");	
		write "init simulation at step " + simulation.cycle;		
	}
	
	output {
		display d {
			species people;
		}
	}	
}