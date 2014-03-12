model ants

global {
	list<list<int>> nbants<-[[0]];
	list<string> statesnames<-[""];
	list<string> categnames<-["empty","carry"];
	list<list<int>> nbantsbydist<-[[0]];
	list xytestvallist<-[[[1,1],[2,2],[3,3]],[[1,2],[2,1],[3,4]],[[1,3],[2,3],[0,1]],[[1,4],[2,5],[0,0]]];

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
  	reflex listupdate {
		int x<-0;
		nbants<-[];
		nbantsbydist<-[];
		statesnames<-[];
		write(list(ant));
		ant x<-one_of(world.ant);
		loop x over:list(world.ant)
		{
			if !(statesnames contains (x.state))
			{				
			add [(list(ant) count (each.state=x.state and each.has_food)),(list(ant) count (each.state=x.state and !each.has_food))] to: nbants;
			add (x.state) to:statesnames;				
			int d<-0;
			list nl<-[];
			loop d from:0 to:9
				{
			add (list(ant) count (each.state=x.state and (((each distance_to center)>gridsize/20*d) and ((each distance_to center)<gridsize/20*(d+1))))) to: nl;
				}
			add nl to:nbantsbydist;
			}
//			add length((list(world.ant) collect (each.next_place distance_to each.location)) where (each=x)) to:nbants;
		}
		write("nbants"+nbants);
		write("nbantsbydist"+nbantsbydist);
		write("states"+statesnames);
	}
}

entities {
	grid ant_grid width: gridsize height: gridsize neighbours: 8 frequency: grid_frequency use_regular_agents: false use_individual_shapes: false{
		const neighbours type: list of: ant_grid <- self neighbours_at 1;
		const is_nest type: bool <- (topology(ant_grid) distance_between [self, center]) < 4;
		rgb color <- is_nest ? nest_color : ((food > 0) ? food_color : ((float(road) < 0.001) ? background : rgb(#009900) + int(road * 5))) update: is_nest ? nest_color : ((food > 0) ?
		food_color : ((float(road) < 0.001) ? background : rgb(#009900) + int(road * 5)));
		int food <- 0;
	}
	species ant skills: [moving] control: fsm {
		float speed <- 1.0;
		bool has_food <- false;
		signal road value: has_food ? 240 : 0 decay: evaporation_rate proportion: diffusion_rate environment: ant_grid;
		point next_place;
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
			next_place <- choose_best_place();
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
 
experiment ChartList type: gui {
	list xytestval<-[[1,1],[2,2],[3,3]];


	output {
		display nbantsbydist
		{
			chart "nbantschartdist" type:histogram style:stack
			{
				datalist legend:statesnames value:nbantsbydist  inverse_series_categories :false style:stack;
			}
		}
		display nbantsdispbis
		{
			chart "nbantschart" type:histogram style:stack
			{
				datalist categoriesnames:categnames  value:nbants legend:statesnames inverse_series_categories :false style:stack;
			}
		}
		display nbantsdisp
		{
			chart "nbantschart" type:histogram style:stack
			{
				datalist legend:categnames  value:nbants categoriesnames:statesnames inverse_series_categories :true style:stack;
			}
		}




		display nbantsdisp2
		{
			chart "nbantsseries" type:series
			{
				datalist legend:categnames  value:nbants categoriesnames:statesnames inverse_series_categories :true;
			}
		}
		display nbantsdisp3
		{
			chart "nbantsseries" type:series
			{
				datalist legend:statesnames  value:nbantsbydist inverse_series_categories :false;
			}
		}
			display xytest
		{
			chart "xttest" type:xy
			{
				data value:[[1,1],[2,2],[3,3]] legend:"prems";
				data value:[[4,4],[2,2],[3,3]] legend:"sec";
				data value:[[5,5],[2,2],[3,3]] legend:"third";
			}
		}
			display xytestlist
		{
			chart "xttestlist" type:xy
			{
				datalist value:xytestvallist;
			}
		}
	}

}


