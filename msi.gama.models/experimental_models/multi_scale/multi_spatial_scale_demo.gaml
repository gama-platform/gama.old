model multi_spatial_scale_demo

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	const environment_size type: int init: 400;
	
	
	// Voronoi var +
	var number_of_points type: int parameter: 'Number of points:' init: 15 min: 1 max: 1000;
	var voronoi_container_size type: int parameter: 'Width of the environment:' init: 120 min: 10 max: 400;
	// Voronoi var -
	

	// Life var +
	var density type: int init: 25 min: 1 max: 99 parameter: 'Initial density of live cells:' category: 'Cells' ;
	var living_conditions type: list init: [2,3] parameter: 'Numbers of live neighbours required to stay alive:' category: 'Cells' ;
	var birth_conditions type: list init: [3] parameter: 'Numbers of live neighbours required to become alive:' category: 'Cells' ;
	var livingcolor type: rgb init: rgb('white') parameter: 'Color of live cells:' category: 'Colors' ;
	var dyingcolor type: rgb init: rgb('red') parameter: 'Color of dying cells:' category: 'Colors' ;
	var emergingcolor type: rgb init: rgb('orange') parameter: 'Color of emerging cells:' category: 'Colors' ;
	var deadcolor type: rgb init: rgb('black') parameter: 'Color of dead cells:' category: 'Colors' ;
	// Life var -
	

	// Ants vars +
	var ants_number type: int init: 100 parameter: 'Number of ants:' min: 1 max: 2000 category: 'Model' ;
	var evaporation_rate type: float init: 0.1 min: 0 max: 1 parameter: 'Rate of evaporation of the signal (%/cycle):' category: 'Model' ;
	var diffusion_rate type: float init: 0.5 min: 0 max: 1 parameter: 'Rate of diffusion of the signal (%/cycle):' category: 'Model' ;
	const gridsize type: int init: 75 ;
	var use_icons type: bool init: false parameter: 'Use icons for the agents:' category: 'Display' ;
	var display_state type: bool init: true parameter: 'Display state of agents:' category: 'Display' ;
	const center type: point init: { (gridsize / 2),  (gridsize / 2)} ;
	const types type: matrix init: file('./images/environment75x75.pgm') ;
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
	// Ant vars -


	const LIFE_CONTAINER_SIZE type: float init: {180, 180};
	const LIFE_LOCATION_1 type: point init: {100, 300};
	const LIFE_LOCATION_2 type: point init: {300, 300};
	
	const VORONOI_CONTAINER_SIZE type: float init: {140, 140};
	const VORONOI_LOCATION_1 type: point init: {75, 100};
	const VORONOI_LOCATION_2 type: point init: {320, 100};
	
	const ANT_CONTAINER_SIZE type: float init: {gridsize, gridsize};
	const ANT_LOCATION type: point init: {200, 100};
	const ANT_GRID_CENTER type: point init: ANT_LOCATION;

	init {
		create species: life_cell_container number: 2 return: life_cell_containers;
		set (life_cell_containers at 0).shape value: ((square (LIFE_CONTAINER_SIZE)) at_location LIFE_LOCATION_1);
		set (life_cell_containers at 1).shape value: ((square (LIFE_CONTAINER_SIZE)) at_location LIFE_LOCATION_2);

		create species: voronoi_container number: 2 return: voronoi_containers;
		set (voronoi_containers at 0).shape value: ((square (VORONOI_CONTAINER_SIZE)) at_location VORONOI_LOCATION_1);
		set (voronoi_containers at 1).shape value: ((square (VORONOI_CONTAINER_SIZE)) at_location VORONOI_LOCATION_2);

		create species: ant_container number: 1 return: ant_containers;
		set (ant_containers at 0).shape value: ((square (ANT_CONTAINER_SIZE)) at_location ANT_LOCATION);
		
		create species: random_mover number: 50;
	}
}

