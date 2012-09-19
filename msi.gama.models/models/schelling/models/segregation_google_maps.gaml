model segregation

import "../include/schelling_common.gaml"    

global {
	list free_places <- [] of: space; 
	list all_places <- [] of: space;
	const google_buildings type: list <- [rgb('#EBE6DC'), rgb('#D1D0CD'), rgb('#F2EFE9'), rgb('#EEEBE1'), rgb('#F9EFE8')] ;
	list available_places of: space ;
	string bitmap_file_name <- '../images/hanoi.png' parameter: 'Name of image file to load:' category: 'Environment' ;
	matrix map_colors;
 
	action initialize_people {
		create people number: number_of_people ;  
		set all_people <- people as list ;  
	}
	 
	action initialize_places { 
		set map_colors <- file(bitmap_file_name) as_matrix {dimensions,dimensions} ;
		ask space as list {
			set color <- map_colors at {grid_x,grid_y} ;
		}
		set all_places <- shuffle ((space as list) where (each.color in google_buildings)) ;
		set free_places <- all_places;
	}  
}
environment width: dimensions height: dimensions {
	grid space width: dimensions height: dimensions neighbours: 8 torus: true ; 
}
entities { 
	species people parent: base  {
		const color type: rgb <- colors at (rnd (number_of_groups - 1));
		list my_neighbours -> {(self neighbours_at neighbours_distance) of_species people} of: people;
		init {
			set location <- (one_of(free_places)).location; 
			remove location as space from: free_places;
		} 
		reflex migrate when: !is_happy {
			add location as space to: free_places;
			set location <- any(free_places).location;
			remove location as space from: free_places;
		}
		aspect geom {
			draw geometry: square(1) color: color  ;
		}
		aspect default {
			draw shape: square color: rgb('black') size: 2 ;
		}
	}
}

experiment schelling type: gui {	
	output {
		display Segregation type:opengl{
			image name: 'bg' file: bitmap_file_name ;
			species people transparency: 0.5 aspect: geom z:0.001;
		}	
		/*display Charts {
			chart name: 'Proportion of happiness' type: pie background: rgb('lightGray') style: exploded position: {0,0} size: {1.0,0.5}{
				data Unhappy value: number_of_people - sum_happy_people ;
				data Happy value: sum_happy_people ;
			}
			chart name: 'Global happiness and similarity' type: series background: rgb('lightGray') axes: rgb('white') position: {0,0.5} size: {1.0,0.5} {
				data happy color: rgb('blue') value:  (sum_happy_people / number_of_people) * 100 style: spline ;
				data similarity color: rgb('red') value: float (sum_similar_neighbours / sum_total_neighbours) * 100 style: step ;
			}
		}*/
	}
}
