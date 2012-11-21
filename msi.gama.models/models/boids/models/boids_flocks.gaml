model boids_flock

import 'boids.gaml'

global {
	float boids_size <- float(3);  
	geometry boids_shape <- circle (boids_size) ;
	float boids_separation <- 4 * boids_size; 
	int flock_creation_distance <- int(boids_separation + 1);
	int min_group_member <- 3;
	
	int update_frequency <- 3;
	int merge_frequency <- 3;
	int merge_possibility <- 0.3;

	bool create_flocks <- false;
	int base_perception_range <- int (xmax / 100) min: 1 ;  
	
	
	reflex create_flocks when: create_flocks {
		let free_boids type: list of: boids value: (boids as list) ;
		
		if ( (length (free_boids)) > 1 ) {
			let satisfying_boids_groups type: list of: list value: (free_boids simple_clustering_by_envelope_distance flock_creation_distance) where ( (length (each)) > min_group_member ) ;
			
			loop one_group over: satisfying_boids_groups {
				let potential_flock_polygon type: geometry value: polygon(one_group collect (boids(each)).location) + (base_perception_range + 5);
				
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
				let new_polygon type: geometry value: polygon( nearby_boids collect (each.location) );				
				if (empty(obstacle overlapping new_polygon)) {
					capture nearby_boids as: boids_in_flock;
				}
			}
		}

		reflex merge_nearby_flocks when: ( (time mod merge_frequency) = 0 ) {
				
			let nearby_flocks type: list of: flock value: (flock overlapping (shape + perception_range)) - self ;
			
			loop f over: nearby_flocks {
				let new_shape type: geometry value: (shape + f.shape + base_perception_range + 5); 
				
				if empty(obstacle overlapping  new_shape) {
					let f_coms type: list of: boids_in_flock value: f.members;
					let released_boids type: list of: boids value: [];
					
					ask f {
						release f_coms as: boids in: world returns: released_coms;
						set released_boids value: released_coms;
						do die;
					}
					capture released_boids as: boids_in_flock;
				} 
			}
		}

		reflex chase_goal {
			let direction_to_nearest_ball type: int value: (self towards (goal)) ;
			let step_distance type: float value: speed * step ;
			let dx type: float value: step_distance * (cos (direction_to_nearest_ball)) ;
			let dy type: float value: step_distance * (sin (direction_to_nearest_ball)) ;
			let envelope type: geometry value: shape.envelope ;
			let topleft_point type: point value: (envelope.points) at 0 ;
			let bottomright_point type: point value: (envelope.points) at 0 ;
			
			loop p over: envelope.points {
				if ( (p.x <= topleft_point.x) and (p.y <= topleft_point.y) ) {
					set topleft_point value: p ;
				}
				
				if ( (p.x >= bottomright_point.x) and (p.y >= bottomright_point.y) ) {
					set bottomright_point value: p ;
				}
			}
			
			if ( (dx + topleft_point.x) < xmin ) {
				let tmp_dx value: dx + topleft_point.x ;
				set dx value: dx - tmp_dx ;
			} else {
				if ( (dx + bottomright_point.x) > xmax ) {
					let tmp_dx value: (dx + bottomright_point.x) - xmax ;
					set dx value: dx - tmp_dx ;
				}
			}
			
			if  (dy + topleft_point.y) < 0 {
				let tmp_dy value: dy + topleft_point.y ;
				set dy value: dy - tmp_dy ;
			} else {
				if ( (dy + topleft_point.y) > ymax ) {
					let tmp_dy value: (dy + bottomright_point.y) - ymax ;
					set dy value: dy - tmp_dy ;
				}
			}
			
			loop com over: (list (boids_in_flock)) {
				set (boids_in_flock (com)).location value: (boids_in_flock (com)).location + {dx, dy} ;
			}
			
			set shape value: convex_hull((polygon ((list (boids_in_flock)) collect (boids_in_flock (each)).location)) + 2.0) ;
		}

		aspect default {
			draw shape: geometry color: color;
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
				draw shape: circle color: ((host as flock).color).darker size: my_age ;
			}
			
		}
	}
}

experiment boids_flocks type: gui {
	parameter 'Create flock?' var: create_flocks <- true;
	parameter 'Moving obstacles?' var: moving_obstacles <- true;
	
	output {
		display default_display {
			species boids_goal;
			species boids aspect: image;
			species obstacle aspect: geom;
			
			species flock aspect: default transparency: 0.5 {
				species boids_in_flock aspect: default;
			}
		}
	}
}