entities {
	species random_mover skills: [situated, moving] {
		var shape type: geometry init: circle (4.0);
		
		reflex move_around {
			do action: wander {
				arg name: speed value: 5;
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb ('green');
		}
	}
	
	species ant_base control: fsm {
		state wandering initial: true { }
	}
	
	species ant_container skills: [situated, moving] inner_environment: true {
		
		delegation ant skills: [moving, visible] species: ant_base {
			var speed type: float init: 2 ;
			var place type: ant_grid value: ant_grid (location );
			var hasFood type: bool init: false ;
			var road type: signal value:  hasFood ? 240 : 0 decay: evaporation_rate proportion: diffusion_rate environment: ant_grid ;
			action pick {
				set hasFood var: hasFood value: true ;
				set place.food var: place.food value: place.food - 1 ;
			}
			action drop {
				set food_gathered var: food_gathered value: food_gathered + 1 ;
				set hasFood var: hasFood value: false ;
				set heading var: heading value: heading - 180 ;
			}
			action choose_best_place {
				let list_places var: list_places value: place neighbours_at 1 of: ant_grid ;
				if condition: (list_places count (each.food > 0)) > 0  {
					return value: (list_places first_with (each.food > 0)) ;
					else {
						let min_nest var: min_nest value:  (list_places min_of (each.nest)) ;
						set list_places var: list_places value: list_places sort ((each.nest = min_nest) ? each.road :  0.0) ;
						return value: last(list_places) ;
					}
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
				set location var: location value: self.choose_best_place [] ;
				transition to: carryingFood when: place.food > 0 {
					do action: pick ;
				}
				transition to: wandering when: (place.road < 0.05) ;
			}
			
			aspect default {
				draw shape: circle empty: !hasFood color: 'orange' ;
			}
		}
		
		topology ant_grid type: grid width: gridsize height: gridsize neighbours: 8 {
			const neighbours init: self neighbours_at 1 type: list of: ant_grid ;
			const multiagent type: bool init: true ;
			const type type: int init: types at {grid_x,grid_y} ;
			const isNestLocation type: bool init: (self distance_to ANT_GRID_CENTER) < 4 ;
			const isFoodLocation type: bool init: type = 2 ;
			var color type: rgb value: isNestLocation ? FF00FF:((food > 0)? blue : ((road < 0.001)? rgb [100,100,100] : ((road > 2)? white : ((road > 0.5)? (C00CC00) : ((road > 0.2)? (C009900) : (C005500)))))) ;
			var food type: int init: isFoodLocation ? 5 : 0 ;
			const nest type: int init: 300 - (self distance_to ANT_GRID_CENTER) ;
			
			init when: location = center {
				create species: ant number: ants_number with: [location::any_location_in (shape)] ;
			}
			
		}
	}
	
	species life_cell_container skills: [situated, moving] inner_environment: true {
		var color type: rgb init: rgb ('green') ;
		
		reflex main {
			ask target: list (life_cell) {
				do action: evolve;
			}
			ask target: list (life_cell) {
				do action: update;
			}
		}
		
		reflex move_around {
			do action: wander {
				arg name: speed value: 1;
			}
		}

		topology life_cell type: grid width: 100 height: 100 neighbours: 8 torus: true {
			var new_state type: bool;
			var state type: bool init: (rnd(100)) < density ;
			var color type: rgb init: state ? livingcolor : deadcolor ;
			
			action evolve {
				let living type: int value: ((self neighbours_at 1) of_species life_cell) count each.state ;
				if condition: state {
					set new_state value: living in living_conditions ;
					set color value: new_state? livingcolor : dyingcolor ;
					else {
						set new_state value: living in birth_conditions ;
						set color value: new_state? emergingcolor : deadcolor ;
					}
				}
			}
			
			action update {
				set state value: new_state;
			}
		}
		
		
		aspect default {
			draw shape: geometry color: rgb ('white');
		}
	}
	
	species center_base {}
	
	species voronoi_container skills: [situated, moving] inner_environment: true {
		var centers type: list of: center value: (center as list);
		
		init {
			create species: center number: number_of_points {
				set color value: [rnd(255),rnd(255),rnd(255)] as rgb;
			}
			set centers value: list (center);
		}

		delegation center species: center_base skills: [situated, visible, moving] {
			var color type: rgb;

			reflex {
				do action: wander {
					arg amplitude value: 90;
				}
			}
			
			aspect default {
				draw shape: circle size: 3 color: color;
				let other value: ((centers - self) with_min_of (self distance_to each));
				draw shape: line to: other color: 'white';
			}
		}


		topology voronoi_grid type: grid width: voronoi_container_size height: voronoi_container_size neighbours: 4 torus: false {
			var color type: rgb init: rgb ('white') value: ((centers with_min_of (self distance_to each)).color);
		}
		
		aspect default {
			draw shape: geometry color: rgb ('blue');
		}
	}
}

environment width: environment_size height: environment_size;

experiment default_experiment type: gui {
	output {
		display topology_test {
			species random_mover;
			
			species ant_container aspect: default transparency: 1 {
				micro_layer ant_grid transparency: 0.5;
				micro_layer ant;
			}
			
			species life_cell_container aspect: default transparency: 0.9 {
				micro_layer life_cell transparency: 0.5;
				micro_layer center;
			}
		
			species voronoi_container aspect: default transparency: 0.9 {
				micro_layer center aspect: default;
				micro_layer voronoi_grid transparency: 0.5;
			}
		}
	}
}
