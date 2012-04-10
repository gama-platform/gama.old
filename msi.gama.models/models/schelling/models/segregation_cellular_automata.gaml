model segregation
// gen by Xml2Gaml


import "../include/schelling_common.gaml"
global {
	var free_places type: list init: [] of: space;
	var all_places type: list init: [] of: space;
	var all_people type: list init: [] of: space;
	action initialize_places {
		set all_places value: shuffle (space as list);
		set free_places value: shuffle(all_places);
	}
	action initialize_people { 
		loop i from: 0 to: number_of_people - 1 {
			let pp value: all_places at i;
			remove item: pp from: free_places;
			add item: pp to: all_people; 
			set pp.color value: colors at (rnd (number_of_groups - 1)) ;
		}
	}
	reflex migrate {
		ask target: all_people as: space {
			do action: migrate;
		}
	} 
} 
environment width: dimensions height: dimensions {
	grid space parent: base width: dimensions height: dimensions neighbours: 8 torus: true {
		var color type: rgb init: black;
		const multiagent type: bool value: false;
		var my_neighbours type: list value: (self neighbours_at neighbours_distance) select (each.color != black) of: space;
		reflex migrate;
		action migrate {
			if condition: !is_happy {
				let pp value: any(free_places);
				add item: self to: free_places;
				remove item: self from: all_people;
				remove item: pp from: free_places;
				add item: pp to: all_people;
				set pp.color value: color;
				set color value: black;
			}
		}
	}
}
output {
	display Segregation {
		grid space;
	}
}
