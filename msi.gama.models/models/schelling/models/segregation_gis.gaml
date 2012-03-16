model segregation
// gen by Xml2Gaml


import "../include/schelling_common.gaml"
global {
		var free_places type: list init: [] of: space; 
	var all_places type: list init: [] of: space;
	var neighbours_distance type: int init: 50 min: 1 parameter: 'Distance of perception:' category: 'Population' max: 1000;
	var shape_file_name type: string init: '../gis/nha2.shp' parameter: 'Path of shapefile to load:' category: 'GIS specific';
	var square_meters_per_people type: int init: 200 parameter: 'Occupancy of people (in m2):' category: 'GIS specific';
	var dimensions type: int;
	action initialize_people {
		create species: space from: shape_file_name with: [surface :: read('AREA')];
		set all_places value: shuffle(space as list);
		set number_of_people value: density_of_people * sum (all_places collect ((each as space).capacity));
		create species: people number: number_of_people;
		ask target: people as list {
			do action: move_to_new_place;
		}
		set all_people value: people as list;
	}
}
entities {
	species people parent: base skills: [situated, visible] {
		const size type: float init: 2; 
		const color type: rgb init: colors at (rnd (number_of_groups - 1));
		const red type: int init: color as list at 0;
		const green type: int init: color as list at 1; 
		const blue type: int init: color as list at 2;
		var current_building type: space init: nil;
		var my_neighbours type: list value: (self neighbours_at neighbours_distance) of_species people;
		action move_to_new_place {
			set current_building value: (shuffle(all_places) first_with ((each.capacity) > 0));
			ask target: current_building {
				do action: accept {
					arg one_people value: myself; 
				}
			}
		}
		reflex migrate when: !is_happy {
			if condition: current_building != nil {
				ask target: current_building {
					do  remove_one one_people:  myself;
				}
			}
			do action: move_to_new_place;
		}
		aspect simple {
			draw shape: geometry size: 5;
		}
	}
	species space skills: situated skills:[moving]
	{	var insiders type: list of: people init: [];
		var color type: rgb init: [255, 255, 255] as rgb;
		var surface type: float;
		var capacity type: int init: 1 + surface / square_meters_per_people;
		reflex {do wander;}
		action accept {
			arg one_people;
			add item: one_people to: insiders of self;
			set location of (one_people as people) value: any_location_in(shape);
			set capacity value: capacity - 1;
		}
		action remove_one {
			arg one_people type:people;
			remove item: one_people from: insiders of self;
			set capacity value: capacity + 1;
		}
		aspect simple {
			let color value: empty(insiders) ? rgb('white') : rgb ([mean (insiders collect each.red), mean (insiders collect each.green), mean (insiders collect each.blue)]);
			draw shape: square color: color size: 40;
		}
		aspect gis {
			let color value: empty(insiders) ? rgb('white') : rgb( [mean (insiders collect each.red), mean (insiders collect each.green), mean (insiders collect each.blue)]);
			let pp value: one_of(space as list);
			draw shape: geometry color: color;
		} 
	}
}
environment bounds: shape_file_name;
output {
	display Town_display refresh_every: 1 {
		species space size: {0.8,0.8} position: {0.1,0.1} aspect: gis;
		species people size: {0.8,0.8} position: {0.1,0.1} aspect: simple;
	}

}
