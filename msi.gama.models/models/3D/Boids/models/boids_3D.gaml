model boids 
global { 
	int number_of_agents parameter: 'Number of agents' <- 100 min: 1 max: 1000000;
	int number_of_obstacles parameter: 'Number of obstacles' <- 0 min: 0;
	float maximal_speed parameter: 'Maximal speed' <- 15.0 min: 0.1 max: 15.0;
	int cohesion_factor parameter: 'Cohesion Factor' <- 200;
	int alignment_factor parameter: 'Alignment Factor' <- 100; 
	float minimal_distance parameter: 'Minimal Distance' <- 10.0; 
	int maximal_turn parameter: 'Maximal Turn' <- 90 min: 0 max: 359; 
	int width_and_height_of_environment parameter: 'Width/Height of the Environment' <- 800;  
	bool torus_environment parameter: 'Toroidal Environment ?' <- false; 
	bool apply_cohesion <- true parameter: 'Apply Cohesion ?';
	bool apply_alignment <- true parameter: 'Apply Alignment ?';   
	bool apply_separation <- true parameter: 'Apply Separation ?';   
	bool apply_goal <- true parameter: 'Follow Goal ?'; 
	bool apply_avoid <- true parameter: 'Apply Avoidance ?';   
	bool apply_wind <- true parameter: 'Apply Wind ?';     
	bool moving_obstacles <- false parameter: 'Moving Obstacles ?';    
	int bounds <- int(width_and_height_of_environment / 20); 
	point wind_vector <- {0,0} parameter: 'Direction of the wind';  
	int goal_duration <- 30 value: (goal_duration - 1); 
	point goal <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 }; 
	list images of: file <- [file('../images/bird1.png'),file('../images/bird2.png'),file('../images/bird3.png')]; 
	int xmin <- bounds;    
	int ymin <- bounds;  
	int xmax <- (width_and_height_of_environment - bounds);    
	int ymax <- (width_and_height_of_environment - bounds);   


	// flock's parameter 
	const two_boids_distance type: float init: 30.0;  
	const merging_distance type: int init: 30;
	var create_flock type: bool init: false;  
	
	init {
		create boids number: number_of_agents { 
			set location <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 };
		} 
		 
		create obstacle number: number_of_obstacles {
			set location <- {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 }; 
		}
		
		create  boids_goal number: 1 {
			set location <- goal;
		}
	}
	
	 reflex create_flocks {
	 	if create_flock {
	 		let free_boids type: list of: boids value: list (boids); 
	 		let potentialBoidsNeighboursMap type: map value: ([] as map);
	 		
	 		loop one_boids over: free_boids {
	 			let free_neighbours 
	 				type: list of: boids 
	 				value: ( ( list ((agents_overlapping (one_boids.shape + (float (two_boids_distance)))) ) of_species boids) );
	 			remove one_boids from: free_neighbours;  

	 			if !(empty (free_neighbours)) {
	 				add (one_boids::free_neighbours) to: potentialBoidsNeighboursMap;
	 			} 
	 		}
	 		
	 		let sorted_free_boids type: list of: boids value: (potentialBoidsNeighboursMap.keys) sort_by (length (list (potentialBoidsNeighboursMap at (boids (each)))));
	 		loop one_boids over: sorted_free_boids {
	 			let one_boids_neighbours type: list of: boids value: list(potentialBoidsNeighboursMap at one_boids);
	 			
	 			if  (one_boids_neighbours != nil) {
	 				loop one_neighbour over: one_boids_neighbours {
	 					remove one_neighbour from: potentialBoidsNeighboursMap; 
	 				}
	 			}
	 		}
	 		
		 	let boids_neighbours type: list of: boids value: (potentialBoidsNeighboursMap.keys);
		 	loop one_key over: boids_neighbours {
		 		put (remove_duplicates ((list (potentialBoidsNeighboursMap at (one_key))) + one_key)) at: one_key in: potentialBoidsNeighboursMap;
		 	}
		 	
		 	loop one_key over: (potentialBoidsNeighboursMap.keys) {
		 		let micro_agents type: list of: boids value: list(potentialBoidsNeighboursMap at one_key);
		 			
		 		if ( (length (micro_agents)) > 1 ) {
		 			create flock number: 1 with: [ color::[rnd (255), rnd (255), rnd (255)] ] { 
		 				capture micro_agents as: boids_delegation;
		 			}
		 		}
		 	} 
		}
	}  
}

environment width: width_and_height_of_environment height: width_and_height_of_environment torus: torus_environment;

