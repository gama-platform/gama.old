model boids



global {
	var number_of_agents type: int parameter: 'true' init: 100 min: 1 max: 1000000;
	var number_of_obstacles type: int parameter: 'true' init: 0 min: 0;
	var maximal_speed type: float parameter: 'true' init: 15 min: 0.1 max: 15;
	var cohesion_factor type: int parameter: 'true' init: 200;
	var alignment_factor type: int parameter: 'true' init: 100;
	var minimal_distance type: float parameter: 'true' init: 10.0;
	var maximal_turn type: int parameter: 'true' init: 90 min: 0 max: 359;
	var width_and_height_of_environment type: int parameter: 'true' init: 800;
	var torus_environment type: bool parameter: 'true' init: false;
	var apply_cohesion type: bool init: true parameter: 'true';
	var apply_alignment type: bool init: true parameter: 'true';
	var apply_separation type: bool init: true parameter: 'true';
	var apply_goal type: bool init: true parameter: 'true';
	var apply_avoid type: bool init: true parameter: 'true';
	var apply_wind type: bool init: true parameter: 'true';
	var moving_obstacles type: bool init: false parameter: 'true';
	var bounds type: int init: width_and_height_of_environment / 20 depends_on: width_and_height_of_environment;
	var wind_vector type: point init: {0,0} parameter: 'true';
	var goal_duration type: int init: 30 value: goal_duration - 1;
	var goal type: point init: {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 };
	var images type: list of: string value: ['../images/bird1.png','../images/bird2.png','../images/bird3.png'];
	var xmin type: int init: bounds; 
	var ymin type: int init: bounds;
	var xmax type: int init: width_and_height_of_environment - bounds;
	var ymax type: int init: width_and_height_of_environment - bounds;
	
	// flock's parameter
	const two_boids_distance type: int init: 30;
	const merging_distance type: int init: 30;
	var create_flock type: bool init: false;
	
	init {
		create species: boids number: number_of_agents {
			set location value: {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 };
		}
		
		create species: obstacle number: number_of_obstacles {
			set location value: {rnd (width_and_height_of_environment - 2) + 1, rnd (width_and_height_of_environment -2) + 1 };
		}
		
		create species: boids_goal number: 1 {
			set location value: goal;
		}
	}
	
	 reflex create_flocks {
	 	if condition: create_flock {
	 		let free_boids type: list of: boids value: list (boids);
	 		let potentialBoidsNeighboursMap type: map value: ([] as map);
	 		
	 		loop one_boids over: free_boids {
	 			let free_neighbours type: list of: boids value: ( ( list ((agents_overlapping (one_boids.shape + (float (two_boids_distance)))) ) of_species boids) );
	 			remove item: one_boids from: free_neighbours;

	 			if condition: !(empty (free_neighbours)) {
	 				add item: one_boids::free_neighbours to: potentialBoidsNeighboursMap;
	 			}
	 		}
	 		
	 		let sorted_free_boids type: list of: boids value: potentialBoidsNeighboursMap.keys sort_by (length (list (potentialBoidsNeighboursMap at (boids (each)))));
	 		loop one_boids over: sorted_free_boids {
	 			let one_boids_neighbours type: list of: boids value: (potentialBoidsNeighboursMap at one_boids);
	 			
	 			if condition: (one_boids_neighbours != nil) {
	 				loop one_neighbour over: one_boids_neighbours {
	 					remove item: one_neighbour from: potentialBoidsNeighboursMap;
	 				}
	 			}
	 		}
	 		
		 	let boids_neighbours type: list of: boids value: potentialBoidsNeighboursMap.keys;
		 	loop one_key over: boids_neighbours {
		 		put item: (remove_duplicates ((list (potentialBoidsNeighboursMap at (one_key))) + one_key)) at: one_key in: potentialBoidsNeighboursMap;
		 	}
		 	
		 	loop one_key over: potentialBoidsNeighboursMap.keys {
		 		let micro_agents type: list of: boids value: potentialBoidsNeighboursMap at one_key;
		 			
		 		if condition: ( (length (micro_agents)) > 1 ) {
		 			create species: flock number: 1 with: [ color::[rnd (255), rnd (255), rnd (255)] ] {
		 				capture target: micro_agents as: boids_delegation;
		 			}
		 		}
		 	}
		}
	}
}

