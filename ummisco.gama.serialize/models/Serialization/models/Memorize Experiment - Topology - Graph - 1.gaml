/**
* Name:  Follow Weighted Network
* Author:  Martine Taillandier
* Description: Model representing how to make a weighted graph and the impacts of the weights on the time to follow the path for the agents. 
* 	Two agents are represented to show this difference : one knowing the weights and following a fast path, an other following a path longer 
* 	without knowing it's a longer path.
* Tags: graph, agent_movement, skill
*/

model weightperagents

global {
	graph road_network;

	init {
		//This road will be slow
		create road {
			shape <- line ([{10,50},{90,50}]);
		}
		//The others will be faster
		create road {
			shape <- line ([{10,50},{10,10}]);
		}
		create road {
			shape <- line ([{10,10},{90,10}]);
		}
		create road {
			shape <- line ([{90,10},{90,50}]);
		}
		
		road_network <- as_edge_graph(road);
	}
	
	reflex w {
		write "Cycke " + cycle;
		write road_network;
	}
	
}

species road {
	aspect geom {
		draw shape color: #blue;
	}
}
	

experiment weightperagents type: memorize {

	reflex write {
		write "===============================";
		write serializeAgent(self.simulation);
		write "===============================";		
	}	
	
	output {
		display map {
			species road aspect: geom;
		}
	}
}
