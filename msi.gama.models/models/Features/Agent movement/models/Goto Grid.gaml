/**
* Name:  Movement on a Grid of Cells
* Author:  Patrick Taillandier
* Description: Model to represent how the agents move from one point to a target agent on a grid of cells, following the shortest path and coloring
* 	in magenta the cells intersecting the path of an agent
* Tag : Grid, Movement of Agents 
*/

model Grid

global {
	init {    
		create goal{
			location <- point(one_of (list(cell)));
		}
		create people number: 10 {
			target <- one_of (goal as list);
			location <- point(one_of (list(cell)));
		}
	} 
}

grid cell width: 50 height: 50 neighbours: 4 {
	rgb color <- #white;
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
		
		//We restrain the movements of the agents only at the grid of cell using the on facet of the goto operator and we return the path
		//followed by the agent
		path followed_path <- self goto (on:cell, target:target, speed:speed, return_path:true);
		
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
