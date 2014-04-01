model grid_move

global {
	file building_shapefile <- file("../includes/building.shp");
	geometry shape <- envelope(building_shapefile);
	int max_memory <- 5;
	float people_size <- 2.0;
	int nb_people <- 500;
	point target_point <- {world.location.x, 0};
	
	init {
		create building from: building_shapefile
		{
			ask cell overlapping self {
				is_obstacle <- true;
				color <- rgb("black");
			}
		}
	
		list<cell> free_cell <- cell where not (each.is_obstacle);
		cell the_target_cell <- cell closest_to target_point;
		create people number: nb_people {
			current_cell <- one_of(free_cell);
			current_cell.is_free <- false;
			remove current_cell from: free_cell;
			location <- current_cell.location;
			target_cell <- the_target_cell;
			memory << current_cell;
			
		}
	}
}

species building {
	float height <- 3.0 + rnd(5);
	aspect default {
		draw shape color: rgb("gray") depth: height;
	}
}
species people {
	cell current_cell;
	cell target_cell;
	list<cell> memory;
	float size <- people_size;
	rgb color <- rgb(rnd(255),rnd(255),rnd(255));
	
	
	
	reflex end when: location distance_to target_cell.location <= 2 * people_size {
		current_cell.is_free <- true;
		do die;
	}
	reflex move {
		list<cell> possible_cells <- current_cell neighbours_at 1 where (not (each.is_obstacle) and each.is_free and not (each in memory));
		if not empty(possible_cells) {
			current_cell.is_free <- true;
			current_cell <- shuffle(possible_cells) with_min_of (each.location distance_to target_cell.location);
			location <- current_cell.location;
			current_cell.is_free <- false;
			memory << current_cell; 
			if (length(memory) > max_memory) {
				remove memory[0] from: memory;
			}
		}
	}
	
	aspect default {
		draw pyramid(size) color: color;
		draw sphere(size/3) at: {location.x,location.y,size} color: color;
	}
}

grid cell width: 150 height: 150  neighbours: 8 frequency: 0 {
	bool is_obstacle <- false;
	bool is_free <- true;
	rgb color <- rgb("white");
}

experiment main type: gui {
	parameter "nb people" var: nb_people min: 1 max: 1000;
	output {
		display map type: opengl ambient_light: 150 camera_pos: {world.location.x,-world.shape.height*1.5,70}
                        camera_look_pos:{world.location.x,0,0}    {
			image '../images/soil.jpg';
			species building refresh: false;
			species people;
			graphics "exit" refresh: false {
				draw sphere(2 * people_size) at: target_point color: rgb("green");	
			}
		}
	}
}
