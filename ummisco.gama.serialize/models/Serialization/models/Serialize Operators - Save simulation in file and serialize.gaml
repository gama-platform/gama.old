/**
* Name: ModelSaveAndSerialize
* Author: Benoit Gaudou
* Description: Save a simulation to a file and display the serialization of the simulation
* Tags: serialization, save_file
*/

model ModelSaveAndSerialize

global {
	int toot <- 0;
	string s <- "test";
	
	init {
		create people number: 1;
	}
}

species people {
	int t;
	list<int> lo <- [1,2,3];
}

experiment SaveSimulation type: gui {
	
	reflex save_simulation when: cycle mod 2 = 0 {
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + save_simulation('../result/file.gsim');
		// alternative to save_simulation : 		save saved_simulation_file('../result/file.gsim', [simulation]);
		write "================ END SAVE + self " + " - " + cycle ;					
	}
	
	reflex serialize_agent when: cycle mod 2 = 1 {
		write "================ Serialize simulation " + self + " - " + cycle;
		write serialize_agent(self.simulation);
		write "================ END Serialize simulation " + self + " - " + cycle;				
	}
	
}
