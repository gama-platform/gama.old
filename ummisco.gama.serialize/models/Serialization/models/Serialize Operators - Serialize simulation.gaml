/**
* Name: Model1
* Author: Benoit Gaudou
* Description: Describes the serialization of a simulation
* Tags: serialization
*/

model Model1

global {
	int toot <- 0;
	string s <- "test";
	
	init {
		create people number: 1;
		
		write "Run 1 step to see in the console the serialization of a simulation.";
	}
}

species people {
	int t;
	list<int> lo <- [1,2,3];
}

experiment SerializeSimulation type: gui {

	reflex store { 
		write "================ Serialize simulation " + self + " - " + cycle;
		write serialize_agent(self.simulation);
		write "================ END Serialize simulation " + self + " - " + cycle;		
	}
}
