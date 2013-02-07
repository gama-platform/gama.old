model balls_groups_clouds

global { 
	point environment_bounds <- {500, 500}; 
	int inner_bounds_x <- (int((environment_bounds.x) / 20))  ;
	int inner_bounds_y <- (int((environment_bounds.y) / 20))  ;
	int xmin <- inner_bounds_x ;
	int ymin <- inner_bounds_y ;       
	int xmax <- int((environment_bounds.x) - inner_bounds_x) ;
	int ymax <- int((environment_bounds.y) - inner_bounds_y) ;
	float MAX_DISTANCE <- environment_bounds.x + environment_bounds.y  ;
	rgb ball_color <- rgb('green'); 
	rgb chaos_ball_color <- rgb('red');
	float ball_size <- float(3);  
	float ball_speed <- float(1);
	float chaos_ball_speed <- 8 * ball_speed;  
	int ball_number <- 200 min: 2 max: 1000;  
	geometry ball_shape <- circle (ball_size) ;
	float ball_separation <- 6 * ball_size; 
	bool create_group parameter: 'Create groups?' <- true; 
	bool create_cloud parameter: 'Create clouds?' <- false; 
	int group_creation_distance <- int(ball_separation + 1);
	int min_group_member <- 3;
	int group_base_speed <- (int(ball_speed * 1.5));
	int base_perception_range <- int (environment_bounds.x / 100) min: 1 ;  
	int creation_frequency <- 3;
	int update_frequency <- 3;
	int merge_frequency <- 3;
	float merge_possibility <- 0.3;
	
	int cloud_creation_distance <- 30 const: true;
	int min_cloud_member <- 3 const: true;
	int cloud_speed <- 3 const: true;
	int cloud_perception_range <- base_perception_range const: true ; 
	
	init {
		create ball number: ball_number ;
		create group_agents_viewer;
		create cloud_agents_viewer;
	}
	
	reflex create_groups when: ( create_group and ((time mod creation_frequency) = 0) ) {
		let free_balls type: list of: ball value: (ball as list) where ((each.state) = 'follow_nearest_ball') ;
		
		if condition: (length (free_balls)) > 1 {
			let satisfying_ball_groups type: list of: list value: (free_balls simple_clustering_by_envelope_distance group_creation_distance) where ( (length (each)) > min_group_member ) ;
			
			loop one_group over: satisfying_ball_groups {
				create group number: 1 returns: new_groups;
				
				ask target: (new_groups at 0) as: group {
					capture target: one_group as: ball_in_group;
				}
			}
		}
	}
	
	reflex create_clouds when: (create_cloud and ((time mod creation_frequency) = 0) ) {
		let candidate_groups type: list value: list(group) where (length(each.members) > (0.05 * ball_number) );
		let satisfying_groups type: list of: list value: (candidate_groups simple_clustering_by_envelope_distance cloud_creation_distance) where (length(each) >= min_cloud_member);
		loop one_cloud over: satisfying_groups {
			create cloud returns: rets;			
			let newCloud type: cloud value: rets at 0;
			ask newCloud as: cloud {
				capture one_cloud as: group_delegation;
			}

			loop gd over: (newCloud.members) {
				ask gd as group_delegation {
					migrate ball_in_group target: ball_in_cloud;
				}
			}
			
			set newCloud.color value: ((group_delegation(one_of(newCloud.members))).color).darker;
		}
	}
}

