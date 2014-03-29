model segregation

import "../include/Common Schelling Segregation.gaml"    

global {
	list<space> free_places <- []; 
	list<space> all_places <- [];
	float percent_similar_wanted <- 0.6;
	int neighbours_distance <- 4; 
	int number_of_groups <- 3;
	const google_buildings type: list <- [rgb("#EBE6DC"), rgb("#D1D0CD"), rgb("#F2EFE9"), rgb("#EEEBE1"), rgb("#F9EFE8")] ;
	list<space> available_places ;
	file bitmap_file_name <- file<unknown, int>("../images/hanoi.png") parameter: "Name of image file to load:" category: "Environment" ;
	matrix<int> map_colors;
 
	action initialize_people {
		create people number: number_of_people ;  
		all_people <- people as list ;  
	}
	    
	action initialize_places { 
		map_colors <- (bitmap_file_name) as_matrix {dimensions,dimensions} ;
		ask space as list {
			color <- rgb(map_colors at {grid_x,grid_y}) ;
		}
		all_places <- shuffle (space where (each.color in google_buildings)) ;
		free_places <- copy(all_places);
	}  
}
environment width: dimensions height: dimensions {
	grid space width: dimensions height: dimensions neighbours: 8 use_individual_shapes: false use_regular_agents: false frequency: 0 ; 
} 
entities { 
	species people parent: base  {

		
		const color type: rgb <- colors at (rnd (number_of_groups - 1));
		list<people> my_neighbours -> {(self neighbours_at neighbours_distance) of_species people};
		init {
			location <- (one_of(free_places)).location; 
			remove location as space from: free_places;
		} 
		reflex migrate when: !is_happy { 
			add location as space to: free_places;
			location <- any(free_places).location;
			remove location as space from: free_places;
		}
		aspect geom {
			draw square(1) color: color  ;
		}
		aspect default {
			draw  square(2) color: rgb("black") ;
		}
	}
}

experiment schelling type: gui {	
	output {
		display Segregation {
			image "bg" file: bitmap_file_name.path ;
			species people transparency: 0.5 aspect: geom;
		}	
		display Charts {
			chart name: "Proportion of happiness" type: pie background: rgb("lightGray") style: exploded position: { 0, 0 } size: { 1.0, 0.5 } {
				data "Unhappy" value: number_of_people - sum_happy_people color: rgb("green");
				data "Happy" value: sum_happy_people color: rgb("yellow");
			}

			chart name: "Global happiness and similarity" type: series background: rgb("lightGray") axes: rgb("white") position: { 0, 0.5 } size: { 1.0, 0.5 } x_range: 20 y_range: 20 {
				data "happy" color: °blue value: (sum_happy_people / number_of_people) * 100 style: spline fill: false;
				data "similarity" color: °red value: (sum_similar_neighbours / sum_total_neighbours) * 100 style: line fill: true ;
			}
		}
	}
}
