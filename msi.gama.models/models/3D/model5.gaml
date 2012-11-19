model model5
// A simple model with one food depot. 
global {
	int t <- 1;
	float evaporation_rate <- 0.1 min: 0.01 max: float(1) ;
	const diffusion_rate type: float <- 0.7 min: 0.0 max: float(1) ;
	const gridsize type: int <- 75; 
	const ants_number type: int <- 50 min: 1 max: 200 parameter: 'Number of Ants:';
	int food_remaining update: list ( ant_grid ) count ( each . food > 0) <- 10;
	const center type: point <- { round ( gridsize / 2 ) , round ( gridsize / 2 ) };
	const types type: file of: int <- file ( 'environment75x75_scarce.pgm' ); 
	init {
		create ant number: ants_number with: [ location :: center, maturity :: true ];
		
		create ant number: 50 with: [ location :: center, maturity :: false ]; 
	} 

} 
environment width: gridsize height: gridsize {
	grid ant_grid width: gridsize height: gridsize neighbours: 8 {
		const type type: int <- (types at { grid_x , grid_y });
		const isNestLocation type: bool <- ( self distance_to center ) < 4;
		const isFoodLocation type: bool <- type = 2;       
		list neighbours <- ( self neighbours_at 1 ) of_species ant_grid of: ant_grid;  
		rgb color <- rgb([ road > 15 ? 255 : ( isNestLocation ? 125 : 0 ) ,road * 30 , road > 15 ? 255 : food * 50 ]) update: rgb([ road > 15 ? 255 : ( isNestLocation ? 125 : 0 ) ,road * 30 , road > 15 ? 255 : food * 50 ]); 
		int food <- isFoodLocation ? 5 : 0; 
		const nest type: int <- int(300 - ( self distance_to center ));
	}
}  
entities {
	species ant skills: [ moving ] {     
		bool maturity;

		rgb color <- maturity ? rgb('red') : rgb('blue');
		ant_grid place function: {ant_grid ( location )};
		bool hasFood <- false;
		signal road value: hasFood ? 240 : 0 decay: evaporation_rate proportion: diffusion_rate environment: ant_grid; 
		bool hasRoad <- false update: place . road > 0.05;
		
		
		reflex wandering when: !maturity or ( ( ! hasFood ) and ( ! hasRoad ) and ( place . food = 0)) {
			do wander amplitude: 120 speed: 1.0;
		}
		reflex looking when: (maturity) and ( ! hasFood ) and ( hasRoad ) and ( place . food = 0 ) { 
			let list_places <- place . neighbours;
			let goal <- list_places first_with ( each . food > 0 );
			if goal != nil {
				set location <- goal.location ; 
			} else {
				let min_nest <- ( list_places min_of ( each . nest ) );
				set list_places <- list_places sort ( ( each . nest = min_nest ) ? each . road : 0.0 ) ;
				set location <- point ( last ( list_places ) ) ;
			}
		}
		reflex taking when: (maturity) and ( ! hasFood ) and ( place . food > 0 ) { 
			set hasFood <- true ;
//			set place . food <- place . food - 1 ;
		}
		
		reflex homing when: (maturity) and ( hasFood ) and ( ! place . isNestLocation ) {
			do goto target:center  speed:1.0;
		}
		reflex dropping when: (maturity) and ( hasFood ) and ( place . isNestLocation ) {
			set hasFood <- false ;
			set heading <- heading - 180 ;
		}
		aspect name: 'default' {
			draw shape: circle size: 2 color: color;
		}
	}
}
experiment Simple type: gui {
	parameter 'Evaporation Rate:' var: evaporation_rate;
	parameter 'Diffusion Rate:' var: diffusion_rate;
	output { 
		display Ants refresh_every: 2 { 
			grid ant_grid;
			species ant aspect: default;
			text tt value: string ( food_remaining ) size: 24.0 position: { 20 , 20 } color: rgb ( 'white' );
		}  
		
		
		display population_graph {
			chart "Species evolution" type: series background: rgb('white') {
				data going_home value: length(list(ant) where (each.hasFood and ( ! (each.place).isNestLocation )  )) color: rgb('blue') ;
				data  approaching_food value: length(list(ant) where (each.hasRoad and !each.hasFood ) ) color: rgb('red') ;
			}
		}
	}
}

