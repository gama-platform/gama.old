model segregation
// gen by Xml2Gaml


import "../include/schelling_common.gaml"   

global {
		var free_places type: list init: [] of: space; 
	var all_places type: list init: [] of: space;
	var neighbours_distance type: int init: 10 max: 100 min: 1 parameter: 'Distance of perception:' category: 'Population' ;
	const google_buildings type: list init: [rgb('#EBE6DC'), rgb('#D1D0CD'), rgb('#F2EFE9'), rgb('#EEEBE1'), rgb('#F9EFE8')] ;
	var available_places type: list of: space ;
	var bitmap_file_name type: string init: '../images/hanoi.png' parameter: 'Name of image file to load:' category: 'Environment' ;
	var map_colors type: matrix ;
	action initialize_places { 
		set map_colors value: file(bitmap_file_name) as_matrix {dimensions,dimensions} ;
		ask target: space as list {
			set color value: map_colors at {grid_x,grid_y} ;
		}
		set all_places value: shuffle ((space as list) select (each.color in google_buildings)) ;
		set available_places value: all_places ; 
	} 
	action initialize_people {
		create species: people number: number_of_people ;  
		set all_people value: people as list ;  
	} 
}
environment width: dimensions height: dimensions {
	grid space width: dimensions height: dimensions neighbours: 8 torus: true ; 
}
entities { 
	species people parent: base  { 
		//var location type: point init: point(all_places first_with empty(each.agents)) ;
		const color type: rgb init: colors at (rnd (number_of_groups - 1)) ;
		var my_neighbours type: list value: (self neighbours_at neighbours_distance) of_species people ;
		init {
			set location value: point(last(available_places)) ; 
			remove item: location as space from: available_places ;
		}
		reflex migrate when: !is_happy {
			let old_loc value: location ;
			set location value: point(any(available_places)) ;  
			remove item: location as space from: available_places ;
			add item: old_loc as space  to: available_places ;
		}
		
	aspect geom {
			draw geometry: square(1) color: color  ;
		}
		aspect default {
			draw shape: square color: rgb('black') size: 2 ;
		}
	}
}
output {
	display Segregation background: rgb('black') {
		image name: 'bg' file: bitmap_file_name size: {0.6,0.6} position: {0.35,0.35} ;
	//	agents agents value: agents of_species people transparency: 0.5 size: {0.6,0.6} position: {0.35,0.35} ;
		grid space size: {0.6,0.6} position: {0.05,0.05} transparency: 0 ;
		species people transparency: 0.5 size: {0.6,0.6} position: {0.05,0.05} aspect: geom ;
		text ttt value: 'Reference image:' /* font: 'Helvetica' */ position: {0.7,0.92} size: {0.2,0.03} color: rgb('black') ;
	}
	display charts {
		chart name: 'Proportion of happiness' type: histogram axes: rgb('white') position: {0,0} size: {1,0.5} {
			data Unhappy value: number_of_people - sum_happy_people ;
			data Happy value: sum_happy_people ;
		}
		chart name: 'Global happiness and similarity' type: series axes: rgb('white') position: {0,0.5} size: {1,0.5} {
			data similarity color: rgb('red') value: float (sum_similar_neighbours / sum_total_neighbours) * 100 style: area ;
			data happy color: rgb('yellow') value:  (sum_happy_people / number_of_people) * 100 style: area ;
		}
	}
}
