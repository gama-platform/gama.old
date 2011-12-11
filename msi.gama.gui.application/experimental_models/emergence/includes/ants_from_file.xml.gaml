model ants
// gen by Xml2Gaml


global {
	var ants_number type: int init: gridsize + 25 parameter: 'Number of ants:' min: 1 max: 2000 category: 'Model';
	var evaporation_rate type: float init: 0.1 min: 0 max: 1 parameter: 'Rate of evaporation of the signal(%/cycle):' category: 'Model';
	var diffusion_rate type: float init: 0.5 min: 0 max: 1 parameter: 'Rate of diffusion of the signal(%/cycle):' category: 'Model';
	const gridsize type: int init: 75;
	var use_icons type: bool init: false parameter: 'Use icons for the agents:' category: 'Display';
	var display_state type: bool init: true parameter: 'Display state of agents:' category: 'Display';
	const center type: point init: {(gridsize / 2),(gridsize / 2)};
	const types type: matrix init: file('images/environment75x75.pgm');
	const ant_shape_empty type: string init: 'icons/ant.png';
	const ant_shape_full type: string init: 'icons/full_ant.png';
	var food_gathered type: int init: 0;
}
environment width: gridsize height: gridsize {
	grid ant_grid width: gridsize height: gridsize neighbours: 8 {
		const neighbours init: self neighbours_at 1 type: list of: ant_grid;
		const multiagent type: bool init: true;
		const type type: int init: types at {grid_x,grid_y};
		const isNestLocation type: bool init:(self distance_to center) < 4;
		const isFoodLocation type: bool init: type = 2;
		var color type: rgb value: isNestLocation ? 'gray' as rgb :
			((food > 0) ? 'blue' as rgb : 
			((road < 0.001) ? [100,100,100] as rgb: 
			((road > 2) ? 'white' as rgb: 
			((road > 0.5) ? #C00CC00 as rgb: 
			((road > 0.2) ? #C009900 as rgb: #C005500 as rgb)))));
		var food type: int init: isFoodLocation ? 5 : 0;
		const nest type: int init: 300 -(self distance_to center);
		init when: location = center {
			create species: ant number: ants_number with: [location::any_location_in(shape)];
		}
	}
}
entities {
	species ant skills: [moving, visible] control: fsm {
		var speed type: float init: 2;
		const color type: rgb init: 'orange';
		var place type: ant_grid value: location as ant_grid;
		var image type: string init: 'ant_shape_empty';
		var hasFood type: bool init: false;
		var road type: signal value: hasFood ? 240 : 0 decay: evaporation_rate proportion: diffusion_rate environment: ant_grid;
		action pick {
			set image value: ant_shape_full;
			set hasFood value: true;
			set place.food value: place.food - 1;
		}
		action drop {
			set food_gathered value: food_gathered + 1;
			set hasFood value: false;
			set image value: ant_shape_empty;
			set heading value: heading - 180;
		}
		action choose_best_place {
			let list_places var: list_places value: place neighbours_at 1 of: ant_grid;
			if condition:(list_places count(each.food > 0)) > 0 {
				return value:(list_places first_with(each.food > 0));
				else {
					let min_nest var: min_nest value:(list_places min_of(each.nest));
					set list_places value: list_places sort((each.nest = min_nest) ? each.road : 0.0);
					return value: last(list_places);
				}
			}
		}
		state wandering initial: true {
			do action: wander {
				arg amplitude type: int value: 120;
			}
			transition to: carryingFood when: place.food > 0 {
				do action: pick;
			}
			transition to: followingRoad when: place.road > 0.05;
		}
		state carryingFood {
			do action: goto {
				arg target value: center;
			}
			transition to: wandering when: place.isNestLocation {
				do action: drop;
			}
		}
		state followingRoad {
			set location value: self.choose_best_place [];
			transition to: carryingFood when: place.food > 0 {
				do action: pick;
			}
			transition to: wandering when:(place.road < 0.05);
		}
		aspect default {
			if condition: use_icons {
				draw image: hasFood ? ant_shape_full : ant_shape_empty rotate: heading size: 2;
				else {
					draw shape: circle empty: !hasFood color: color;
				}
			}
			if condition: display_state {
				draw text: state at: location + {-3,1.5} color: 'black' as rgb size: 0.8;
			}
		}
	}
}
output {
	display Ants refresh_every: 2 {
		grid ant_grid;
		species ant;
		text percentage value: 'Food foraged : ' + string((food_gathered / 5.7) with_precision 2) + '%' position: {0.1,0.9} color: rgb('white') size: {1,0.02};
		text ants_lost value: 'Ants scouting : ' +(ant as list count(each.state = 'wandering')) position: {0.1,0.95} color: rgb('white') size: {1,0.02};
	}
	inspect name: 'agents' type: agent;
	inspect name: 'species' type: species;
}
