model ants

global {
	var ants_number type: int init: 100 parameter: 'Number of ants:' min: 1 max: 2000 category: 'Model' ;
	var evaporation_rate type: float init: 0.1 min: 0 max: 1 parameter: 'Rate of evaporation of the signal (%/cycle):' category: 'Model' ;
	var diffusion_rate type: float init: 0.5 min: 0 max: 1 parameter: 'Rate of diffusion of the signal (%/cycle):' category: 'Model' ;
	const gridsize type: int init: 75 ;
	var use_icons type: bool init: true parameter: 'Use icons for the agents:' category: 'Display' ;
	var display_state type: bool init: true parameter: 'Display state of agents:' category: 'Display' ;
	const center type: point init: { (gridsize / 2),  (gridsize / 2)} ;
	const types type: file init: (read(image('../images/environment75x75.pgm'))) ;
	const ant_shape_empty type: string init: '../icons/ant.png' ;
	const ant_shape_full type: string init: '../icons/full_ant.png'  ;
	const black type: rgb init: rgb('black') ;
	const blue type: rgb init: rgb('blue') ;
	const green type: rgb init: rgb('green') ;
	const white type: rgb init: rgb('white') ; 
	const FF00FF type: rgb init: rgb('gray') ; 
	const C00CC00 type: rgb init: rgb('#00CC00') ;   
	const C009900 type: rgb init: rgb('#009900') ; 
	const C005500 type: rgb init: rgb('#005500') ; 
	const yellow type: rgb init: rgb('yellow') ; 
	const red type: rgb init: rgb('red') ;  
	const orange type: rgb init: rgb('orange') ; 
	var food_gathered type: int init: 0 ;

}
environment width: gridsize height: gridsize {  
	grid ant_grid width: gridsize height: gridsize neighbours: 8 {
		const neighbours init: self neighbours_at 1 type: list of: ant_grid ;
		const multiagent type: bool init: true ;
		const type type: int init: types at {grid_x,grid_y} ;
		const isNestLocation type: bool init: (self distance_to center) < 4 ;
		const isFoodLocation type: bool init: type = 2 ; 
		var color type: rgb value: isNestLocation ? FF00FF:((food > 0)? blue : ((road < 0.001)? rgb ([100,100,100]) : ((road > 2)? white : ((road > 0.5)? (C00CC00) : ((road > 0.2)? (C009900) : (C005500)))))) ;
		var food type: int init: isFoodLocation ? 5 : 0 ;
		const nest type: int init: 300 - (self distance_to center) ;
		init when: location = center {
			create species: ant number: ants_number with: [location::any_location_in (shape)] ;
		}
	}
}
entities {
	species ant skills: [moving, visible] control: fsm {
		var speed type: float init: 2 ;
		var place type: ant_grid value: ant_grid (location ); 
		var im type: string init: 'ant_shape_empty' ;
		var hasFood type: bool init: false ;
		var road type: signal value:  hasFood ? 240.0 : 0.0 decay: evaporation_rate proportion: diffusion_rate environment: ant_grid ;
		action pick {
			set im var: im value: ant_shape_full ;
			set hasFood var: hasFood value: true ;
			set place.food var: place.food value: place.food - 1 ;
		}
		action drop {
			set food_gathered var: food_gathered value: food_gathered + 1 ;
			set hasFood var: hasFood value: false ;
			set heading var: heading value: heading - 180 ;
		}
		action choose_best_place type: ant_grid {
			let list_places value: place.neighbours of: ant_grid ;
			if condition: (list_places count (each.food > 0)) > 0  {
				return value: (list_places first_with (each.food > 0)) ;
			} else {
					let min_nest var: min_nest value:  (list_places min_of (each.nest)) ;
					set list_places var: list_places value: list_places sort ((each.nest = min_nest) ? each.road :  0.0) ;
					return value: last(list_places) ;
				}
		}
		state wandering initial: true {
			do action: wander {
				arg amplitude type: int value: 120 ;
			} 
			transition to: carryingFood when: place.food > 0 {
				do action: pick ;
			}
			transition to: followingRoad when: place.road > 0.05 ;
		}
		state carryingFood {
			do action: goto {
				arg target value: center ;
			}
			transition to: wandering when: place.isNestLocation { 
				do action: drop ;
			}
		}
		state followingRoad {
			set location var: location value: (self choose_best_place []) as point ;
			transition to: carryingFood when: place.food > 0 {
				do action: pick ;
			}
			transition to: wandering when: (place.road < 0.05) ;
		}
		aspect text {
			if condition: use_icons {
				draw image: hasFood ? ant_shape_full : ant_shape_empty rotate: heading at: my location size: 3 ;
			} else {
					draw shape: circle empty: !hasFood color: rgb ('orange') ;
				}
			if condition: display_state {
				draw text: state at: location + {-3,1.5} color: white size: 0.8 ;
			}
		}
		aspect default {
			draw shape: circle empty: !hasFood color: 'orange' ;
		}
	}
}
output {
	display Ants refresh_every: 1 {
		grid ant_grid ;
		species ant aspect: text ;
	}
	inspect name: 'agents' type: agent ;
	inspect name: 'species' type: species ;
}

// No experiments defined here yet. The default one will pick the available parameters and outputs
