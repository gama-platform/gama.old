/**
* Name: Snake
* Author: Patrick Taillandier
* Tags: game, snake
*/

model snake

global {
	list<cell> cells_not_wall;
	int environment_size <- 20;
	bool game_is_running;
	
	geometry shape <- square(environment_size);
	snake the_snake;
	init {
		create wall with: (shape: rectangle(environment_size,1), location:{environment_size/2.0,environment_size-0.5});
		create wall with: (shape: rectangle(environment_size,1), location:{environment_size/2.0,0.5});
		create wall with: (shape: rectangle(1, 100.0), location:{0.5,environment_size/2.0});
		create wall with: (shape: rectangle(1, 100.0), location:{environment_size -0.5,environment_size/2.0});
		cells_not_wall <- cell where (not each.is_wall);

		do init_game;		
		write "***** TO PLAY *****";
		write "Up: 'e'/arrow up\nDown: 'd'/arrow down\nRight: 'f'/right arrow\nLeft: 's'/left arrow";
		create HUD;

	}
	
	action init_game {
		game_is_running <- false;
		
		ask snake{
			do die;
		}
		ask food {
			do die;
		}
		
		create snake with:(shape: square(1), location:location, headings: [0.0]) {
			my_cell <- cell(location);
			cells << my_cell;
		}
		the_snake <- first(snake);
		create food with: (location:(one_of(free_cells()).location)) {
			ask cell(location) {
				is_food <- true;
			}
		}
	}
	
	action move_up {
		
		if ( ! empty(the_snake.headings) and last(the_snake.headings) != 90
			or empty(the_snake.headings) and the_snake.current_heading != 90
		) {
			the_snake.headings <+ -90.0;
		}
	}
	action move_down {
		if ( ! empty(the_snake.headings) and last(the_snake.headings) != -90
			or empty(the_snake.headings) and the_snake.current_heading != -90
		) {
			the_snake.headings <+ 90.0;
		}
	}
	action move_left {
		if ( ! empty(the_snake.headings) and length(the_snake.headings) > 0 and last(the_snake.headings) != 0
			or empty(the_snake.headings) and the_snake.current_heading != 0
		) {
			the_snake.headings <+ 180.0;
		}
	}
	action move_right {
		if ( ! empty(the_snake.headings) and last(the_snake.headings)!= 180
			or empty(the_snake.headings) and the_snake.current_heading != 180
		) {
			the_snake.headings <+ 0.0;
		}
	}
	action start_count_down {
		ask HUD{
			do start_count_down;
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
	float current_heading <- 0.0;
	list<float> headings <- [0.0];
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
			do init_game;
		}
	}
	
	reflex move when:game_is_running{
		if ! empty(headings){
			current_heading <- first(headings); 	
			remove from:headings index:0;
		}
		switch current_heading {
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

species HUD skills:[thread]{
	
	font hud_font <- font("Helvetica", 80, #bold) ;
	int count <- 3;
	bool is_counting <- false;
	
	aspect default {
		if is_counting {
			draw ""+count at:{world.shape.width/2-1, world.shape.height/2-1} 
							color: rnd_color(255) 
							font:hud_font ;
		}
		else if ! game_is_running{
			draw "Press space to"at:{1, 10}  font:hud_font color:#red;
			draw "start the game" at:{2, 20}  font:hud_font color:#red;
		}
	}
	
	action start_count_down {
		count <- 5;
		is_counting <- true;
		
		do run_thread interval:1#second;
	}
	
	//counting down
	action thread_action {
		count <- count - 1;
		if count = 0 {
			do end_thread;
			is_counting <- false;
			ask world {
				game_is_running <- true;
			}
		}
	}
	
	
}


experiment snake_game type: gui autorun: true{
	float minimum_cycle_duration <- 0.3;
	parameter "Size of the environment" var: environment_size <- 30 min: 10 max: 100;
	parameter "Game speed (0.0: fast; 1.0: slow)" var: minimum_cycle_duration <- 0.2 min: 0.0 max: 1.0;
	output {
		display map type:2d antialias:false {
			species wall;
			species snake;
			species food;
			species HUD;
			event "e" {ask simulation { do move_up;}}
			event #arrow_up {ask simulation { do move_up;}}
			event "d" {ask simulation { do move_down;}}
			event #arrow_down {ask simulation { do move_down;}}
			event "f" {ask simulation { do move_right;}}
			event #arrow_right {ask simulation { do move_right;}}
			event "s" {ask simulation { do move_left;}}
			event #arrow_left {ask simulation { do move_left;}}
			event " " { ask simulation { do start_count_down;}}
			
		}
	}
}


