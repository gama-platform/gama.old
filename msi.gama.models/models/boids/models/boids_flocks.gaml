model boids_flock

import 'boids.gaml'

global {
	float boids_size <- float(3);  
	geometry boids_shape <- circle (boids_size) ;
	float boids_separation <- 4 * boids_size; 
	int flock_creation_distance <- int(boids_separation + 1);
	int min_group_member <- 3;
	
	int update_frequency <- 3;
	int merge_frequency <- 5;

	bool create_flocks <- false;
	int base_perception_range <- int (xmax / 100) min: 1 ;
	
	init {
		create boids_agents_viewer;
		create flock_agents_viewer;
		create boids_in_flock_viewer;
	}
	
	reflex create_flocks when: create_flocks {
		let free_boids type: list of: boids value: (boids as list) ;
		
		if ( (length (free_boids)) > 1 ) {
			let satisfying_boids_groups type: list of: list value: (free_boids simple_clustering_by_envelope_distance flock_creation_distance) where ( (length (each)) > min_group_member ) ;
			
			loop one_group over: satisfying_boids_groups {
				let potential_flock_polygon type: geometry value: convex_hull ( solid( polygon(one_group collect (boids(each)).location) ) + (base_perception_range + 5) );
				
				if (empty ( obstacle overlapping potential_flock_polygon))  {
					create flock returns: new_flocks;
					
					ask (new_flocks at 0) as: flock {
						capture one_group as: boids_in_flock;
					}
				}
			}
		}
	}
}

entities {
	species flock skills: [moving] {
		rgb color <- rgb ([ rnd(255), rnd(255), rnd(255) ]) ;
		geometry shape <- polygon ( (list (boids_in_flock)) ) buffer  10 ;
		float perception_range <- float(base_perception_range + (rnd(5))) ;
		float speed value: self average_speed [];
		
		action average_speed type: float {
			let sum type: float value: 0;
			
			loop s over: (members collect (boids_in_flock(each)).speed) {
				set sum value: sum + s;
			}
			
			return (sum/(length(members)));
		}
		
		reflex disaggregate {
			let buffered_shape type: geometry value: shape + perception_range;
			
			if !(empty (obstacle overlapping buffered_shape)) {
				release members as: boids in: world;
				do die;
			}			
		}

		reflex capture_nearby_boids when: ( (time mod update_frequency) = 0 ) {
			let buffered_shape type: geometry value: shape + perception_range;
			let nearby_boids type: list of: boids value: (boids overlapping buffered_shape);
			
			if ( !(empty (nearby_boids)) ) {
				let new_polygon type: geometry value: convex_hull( solid(shape + polygon( nearby_boids collect (each.location) ) ) );				
				if (empty (obstacle overlapping new_polygon)) {
					capture nearby_boids as: boids_in_flock;
				}
			}
		}

		reflex merge_nearby_flocks when: ( (time mod merge_frequency) = 0 ) {
			
			loop f over: (list(flock) - self) {
				if (shape intersects f.shape) {
					geometry new_shape <- convex_hull(polygon ( shape.points + f.shape.points) );
					if empty(obstacle overlapping new_shape) {
						let released_boids type: list of: boids value: [];
						
						ask f {
							release members as: boids in: world returns: released_coms;
							set released_boids value: list(released_coms);
							do die;
						}
						capture released_boids as: boids_in_flock;
						set shape value: convex_hull( polygon ( members collect (boids_in_flock(each).location) ) ); 
					}
				}
			}
		}

		reflex chase_goal {
			let direction_to_nearest_ball type: int value: (self towards (goal)) ;
			let step_distance type: float value: speed * step ;
			let dx type: float value: step_distance * (cos (direction_to_nearest_ball)) ;
			let dy type: float value: step_distance * (sin (direction_to_nearest_ball)) ;
			let envelope type: geometry value: shape.envelope ;
			
			
			let points_sort_x type: list of: point value: (envelope.points) sort_by (each.x);
			let points_sort_y type: list of: point value: (envelope.points) sort_by (each.y);
			
			
			let topleft_x type: int value: ( first(points_sort_x) ).x;
			let topleft_y type: int value: (first(points_sort_y) ).y;
			let bottomright_x type: int value: (last(points_sort_x)).x;
			let bottomright_y type: int value: (last(points_sort_y)).y;
			
			if ( (dx + topleft_x) < xmin ) {
				set dx value: 0;
			} else {
				if ( (dx + bottomright_x) > xmax ) {
					set dx value: 0;
				}
			}
			
			if  (dy + topleft_y) < 0 {
				set dy value: 0 ;
			} else {
				if ( (dy + bottomright_y) > ymax ) {
					set dy value: 0 ;
				}
			}
			
			loop com over: (list (boids_in_flock)) {
				set (boids_in_flock (com)).location value: (boids_in_flock (com)).location + {dx, dy} ;
			}
			set shape value: convex_hull(polygon ( list(boids_in_flock) collect (each.location) ) ) ;
			
		}

		aspect default {
			draw shape color: color;
		}
		
		species boids_in_flock parent: boids {
			float my_age <- 1.0 value: my_age + 0.01;
			 
			reflex separation when: apply_separation {
			}
			
			reflex alignment when: apply_alignment {
			}
			 
			reflex cohesion when: apply_cohesion {
			}
			
			reflex avoid when: apply_avoid {
			}
			
			reflex follow_goal when: apply_goal {
			}
			
			reflex wind when: apply_wind {
			}
			  
			action do_move {  
			}
			
			reflex movement {
				do bounding;
				do do_move;
			}
			
			aspect default {
				draw  circle(my_age) color: ((host as flock).color).darker ;
			}
			
		}
	}

	species flock_agents_viewer  { 
		aspect default {
			draw  'Flocks: ' + (string (length (list(flock)))) at: {width_and_height_of_environment - 810, (width_and_height_of_environment) - 5} color: rgb('blue') size: 80 style: bold ;
		}
	}

	species boids_agents_viewer  { 
		aspect default {
			draw text: 'Boids: ' + (string (length (list(boids)))) at: {width_and_height_of_environment - 810, (width_and_height_of_environment) - 165} color: rgb('blue') size: 80 style: bold;
		}
	}

	species boids_in_flock_viewer  { 
		aspect default {
			draw text: 'Boids in flocks: ' + (string (number_of_agents - (length (list(boids))) ) ) at: {width_and_height_of_environment - 810, width_and_height_of_environment - 85} color: rgb('blue') size: 80 style: bold;
		}
	}
}

experiment boids_flocks type: gui {
	parameter 'Create flock?' var: create_flocks <- true;
	parameter 'Number of boids' var: number_of_agents <- 300;
	parameter 'Environment size' var: width_and_height_of_environment <- 1600;
	parameter 'Moving obstacles?' var: moving_obstacles <- true;
	parameter 'Torus environment?' var: torus_environment <- false;
	parameter 'Number of obstacles' var: number_of_obstacles <- 5;
	
	
	output {
		display default_display {
			species boids_goal;
			species boids aspect: image;
			species obstacle aspect: geom;
			
			species flock aspect: default transparency: 0.5 {
				species boids_in_flock aspect: default;
			}
			
			species flock_agents_viewer;
			species boids_agents_viewer;
			species boids_in_flock_viewer;
		}
	}
}