entities {
	species base skills: [moving] ;
	
	species ball parent: base control: fsm {
		//var shape type: geometry <- ball_shape at_location location ;
		float speed <- ball_speed; 
		rgb color <- ball_color;
		int beginning_chaos_time; 
		int time_in_chaos_state;
		
		init {
			let continue_loop type: bool value: true ; 
			loop while: continue_loop {
				let tmp_location type: point value: {(rnd (xmax - xmin)) + xmin, (rnd (ymax - ymin)) + ymin} ;
				let potential_geom type: geometry value: ball_shape at_location tmp_location ; 
				
				if ( empty ( (ball as list) where  ( each.shape intersects potential_geom ) ) )  {
					set location value: tmp_location ;
					set continue_loop value: false ;
				}
			}
		}
		
		action separation {
			arg nearby_balls type: list ;
			
			let repulsive_dx type: float value: 0 ;
			let repulsive_dy type: float value: 0 ;
			loop nb over: nearby_balls {
				let repulsive_distance var: repulsive_distance type: float value: ball_separation - ( location distance_to (ball (nb)).location ) ;
				let repulsive_direction var: repulsive_direction type: int value: ((ball (nb)).location) towards (location) ;
				set repulsive_dx value: repulsive_dx + (repulsive_distance * (cos (repulsive_direction))) ;
				set repulsive_dy  value: repulsive_dy + (repulsive_distance * (sin (repulsive_direction))) ;
			}
			set location value: location + {repulsive_dx, repulsive_dy} ;
		}
		
		action in_bounds type: bool {
			arg a_point type: point ;
			
			return ( !(a_point.x < xmin) and !(a_point.x > xmax) and !(a_point.y < ymin) and !(a_point.y > ymax) ) ;
		}
		 
		state follow_nearest_ball initial: true {
			enter {
				set color value: ball_color ;
				set speed value: ball_speed ;
			}
			
			let nearest_free_ball type: ball value: ( ( (list (ball) - self) where ((each.state) = 'follow_nearest_ball') ) closest_to self ) ;
			if condition: nearest_free_ball != nil {
				set heading value: self towards (nearest_free_ball) ;
				let step_distance type: float value: speed * step ;
				let step_x type: float value: step_distance * (cos (heading)) ;
				let step_y type: float value: step_distance * (sin (heading)) ;
				let tmp_location var: tmp_location type: point value: location + {step_x, step_y} ;
				if condition: (self in_bounds [ a_point :: tmp_location ] ) {
					set location value: tmp_location ;
					do action: separation {
						arg nearby_balls value: ((ball overlapping (shape + ball_separation)) - self) ;
					}
				}
			}
		}
		
		state chaos {
			enter {
				set beginning_chaos_time value: time ;
				set time_in_chaos_state value: 10 + (rnd(10)) ;
				set color value: chaos_ball_color ;
				set speed value: chaos_ball_speed ;
				set heading value: rnd(359) ;
			}
			
			let step_distance var: step_distance type: float value: speed * step ;
			let step_x var: step_x type: float value: step_distance * (cos (heading)) ;
			let step_y var: step_y type: float value: step_distance * (sin (heading)) ;
			let tmp_location type: point value: location + {step_x, step_y} ;
			if condition: (self in_bounds [ a_point :: tmp_location]) {
				set location value: tmp_location ;
				do action: separation {
					arg nearby_balls value: ((ball overlapping (shape + ball_separation)) - self) ;
				}
			}
			
			transition to: follow_nearest_ball when: time > (beginning_chaos_time + time_in_chaos_state) ;
		}
		
		aspect default {
			draw shape: geometry color: color size: ball_size ;
		}
	}
	
	species group parent: base {
		rgb color <- rgb ([ rnd(255), rnd(255), rnd(255) ]) ;
		
		geometry shape <- polygon ( (list (ball_in_group)) ) buffer  10 ;
		
		float speed value: float(group_base_speed) ;
		float perception_range value: float(base_perception_range + (rnd(5))) ;
		ball nearest_free_ball value: ( (ball as list) where ( (each.state = 'follow_nearest_ball') ) ) closest_to self ;
		group nearest_smaller_group value: ( ( (group as list) - [self] ) where ( (length (each.members)) < (length (members)) ) ) closest_to self ;
		base target value: (self get_nearer_target []) depends_on: [nearest_free_ball, nearest_smaller_group] ;
		
		action get_nearer_target type: base {
			if condition: (nearest_free_ball = nil) and (nearest_smaller_group = nil) {
				return nil ;
			}
			
			let distance_to_ball type: float value: (nearest_free_ball != nil) ? (self distance_to nearest_free_ball) : MAX_DISTANCE ;
			let distance_to_group type: float value: (nearest_smaller_group != nil) ? (self distance_to nearest_smaller_group) : MAX_DISTANCE ;
			if (distance_to_ball < distance_to_group) {
				return nearest_free_ball ;
			}
			
			return nearest_smaller_group ;
		}
		
		action separate_components {
			loop com over: (list (ball_in_group)) {
				let nearby_balls type: list of: ball_in_group value:  ((ball_in_group overlapping (com.shape + ball_separation)) - com) where (each in members) ;
				let repulsive_dx type: float value: 0 ;
				let repulsive_dy type: float value: 0 ;
				loop nb over: nearby_balls { 
					let repulsive_distance type: float value: ball_separation - ( (ball_in_group (com)).location distance_to nb.location ) ;
					let repulsive_direction type: int value: (nb.location) direction_to ((ball_in_group (com)).location) ;
					set repulsive_dx value: repulsive_dx + (repulsive_distance * (cos (repulsive_direction))) ;
					set repulsive_dy value: repulsive_dy + (repulsive_distance * (sin (repulsive_direction))) ;
				}
				
				set (ball_in_group (com)).location value: (ball_in_group (com)).location + {repulsive_dx, repulsive_dy} ;
			}
		}
		
		species ball_in_group parent: ball topology: topology((world).shape)  {
			
			float my_age <- 1.0 value: my_age + 0.01;
			 
			state follow_nearest_ball initial: true { }
			
			state chaos { }
			
			aspect default {
				draw shape: circle color: ((host as group).color).darker size: my_age ;
			}
		}
		
		reflex capture_nearby_free_balls when: (time mod update_frequency) = 0 {
			let nearby_free_balls type: list value: (ball overlapping (shape + perception_range)) where (each.state = 'follow_nearest_ball');
			if !(empty (nearby_free_balls)) {
				capture nearby_free_balls as: ball_in_group;
			}
		}
		
		action disaggregate {
			let released_coms type: list of: ball_in_group value: (list (ball_in_group)) ;
			release  released_coms as: ball in: world {
				set state value: 'chaos' ;
			}
			
			do die ;
		}
		
		reflex merge_nearby_groups when: (time mod merge_frequency) = 0 {
			if ( (target != nil) and ((species_of (target)) = group) ) {
				let nearby_groups type: list of: group value: (group overlapping (shape + perception_range)) - self ;
				
				if target in nearby_groups {
					if (rnd(10)) < (merge_possibility * 10) {
						let target_coms var: target_coms type: list of: ball_in_group value: target.members ;
						let released_balls type: list of: ball value: [];
						ask target {
							release target_coms as: ball in: world returns: released_coms;
							set released_balls value: list(released_coms);
							do die ;
						}
						capture released_balls as: ball_in_group; 
					}
				else { ask target as group {do disaggregate ;} }
				}
			}
		}
		
		reflex chase_target when: (target != nil) {
			let direction_to_nearest_ball type: int value: (self towards (target)) ;
			let step_distance type: float value: speed * step ;
			let dx type: float value: step_distance * (cos (direction_to_nearest_ball)) ;
			let dy type: float value: step_distance * (sin (direction_to_nearest_ball)) ;
			let envelope type: geometry value: shape.envelope ;
			let topleft_point type: point value: (envelope.points) at 0 ;
			let bottomright_point type: point value: (envelope.points) at 0 ;
			
			loop p over: envelope.points {
				if condition: ( (p.x <= topleft_point.x) and (p.y <= topleft_point.y) ) {
					set topleft_point value: p ;
				}
				
				if condition: ( (p.x >= bottomright_point.x) and (p.y >= bottomright_point.y) ) {
					set bottomright_point value: p ;
				}
			}
			
			if condition: ( (dx + topleft_point.x) < 0 ) {
				let tmp_dx value: dx + topleft_point.x ;
				set dx value: dx - tmp_dx ;
			} else {
				if condition: (dx + bottomright_point.x) > (environment_bounds.x) {
					let tmp_dx value: (dx + bottomright_point.x) - environment_bounds.x ;
					set dx value: dx - tmp_dx ;
				}
			}
			
			if  (dy + topleft_point.y) < 0 {
				let tmp_dy value: dy + topleft_point.y ;
				set dy value: dy - tmp_dy ;
			} else {
				if condition: (dy + topleft_point.y) > (environment_bounds.y) {
					let tmp_dy value: (dy + bottomright_point.y) - (environment_bounds.y) ;
					set dy value: dy - tmp_dy ;
				}
			}
			
			loop com over: (list (ball_in_group)) {
				set (ball_in_group (com)).location value: (ball_in_group (com)).location + {dx, dy} ;
			}
			
			set shape value:  convex_hull((polygon ((list (ball_in_group)) collect (ball_in_group (each)).location)) + 2.0) ;
		}
		
		reflex self_disaggregate {
			if condition: ( ( length (members) ) > ( 0.8 * (ball_number) ) ) {
				do action: disaggregate ;
			}
		}
		
		aspect default {
			draw shape: geometry color: color;
		}
	}
	
	species cloud parent: base {
		geometry shape value: convex_hull(polygon(members collect (((group_delegation(each)).shape).location)));
		rgb color;
				
		species group_delegation parent: group topology: (topology(world.shape)) {
			geometry shape value: convex_hull( (polygon ( (list (ball_in_cloud)) collect (each.location) )) ) buffer  10 ;

			reflex capture_nearby_free_balls when: (time mod update_frequency) = 0 {
			}
			
			reflex merge_nearby_groups when: (time mod merge_frequency) = 0 {
			}
			
			reflex chase_target when: (target != nil) {
			}
			
			reflex self_disaggregate {
			}
			
			action move {
				arg with_heading type: float;
				arg with_speed type: float;
				
				loop m over: members {
					ask m as ball_in_cloud {
						do move with: [ with_heading :: with_heading, with_speed :: with_speed ];
					}
				}
			}
			 
			species ball_in_cloud parent: ball_in_group topology: (world.shape) as topology control: fsm {
				
				action move {
					arg with_heading type: float;
					arg with_speed type: float;

					let dx type: float value: cos(with_heading) * with_speed;
					let dy type: float value: sin(with_heading) * with_speed;
					set location value: { ( (location.x) + dx ), ( (location.y) + dy )};
				}
				
				aspect default {}				
			}
		}
		
		group target_group;

		reflex chase_group {
			if ( (target_group = nil) or (dead(target_group)) ) {
				set target_group value: one_of(group);
			}
			
			if (target_group != nil) {
				let direction_target type: float value: self towards(target_group);
				
				loop m over: members {
					ask m as group_delegation {
						do move with: [ with_heading :: direction_target, with_speed :: cloud_speed ];
					}				
				}
			}
		}
		
		action can_capture type: bool {
			arg a_group type: group;
			
			if (shape overlaps a_group.shape) { return true; }
			
			loop gd over: members {
				if ( (a_group.shape) overlaps ( ( group_delegation(gd)).shape ) ) { return true; }
			}
			
			return false;
		}
		
		reflex capture_group {
			if ( (target_group != nil) and !(dead(target_group)) ) {
				if (self can_capture [ a_group :: target_group]) {

					capture target_group as: group_delegation returns: gds;
	
					ask (gds at 0) as: group_delegation {
						migrate ball_in_group target: ball_in_cloud;
					}
				}
			}
		}
		
		reflex disaggregate when: (empty(list(group))) {
			loop m over: members {
				ask group_delegation(m) as: group_delegation {
					migrate ball_in_cloud target: ball_in_group;
				}
			}
			
			release members as: group in: world returns: r_groups;
			
			loop rg over: r_groups {
				ask rg as: group { do disaggregate; }
			}
			
			do die;
		}
	 	 
		aspect default {
			draw shape: geometry color: color  empty: true;
			draw text: name + ' with ' + (string(length(members))) + ' groups.' size: 15 color: color style: bold at: {location.x - 65, location.y};
		}
	}
	
	species group_agents_viewer  { 
		aspect default {
			draw text: 'Number of groups: ' + (string (length (world.agents of_generic_species group))) at: {(environment_bounds.x)/2 - 210, (environment_bounds.y)/2} color: rgb('blue') size: 40 style: bold ;
		}
	}

	species cloud_agents_viewer  { 
		aspect default {
			draw text: 'Number of clouds: ' + (string (length (list(cloud)))) at: {(environment_bounds.x)/2 - 210, (environment_bounds.y)/2} color: rgb('green') size: 40 style: bold;
		}
	}
}

environment bounds: environment_bounds ;

experiment default_experiment type: gui {
	output {
		display name: 'Standard display' {
			species ball aspect: default transparency: 0.5 ;
			
			species group aspect: default transparency: 0.5 {
				species ball_in_group;
			}
			
			species cloud aspect: default {
				species group_delegation transparency: 0.9 {
					species ball_in_cloud;
					species ball_in_group;
				}
			}
		}
		
		display name: 'Ball display' {
			species ball;
		}
		
		display name: 'Group display' {
			species group;
			species group_agents_viewer;
		}
		
		display name: 'Cloud display' {
			species cloud;
			species cloud_agents_viewer;
		}
		
		monitor groups value: list (group);
		monitor length_groups value: length (list (group));
		monitor length_clouds value: length(list(cloud));
	}

}
