model ants

global {
	float evaporation_rate <- 0.10 min: 0.0 max: 1.0 parameter: 'Rate of evaporation of the signal (%/cycle):' category: 'Signals';
	float diffusion_rate <- 0.5 min: 0.0 max: 1.0 parameter: 'Rate of diffusion of the signal (%/cycle):' category: 'Signals';
	int gridsize <- 100 min: 30 parameter: 'Width and Height of the grid:' category: 'Environment and Population';
	int ants_number <- 50 min: 1 parameter: 'Number of ants:' category: 'Environment and Population';
	int grid_frequency <- 1 min: 1 max: 100 parameter: 'Grid updates itself every:' category: 'Environment and Population';
	int number_of_food_places <- 5 min: 1 parameter: 'Number of food depots:' category: 'Environment and Population';
	float grid_transparency <- 1.0;
	const ant_shape_empty type: file <- file('../icons/ant.png');
	const ant_shape_full type: string <- '../icons/full_ant.png';
	const center type: point <- { round(gridsize / 2), round(gridsize / 2) };
	var food_gathered type: int <- 1;
	var food_placed type: int <- 1;
	const background type: rgb <- rgb(#99CC66);
	const food_color type: rgb <- rgb(#312200);
	const nest_color type: rgb <- rgb(#000000); 

	geometry shape <- square(gridsize);
	init {
		loop times: number_of_food_places {
			point loc <- { rnd(gridsize - 10) + 5, rnd(gridsize - 10) + 5 };
			list<ant_grid> food_places <- (ant_grid where ((each distance_to loc) < 5));
			ask food_places {
				if food = 0 {
					food <- 5;
					food_placed <- food_placed + 5;
					color <- food_color;  
				}                                           
			}
		}
		create ant number: ants_number with: (location: center);
	}
  
}

entities {
	grid ant_grid width: gridsize height: gridsize neighbours: 8 frequency: grid_frequency use_regular_agents: false use_individual_shapes: false{
		const neighbours type: list of: ant_grid <- self neighbours_at 1;
		const is_nest type: bool <- (topology(ant_grid) distance_between [self, center]) < 4;
		rgb color <- is_nest ? nest_color : ((food > 0) ? food_color : ((road < 0.001) ? background : rgb(#009900) + int(road * 5))) update: is_nest ? nest_color : ((food > 0) ?
		food_color : ((road < 0.001) ? background : rgb(#009900) + int(road * 5)));
		int food <- 0;
	}
	species ant skills: [moving] control: fsm {
		float speed <- 1.0;
		bool has_food <- false;
		signal road update: has_food ? 240 : 0 decay: evaporation_rate proportion: diffusion_rate environment: ant_grid;
		
		action pick (int amount) {
			has_food <- true;
			ant_grid place <- ant_grid(location);
			place.food <- place.food - amount;
		}
	
		action drop {
			food_gathered <- food_gathered + 1;
			has_food <- false;
			heading <- heading - 180;
		}
	
		point choose_best_place {
			container list_places <- (ant_grid(location)).neighbours;
			if (list_places count (each.food > 0)) > 0 {
				return point(list_places first_with (each.food > 0));
			} else {
				list_places <- (list_places where ((each.road > 0) and ((each distance_to center) > (self distance_to center)))) sort_by (each.road);
				return point(last(list_places));
			}
	
		}
	
		reflex drop when: has_food and (ant_grid(location)).is_nest {
			do drop();
		}
	
		reflex pick when: !has_food and (ant_grid(location)).food > 0 {
			do pick(1);
		}
	
		state wandering initial: true {
			do wander(amplitude: 90);
			float pr <- (ant_grid(location)).road;
			transition to: carryingFood when: has_food;
			transition to: followingRoad when: (pr > 0.05) and (pr < 4);
		}
	
		state carryingFood {
			do goto(target: center);
			transition to: wandering when: !has_food;
		}
	
		state followingRoad {
			point next_place <- choose_best_place();
			float pr <- (ant_grid(location)).road;
			location <- next_place;
			transition to: carryingFood when: has_food;
			transition to: wandering when: (pr < 0.05) or (next_place = nil);
		}
	
		aspect info {
			draw circle(1) empty: !has_food color: rgb('red');
			if (destination != nil) {
				draw line([location, destination]) color: rgb('white');
			}
	
			draw circle(4) empty: true color: rgb('white');
			draw string(self as int) color: rgb('white') size: 1;
			draw state color: rgb('yellow') size: 10 °px at: my location + { 1, 1 } style: "bold";
		}
	
		aspect icon {
			draw ant_shape_empty size: 10 rotate: my heading + 1;
		}
	
		aspect default {
			draw square(1) empty: !has_food color: rgb('blue') rotate: my heading;
		}
	}	
}



experiment Displays type: gui {
	point quadrant_size <- { 0.5, 0.5 };
	float font_size {
		12 °px
	}
	float inc <- 0.001;
	float pos <- 0.0;
	reflex moving_quadrant {
	//pos <- pos + inc;
		if (pos > 0.5 or pos <= 0) {
			inc <- -inc;
		}
		
	}

	float carrying -> { cycle = 0 ? 0 : (((100 * world.ant count (each.has_food or each.state = "followingRoad")) / length(world.ant)) with_precision 2) };
	output {
		display Ants background: rgb('white') refresh_every: 1 type: opengl {
		// First quadrant
			image '../images/soil.jpg' position: { pos, pos } size: quadrant_size;
			text "position {0,0} size {0.5, 0.5}: image, cells and ants as icons" size: font_size position: { pos + 0.01, pos + 0.03 } color: rgb("yellow") font: "Helvetica" style: bold;
			agents "agents" transparency: 0.5 position: { pos, pos } size: quadrant_size value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			species ant position: { pos, pos } size: quadrant_size aspect: icon;

			//Second quadrant
			grid ant_grid lines: rgb("black") position: { 0.5, 0 } size: quadrant_size;
			text "position {0.5,0} size {0.5, 0.5}: grid and simple ants" size: font_size position: { 0.51, 20 °px } color: rgb("white") font: "Helvetica" style: bold;
			species ant position: { 0.5, 0 } size: quadrant_size aspect: info;

			//Third quadrant
			quadtree 'qt' position: { 0, 0.5 } size: quadrant_size;
			text "position {0,0.5} size {0.5, 0.5}: quadtree and ants" size: font_size position: { 0.01, 0.53 } color: rgb("blue") font: "Helvetica" style: bold;
			species ant position: { 0, 0.5 } size: quadrant_size aspect: default;

			//Fourth quadrant
			chart name: 'Proportion of workers' type: pie background: rgb('white') style: exploded position: { 0.5, 0.50 } size: quadrant_size transparency: 0.5 + pos {
				data 'Working' value: carrying color: rgb("red");
				data 'Idle' value: 100 - carrying color: rgb("blue");
			}

			text ('Food foraged: ' + (((food_placed = 0 ? 0 : food_gathered / food_placed) * 100) with_precision 2) + '%') position: { 0.51, 0.53 } color: rgb('black') size: font_size
			style: bold;
			text 'Carrying ants: ' + carrying + '%' position: { 0.75, 0.53 } color: rgb('black') size: font_size style: bold;
		}
	}
}

experiment Complete type: gui {
	parameter name: 'Number:' var: ants_number init: 100 unit: 'ants' category: 'Environment and Population';
	parameter name: 'Grid dimension:' var: gridsize init: 100 unit: '(number of rows and columns)' category: 'Environment and Population';
	parameter name: 'Number of food depots:' var: number_of_food_places init: 5 min: 1 category: 'Environment and Population';

	// Experimentator

	init {
		write "Experimentator agent running " + self;
	   ants_number <- 200;
	}


	output {
		display Ants background: rgb('white') refresh_every: 1{
			image '../images/soil.jpg' position: { 0.05, 0.05 } size: { 0.9, 0.9 };
			agents "agents" transparency: 0.5 position: { 0.05, 0.05 } size: { 0.9, 0.9 } value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			species ant position: { 0.05, 0.05 } size: { 0.9, 0.9 } aspect: icon;
			text ('Food foraged: ' + (((food_placed = 0 ? 0 : food_gathered / food_placed) * 100) with_precision 2) + '%') position: { 0.05, 0.03 } color: rgb('black') size: { 1, 0.02 };
			text 'Carrying ants: ' + (((100 * ant count (each.has_food or each.state = "followingRoad")) / length(ant)) with_precision 2) + '%' position: { 0.5, 0.03 } color: rgb('black')
			size: { 1, 0.02 };
		}
		inspect "One" type: table value: ant attributes: ['name', 'location', 'heading','state'];
		inspect "Two" type: table value: 10 among ant attributes: ['state'];
	}
}

experiment Batch type: batch repeat: 2 keep_seed: true until: (food_gathered = food_placed) or (time > 400) {
	parameter 'Size of the grid:' var: gridsize init: 75 unit: 'width and height';
	parameter  'Number:' var: ants_number init: 200 unit: 'ants';
	parameter  'Evaporation:' var: evaporation_rate among: [0.1, 0.2, 0.5, 0.8, 1.0] unit: 'rate every cycle (1.0 means 100%)';
	parameter  'Diffusion:' var: diffusion_rate min: 0.1 max: 1.0 unit: 'rate every cycle (1.0 means 100%)' step: 0.3;
	method exhaustive maximize: food_gathered;
	
	reflex info_sim{
		write "Running a new simulation " + simulation; 
	}
	
	permanent {
		display Ants background: rgb('white') refresh_every: 1 {
			chart "Food Gathered" type: series {
				data "Food" value: food_gathered;
			}
		}
	}
}

experiment Genetic type: batch repeat: 2 keep_seed: true until: (food_gathered = food_placed) or (time > 400) {
	parameter name: 'Size of the grid:' var: gridsize init: 75 unit: '(width and height)';
	parameter name: 'Number:' var: ants_number init: 200 unit: 'ants';
	parameter name: 'Evaporation:' var: evaporation_rate among: [0.1, 0.2, 0.5, 0.8, 1.0] unit: 'rate every cycle (1.0 means 100%)';
	parameter name: 'Diffusion:' var: diffusion_rate min: 0.1 max: 1.0 unit: 'rate every cycle (1.0 means 100%)' step: 0.3;
	method genetic maximize: food_gathered pop_dim: 5 crossover_prob: 0.7 mutation_prob: 0.1 nb_prelim_gen: 1 max_gen: 20;
}

experiment Quadtree type: gui {
	output {
		monitor name: 'Food gathered' value: food_gathered;
		display QuadTree {
			quadtree 'qt';
		}

		display Ants background: rgb('white') refresh_every: 1 {
			grid ant_grid lines: rgb('black');
			species ant aspect: default;
		}
	}
}

experiment Callback type: gui parent: Complete { //Inherits from experiment "complete" its parameters (outputs will be done later)
	int i <- 0;
	
	
	action _step_ { // Redefinition of the default _step_ action (could be written in a reflex in that case)
	
		write "Experiment step " + i;
		i <- i + 1; // Right now, experiments do not have "cycles"
		
		loop times: 20 {
			ask simulation {
				write "Simulation cycle " + cycle;
				do _step_; // we ask the simulation to run 400 times
			}
		}

		ask simulation {
			do die; // the simulation is disposed
		}
		int n <- int(user_input( 'Simulation '  + i, ['Ants number ?'::100])['Ants number ?']);
		create ants_model with: [ants_number:: n]; // automatically modifies "simulation". 'ants_model' is the species of the model in which the experiment is defined
		write "Number of ants: " + simulation.ants_number; // We verify it is correct
	}
}