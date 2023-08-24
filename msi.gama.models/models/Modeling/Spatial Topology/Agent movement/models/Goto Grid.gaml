/**
* Name:  Movement on a Grid of Cells
* Author:  Patrick Taillandier
* Description: Model to represent how the agents move from one point to a target agent on a grid of cells with obstacles, following the shortest path and coloring 
* 	in magenta the cells intersecting the path of an agent
* Tags: grid, agent_movement, skill, obstacle, shortest_path
*/

model Grid

global {
	/*4 algorithms for the shortest path computation on a grid:
	*      - A* : default algorithm: Very efficient for both Moore (8) and Von Neumann (4) neighborhoods. An introduction to A*: http://www.redblobgames.com/pathfinding/a-star/introduction.html
	*      - Dijkstra : Classic Dijkstra algorithm. An introduction to Dijkstra : http://www.redblobgames.com/pathfinding/a-star/introduction.html
	*      - JPS : Jump Point Search, only usable for Moore (8) neighborhood. Most of time, more efficient than A*. An introduction to JPS: https://harablog.wordpress.com/2011/09/07/jump-point-search/#3
	*      - BF : Breadth First Search. Should only be used for Von Neumann (4) neighborhood. An introduction to BF: http://www.redblobgames.com/pathfinding/a-star/introduction.html
	*/
	
	string algorithm <- "A*" among: ["A*", "Dijkstra", "JPS", "BF"] parameter: true;
	int neighborhood_type <- 8 among:[4,8] parameter: true;
	

	init {    
		create goal{
			location <- (one_of (cell where not each.is_obstacle)).location;
		}
		create people number: 10 {
			target <- one_of (goal);
			location <-  (one_of (cell where not each.is_obstacle)).location;
		}
	} 
}

grid cell width: 50 height: 50 neighbors: neighborhood_type optimizer: algorithm {
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
	float speed <- float(3);
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
		do goto (on:(cell where not each.is_obstacle), target:target, speed:speed, recompute_path: false);
		
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
