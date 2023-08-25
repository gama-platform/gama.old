/**
* Name: evacuationgoto
* Author: Patrick Taillandier
* Description: A 3D model with walls and exit, and people agents trying to evacuate 
* 	from the area to a exit location, avoiding the walls with a discretized space by a grid
* Tags: 3d, shapefile, gis, agent_movement, skill, grid
*/

model evacuationgoto

global {
	//Shapefile of the walls
	file wall_shapefile <- shape_file("../includes/walls.shp");
	//Shapefile of the exit
	file exit_shapefile <- shape_file("../includes/exit.shp");
	//DImension of the grid agent
	int nb_cols <- 50;
	int nb_rows <- 50;
	
	//Shape of the world initialized as the bounding box around the walls
	geometry shape <- envelope(wall_shapefile);
	
	init {
		//Creation of the wall and initialization of the cell is_wall attribute
		create wall from: wall_shapefile {
			ask cell overlapping self {
				is_wall <- true;
			}
		}
		//Creation of the exit and initialization of the cell is_exit attribute
		create exit from: exit_shapefile {
			ask (cell overlapping self) where not each.is_wall{
				is_exit <- true;
			}
		}
		//Creation of the people agent
		create people number: 50{
			//People agent are placed randomly among cells which aren't wall
			location <- one_of(cell where not each.is_wall).location;
			//Target of the people agent is one of the possible exits
			target <- one_of(cell where each.is_exit).location;
		}
	}
}
//Grid species to discretize space
grid cell width: nb_cols height: nb_rows neighbors: 8 {
	bool is_wall <- false;
	bool is_exit <- false;
	rgb color <- #white;	
}
//Species exit which represent the exit
species exit {
	aspect default {
		draw shape color: #blue;
	}
}
//Species which represent the wall
species wall {
	aspect default {
		draw shape color: #black depth: 10;
	}
}
//Species which represent the people moving from their location to an exit using the skill moving
species people skills: [moving]{
	//Evacuation point
	point target;
	rgb color <- rnd_color(255);
	
	//Reflex to move the agent 
	reflex move {
		//Make the agent move only on cell without walls
		do goto target: target speed: 1.0 on: (cell where not each.is_wall) recompute_path: false;
		//If the agent is close enough to the exit, it dies
		if (self distance_to target) < 2.0 {
			do die;
		}
	}
	aspect default {
		draw pyramid(2.5) color: color;
		draw sphere(1) at: {location.x,location.y,2} color: color;
	}
}
experiment evacuationgoto type: gui {
	float minimum_cycle_duration <- 0.04; 
	output {
		display map type: 3d axes:false{
			image "../images/floor.jpg";
			species wall refresh: false;
			species exit refresh: false;
			species people;
			
		}
	}
}
