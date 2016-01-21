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
		write "Simulation " + int(self) + " created";
	}
  
}


grid ant_grid width: gridsize height: gridsize neighbors: 8 frequency: grid_frequency use_regular_agents: false use_individual_shapes: false{
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
		container<ant_grid> list_places <- container<ant_grid>((ant_grid(location)).neighbors);
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

	aspect icon {
		draw ant_shape_empty size: 10 rotate: my heading + 1;
	}

}	


experiment "4 Simulations" type: gui {
	parameter name: 'Number:' var: ants_number init: 100 unit: 'ants' category: 'Environment and Population';
	parameter name: 'Grid dimension:' var: gridsize init: 100 unit: '(number of rows and columns)' category: 'Environment and Population';
	parameter name: 'Number of food depots:' var: number_of_food_places init: 5 min: 1 category: 'Environment and Population';

	// We create three supplementary simulations using the species name 'ants_model' (automatically created from the name of the model + '_model')

	init {
		create simulation with: [ants_number::200,evaporation_rate::0.7,diffusion_rate::0.2];
		create simulation with: [ants_number::50,evaporation_rate::0.05,diffusion_rate::0.8];
		create simulation with: [ants_number::10,evaporation_rate::0.3,diffusion_rate::0.6];
	}
	
	permanent {
		display Comparison background: #white {
			chart "Food Gathered" type: series {
				data "Food 0" value: ants_model[0].food_gathered color: #blue;
				data "Food 1" value: ants_model[1].food_gathered color: #green;
				data "Food 2" value: ants_model[2].food_gathered color: #darkgray;
				data "Food 3" value: ants_model[3].food_gathered color: (#orange).darker;
			}
		}
	}


	output {
		display Ants background: #white type: opengl{
			image '../images/soil.jpg' position: { 0.05, 0.05 } size: { 0.9, 0.9 };
			agents "agents" transparency: 0.5 position: { 0.05, 0.05 } size: { 0.9, 0.9 } value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			species ant position: { 0.05, 0.05 } size: { 0.9, 0.9 } aspect: icon;
			text ('Food foraged: ' + (((food_placed = 0 ? 0 : food_gathered / food_placed) * 100) with_precision 2) + '%') position: { 0.05, 0.03 } color: #black size: { 1, 0.02 };
			text 'Carrying ants: ' + (((100 * ant count (each.has_food or each.state = "followingRoad")) / length(ant)) with_precision 2) + '%' position: { 0.5, 0.03 } color: #black
			size: { 1, 0.02 };
		}
		inspect "Ants" type: table value: ant attributes: ['name', 'location', 'heading','state'];

	}
}


