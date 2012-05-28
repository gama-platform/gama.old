model segregation
// gen by Xml2Gaml


import "../include/schelling_common.gaml"
global { 
	var dimensions type: int;
	var neighbours_distance type: int init: 50 max: 100 min: 1 parameter: 'Distance of perception:' category: 'Population';
	var shape_file_name type: string init: '../gis/nha2.shp' parameter: 'Path of shapefile to load:' category: 'GIS specific';
	action initialize_people {
		set number_of_people value: density_of_people * length(all_places);
		create species: people number: number_of_people {
			set location value: point(one_of(space)); 
		} 
		set all_people value: people as list;
	}
	action description {
		do action: write {
			arg message value: 'This model is destined to be used as a skeleton for the GIS tutorial. It is not functional.';
		}
	}
} 

entities {
	species people parent: base  { 
		set shape value: circle(2);
		const size type: float init: 2;
		const color type: rgb init: colors at (rnd (number_of_groups - 1));
		var my_neighbours type: list value: (self neighbours_at neighbours_distance) of_species people;
		reflex migrate when: !is_happy;
	}  
	species space  {
		var agents type: list of: people init: [];  
	}    
}
environment width: 5#km height: 4#km;
output;  
  