entities {
	species name: boids_goal skills: [moving] {
		const range type: float init: 20.0;
		const size type: float init: 10.0;
		
		reflex wander { 
			do  wander amplitude: 45 speed: 20;  
			set goal value: location;
		}
		
		aspect default {
			draw circle(10) color: rgb ('red') size: 10;
			draw circle(10) color: rgb ('orange') size: 40 empty: true;
		}
	} 
	
	species flock  {
		var cohesionIndex type: float init: two_boids_distance value: (two_boids_distance + (length (members)));
		var color type: rgb init: rgb ([64, 64, 64]);
	 	var shape type: geometry value: !(empty (members)) ? ( (polygon (members collect (boids_delegation (each)).location )) + 2.0 ) : ( polygon ([ {rnd (width_and_height_of_environment), rnd (width_and_height_of_environment)} ]) );
		 
 
		species boids_delegation parent: boids topology: topology(world.shape)  {
			
			// je ne comprends pas pourquoi cette liste peut contenir des agents morts de l'esp�ces "boids_delegation"?
			list others value: ( (boids_delegation overlapping (shape + range))) - self;

			action compute_mass_center type: point {
				loop o over: others {
					if condition: dead(o) { // �a peut faire lever un message "warning" dans la vue "Errors" 
						do write message: 'in ' + name + ' agent with others contains death agents'; 
					} 
				}
				 
				return (length(others) > 0) ? (mean (others collect (each.location)) ) as point : location;
			}

			reflex separation when: apply_separation {
			}
			
			reflex alignment when: apply_alignment {
			}
			
			reflex cohesion when: apply_cohesion {
				let acc value: ((self compute_mass_center []) as point) - location;
				set acc value: acc / cohesion_factor;
				set velocity value: velocity + acc;
			}
			
			reflex avoid when: apply_avoid {
			}		
		}
		
		reflex capture_release_boids {
			 let removed_components type: list of: boids_delegation value: (list (boids_delegation)) where ((each distance_to location) > cohesionIndex );
			 if !(empty (removed_components)) {
			 	release removed_components;
			 }
			 
			 let added_components type: list of: boids value: (list (boids)) where ((each distance_to location) < cohesionIndex );
			 if !(empty (added_components)) {
			 	capture added_components as: boids_delegation;
			 }
		}
		
		reflex dispose when: ((length (members)) < 2) {
			 release members;
			 do die;
		}
		
		reflex merge_nearby_flocks {
			let nearby_flocks type: list of: flock value: (flock overlapping (shape +  merging_distance));
			if !(empty (nearby_flocks)) {
			 	set nearby_flocks <- nearby_flocks sort_by (length (each.members));
			 	let largest_flock <- nearby_flocks at ((length (nearby_flocks)) - 1);
			 	 
			 	remove largest_flock from: nearby_flocks;
			 	 
			 	let added_components type: list of: boids value: [];
			 	loop one_flock over: nearby_flocks {
			 		release one_flock.members returns: released_boids; 
			 		
			 		loop rb over: list(released_boids) {
			 			add rb to: added_components;
			 		}
			 	}
			 	
			 	if !(empty (added_components)) { 
			 		ask largest_flock {
			 			capture added_components as: boids_delegation;
			 		}
			 	} 
			 }
		}
		
		aspect default {
			draw geometry: shape color: color;
		}
	}
	
	species boids skills: [moving] {
		float speed max: maximal_speed <- maximal_speed;
		float range <- minimal_distance * 2;
		int heading max: heading + maximal_turn min: heading - maximal_turn;
		point velocity <- {0,0};
		int size <- 5;
		
		list others update: ((boids overlapping (circle (range)))  - self);
		
		point mass_center update:  (length(others) > 0) ? (mean (others collect (each.location)) ) as point : location;
		
		reflex separation when: apply_separation {
			let acc value: {0,0};
			loop boid over: (boids overlapping (circle(minimal_distance)))  {
				set acc <- acc - ((location of boid) - location);
			}  
			set velocity <- velocity + acc;
		}
		
		reflex alignment when: apply_alignment {
			let acc <- (mean (others collect (each.velocity)) as point) - velocity;
			set velocity <- velocity + (acc / alignment_factor);
		}
		 
		reflex cohesion when: apply_cohesion {
			let acc value: mass_center - location;
			set acc value: acc / cohesion_factor;
			set velocity value: velocity + acc; 
		}
		
		reflex avoid when: apply_avoid {
			let acc <- {0,0};
			let nearby_obstacles <- (obstacle overlapping (circle (range)) );
			loop obs over: nearby_obstacles {
				set acc <- acc - ((location of obs) - my (location));
			}
			set velocity <- velocity + acc; 
		}
		
		action bounding {
			if  !(torus_environment) {
				if  (location.x) < xmin {
					set velocity <- velocity + {bounds,0};
				} else if (location.x) > xmax {
					set velocity <- velocity - {bounds,0};
				}
				
				if (location.y) < ymin {
					set velocity <- velocity + {0,bounds};
				} else if (location.y) > ymax {
					set velocity <- velocity - {0,bounds};
				}
				
			}
		}
		
		reflex follow_goal when: apply_goal {
			set velocity <- velocity + ((goal - location) / cohesion_factor);
		}
		
		reflex wind when: apply_wind {
			set velocity <- velocity + wind_vector;
		}
		  
		action do_move {  
			if ((velocity.x) as int = 0) and ((velocity.y) as int = 0) {
				set velocity <- {(rnd(4)) -2, (rnd(4)) - 2};
			}
			let old_location <- location;
			do goto target: location + velocity;
			set velocity <- location - old_location;
		}
		
		reflex movement {
			do bounding;
			do do_move;
		}
		
		aspect image {
			draw image: images at (rnd(2)) size: 35 rotate: heading color: rgb('black');      
		}
		
		aspect default { 
			draw triangle(15) rotate: 90 + heading color: rgb('yellow');
		}
		
		aspect dynamicColor{
			let hue <- heading/360;
			let  color <- color hsb_to_rgb ([hue,1.0,1.0]);
			let geometry1 <- geometry (triangle(20));
			draw geometry: geometry1    size: 15 rotate: 90 + heading color: color border:color;
		}
	} 
	
	species obstacle skills: [moving] {
		float speed <- 0.1;
		 		
		aspect default {
			draw triangle(20) color: rgb('yellow');
		}
	}
}



experiment start type: gui {
	output {
		
		display RealBoids   type:opengl{
			image name:'background' file:'../images/ocean.jpg' z:0;
			species boids aspect: image z:0.2 transparency:0.5;
			species boids_goal z:0.25 transparency:0.2;
			species obstacle ;	
			
		}
		
		display DynamicColor   type:opengl{
			species boids  aspect: dynamicColor z:0.25;
			species boids_goal  transparency:0.2 z:0.25;	
		}
		
		

	}
}
