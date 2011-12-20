model ants
// A simple model with one food depot. 
global {
	var evaporation_rate type: float init: 0.1 min: 0.01 max: 1 parameter:
	'Evaporation Rate:';
	const diffusion_rate type: float init: 0.5 min: 0 max: 1 parameter:
	'Diffusion Rate:';
	const gridsize type: int init: 75;
	const ants_number type: int init: 100 min: 1 max: 200 parameter:
	'Number of Ants:';
	var food_remaining type: int value: list ( ant_grid ) count ( each . food > 0
	) init: 10;
	const center type: point init: { round ( gridsize / 2 ) , round ( gridsize / 2 )
	};
	const types type: file init: file ( '../images/environment75x75_scarce.pgm' );
	init {
		create species: ant number: ants_number with: [ location :: center ];
	}

}
environment width: gridsize height: gridsize {
	grid ant_grid width: gridsize height: gridsize neighbours: 8 {
		var neighbours init: ( self neighbours_at 1 ) of_species ant_grid type: list
		of: ant_grid;
		const type type: int init: types at { grid_x , grid_y };
		const isNestLocation type: bool init: ( self distance_to center ) < 4;
		const isFoodLocation type: bool init: type = 2;
		var color type: rgb value: [ road > 15 ? 255 : ( isNestLocation ? 125 : 0 ) ,
		road * 30 , road > 15 ? 255 : food * 50 ];
		var food type: int init: isFoodLocation ? 5 : 0;
		const nest type: int init: 300 - ( self distance_to center );
	}
}
entities {
	species ant skills: [ moving ] {
		var color type: rgb init: 'red';
		var place type: ant_grid value: ant_grid ( location );
		var hasFood type: bool init: false;
		var road type: signal value: hasFood ? 240 : 0 decay: evaporation_rate
		proportion: diffusion_rate environment: ant_grid;
		var hasRoad type: bool init: false value: place . road > 0.05;
		reflex wandering when: ( ! hasFood ) and ( ! hasRoad ) and ( place . food = 0
		) {
			do action: wander {
				arg amplitude type: int value: 120;
				arg speed type: float value: 1.0;
			}
		}
		reflex looking when: ( ! hasFood ) and ( hasRoad ) and ( place . food = 0 ) {
			let list_places value: place . neighbours;
			let goal value: list_places first_with ( each . food > 0 );
			if condition: goal != nil {
				set location value: goal ;
				else {
					let min_nest value: ( list_places min_of ( each . nest ) );
					set list_places value: list_places sort ( ( each . nest = min_nest ) ?
					each . road : 0.0 ) ;
					set location value: point ( last ( list_places ) ) ;
				}
			}
		}
		reflex taking when: ( ! hasFood ) and ( place . food > 0 ) {
			set hasFood value: true ;
			set place . food value: place . food - 1 ;
		}
		
		reflex homing when: ( hasFood ) and ( ! place . isNestLocation ) {
			do action: goto with: [target::center, speed::1.0];
		}
		reflex dropping when: ( hasFood ) and ( place . isNestLocation ) {
			set hasFood value: false ;
			set heading value: heading - 180 ;
		}
		aspect name: 'default' {
			draw shape: circle size: 2 color: color;
		}
	}
}
experiment name: 'Simple' type: gui {
	output {
		display Ants refresh_every: 2 {
			grid ant_grid;
			species ant aspect: default;
			text tt value: string ( food_remaining ) size: 24 position: { 20 , 20 }
			color: rgb ( 'white' );
		}
	}
}

// This experiment explores two parameters with an exhaustive strategy, 
// repeating each simulation two times, in order to find the best combination 
// of parameters to minimize the time taken by ants to gather all the food
experiment name: 'Exhaustive optimization' type: batch repeat: 2 keep_seed: true
until: ( food_remaining = 0 ) or ( time > 400 ) {
	parameter name: 'Evaporation rate' var: evaporation_rate among: [ 0.1 , 0.2 ,
	0.5 , 0.8 , 1.0 ];
	parameter name: 'Diffusion rate' var: diffusion_rate min: 0.1 max: 1.0 step:
	0.3;
	method exhaustive minimize: time;
	save to: 'ant_exhaustive_optimization' rewrite: false;
}

// This experiment simply explores two parameters with an exhaustive strategy, 
// repeating each simulation two times, and saves the time taken by ants to gather all the food in a file
experiment Repeated type: batch repeat: 2 keep_seed: true until: (
food_remaining = 0 ) or ( time > 400 ) {
	parameter name: 'Evaporation rate' var: evaporation_rate among: [ 0.1 , 0.2 ,
	0.5 , 0.8 , 1.0 ];
	parameter name: 'Diffusion rate' var: diffusion_rate min: 0.1 max: 1.0 step:
	0.3;
	save to: 'ant_exhaustive' data: time rewrite: false;
}

// This experiment explores two parameters with a GA strategy, 
// repeating each simulation two times, in order to find the best combination 
// of parameters to minimize the time taken by ants to gather all the food 
experiment Genetic type: batch keep_seed: true repeat: 3 until: ( food_remaining
= 0 ) or ( time > 400 ) {
	parameter name: 'Evaporation rate' var: evaporation_rate min: 0.05 max: 0.7
	step: 0.01;
	parameter name: 'Diffusion rate' var: diffusion_rate min: 0.0 max: 1.0 step:
	0.01;
	method genetic pop_dim: 5 crossover_prob: 0.7 mutation_prob: 0.1
	nb_prelim_gen: 1 max_gen: 20 minimize: time;
	save to: 'ant_genetic_algorithm' rewrite: false;
}
