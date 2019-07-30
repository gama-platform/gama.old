/**
* Name: Model1
* Author: Benoit Gaudou
* Description: Save a simulation to file
* Tags: serialization, save_file
*/

model Model3

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
	
	reflex store {
		write "================ START SAVE + self " + " - " + cycle ;		
		write "Save of simulation : " + save_simulation('../result/file.gsim');
		write "================ END SAVE + self " + " - " + cycle ;			
	}
}
