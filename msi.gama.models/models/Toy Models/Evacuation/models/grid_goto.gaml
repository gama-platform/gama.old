/**
 *  evacuationgoto
 *  Author: Patrick Taillandier
 *  Description: 
 */

model evacuationgoto

global {
	file wall_shapefile <- shape_file("../includes/walls.shp");
	file exit_shapefile <- shape_file("../includes/exit.shp");
	int nb_cols <- 50;
	int nb_rows <- 50;
	
	geometry shape <- envelope(wall_shapefile);
	
	init {
		create wall from: wall_shapefile {
			ask cell overlapping self {
				is_wall <- true;
			}
		}
		create exit from: exit_shapefile {
			ask (cell overlapping self) where not each.is_wall{
				is_exit <- true;
			}
		}
		create people number: 50{
			location <- one_of(cell where not each.is_wall).location;
			target <- one_of(cell where each.is_exit).location;
		}
	}
}

grid cell width: nb_cols height: nb_rows neighbors: 8 {
	bool is_wall <- false;
	bool is_exit <- false;
	rgb color <- #white;	
}

species exit {
	aspect default {
		draw shape color: #blue;
	}
}

species wall {
	aspect default {
		draw shape color: #black depth: 10;
	}
}

species people skills: [moving]{
	point target;
	rgb color <- rnd_color(255);
	reflex move {
		do goto target: target speed: 1 on: (cell where not each.is_wall) recompute_path: false;
		if (self distance_to target) < 2.0 {
			do die;
		}
	}
	aspect default {
		draw pyramid(2) color: color;
		draw sphere(1) at: {location.x,location.y,2} color: color;
	}
}
experiment evacuationgoto type: gui {
	output {
		display map type: opengl{
			image "../images/floor.jpg";
			species wall refresh: false;
			species exit refresh: false;
			species people;
			
		}
	}
}