environment width: width_and_height_of_environment height: width_and_height_of_environment torus: torus_environment;

entities {
	species boids_goal skills: [moving] {
		const range type: float init: 20;
		const size type: float init: 10;
		
		reflex {
			do action: wander {
				arg amplitude value: 45;
				arg speed value: 20;
			}
			set goal value: location;
		}
		
		aspect default {
			draw shape: circle color: 'red' size: 10;
			draw shape: circle color: 'orange' size: 40 empty: true;
		}
	}
	
	species flock skills: situated {
		var cohesionIndex type: float init: two_boids_distance value: (two_boids_distance + (length (members)));
		var color type: rgb init: rgb [64, 64, 64];
	 	var shape type: geometry value: !(empty (members)) ? ( (polygon (members collect (boids_delegation (each)).location )) + 2.0 ) : ( polygon [ {rnd (width_and_height_of_environment), rnd (width_and_height_of_environment)} ] );
		 

		species boids_delegation parent: boids topology: (world).shape  {
			
			// je ne comprends pas pourquoi cette liste peut contenir des agents morts de l'espces "boids_delegation"?
			var others type: list of: boids_delegation value: ( (agents_overlapping (shape + range)) of_species boids_delegation) - self;

			action compute_mass_center type: point {
				loop o over: others {
					if condition: dead(o) { // a peut faire lever un message "warning" dans la vue "Errors"
						do action: write {
							arg name: message value: 'in ' + name + ' agent with others contains death agents';
						}
					}
				}
				
				return value: (length(others) > 0) ? (mean (others collect (each.location)) ) as point : location;
			}

			reflex separation when: apply_separation {
			}
			
			reflex alignment when: apply_alignment {
			}
			
			reflex cohesion when: apply_cohesion {
				let acc value: ((self.compute_mass_center []) as point) - location;
				set acc value: acc / cohesion_factor;
				set velocity value: velocity + acc;
			}
			
			reflex avoid when: apply_avoid {
			}		
		}
		
		reflex capture_release_boids {
			 let removed_components type: list of: boids_delegation value: (list (boids_delegation)) where ( ( (boids_delegation (each)) distance_to location) > cohesionIndex );
			 if condition: !(empty (removed_components)) {
			 	release target: removed_components;
			 }
			 
			 let added_components type: list of: boids value: (list (boids)) where ( ( ((boids (each)) distance_to location)) < cohesionIndex );
			 if condition: !(empty (added_components)) {
			 	capture target: added_components as: boids_delegation;
			 }
		}
		
		reflex dispose when: ((length (members)) < 2) {
			 release target: members;
			 do action: die;
		}
		
		reflex merge_nearby_flocks {
			let nearby_flocks type: list of: flock value: (list (agents_overlapping (shape + (float (merging_distance))) ) ) of_species flock;
			if condition: !(empty (nearby_flocks)) {
			 	set nearby_flocks value: nearby_flocks sort_by (length ((flock (each)).members));
			 	let largest_flock type: flock value: (nearby_flocks at ((length (nearby_flocks)) - 1));
			 	
			 	remove item: largest_flock from: nearby_flocks;
			 	 
			 	let added_components type: list of: boids value: [];
			 	loop one_flock over: nearby_flocks {
			 		release target: one_flock.members returns: released_boids;
			 		
			 		loop rb over: released_boids {
			 			add item: rb to: added_components;
			 		}
			 	}
			 	
			 	if condition: !(empty (added_components)) {
			 		ask target: largest_flock {
			 			capture target: added_components as: boids_delegation;
			 		}
			 	}
			 }
		}
		
		aspect default {
			draw shape: geometry color: color;
		}
	}
	
	species boids skills: [moving] {
		var speed type: float max: maximal_speed init: maximal_speed;
		var range type: float init: minimal_distance * 2;
		var heading type: int max: heading + maximal_turn min: heading - maximal_turn;
		var velocity type: point init: {0,0};
//		var others type: list value: (list ((self neighbours_at range) of_species boids) ) - self;
		var others type: list value: ( (agents_overlapping (shape + range)) of_species boids) - self;
		const size type: int init: 5;
		
		
		action others_at type: list of: boids {
			arg distance type: float;
			
			return value: others where ((self distance_to each) < distance);
		}
		
		reflex separation when: apply_separation {
			let acc value: {0,0};
			loop boid over: (list (self.others_at [distance :: minimal_distance]) ) of_species boids {
				set acc value: acc - ((location of boid) - location);
			}
			set velocity value: velocity + acc;
		}
		
		action compute_mass_center type: point {
			return value: (length(others) > 0) ? (mean (others collect (each.location)) ) as point : location;
		}
		
		reflex alignment when: apply_alignment {
			let acc value: (mean (others collect (each.velocity)) as point) - velocity;
			set velocity value: velocity + (acc / alignment_factor);
		}
		
		reflex cohesion when: apply_cohesion {
			let acc value: ((self.compute_mass_center []) as point) - location;
			set acc value: acc / cohesion_factor;
			set velocity value: velocity + acc;
		}
		
		reflex avoid when: apply_avoid {
			let acc value: {0,0};
			
			let nearby_obstacles type: list of: obstacle value: (agents_overlapping (shape + (minimal_distance * 2)) ) of_species obstacle;
			
			loop obs over: nearby_obstacles {
				set acc value: acc - ((location of obs) - my location);
			}
			set velocity value: velocity + acc;
		}
		
		action bounding {
			if condition: !torus_environment {
				if condition: (location.x) < xmin {
					set velocity value: velocity + {bounds,0};
				}
				if condition: (location.x) > xmax {
					set velocity value: velocity - {bounds,0};
				}
				if condition: (location.y) < ymin {
					set velocity value: velocity + {0,bounds};
				}
				if condition: (location.y) > ymax {
					set velocity value: velocity - {0,bounds};
				}
			}
		}
		
		reflex follow_goal when: apply_goal {
			set velocity value: velocity + ((goal - location) / cohesion_factor);
		}
		
		reflex wind when: apply_wind {
			set velocity value: velocity + wind_vector;
		}
		
		action do_move {
			if condition: ((velocity.x) as int = 0) and ((velocity.y) as int = 0) {
				set velocity value: {(rnd(4)) -2, (rnd(4)) - 2};
			}
			let old_location value: location;
			do action: goto {
				arg target value: location + velocity;
			}
			set velocity value: location - old_location;
		}
		
		reflex movement {
			do action: bounding;
			do action: do_move;
		}
		
		aspect image {
			draw image: images at (rnd(2)) size: 35 rotate: heading color: 'black';
		}
		
		aspect default {
			draw shape: triangle size: 15 rotate: 90 + heading color: 'yellow';
		}
	}
	
	species obstacle skills: [moving] {
		var speed type: float init: 0.1;
		
		reflex when: moving_obstacles {
			if condition: flip(0.5) {
				do action: goto {
					arg target value: one_of(boids) as list;
				}
				else {
					do action: wander {
						arg amplitude value: 360;
					}
				}
			}
		}
		
		aspect default {
			draw shape: triangle color: rgb('yellow') size: 20;
		}
	}
}

experiment with_flocks type: gui {
	parameter name: 'Create flock?' var: create_flock init: true category: 'Flock: ';
	
	output {
		display Sky refresh_every: 1 {
			image name:'background' file:'../images/sky.jpg';
			species boids;
			species boids_goal;
			species obstacle;
			
			species flock transparency: 0.5 {
				species boids_delegation;
			}
		}
		
		monitor flocks value: length (flock);
	}
}

experiment without_flocks type: gui {
	output {
		inspect name: 'Inspector' type: agent;
		display Sky refresh_every: 1 {
			image name:'background' file:'../images/sky.jpg';
			species boids aspect: image;
			species boids_goal;
			species obstacle;
		}
	}
}
