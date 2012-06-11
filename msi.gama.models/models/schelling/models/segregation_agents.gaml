model segregation
// gen by Xml2Gaml


//import "../include/schelling_common.gaml" 
import "../include/schelling_common.gaml"

global {
	var free_places type: list init: [] of: space;   
	var all_places type: list init: [] of: space; 
	action initialize_people {
		create species: people number: number_of_people;     
		set all_people value: people as list;  
	} 
	action initialize_places { 
		set all_places value: shuffle (space as list);
		set free_places value: all_places;
	} 
}
environment width: dimensions height: dimensions {  
	grid space width: dimensions height: dimensions neighbours: 8 torus: false {
		const color type: rgb <- black;
	}
} 
entities {
	species people parent: base skills: [situated, visible] {
		const color type: rgb init: colors at (rnd (number_of_groups - 1));
		var my_neighbours type: list value: (self neighbours_at neighbours_distance) of_species people of: people;
		init {
			remove item: location as space from: free_places;
		} 
		reflex migrate when: !is_happy {
			//warn string(self) + " is migrating";
			add item: location as space to: free_places;
			set location value: point(any(free_places));
			remove item: location as space from: free_places;
		}
		
		aspect default{ 
			draw shape:circle size:1.2 color: color; 
		}
	}
}
output {
	display Segregation {
		species people;
	}
	inspect name: 'agents' type: agent;
}
