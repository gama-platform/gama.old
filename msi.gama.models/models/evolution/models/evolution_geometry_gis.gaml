model evolution_geometry
// gen by Xml2Gaml 
global {
	var shape_file_name_init type: string init: '../gis/init.shp' parameter:
	'Path of shapefile to load for the initial agent:' category: 'GIS specific';
	var shape_file_name_background type: string init: '../gis/background.shp'
	parameter: 'Path of shapefile to load for the background:' category:    
	'GIS specific';
	var dying_size type: float init: 10000 min: 100 parameter: 
	'Size (area) from which an agent dies:' category: 'Population';
	var crossover_size type: float init: 1000 min: 100 parameter:
	'Min size (area) for crossover:' category: 'Population';
	var minimum_size type: float init: 500 min: 100 parameter:
	'Minimum size (area) of a agent produced by a crossover:' category: 
 	'Population';
	var time_wthout_co type: int init: 7 min: 1 parameter: 
	'Number of steps without crossing-over for an agent:' category: 'Population';
	var speed type: float init: 10 min: 1 parameter: 'Agent deplacement speed:'
	category: 'Population'; 
	var crossover_rate type: float init: 0.95 min: 0.1 parameter:
	'Rate of crossover' category: 'Population'; 
	var scaling_factor type: float init: 1.05 min: 1.001 parameter: 
	'Scaling factor for agent geometry (at each step):' category: 'Population';
	var angle_rotation_max type: float init: 45 min: 0 parameter:
	'Max rotation angle for agent geometry (at each step):' category:
	'Population';
	var nb_partners_max type: int init: 1 min: 1 parameter:
	'Max number of possible partners for crossing-overs (per step)' category:
	'Population';
	var max_side_size type: int init: 5 min: 1 parameter:
	'Size max of the initiale side of an agent:' category: 'Population';
	var background_size_side type: int init: 80 min: 20 max: 100 parameter:
	'Size background side:' category: 'Population';
	
	var the_background type: geometry;

	reflex stop when: empty ( people as list ) {
		do action: halt;
	}
	init {
		create species: background from: shape_file_name_background {
			set color value: rgb ([ 255 , 240 , 240 ]);
		}
		
		set the_background value: ((first (background as list)).shape).contour;

	}
}
environment bounds: shape_file_name_background;

	species people topology: topology(shapefile(shape_file_name_init)) {
		rgb color init: rgb ( [ rnd ( 255 ) , rnd ( 255 ) , rnd ( 255 ) ]);
		var location_new_Ag type: point init: nil;
		var color_new_Ag type: rgb init: nil;
		var nb_last_rep type: int init: 0;
		var new_ag type: geometry;
		
		reflex evolve {
			set nb_last_rep <- nb_last_rep + 1;
			set shape <- shape scaled_by scaling_factor;
			set shape <- shape rotated_by ((rnd ( 100 * angle_rotation_max))/ 100.0);
			
		}
		reflex move {
			set location <- location + { speed * ( 1 - rnd ( 2 ) ) , speed * ( 1 - rnd ( 2 ) ) };
			if ( (shape intersects the_background) or (shape.area > dying_size)) {
				do die; 
			}
			
		}
		reflex crossover when: ( shape.area > crossover_size ) and ( nb_last_rep > 
		time_wthout_co ) { 
			let nb_partners <- 0;
			let list_people <- shuffle ( people as list );
			loop i from: 1 to: 5 {
			switch i {
				match 1 {
					do write message: '1';
				}
				match_one [2, 3, 4] {
					do write message: "2, 3 , 4";
				}
				default {
					do write message:'default';
				}
			} }
	
			loop p over: list_people of_species people {
				if ( p != self ) and ( nb_partners <= nb_partners_max ) and (rnd ( 100 ) < ( crossover_rate * 100 ) ) and ( (p.shape).area > crossover_size )
				and ( p . nb_last_rep > time_wthout_co ) and (shape intersects p.shape) {
					set nb_partners value: nb_partners + 1;
					set new_ag value: convex_hull (shape inter p.shape);
					if ( new_ag != nil ) and ( new_ag.area > minimum_size ) {
						set nb_last_rep value: 0;
						ask target: p {
							set nb_last_rep value: 0;
						}
						
						create people number: 1 {
							set color value: (myself.color + p.color) / 2;
							set shape value: myself.new_ag;
						}
					}
				}
			}
		}
		
		aspect default {
			draw shape: geometry color: color;
		}
	}
	species background {
		rgb color;
		aspect default {
			draw shape: geometry color: color;
		}
	}
	output {
		display space_display refresh_every: 1 {
			species background;
			species people transparency: 0.3;
		}
	}