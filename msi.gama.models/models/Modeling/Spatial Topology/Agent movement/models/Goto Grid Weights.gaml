/**
* Name:  Movement on a Grid of Cells
* Author:  Patrick Taillandier
* Description: Model to represent how the agents move from one point to a target agent on a grid of cells with obstacles, following the shortest path and coloring 
* 	in magenta the cells intersecting the path of an agent
* Tags: grid, agent_movement, skill, obstacle, shortest_path
*/

model Grid

global {
	/*2 algorithms for the shortest path computation on a grid with weights:
	*      - A* : default algorithm: An introduction to A*: http://www.redblobgames.com/pathfinding/a-star/introduction.html
	*      - Dijkstra : Classic Dijkstra algorithm. An introduction to Dijkstra : http://www.redblobgames.com/pathfinding/a-star/introduction.html
	*/
	
	file dem <- file("../includes/vulcano_50.asc");
	geometry shape <- envelope(dem);
	string algorithm <- "A*" among: ["A*", "Dijkstra"] parameter: true;
	int neighborhood_type <- 8 among:[4,8] parameter: true;
	map<cell,float> cell_weights;

	init {    
		ask cell {grid_value <- grid_value * 5;}  
		float max_val <- cell max_of (each.grid_value);
		ask cell {
			float val <- 255 * (1 - grid_value / max_val);
			color <- rgb(val, val,val);
		}
		cell_weights <- cell as_map (each::each.grid_value);
		create goal{
			location <- (one_of (cell where not each.is_obstacle)).location;
		}
		create people number: 10 {
			target <- one_of (goal);
			location <-  (one_of (cell where not each.is_obstacle)).location;
		}
	} 
}

grid cell file: dem neighbors: neighborhood_type optimizer: algorithm {
	bool is_obstacle <- flip(0.2);
	rgb color <- is_obstacle ? #black : #white;
} 
	 
species goal {
	aspect default { 
		draw circle(0.5) color: #red;
	}
}  
	
	  
species people skills: [moving] {
	goal target;
	float speed <- 1.0;
	aspect default {
		draw circle(0.5) color: #green;
		if (current_path != nil) {
			draw current_path.shape color: #red;
		}
	}
	
	reflex move when: location != target{
		//We restrain the movements of the agents only at the grid of cells that are not obstacle using the on facet of the goto operator and we return the path
		//followed by the agent
		//the recompute_path is used to precise that we do not need to recompute the shortest path at each movement (gain of computation time): the obtsacles on the grid never change.
		do goto (on:cell_weights, target:target, speed:speed, recompute_path: false);
		
		//As a side note, it is also possible to use the path_between operator and follow action with a grid
		//Add a my_path attribute of type path to the people species
		//if my_path = nil {my_path <- path_between((cell where not each.is_obstacle), location, target);}
		//do follow (path: my_path);
	}
}

experiment goto_grid type: gui {
	output {
		display objects_display type:2d antialias:false{
			grid cell border: #black;
			species goal aspect: default ;
			species people aspect: default ;
		}
	}
}
