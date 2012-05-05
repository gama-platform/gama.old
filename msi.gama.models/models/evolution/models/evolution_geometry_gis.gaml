model evolution_geometry
// gen by Xml2Gaml 
global {
	file shape_file_name_init parameter: 'Path of shapefile to load for the initial agent:' category: 'GIS specific' <- '../gis/init.shp' ;
	file shape_file_name_background parameter: 'Path of shapefile to load for the background:' category: 'GIS specific' <- '../gis/background.shp';
	float dying_size min: 100 parameter: 'Size (area) from which an agent dies:' category: 'Population' <-10000 ; 
	float crossover_size min: 100 parameter: 'Min size (area) for crossover:' category: 'Population' <- 1000;
	float minimum_size min: 100 parameter: 'Minimum size (area) of a agent produced by a crossover:' category: 'Population' <- 500;
	int time_wthout_co min: 1 parameter: 'Number of steps without crossing-over for an agent:' category: 'Population' <- 7;
	float speed min: 1 parameter: 'Agent deplacement speed:' category: 'Population' <- 10; 
	float crossover_rate min: 0.1 parameter:'Rate of crossover' category: 'Population' <- 0.95; 
	float scaling_factor min: 1.001 parameter: 'Scaling factor for agent geometry (at each step):' category: 'Population' <- 1.05;
	float angle_rotation_max min: 0 parameter: 'Max rotation angle for agent geometry (at each step):' category:'Population' <- 45;
	int nb_partners_max min: 1 parameter: 'Max number of possible partners for crossing-overs (per step)' category: 'Population' <- 1;
	int max_side_size min: 1 parameter: 'Size max of the initiale side of an agent:' category: 'Population' <- 5; 
	int background_size_side min: 20 max: 100 parameter:'Size background side:' category: 'Population' <- 80 ;
	
	geometry the_background;

	reflex stop when: empty ( people as list ) {
		do halt;
  	} 
	init {
		create background from: shape_file_name_background {
			set color <- rgb ([ 255 , 240 , 240 ]);
		}
		set the_background <- ((first (background as list)).shape).contour;
	}
}
environment bounds: shape_file_name_background;

species people topology: topology(shape_file_name_init) {
	rgb color <- rgb ( [ rnd ( 255 ) , rnd ( 255 ) , rnd ( 255 ) ]);
	point location_new_Ag <- nil;
	rgb color_new_Ag <- nil;
	int nb_last_rep <- 0;
	geometry new_ag ;
		
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
	
	reflex crossover when: ( shape.area > crossover_size ) and ( nb_last_rep > time_wthout_co ) { 
		let nb_partners type: int <- 0;
		let list_people type: list of: people <- shuffle ( people as list );
		loop p over: list_people of_species people {
			if ( p != self ) and ( nb_partners <= nb_partners_max ) and (rnd ( 100 ) < ( crossover_rate * 100 ) ) and ( (p.shape).area > crossover_size ) and ( p . nb_last_rep > time_wthout_co ) and (shape intersects p.shape) {
				set nb_partners <- nb_partners + 1;
				set new_ag <- convex_hull (shape inter p.shape);
				if ( new_ag != nil ) and ( new_ag.area > minimum_size ) {
					set nb_last_rep <- 0;
					ask p {
						set nb_last_rep <- 0;
					}
					create people number: 1 {
						set color <- (myself.color + p.color) / 2;
						set shape <-  myself.new_ag;
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