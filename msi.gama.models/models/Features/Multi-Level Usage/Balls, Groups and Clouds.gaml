/**
 * This model demonstrates a use-case of the multi-level modeling feature.
 * 
 * It 's a three-level (of organisation) model. 
 * 
 * "ball" agents are represented at three levels of organization :
 * 		1. "ball" species when they "stays alone",
 * 		2. "ball_in_group" species when "ball" agents are members of a "group" agent,
 * 		3. "ball_in_cloud" species when "ball" agents are members of a "group" agent which is in turn a members of a "cloud" agent.
 * 
 * "group" agent is formed by a set of nearby balls.
 * "group" agents are represented at two levels of organization :
 * 		1. "group" species when they "stay alone",
 * 		2. "group_delegation" species when they are member of a "cloud" agent.
 * 
 * "cloud" agent is formed by a set of nearby groups.
 * 
 * Agents can change their representation/organization levels (species) thanks to the "capture", "release" and "migrate" statements.
 */
model balls_groups_clouds

global { 
	// Parameters
	bool create_group <- true; 
	bool create_cloud <- false; 
	
	// Environment
	point environment_bounds <- {500, 500}; 
	geometry shape <- rectangle(environment_bounds) ;		
	
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
	
	reflex create_groups when: ( create_group and ((cycle mod creation_frequency) = 0) ) {
		list<ball> free_balls <- ball where ((each.state) = 'follow_nearest_ball') ;

		if (length (free_balls) > 1) {
			list<list> satisfying_ball_groups <- (free_balls simple_clustering_by_distance group_creation_distance) where ( (length (each)) > min_group_member ) ;
			
			loop one_group over: satisfying_ball_groups {
				create group returns: new_groups;
				
				ask (new_groups at 0) as: group {
					capture one_group as: ball_in_group; 
				}
			}
		}
	}
	
	reflex create_clouds when: (create_cloud and ((cycle mod creation_frequency) = 0) ) {
		list<group> candidate_groups <- group where (length(each.members) > (0.05 * ball_number) );
		list<list> satisfying_groups <- (candidate_groups simple_clustering_by_distance cloud_creation_distance) where (length(each) >= min_cloud_member);
		
		loop one_group over: satisfying_groups {
			create cloud returns: rets;			
			cloud newCloud <- rets at 0; 
			ask newCloud as: cloud {
				capture one_group as: group_delegation;
			}

			loop gd over: (newCloud.members) {
				ask gd as group_delegation {
					migrate ball_in_group target: ball_in_cloud;
				}
			} 
			
			newCloud.color <- ((group_delegation(one_of(newCloud.members))).color).darker;
		}
	}
}

	species base skills: [moving] ;
	
	species ball parent: base control: fsm  { 
		
		float speed <- ball_speed; 
		rgb color <- ball_color;
		int beginning_chaos_time; 
		int time_in_chaos_state;
		
		init {
			bool continue_loop <- true ; 
			loop while: continue_loop {
				point tmp_location <- {(rnd (xmax - xmin)) + xmin, (rnd (ymax - ymin)) + ymin} ;
				geometry potential_geom <- ball_shape at_location tmp_location ; 
				
				if ( empty ( ball where  ( each intersects potential_geom ) ) )  {
					location <- tmp_location ;
					continue_loop <- false ;
				}
			}
		}
		
		action separation (list<ball> nearby_balls) {
			float repulsive_dx <- 0.0 ;
			float repulsive_dy <- 0.0 ;
			loop nb over: nearby_balls { 
				float repulsive_distance <- ball_separation - ( location distance_to ( nb).location ) ;
				int repulsive_direction <- ((nb).location) towards (location) ;
				repulsive_dx <- repulsive_dx + (repulsive_distance * (cos (repulsive_direction))) ;
				repulsive_dy <- repulsive_dy + (repulsive_distance * (sin (repulsive_direction))) ;
			}
			location <- location + {repulsive_dx, repulsive_dy} ;
		}
		
		bool in_bounds (point a_point) {
			return ( !(a_point.x < xmin) and !(a_point.x > xmax) and !(a_point.y < ymin) and !(a_point.y > ymax) ) ;
		}
		 
		state follow_nearest_ball initial: true {
			enter {   
				color <- ball_color ;
				speed <- ball_speed ;
			}
			list<ball> free_balls <- (list (ball) - self) where ((each.state) = 'follow_nearest_ball') ;
			ball nearest_free_ball <- free_balls closest_to self;
			if nearest_free_ball != nil {
				heading <- self towards (nearest_free_ball) ; 
				float step_distance <- speed * step ;
				float step_x <- step_distance * (cos (heading)) ;
				float step_y <- step_distance * (sin (heading)) ; 
				point tmp_location <- location + {step_x, step_y} ;
				if (self in_bounds (tmp_location) ) {
					location <- tmp_location ;
					do separation (((ball overlapping (shape + ball_separation)) - self));
				}
			}
		}
		
		state chaos {
			enter {
				beginning_chaos_time <- int(time) ;
				time_in_chaos_state <- 10 + (rnd(10)) ;
				color <- chaos_ball_color ;
				speed <- chaos_ball_speed ;
				heading <- rnd(359) ;
			}
			
			float step_distance <- speed * step ;
			float step_x <- step_distance * (cos (heading)) ;
			float step_y <- step_distance * (sin (heading)) ;
			point tmp_location <- location + {step_x, step_y} ;
			if (self in_bounds (tmp_location)) {
				location <- tmp_location ;
				do separation (nearby_balls: (ball overlapping (shape + ball_separation)) - self);
			}
			
			transition to: follow_nearest_ball when: time > (beginning_chaos_time + time_in_chaos_state) ;
		}
		
		aspect default {
			draw shape color: color size: ball_size ;
		}
	}
	
	species group parent: base { 
		rgb color <- rgb ([ rnd(255), rnd(255), rnd(255) ]) ;
		
		geometry shape <- polygon (ball_in_group) buffer  10 ;
		
		float speed update: float(group_base_speed) ;
		float perception_range update: float(base_perception_range + (rnd(5))) ;
		ball nearest_free_ball update: ( ball where ( (each.state = 'follow_nearest_ball') ) ) closest_to self ;
		group nearest_smaller_group update: ( ( (group as list) - self ) where ( (length (each.members)) < (length (members)) ) ) closest_to self ;
		base target update: (self get_nearer_target []) depends_on: [nearest_free_ball, nearest_smaller_group] ;
		
		base get_nearer_target {
			if  (nearest_free_ball = nil) and (nearest_smaller_group = nil) {
				return nil ;
			}
			
			float distance_to_ball <- (nearest_free_ball != nil) ? (self distance_to nearest_free_ball) : MAX_DISTANCE ;
			float distance_to_group <- (nearest_smaller_group != nil) ? (self distance_to nearest_smaller_group) : MAX_DISTANCE ;
			if (distance_to_ball < distance_to_group) {
				return nearest_free_ball ;
			}
			 
			return nearest_smaller_group ;
		}
		
		action separate_components {
			loop com over: (list (ball_in_group)) {
				list<ball_in_group> nearby_balls <-  ((ball_in_group overlapping (com.shape + ball_separation)) - com) where (each in members) ;
				float repulsive_dx <- 0.0 ;
				float repulsive_dy <- 0.0 ;
				loop nb over: nearby_balls { 
					float repulsive_distance <- ball_separation - ( (ball_in_group (com)).location distance_to nb.location ) ;
					int repulsive_direction <- (nb.location) direction_to ((ball_in_group (com)).location) ;
					repulsive_dx <- repulsive_dx + (repulsive_distance * (cos (repulsive_direction))) ;
					repulsive_dy <- repulsive_dy + (repulsive_distance * (sin (repulsive_direction))) ;
				}
				
				(ball_in_group (com)).location <- (ball_in_group (com)).location + {repulsive_dx, repulsive_dy} ;
			}
		}
		
		species ball_in_group parent: ball topology: topology((world).shape)  {
			
			float my_age <- 1.0 update: my_age + 0.01;
			 
			state follow_nearest_ball initial: true { }
			
			state chaos { }
			
			aspect default {
				draw circle(my_age) color: ((host as group).color).darker ;
			}
		}
		
		reflex capture_nearby_free_balls when: (cycle mod update_frequency) = 0 {
			list<ball> nearby_free_balls <- (ball overlapping (shape + perception_range)) where (each.state = 'follow_nearest_ball');
			if !(empty (nearby_free_balls)) {
				capture nearby_free_balls as: ball_in_group;
			}
		}
		
		action disaggregate {
			release members as: ball in: world {
				set state value: 'chaos' ;
			}
			
			do die ;
		}
		
		reflex merge_nearby_groups when: (cycle mod merge_frequency) = 0 {
			if ( (target != nil) and ((species_of (target)) = group) ) {
				list<group> nearby_groups <- (group overlapping (shape + perception_range)) - self ;
				
				if target in nearby_groups {
					if (rnd(10)) < (merge_possibility * 10) {
						list<ball_in_group> target_coms <- list<ball_in_group>(target.members) ;
						list<ball> released_balls <- [];
						ask target {
							release target_coms as: ball in: world returns: released_coms;
							released_balls <- list(released_coms);
							do die ;
						}
						capture released_balls as: ball_in_group; 
					}
				else { ask target as group {do disaggregate ;} }
				}
			}
		}
		
		reflex chase_target when: (target != nil) {
			int direction_to_nearest_ball <- (self towards (target)) ;
			float step_distance <- speed * step ;
			float dx <- step_distance * (cos (direction_to_nearest_ball)) ;
			float dy <- step_distance * (sin (direction_to_nearest_ball)) ;
			geometry envelope <- shape.envelope ;
			point topleft_point <- (envelope.points) at 0 ;
			point bottomright_point <- (envelope.points) at 0 ;
			
			loop p over: envelope.points {
				if ( (p.x <= topleft_point.x) and (p.y <= topleft_point.y) ) {
					topleft_point <- p ;
				}
				
				if ( (p.x >= bottomright_point.x) and (p.y >= bottomright_point.y) ) {
					bottomright_point <- p ;
				}
			}
			
			if ( (dx + topleft_point.x) < 0 ) {
				float tmp_dx <- dx + topleft_point.x ;
				dx <- dx - tmp_dx ;
			} else {
				if (dx + bottomright_point.x) > (environment_bounds.x) {
					float tmp_dx <- (dx + bottomright_point.x) - environment_bounds.x ;
					dx <- dx - tmp_dx ;
				}
			}
			
			if (dy + topleft_point.y) < 0 {
				float tmp_dy <- dy + topleft_point.y ;
				dy <- dy - tmp_dy ;
			} else {
				if (dy + topleft_point.y) > (environment_bounds.y) {
					float tmp_dy <- (dy + bottomright_point.y) - (environment_bounds.y) ;
					dy <- dy - tmp_dy ;
				}
			}
			
			loop com over: (list (ball_in_group)) {
				(ball_in_group (com)).location <- (ball_in_group (com)).location + {dx, dy} ;
			}
			
			shape <- convex_hull((polygon ((list (ball_in_group)) collect (ball_in_group (each)).location)) + 2.0) ;
		}
		
		reflex self_disaggregate {
			if ( ( length (members) ) > ( 0.8 * (ball_number) ) ) {
				do disaggregate ;
			}
		}
		
		aspect default {
			draw shape color: color;
		}
	}
	
	species cloud parent: base {
		geometry shape <- convex_hull(polygon(members collect (((group_delegation(each)).shape).location))) update: convex_hull(polygon(members collect (((group_delegation(each)).shape).location)));

		rgb color;
				
		species group_delegation parent: group topology: (topology(world.shape)) {
			geometry shape <- convex_hull( (polygon ( (list (ball_in_cloud)) collect (each.location) )) ) buffer 10 update: convex_hull( (polygon ( (list (ball_in_cloud)) collect (each.location) )) ) buffer  10 ;

			reflex capture_nearby_free_balls when: false {
			}
			
			reflex merge_nearby_groups when: false {
			}
			
			reflex chase_target when: false {
			}
			
			reflex self_disaggregate {
			}
			
			action move2 (float with_heading, float with_speed) {

				loop m over: members {
					ask m as ball_in_cloud {
						do move2 (with_heading,with_speed);
					}
				}
			}
			 
			species ball_in_cloud parent: ball_in_group topology: (world.shape) as topology control: fsm {
				
				action move2 (float with_heading, float with_speed) {
					float dx <- cos(with_heading) * with_speed;
					float dy <- sin(with_heading) * with_speed;
					location <- { ( (location.x) + dx ), ( (location.y) + dy )};
				}
				
				aspect default {}				
			}
		}
		
		group target_group;

		reflex chase_group {
			if ( (target_group = nil) or (dead(target_group)) ) {
				target_group <- one_of(group);
			}
			
			if (target_group != nil) {
				int direction_target <- self towards(target_group);
				
				loop m over: members {
					ask m as group_delegation {
						do move2 with: [ with_heading :: direction_target, with_speed :: cloud_speed ];
					}				
				}
			}
		}
		
		bool can_capture (group a_group) {
			
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
			draw shape color: color empty: true;
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


experiment group_experiment type: gui {
	parameter 'Create groups?' var: create_group <- true;
	parameter 'Create clouds?' var: create_cloud <- false;
		
	output {
		display 'Standard display' {
			species ball aspect: default transparency: 0.5 ;
			
			species group aspect: default transparency: 0.5 {
				species ball_in_group;
			}
		}
		
		display 'Ball display' {
			species ball;
		}
		
		display 'Group display' {
			species group;
			species group_agents_viewer;
		}
	}
}

experiment cloud_experiment type: gui {

	parameter 'Create groups?' var: create_group <- true;
	parameter 'Create clouds?' var: create_cloud <- true;
		
	output {
		display 'Standard display' {
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
		
		display 'Ball display' {
			species ball;
		}
		
		display 'Group display' {
			species group;
			species group_agents_viewer;
		}

		display 'Cloud display' {
			species cloud;
		}
		
		monitor "Balls" value: length(ball);
		monitor "Groups" value: length(group);
		monitor "Clouds" value: length(cloud);
	}
}

