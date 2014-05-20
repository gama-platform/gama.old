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
	
	graph graph_from_grid;
	
	init {
		geometry free_space <- copy (shape);
		create exit from: exit_shapefile;
		create wall from: wall_shapefile {
			ask cell overlapping self {
				is_wall <- true;
				color <- #magenta;
			}
			free_space <- free_space - (shape + 1);
		}
		create people number: 50{
			location <- any_location_in (free_space);
			target <- any_location_in(one_of(exit));
		}
		graph_from_grid <- grid_cells_to_graph(cell where not each.is_wall);
	}
}

grid cell width: nb_cols height: nb_rows neighbours: 8 {
	bool is_wall <- false;
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
		do goto target: target speed: 1 on: graph_from_grid;
		if (location = target) {
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
