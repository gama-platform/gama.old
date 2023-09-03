/**
* Name: Ant Foraging (Complex)
* Author: 
* Description: Toy Model ant using the question of how ants search food and use pheromons to return to their 
* nest once they did find food.	
* Tags: gui, fsm, grid, diffusion
*/
@no_warning
model ants

global {
//Utilities



	bool use_icons <- true;
	bool display_state <- false;
	//Evaporation value per cycle
	float evaporation_per_cycle <- 5.0 min: 0.0 max: 240.0 parameter: 'Evaporation of the signal (unit/cycle):' category: 'Signals';
	//Diffusion rate of the pheromon among the grid
	float diffusion_rate <- 1.0 min: 0.0 max: 1.0 parameter: 'Rate of diffusion of the signal (%/cycle):' category: 'Signals';
	//Size of the grid
	int gridsize <- 100 min: 30 parameter: 'Width and Height of the grid:' category: 'Environment and Population';
	//Number of ants
	int ants_number <- 200 min: 1 parameter: 'Number of ants:' category: 'Environment and Population';
	//Frequency of update of the grid
	int grid_frequency <- 1 min: 1 max: 100 parameter: 'Grid updates itself every:' category: 'Environment and Population';
	//Number of food places among the grid
	int number_of_food_places <- 5 min: 1 parameter: 'Number of food depots:' category: 'Environment and Population';
	float grid_transparency <- 1.0;
	image_file ant_shape const: true <- file('../images/ant.png');
	geometry ant_shape_svg const: true <- geometry(svg_file("../images/ant.svg"));
	obj_file ant3D_shape const: true <- obj_file('../images/fire-ant.obj', '../images/fire-ant.mtl', -90::{1, 0, 0});
	font regular <- font("Helvetica", 14, #bold);
	font bigger <- font("Helvetica", 18, #bold);

	//The center of the grid that will be considered as the nest location
	point center const: true <- {round(gridsize / 2), round(gridsize / 2)};
	int food_gathered <- 1;
	int food_placed <- 1;
	rgb background const: true <- rgb(99, 200, 66);
	rgb food_color const: true <- rgb(31, 22, 0);
	rgb nest_color const: true <- rgb(0, 0, 0);
	geometry shape <- square(gridsize);
	image_file terrain <- image_file("../images/soil.jpg");
	matrix<float> grid_values <- matrix<float>(as_matrix(terrain, {gridsize, gridsize}));

	init {

	// Normalization of the grid values
		float min <- min(grid_values);
		float max <- max(grid_values);
		float range <- (max - min) / 2.5;
		loop i from: 0 to: gridsize - 1 {
			loop j from: 0 to: gridsize - 1 {
				grid_values[i, j] <- (grid_values[i, j] - min) / range;
			}

		}

		//Creation of the food places placed randomly with a certain distance between each
		loop times: number_of_food_places {
			point loc <- {rnd(gridsize - 10) + 5, rnd(gridsize - 10) + 5};
			list<ant_grid> food_places <- (ant_grid where ((each distance_to loc) < 5));
			ask food_places {
				if food = 0 {
					food <- 5;
					food_placed <- food_placed + 5;
					color <- food_color;
				}

			}

		}
		//Creation of the ants that will be placed in the nest
		create ant number: ants_number with: (location: center);
	}
	//Reflex to diffuse the pheromon among the grid
	reflex diffuse {
		diffuse var: road on: ant_grid proportion: diffusion_rate radius: 3 propagation: gradient method: convolution;
	} }

	//Grid used to discretize the space to place food
grid ant_grid width: gridsize height: gridsize neighbors: 8 frequency: grid_frequency use_regular_agents: false use_individual_shapes: false {
	bool is_nest const: true <- (topology(ant_grid) distance_between [self, center]) < 4;
	float road <- 0.0 max: 240.0 update: (road <= evaporation_per_cycle) ? 0.0 : road - evaporation_per_cycle;
	rgb color <- is_nest ? nest_color : ((food > 0) ? food_color : ((road < 0.001) ? background : rgb(0, 99, 0) + int(road * 5))) update: is_nest ? nest_color : ((food > 0) ?
	food_color : ((road < 0.001) ? background : rgb(0, 99, 0) + int(road * 5)));
	int food <- 0;
}
//Species ant that will move and follow a final state machine
species ant skills: [moving] control: fsm {
	float speed <- 1.0;
	bool has_food <- false;

	//Reflex to place a pheromon stock in the cell
	reflex diffuse_road when: has_food = true {
		ant_grid(location).road <- ant_grid(location).road + 100.0;
	}
	//Action to pick food
	action pick (int amount) {
		has_food <- true;
		ant_grid place <- ant_grid(location);
		place.food <- place.food - amount;
	}
	//Action to drop food
	action drop {
		food_gathered <- food_gathered + 1;
		has_food <- false;
		heading <- heading - 180;
	}
	//Action to find the best place in the neighborhood cells
	point choose_best_place {
		container list_places <- ant_grid(location).neighbors;
		if (list_places count (each.food > 0)) > 0 {
			return point(list_places first_with (each.food > 0));
		} else {
			list_places <- (list_places where ((each.road > 0) and ((each distance_to center) > (self distance_to center)))) sort_by (each.road);
			return point(last(list_places));
		}

	}
	//Reflex to drop food once the ant is in the nest
	reflex drop when: has_food and (ant_grid(location)).is_nest {
		do drop();
	}
	//Reflex to pick food when there is one at the same location
	reflex pick when: !has_food and (ant_grid(location)).food > 0 {
		do pick(1);
	}
	//Initial state to make the ant wander 
	state wandering initial: true {
		do wander(amplitude: 90.0);
		float pr <- (ant_grid(location)).road;
		transition to: carryingFood when: has_food;
		transition to: followingRoad when: (pr > 0.05) and (pr < 4);
	}
	//State to carry food once it has been found
	state carryingFood {
		do goto(target: center);
		transition to: wandering when: !has_food;
	}
	//State to follow a pheromon road if once has been found
	state followingRoad {
		point next_place <- choose_best_place();
		float pr <- (ant_grid(location)).road;
		location <- next_place;
		transition to: carryingFood when: has_food;
		transition to: wandering when: (pr < 0.05) or (next_place = nil);
	}

	aspect info {
		if (use_icons) {
			draw ant_shape size: {7, 5} rotate: my heading + 1;
		} else {
			draw circle(1) wireframe: !has_food color: #red;
		}

		if (destination != nil) {
			draw line([location + {0, 0, 0.5}, {location.x + 5 * cos(heading), location.y + 5 * sin(heading)} + {0, 0, 0.5}]) + 0.1 color: #white border: false end_arrow: 1.2;
		}

		if (display_state) {
			draw string(self as int) color: #white font: regular at: my location + {0, -1, 0.5} anchor: #center;
			draw state color: #yellow font: bigger at: my location + {0, 0, 0.5} anchor: #center;
		}

	}

	aspect threeD {
		draw ant3D_shape size: {7, 5} at: (location + {0, 0, 1}) rotate: heading;
	}

	aspect icon {
		draw ant_shape size: {7, 5} rotate: my heading + 1 wireframe: true;
	}

	aspect icon_svg {
		draw (ant_shape_svg) size: {5, 7} at: (location)rotate: my heading + 90 color: #black;
	} }

	//Complete experiment that will inspect all ants in a table
experiment "With Inspector" type: gui {
	parameter 'Number:' var: ants_number init: 100 unit: 'ants' category: 'Environment and Population';
	parameter 'Grid dimension:' var: gridsize init: 100 unit: '(number of rows and columns)' category: 'Environment and Population';
	parameter 'Number of food depots:' var: number_of_food_places init: 5 min: 1 category: 'Environment and Population';
	output {
		layout #split editors: false;
		display Ants type: 3d axes:false{
			image terrain position: {0.05, 0.05} size: {0.9, 0.9} refresh: false;
			agents "agents" transparency: 0.7 position: {0.05, 0.05} size: {0.9, 0.9} value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			species ant position: {0.05, 0.05, 0.05} size: {0.9, 0.9} aspect: icon_svg;
			overlay transparency: 0.3 background: rgb(99, 85, 66, 255) position: {50 #px, 50 #px} size: {250 #px, 150 #px} border: rgb(99, 85, 66, 255) rounded: true {
				draw ant_shape at: {60 #px, 70 #px} size: {140 #px, 100 #px} rotate: -60;
				draw ('Food foraged: ' + (((food_placed = 0 ? 0 : food_gathered / food_placed) * 100) with_precision 2) + '%') at: {40 #px, 70 #px} font: font("Arial", 18, #bold) color:
				#white;
				draw ('Carrying ants: ' + (((100 * ant count (each.has_food or each.state = "followingRoad")) / length(ant)) with_precision 2) + '%') at: {40 #px, 100 #px} font:
				font("Arial", 18, #bold) color: #white;
			}

		}

		inspect "All ants" type: table value: ant attributes: ['name', 'state'];
	}

}

experiment "Classic" type: gui record: every(10) {
	
	parameter 'Number of ants:' var: ants_number category: 'Model';
	parameter 'Evaporation of the signal (unit/cycle):' var: evaporation_per_cycle category: 'Model';
	parameter 'Rate of diffusion of the signal (%/cycle):' var: diffusion_rate category: 'Model';
	parameter 'Use icons for the agents:' var: use_icons category: 'Display';
	parameter 'Display state of agents:' var: display_state category: 'Display';
	
	user_command "Save" {	save simulation to: '../result/file.simulation' format: "json" ;}
	
	output {
		display Ants antialias: false type: 3d {
			light #ambient intensity: 127;
			light #default intensity: 127;
			image terrain refresh: false;
			agents "Grid" transparency: 0.4 value: ant_grid where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			species ant aspect: info;
		}

	}

}

//Complete experiment that will inspect all ants in a table
experiment "3D View" type: gui {
	parameter 'Number:' var: ants_number init: 30 unit: 'ants' category: 'Environment and Population';
	parameter 'Grid dimension:' var: gridsize init: 100 unit: '(number of rows and columns)' category: 'Environment and Population';
	parameter 'Number of food depots:' var: number_of_food_places init: 5 min: 1 category: 'Environment and Population';
	output {
		

		display Ants3D type: 3d show_fps: true antialias: false{
			grid ant_grid elevation: grid_values triangulation: true texture: terrain refresh: false;
			agents "Trail" transparency: 0.7 position: {0.05, 0.05, 0.02} size: {0.9, 0.9} value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			species ant position: {0.05, 0.05, 0.025} size: {0.9, 0.9} aspect: threeD;
		}

	}

}

//Experiment to show how to make multi simulations
experiment "3 Simulations" type: gui record: every(10#cycle) {
	
	parameter 'Number:' var: ants_number init: 100 unit: 'ants' category: 'Environment and Population';
	parameter 'Grid dimension:' var: gridsize init: 100 unit: '(number of rows and columns)' category: 'Environment and Population';
	parameter 'Number of food depots:' var: number_of_food_places init: 5 min: 1 category: 'Environment and Population';
	

	// We create 2 supplementary simulations using the species name 'ants_model' (automatically created from the name of the model + '_model')
	init {
		create ants_model with: [ants_number::200, evaporation_per_cycle::100, diffusion_rate::0.2];
		create ants_model with: [ants_number::10, evaporation_per_cycle::72, diffusion_rate::0.6];
	}


	permanent {
		
		display Comparison background: #white {
			chart "Food Gathered" type: series {
				loop s over: simulations {
					if (!dead(s)) {
					data "Food " + int(s) value: s.food_gathered color: s.color marker: false style: line thickness: 5;
				}}

			}

		}

	}

	output {
		layout #split editors: false consoles: false toolbars: true tabs: false tray: false parameters: true;
		display Ants background: color type: 3d toolbar: color axes: false {
			image terrain position: {0.05, 0.05} size: {0.9, 0.9} refresh: false;
			agents "agents" transparency: 0.5 position: {0.05, 0.05} size: {0.9, 0.9} value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			species ant position: {0.05, 0.05} size: {0.9, 0.9} aspect: icon;
		}

	}

}





