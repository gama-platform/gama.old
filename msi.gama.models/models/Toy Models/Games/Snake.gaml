/**
* Name: Snake
* Author: Patrick Taillandier
* Tags: game, snake
*/

model snake

global {
	list<cell> cells_not_wall;
	int environment_size <- 20;
	
	geometry shape <- square(environment_size);
	snake the_snake;
	init {
		create wall with: (shape: rectangle(environment_size,1), location:{environment_size/2.0,environment_size-0.5});
		create wall with: (shape: rectangle(environment_size,1), location:{environment_size/2.0,0.5});
		create wall with: (shape: rectangle(1, 100.0), location:{0.5,environment_size/2.0});
		create wall with: (shape: rectangle(1, 100.0), location:{environment_size -0.5,environment_size/2.0});
		create snake with:(shape: square(1), location:location, heading: 0.0) {
			my_cell <- cell(location);
			cells << my_cell;
		}
		the_snake <- first(snake);
		cells_not_wall <- cell where (not each.is_wall);
		create food with: (location:(one_of(free_cells()).location)) {
			ask cell(location) {
				is_food <- true;
			}
		}
		
		write "***** TO PLAY *****";
		write "Up: 'e'\nDown: 'd'\nRight: 'f'\nLeft: ''s";
		do tell("Start of the game",false);
		
	}
	
	action move_up {
		if (the_snake.heading_last != 90) {
			the_snake.heading <- -90.0;
		}
	}
	action move_down {
		if (the_snake.heading_last != -90) {
			the_snake.heading <- 90.0;
		}
	}
	action move_left {
		if (the_snake.heading_last != 0) {
			the_snake.heading <- 180.0;
		}
	}
	action move_right {
		if (the_snake.heading_last != 180) {
			the_snake.heading <- 0.0;
		}
	}
	
	list<cell> free_cells {
		return cells_not_wall - the_snake.cells;
	}
}

grid cell width: environment_size height: environment_size use_individual_shapes: false use_neighbors_cache: false use_regular_agents: false{
	bool is_wall <-false;
	bool is_food <- false;
	
}

species wall {
	
	init {
		ask cell overlapping self {
			is_wall <- true;
		}
	}
	aspect default {
		draw shape color: #black;
	}
}
species food {
	aspect default {
		draw circle(0.5) color: #red;
	}
}
species snake  {
	float heading_last <- 0.0;
	float heading <- 0.0;
	cell my_cell;
	list<cell> cells;
	aspect default {
		loop c over: cells {
			draw c.shape color: #blue;
		}
	}
	
	action end_of_game {
		
		ask world {
			do tell("End of game, score: " + length(myself.cells), false);
			do pause;
		}
	}
	
	reflex move {
		heading_last <- heading;
		switch heading {
			match 0.0 {
				my_cell <- cell[my_cell.grid_x +1, my_cell.grid_y];
				if (my_cell.grid_x = (environment_size -1)) {
					do end_of_game;
				}
				
			}
			match 180.0 {
				my_cell <- cell[my_cell.grid_x -1, my_cell.grid_y];
				if (my_cell.grid_x = 0) {
					do end_of_game;
				}
			}
			match -90.0 {
				my_cell <- cell[my_cell.grid_x, my_cell.grid_y -1];
				if (my_cell.grid_y = 0) {
					do end_of_game;
				}
			}
			match 90.0 {
				my_cell <- cell[my_cell.grid_x, my_cell.grid_y +1];
				if (my_cell.grid_y = (environment_size -1)) {
					do end_of_game;
				}
			}
		}
		if my_cell in cells {
			do end_of_game;
		}
		cells << my_cell;
		if my_cell.is_food{
			my_cell.is_food <- false;
			ask one_of(world.free_cells()) {
				is_food <- true;
				first(food).location <- location;
			}
			
		} else {
			cells >> first(cells);
		}
	}
}

experiment snake_game type: gui autorun: true{
	float minimum_cycle_duration <- 0.3;
	parameter "Size of the environment" var: environment_size <- 30 min: 10 max: 100;
	parameter "Game speed (0.0: fast; 1.0: slow)" var: minimum_cycle_duration <- 0.2 min: 0.0 max: 1.0;
	output {
		display map {
			species wall;
			species snake;
			species food;
			event "e" action: move_up;
			event "d" action: move_down;
			event "f" action: move_right;
			event "s" action: move_left;
			
		}
	}
}
