/**
* Name: ModelSaveAndSerialize
* Author: Benoit Gaudou
* Description: Save a simulation to a file and display the serialization of the simulation
* Tags: serialization, save_file
*/

model SavingSimulation

global {
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
		save simulation to: '../result/file.simulation' format: "json" ;
		write "================ END SAVE + self " + " - " + cycle ;					
	}
	
	reflex serialize_agent when: cycle mod 2 = 1 {
		write "================ Serialize simulation " + self + " - " + cycle;
		write serialize(self.simulation, 'json', false);
		write "================ END Serialize simulation " + self + " - " + cycle;				
	}
	
}
