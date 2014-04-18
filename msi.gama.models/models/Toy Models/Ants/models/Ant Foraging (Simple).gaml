model ants
// A simple model with one food depot. 
global {
	int t <- 1;
	float evaporation_rate <- 0.1 min: 0.01 max: float(1) ;
	const diffusion_rate type: float <- 0.7 min: 0.0 max: float(1) ;
	const gridsize type: int <- 75; 
	int ants_number  <- 50 min: 1 max: 200 parameter: 'Number of Ants:';
	int food_remaining update: list ( ant_grid ) count ( each . food > 0) <- 10;
	const center type: point <- { round ( gridsize / 2 ) , round ( gridsize / 2 ) };
	const types type: matrix of: int <- matrix<int> (pgm_file ( '../images/environment75x75_scarce.pgm' )); 
	
	geometry shape <- square(gridsize);
	
	init {
		create ant number: ants_number with: [ location :: center ];
	} 
	
	action press (point loc, list selected_agents)
	{
		write("press " + loc.x + " " + loc.y + " "+selected_agents);
	}
	action release (point loc, list selected_agents)
	{
		write("release");
	}
	action click  (point loc, list selected_agents)
	{
		write("click");
	}
	action click2   (point loc, list selected_agents)
	{
		write("click2");
	}

} 
entities {
	grid ant_grid width: gridsize height: gridsize neighbours: 8 {
		bool isNestLocation  <- ( self distance_to center ) < 4;
		bool isFoodLocation <-  types[grid_x , grid_y] = 2;       
		list<ant_grid> neighbours <- self neighbours_at 1;  
		rgb color <- rgb([ self.road > 15 ? 255 : ( isNestLocation ? 125 : 0 ) , self.road * 30 , self.road > 15 ? 255 : food * 50 ]) update: rgb([ self.road > 15 ? 255 : ( isNestLocation ? 125 : 0 ) ,self.road * 30 , self.road > 15 ? 255 : food * 50 ]); 
		int food <- isFoodLocation ? 5 : 0; 
		const nest type: int <- int(300 - ( self distance_to center ));
	}
	species ant skills: [ moving ] {     
		rgb color <- rgb('red');
		ant_grid place function: {ant_grid ( location )};
		bool hasFood <- false;
		signal road update: hasFood ? 240 : 0 decay: evaporation_rate proportion: diffusion_rate environment: ant_grid; 
		bool hasRoad <- false update: place . road > 0.05;
		reflex wandering when: ( ! hasFood ) and ( ! hasRoad ) and ( place . food = 0) {
			do wander amplitude: 120 speed: 1.0;
		}
		reflex looking when: ( ! hasFood ) and ( hasRoad ) and ( place . food = 0 ) { 
			list<ant_grid> list_places <- place . neighbours;
			ant_grid goal <- list_places first_with ( each . food > 0 );
			if goal != nil {
				location <- goal.location ; 
			} else {
				int min_nest <- ( list_places min_of ( each . nest ) );
				list_places <- list_places sort ( ( each . nest = min_nest ) ? each . road : 0.0 ) ;
				location <- point ( last ( list_places ) ) ;
			}
		}
		reflex taking when: ( ! hasFood ) and ( place . food > 0 ) { 
			hasFood <- true ;
			place . food <- place . food - 1 ;
		}
		
		reflex homing when: ( hasFood ) and ( ! place . isNestLocation ) {
			do goto target:center  speed:1.0;
		}
		reflex dropping when: ( hasFood ) and ( place . isNestLocation ) {
			hasFood <- false ;
			heading <- heading - 180 ;
		}
		aspect default {
			draw circle(2.0) color: color;
		}
		
	}
}
experiment Simple type:gui {
	parameter 'Evaporation Rate:' var: evaporation_rate;
	parameter 'Diffusion Rate:' var: diffusion_rate;
	output { 
		display Ants refresh_every: 2 { 
			grid ant_grid;
			species ant aspect: default;
			text string ( food_remaining ) size: 24.0 position: { 20 , 20 } color: rgb ( 'white' );
			event mouse_down action:press;
			event mouse_up action:release;
		}  
		display Ants_2 refresh_every: 2 { 
			grid ant_grid;
			text string ( food_remaining ) size: 24.0 position: { 20 , 20 } color: rgb ( 'white' );
			event mouse_down action:press;
			event mouse_up action:click2;
		}  
	}
}

// This experiment explores two parameters with an exhaustive strategy, 
// repeating each simulation two times, in order to find the best combination 
// of parameters to minimize the time taken by ants to gather all the food
experiment 'Exhaustive optimization' type: batch repeat: 2 keep_seed: true until: ( food_remaining = 0 ) or ( time > 400 ) {
	parameter 'Evaporation rate' var: evaporation_rate among: [ 0.1 , 0.2 ,
	0.5 , 0.8 , 1.0 ];
	parameter 'Diffusion rate' var: diffusion_rate min: 0.1 max: 1.0 step:
	0.3;
	method exhaustive minimize: time;
}

// This experiment simply explores two parameters with an exhaustive strategy, 
// repeating each simulation two times
experiment Repeated type: batch repeat: 2 keep_seed: true until: (
food_remaining = 0 ) or ( time > 400 ) {
	parameter 'Evaporation rate' var: evaporation_rate among: [ 0.1 , 0.2 ,0.5 , 0.8 , 1.0 ];
	parameter 'Diffusion rate' var: diffusion_rate min: 0.1 max: 1.0 step:0.3;
}

// This experiment explores two parameters with a GA strategy, 
// repeating each simulation two times, in order to find the best combination 
// of parameters to minimize the time taken by ants to gather all the food 
experiment Genetic type: batch keep_seed: true repeat: 3 until: ( food_remaining
= 0 ) or ( time > 400 ) {
	parameter 'Evaporation rate' var: evaporation_rate min: 0.05 max: 0.7
	step: 0.01;
	parameter 'Diffusion rate' var: diffusion_rate min: 0.0 max: 1.0 step:
	0.01;
	method genetic pop_dim: 5 crossover_prob: 0.7 mutation_prob: 0.1
	nb_prelim_gen: 1 max_gen: 20 minimize: time;
}
