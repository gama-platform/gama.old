model ants

global {
	float evaporation_per_cycle <- 5.0 min: 0.0 max: 240.0 parameter: 'Evaporation of the signal (unit/cycle):' category: 'Signals';
	float diffusion_rate <- 1.0 min: 0.0 max: 1.0 parameter: 'Rate of diffusion of the signal (%/cycle):' category: 'Signals';
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
	
	reflex diffuse {
      diffuse var:road on:ant_grid proportion: diffusion_rate radius:3 propagation: gradient;
   }
  
}


grid ant_grid width: gridsize height: gridsize neighbors: 8 frequency: grid_frequency use_regular_agents: false use_individual_shapes: false{
	const neighbours type: list of: ant_grid <- self neighbors_at 1;
	const is_nest type: bool <- (topology(ant_grid) distance_between [self, center]) < 4;
	float road <- 0.0 max: 240.0 update: (road <= evaporation_per_cycle) ? 0.0 : road - evaporation_per_cycle;
	rgb color <- is_nest ? nest_color : ((food > 0) ? food_color : ((road < 0.001) ? background : rgb(#009900) + int(road * 5))) update: is_nest ? nest_color : ((food > 0) ?
	food_color : ((road < 0.001) ? background : rgb(#009900) + int(road * 5)));
	int food <- 0;
}
species ant skills: [moving] control: fsm {
	float speed <- 1.0;
	bool has_food <- false;
	
	reflex diffuse_road when:has_food=true{
      ant_grid(location).road <- ant_grid(location).road + 100.0;
   }
	
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
		draw circle(1) empty: !has_food color: #red;
		if (destination != nil) {
			draw line([location + {0,0,0.5}, destination + {0,0,0.5}]) + 0.1 color: #white border: false;
		}

		draw circle(4) empty: true color: #white;
		draw string(self as int) color: #white font: font("Helvetica", 12 * #zoom, #bold) at: my location - {1, 1, -0.5};
		draw state color: #yellow  font: font("Helvetica", 10 * #zoom, #plain) at: my location + { 1, 1, 0.5 } perspective: false;
	}

	aspect icon {
		draw ant_shape_empty size: {7,5} rotate: my heading + 1;
	}

	aspect default {
		draw square(1) empty: !has_food color: #blue rotate: my heading;
	}
}	

experiment Displays type: gui {
	point quadrant_size <- { 0.5, 0.5 };
	float inc <- 0.001;
	float pos <- 0.0;
	reflex moving_quadrant {
	//pos <- pos + inc;
		if (pos > 0.5 or pos <= 0) {
			inc <- -inc;
		}
		
	}

	output {
		display Ants background: #white type: opengl {
			image '../images/soil.jpg' position: { pos, pos } size: quadrant_size;
			agents "agents" transparency: 0.5 position: { pos, pos } size: quadrant_size value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			species ant position: { pos, pos } size: quadrant_size aspect: icon;
			grid ant_grid lines: #darkgray position: { 0.5, 0 } size: quadrant_size;
			species ant position: { 0.5, 0 } size: quadrant_size aspect: info;
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
		display Ants type: opengl {
			image '../images/soil.jpg' position: { 0.05, 0.05 } size: { 0.9, 0.9 };
			agents "agents" transparency: 0.5 position: { 0.05, 0.05 } size: { 0.9, 0.9 } value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest)) ;
			species ant position: { 0.05, 0.05 } size: { 0.9, 0.9 } aspect: icon;
			graphics "Texts" position: { 0,0, 0.05 }  {
				draw ('Food foraged: ' + (((food_placed = 0 ? 0 : food_gathered / food_placed) * 100) with_precision 2) + '%') at: {10, 6, 0.1} font:font("Arial", 16 * #zoom, #plain) color: #black;
				draw ('Carrying ants: ' + (((100 * ant count (each.has_food or each.state = "followingRoad")) / length(ant)) with_precision 2) + '%') at: {shape.width / 2 + 10, 6, 0.1} font:font("Arial", 16 * #zoom , #plain) color: #black;
			}
					
		}
		inspect "All ants" type: table value: ant attributes: ['name', 'location', 'heading','state'];
	}
}

experiment Batch type: batch repeat: 4 keep_seed: true until: (food_gathered = food_placed) or (time > 1000) {
	parameter 'Size of the grid:' var: gridsize init: 75 unit: 'width and height';
	parameter name: 'Number:' var: ants_number among:[10,20,50] unit: 'ants';
	parameter  'Evaporation:' var: evaporation_per_cycle among: [0.1, 0.5, 2.0, 10.0] unit: 'units every cycle';
	parameter  'Diffusion:' var: diffusion_rate min: 0.1 max: 1.0 unit: 'rate every cycle (1.0 means 100%)' step: 0.2;
	method exhaustive maximize: food_gathered;

	
	permanent {
		display Comparison background: #white {
			chart "Food Gathered" type: series {
					data "Min" value:  min(ants_model collect each.food_gathered ) style: spline color: #darkgreen ;
					data "Max" value:  max(ants_model collect each.food_gathered ) style: spline color: #red ;
			}
		}
	}
}

experiment Genetic type: batch repeat: 2 keep_seed: true until: (food_gathered = food_placed) or (time > 1000) {
	parameter name: 'Size of the grid:' var: gridsize init: 75 unit: '(width and height)';
	parameter name: 'Number:' var: ants_number among:[10,20,50] unit: 'ants';
	parameter  'Evaporation:' var: evaporation_per_cycle among: [0.1, 0.5, 2.0, 10.0] unit: 'units every cycle';
	parameter name: 'Diffusion:' var: diffusion_rate min: 0.1 max: 1.0 unit: 'rate every cycle (1.0 means 100%)' step: 0.2;
	method genetic maximize: food_gathered pop_dim: 5 crossover_prob: 0.7 mutation_prob: 0.1 nb_prelim_gen: 1 max_gen: 20;
	
		permanent {
		display Comparison background: #white {
			chart "Food Gathered" type: series {
					data "Min" value:  min(ants_model collect each.food_gathered ) style: spline color: #darkgreen ;
					data "Max" value:  max(ants_model collect each.food_gathered ) style: spline color: #red ;
			}
		}
	}
}

