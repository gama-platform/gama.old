model ants

global {
	int ants_number <- 100 min: 1 max: 2000 ;
	float evaporation_rate <- 0.1 min: 0.0 max: 1.0 ;
	float diffusion_rate <- 0.5 min: 0.0 max: 1.0 ;
	bool use_icons <- true ;
	bool display_state <- true;
	int gridsize <- 75 ;
	const center type: point <- { (gridsize / 2),  (gridsize / 2)} ;
	const types type: file <- (pgm_file('../images/environment75x75.pgm')) ;
	const ant_shape_empty type: string <- '../icons/ant.png' ;
	const ant_shape_full type: string <- '../icons/full_ant.png'  ;
	const C00CC00 type: rgb <- rgb('#00CC00') ;    
	const C009900 type: rgb <- rgb('#009900') ; 
	const C005500 type: rgb <- rgb('#005500') ; 
	int food_gathered <- 0 ;    
	geometry shape <- square(gridsize);
	init{  
		create ant number: ants_number with: [location::any_location_in (ant_grid(center))] ;
	}

}

entities {
	grid ant_grid width: gridsize height: gridsize neighbours: 8 use_regular_agents: false {
		list<ant_grid> neighbours <- self neighbours_at 1;
		bool multiagent <- true ;
		int type <- int(types at {grid_x,grid_y}) ;
		bool isNestLocation <- (self distance_to center) < 4 ;
		bool isFoodLocation <- type = 2 ; 
		rgb color <- isNestLocation ? °violet:((food > 0)? °blue : ((road < 0.001)? rgb ([100,100,100]) : ((road > 2)? °white : ((road > 0.5)? (C00CC00) : ((road > 0.2)? (C009900) : (C005500)))))) update: isNestLocation ? °violet:((food > 0)? °blue : ((road < 0.001)? rgb ([100,100,100]) : ((road > 2)? °white : ((road > 0.5)? (C00CC00) : ((road > 0.2)? (C009900) : (C005500)))))) ;
		int food <- isFoodLocation ? 5 : 0 ;
		const nest type: int <- 300 - int(self distance_to center) ;
		
	}
	species ant skills: [moving] control: fsm {
		float speed <- 2.0 ;
		ant_grid place update: ant_grid (location ); 
		string im <- 'ant_shape_empty' ;
		bool hasFood <- false ;
		/**
		 * This variable defines the chemical signal that will be followed by the ants
		 */
		signal road update: hasFood ? 240.0 : 0.0 decay: evaporation_rate proportion: diffusion_rate environment: ant_grid ;
		action pick {
			im <- ant_shape_full ;
			hasFood <- true ;
			place.food <- place.food - 1 ;
		}
		action drop {
			food_gathered <- food_gathered + 1 ;  
			hasFood <- false ;
			heading <- heading - 180 ;
		}
		action choose_best_place type: ant_grid {  
			list<ant_grid> list_places <- place.neighbours ;
			if (list_places count (each.food > 0)) > 0  { 
				return (list_places first_with (each.food > 0)) ;
			} else {
					int min_nest  <-  (list_places min_of (each.nest)) ;
					list_places <- list_places sort ((each.nest = min_nest) ? each.road :  0.0) ;
					return last(list_places) ;
				}
		} 
		state wandering initial: true { 
			do wander amplitude:120 ;
			transition to: carryingFood when: place.food > 0 {
				
				 
				do pick ;
			}
			transition to: followingRoad when: place.road > 0.05 ; 
		}
		state carryingFood {
			do goto target: center ;
			transition to: wandering when: place.isNestLocation { 
				do drop ;
			}
		}
		state followingRoad {
			location <- (self choose_best_place()) as point ;
			transition to: carryingFood when: place.food > 0 {
				do pick ;
			}
			transition to: wandering when: (place.road < 0.05) ;
		}
		aspect text {
			if use_icons {
				draw  hasFood ? file(ant_shape_full) : file(ant_shape_empty) rotate: heading at: location size: 5 ;
			} else {
				draw circle(1.0) empty: !hasFood color: rgb ('orange') ;
			}
			if condition: display_state {
				draw state at: location + {-3,1.5} color: °white size: 0.8 ;
			}
		} 
		aspect default {
			draw shape: circle(1.0) empty: !hasFood color: rgb('orange') ; 
		}           
	}
}
experiment Ant type: gui {
	parameter 'Number of ants:' var: ants_number category: 'Model' ;
	parameter 'Rate of evaporation of the signal (%/cycle):' var: evaporation_rate category: 'Model' ;
	parameter 'Rate of diffusion of the signal (%/cycle):' var: diffusion_rate category: 'Model' ;
	parameter 'Use icons for the agents:' var: use_icons category: 'Display' ;
	parameter 'Display state of agents:' var: display_state category: 'Display' ;
	output {
		display Ants type: opengl {
			grid ant_grid ;
			species ant aspect: text ;
		}
	}
}


