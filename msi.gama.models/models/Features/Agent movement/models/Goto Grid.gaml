/**
* Name:  Movement on a Grid of Cells
* Author:  Patrick Taillandier
* Description: Model to represent how the agents move from one point to a target agent on a grid of cells with obstacles, following the shortest path and coloring
* 	in magenta the cells intersecting the path of an agent
* Tag : Grid, Movement of Agents, Skill
*/

model Grid

global {
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

grid cell width: 50 height: 50 neighbours: 4 {
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
	}
	
	reflex move when: location != target{
		//Neighs contains all the neighbours cells that are reachable by the agent plus the cell where it's located
		list<cell> neighs <- (cell(location) neighbors_at speed) + cell(location); 
		
		//We restrain the movements of the agents only at the grid of cells that are not obstacle using the on facet of the goto operator and we return the path
		//followed by the agent
		//the recompute_path is used to precise that we do not need to recompute the shortest path at each movement (gain of computation time): the obtsacles on the grid never change.
		path followed_path <- self goto (on:(cell where not each.is_obstacle), target:target, speed:speed, return_path:true, recompute_path: false);
		
		//As a side note, it is also possible to use the path_between operator and follow action with a grid
		//Add a my_path attribute of type path to the people species
		//if my_path = nil {my_path <- path_between((cell where not each.is_obstacle), location, target);}
		//path followed_path <- self follow (path: my_path,  return_path:true);
		
		if (followed_path != nil) and not empty(followed_path.segments) {
			geometry path_geom <- geometry(followed_path.segments);
			
			//The cells intersecting the path followed by the agent are colored in magenta
			ask (neighs where (each.shape intersects path_geom)) { color <- #magenta;}
		}	
	}
}

experiment goto_grid type: gui {
	output {
		display objects_display {
			grid cell lines: #black;
			species goal aspect: default ;
			species people aspect: default ;
		}
	}
}
