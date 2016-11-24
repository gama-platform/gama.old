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
	map<road, float> roads_weight;
	graph road_network;
	float slow_coeff <- 3.0;
	init {
		//This road will be slow
		create road {
			shape <- line ([{10,50},{90,50}]);
			slow <- true;
		}
		//The others will be faster
		create road {
			shape <- line ([{10,50},{10,10}]);
			slow <- false;
		}
		create road {
			shape <- line ([{10,10},{90,10}]);
			slow <- false;
		}
		create road {
			shape <- line ([{90,10},{90,50}]);
			slow <- false;
		}
		
		//Weights map of the graph for those who will know the shortest road by taking into account the weight of the edges
		roads_weight <- road as_map (each:: each.shape.perimeter * (each.slow ? slow_coeff : 1.0));
		road_network <- as_edge_graph(road);
		
		//people with information about the traffic
		create people {
			color <- #blue;
			size <- 2.0;
			roads_knowledge <- roads_weight;
		}
		
		//people without information about the traffic
		create people {
			color <- #yellow;
			size <- 1.0;
			roads_knowledge <- road as_map (each:: each.shape.perimeter);
		}
	}
	
}

species road {
	bool slow;
	aspect geom {
		draw shape color: slow ? #red : #green;
	}
}
	
species people skills: [moving] {
	map<road, float> roads_knowledge;
	point the_target;
	rgb color;
	float size;
	path path_to_follow;
	
	init {
		the_target <- {90,50};
		location <- {10,50};
	}
		
	reflex movement when: location != the_target{
		if (path_to_follow = nil) {
			
			//Find the shortest path using the agent's own weights to compute the shortest path
			path_to_follow <- path_between(road_network with_weights roads_knowledge, location,the_target);
		}
		//the agent follows the path it computed but with the real weights of the graph
		do follow path:path_to_follow speed: 5.0 move_weights: roads_weight;
	}
		
	aspect base {
		draw circle(size) color: color;
	}
}

experiment weightperagents type: gui {
	float minimum_cycle_duration <- 0.1;
	output {
		display map {
			species road aspect: geom;
			species people aspect: base;
		}
	}
}
