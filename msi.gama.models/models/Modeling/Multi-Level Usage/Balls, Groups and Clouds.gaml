/**
* Name: Balls, Groups and Clouds Multilevel Architecture
* Author: 
* Description: This model shows how to use multi-level architecture to group agents, and regroup groups. The operators capture 
* 	is used to capture an agent by a group and change its species as a species contained by the group and defined in the group species section. 
* 	The operator release is used to release contained agents and change them into an other species. The experiment shows ball moving 
* 	randomly, and following other balls. When they are close to each other, they generate a group of balls with its own behavior. A group of group 
* 	agents generate a cloud in the same way. When the number of balls contained inside the group is too high, the group disappears and releases 
* 	all its balls repulsively. 
* Tags: multi_level, agent_movement
*/
model balls_groups_clouds

global {
// Parameters
	bool create_group <- true;
	bool create_cloud <- true;

	// Environment
	point environment_bounds <- {500, 500};
	geometry shape <- rectangle(environment_bounds);

	//Define a inner environment smaller inside the environment
	int inner_bounds_x <- (int((environment_bounds.x) / 20));
	int inner_bounds_y <- (int((environment_bounds.y) / 20));
	int xmin <- inner_bounds_x;
	int ymin <- inner_bounds_y;
	int xmax <- int((environment_bounds.x) - inner_bounds_x);
	int ymax <- int((environment_bounds.y) - inner_bounds_y);
	float MAX_DISTANCE <- environment_bounds.x + environment_bounds.y;

	//Global variables for ball agents
	rgb ball_color <- #green;
	rgb chaos_ball_color <- #red;
	float ball_size <- float(3);
	float ball_speed <- float(1);
	float chaos_ball_speed <- 8 * ball_speed;
	int ball_number <- 400 min: 2 max: 1000;
	geometry ball_shape <- circle(ball_size);
	float ball_separation <- 6 * ball_size;

	//Global variables for group agents
	int group_creation_distance <- int(ball_separation + 1);
	int min_group_member <- 3;
	int group_base_speed <- (int(ball_speed * 1.5));
	int base_perception_range <- int(environment_bounds.x / 100) min: 1;
	int creation_frequency <- 3;
	int update_frequency <- 3;
	int merge_frequency <- 3;
	float merge_possibility <- 0.3;

	//Global variables for Clouds Agents
	int cloud_creation_distance <- 30 const: true;
	int min_cloud_member <- 3 const: true;
	int cloud_speed <- 3 const: true;
	int cloud_perception_range <- base_perception_range const: true;

	init {
		create ball number: ball_number;
		create group_agents_viewer;
	}

	//The simulation will try to create group at each frequence cycle
	reflex create_groups when: (create_group and ((cycle mod creation_frequency) = 0)) {
	//create a list from all balls following the nearest ball
		list<ball> free_balls <- ball where ((each.state) = 'follow_nearest_ball');
		if (length(free_balls) > 1) {
		//Clustering of the balls according to their distance with at least a minimal number of balls in a group
			list<list<ball>> satisfying_ball_groups <- (free_balls simple_clustering_by_distance group_creation_distance) where ((length(each)) > min_group_member);
			loop one_group over: satisfying_ball_groups {
				create group {
					capture one_group as: ball_in_group;
				}
				//Capture by the new groups created of the different balls present in the list one_group
			}

		}

	}

	//The simulation will try to create clouds at each frequence cycle
	reflex create_clouds when: (create_cloud and ((cycle mod creation_frequency) = 0)) {
	//A cloud can be created only using group with a number of balls inside greater than 5% of the total ball number
		list<group> candidate_groups <- group where (length(each.members) > (0.05 * ball_number));

		//A cloud can be created also only using group which aren't too far away 
		list<list<group>> satisfying_groups <- (candidate_groups simple_clustering_by_distance cloud_creation_distance) where (length(each) >= min_cloud_member);

		//Creation of the different clouds using the groups satisfying both conditions
		loop one_group over: satisfying_groups {
			create cloud  {
				capture one_group as: group_delegation {
					migrate ball_in_group target: ball_in_cloud;
				}
				color <- one_of(group_delegation).color.darker;
			}
		}
	}
}

//Species with a specified type of control architecture, here the final state machine FSM
species ball control: fsm {
	float speed <- ball_speed;
	rgb color <- ball_color;
	int beginning_chaos_time;
	int time_in_chaos_state;

	//create the ball in a certain way to not make balls intersect each other
	init {
		bool continue_loop <- true;
		loop while: continue_loop {
			point tmp_location <- {(rnd(xmax - xmin)) + xmin, (rnd(ymax - ymin)) + ymin};
			geometry potential_geom <- ball_shape at_location tmp_location;
			if (empty(ball where (each intersects potential_geom))) {
				location <- tmp_location;
				continue_loop <- false;
			}

		}

	}

	//Action used to separate the balls and make them repulsive for the other balls of the group
	action separation (list<ball> nearby_balls) {
		float repulsive_dx <- 0.0;
		float repulsive_dy <- 0.0;
		loop nb over: nearby_balls {
			float repulsive_distance <- ball_separation - (location distance_to nb.location);
			float repulsive_direction <- (nb.location) towards (location);
			repulsive_dx <- repulsive_dx + (repulsive_distance * (cos(repulsive_direction)));
			repulsive_dy <- repulsive_dy + (repulsive_distance * (sin(repulsive_direction)));
		}

		location <- location + {repulsive_dx, repulsive_dy};
	}

	bool in_bounds (point a_point) {
		return (!(a_point.x < xmin) and !(a_point.x > xmax) and !(a_point.y < ymin) and !(a_point.y > ymax));
	}

	//State that will make the agent follows the closest ball if it is not in the chaos state anymore
	state follow_nearest_ball initial: true {
		enter {
			color <- ball_color;
			speed <- ball_speed;
		}

		ball nearest_free_ball <- (ball where ((each.state) = 'follow_nearest_ball')) closest_to self;
		if nearest_free_ball != self {
			float heading <- self towards (nearest_free_ball);
			float step_distance <- speed * step;
			float step_x <- step_distance * (cos(heading));
			float step_y <- step_distance * (sin(heading));
			point tmp_location <- location + {step_x, step_y};
			if (self.in_bounds(tmp_location)) {
				location <- tmp_location;
				do separation(((ball overlapping (shape + ball_separation)) - self));
			}

		}

	}

	//Make the ball move randomly during a certain time
	state chaos {
		enter {
			beginning_chaos_time <- int(time);
			time_in_chaos_state <- 10 + (rnd(10));
			color <- chaos_ball_color;
			speed <- chaos_ball_speed;
			float heading <- rnd(360.0);
		}

		float step_distance <- speed * step;
		float step_x <- step_distance * (cos(heading));
		float step_y <- step_distance * (sin(heading));
		point tmp_location <- location + {step_x, step_y};
		if (self.in_bounds(tmp_location)) {
			location <- tmp_location;
			do separation(nearby_balls: (ball overlapping (shape + ball_separation)) - self);
		}

		transition to: follow_nearest_ball when: time > (beginning_chaos_time + time_in_chaos_state);
	}

	aspect default {
		draw ball_shape color: color size: ball_size at: self.location;
	} }

	//Species representing the group of balls
species group {
	rgb color <- rgb([rnd(255), rnd(255), rnd(255)]);
	geometry shape <- any_point_in(host) update: convex_hull(polygon(ball_in_group collect each.location));
	float speed update: float(group_base_speed);

	//Parameter to capture the balls contains in the perception range
	float perception_range update: float(base_perception_range + (rnd(5)));
	agent target update: get_nearer_target();

	//Function to return the closest ball or small group of balls that the agent could capture
	agent get_nearer_target {
		int size <- length(members);
		ball nearest_free_ball <- (ball where ((each.state = 'follow_nearest_ball'))) closest_to self;
		group nearest_smaller_group <- ((group - self) where ((length(each.members)) < size)) closest_to self;
		if (nearest_free_ball = nil) and (nearest_smaller_group = nil) {
			return nil;
		}

		float distance_to_ball <- (nearest_free_ball != nil) ? (self distance_to nearest_free_ball) : MAX_DISTANCE;
		float distance_to_group <- (nearest_smaller_group != nil) ? (self distance_to nearest_smaller_group) : MAX_DISTANCE;
		return (distance_to_ball < distance_to_group) ?  nearest_free_ball :  nearest_smaller_group;
	}

	//Action to use when the group of balls explode
	action separate_components {
		loop com over: (list(ball_in_group)) {
			list<ball_in_group> nearby_balls <- ((ball_in_group overlapping (com.shape + ball_separation)) - com) where (each in members);
			float repulsive_dx <- 0.0;
			float repulsive_dy <- 0.0;
			loop nb over: nearby_balls {
				float repulsive_distance <- ball_separation - ((ball_in_group(com)).location distance_to nb.location);
				float repulsive_direction <- (nb.location) direction_to ((ball_in_group(com)).location);
				repulsive_dx <- repulsive_dx + (repulsive_distance * (cos(repulsive_direction)));
				repulsive_dy <- repulsive_dy + (repulsive_distance * (sin(repulsive_direction)));
			}

			(ball_in_group(com)).location <- (ball_in_group(com)).location + {repulsive_dx, repulsive_dy};
		}

	}

	//Species that will represent the balls captured by the group agent
	species ball_in_group parent: ball topology: topology((world).shape) {
		float my_age <- 1.0 update: my_age + 0.01;
		state follow_nearest_ball initial: true {
		}

		state chaos {
		}

		aspect default {
			draw circle(my_age) color: ((host as group).color).darker;
		}

	}

	//Reflex to capture all the balls close to the group agent
	reflex capture_nearby_free_balls when: (cycle mod update_frequency) = 0 {
		list<ball> nearby_free_balls <- (ball overlapping (shape + perception_range)) where (each.state = 'follow_nearest_ball');
		if !(empty(nearby_free_balls)) {
			capture nearby_free_balls as: ball_in_group;
		}

	}

	//Action to do when the group is disaggregated
	action disaggregate {
		release list<agent>(members) as: ball in: world {
			state <- 'chaos';
		}

		do die;
	}

	//Reflex to merge the group close to the agent when the cycle is in the frequency of merging
	reflex merge_nearby_groups when: (cycle mod merge_frequency) = 0 and (target != nil) and ((species_of(target)) = group) {
		list<group> nearby_groups <- (group overlapping (shape + perception_range)) - self;
		if target in nearby_groups {
			if (rnd(10)) < (merge_possibility * 10) {
				list<ball> released_balls <- [];
				ask target {
					release list(ball_in_group) as: ball in: world {
						released_balls << self;
					}
					do die;
				}
				capture released_balls as: ball_in_group;
			} else {
				ask target as group {
					do disaggregate;
				}

			}

		}

	}

	//Reflex to chase a target agent 
	reflex chase_target when: (target != nil) {
		float direction_to_nearest_ball <- (self towards (target));
		float step_distance <- speed * step;
		float dx <- step_distance * (cos(direction_to_nearest_ball));
		float dy <- step_distance * (sin(direction_to_nearest_ball));
		geometry envelope <- shape.envelope;
		point topleft_point <- (envelope.points) at 0;
		point bottomright_point <- (envelope.points) at 0;
		loop p over: envelope.points {
			if ((p.x <= topleft_point.x) and (p.y <= topleft_point.y)) {
				topleft_point <- p;
			}

			if ((p.x >= bottomright_point.x) and (p.y >= bottomright_point.y)) {
				bottomright_point <- p;
			}

		}

		if ((dx + topleft_point.x) < 0) {
			float tmp_dx <- dx + topleft_point.x;
			dx <- dx - tmp_dx;
		} else {
			if (dx + bottomright_point.x) > (environment_bounds.x) {
				float tmp_dx <- (dx + bottomright_point.x) - environment_bounds.x;
				dx <- dx - tmp_dx;
			}

		}

		if (dy + topleft_point.y) < 0 {
			float tmp_dy <- dy + topleft_point.y;
			dy <- dy - tmp_dy;
		} else {
			if (dy + topleft_point.y) > (environment_bounds.y) {
				float tmp_dy <- (dy + bottomright_point.y) - (environment_bounds.y);
				dy <- dy - tmp_dy;
			}

		}

		loop com over: ball_in_group {
			com.location <- com.location + {dx, dy};
		}

		//shape <- convex_hull((polygon(ball_in_group collect each.location)) + 2.0);
	}
	//Reflex to disaggregate the group if it is too important ie the number of balls is greater than 80% of the total ball number
	reflex self_disaggregate {
		if ((length(members)) > (0.8 * (ball_number))) {
			do disaggregate;
		}

	}

	aspect default {
		draw shape color: color;
	}

}

//Species cloud that will be created by an agglomeration of groups.
species cloud {
	geometry shape <- any_point_in(host) update: convex_hull(polygon(group_delegation collect ((each.shape).location)));
	rgb color;

	//Species contained in the cloud to represent the groups captured by the cloud agent
	species group_delegation parent: group topology: (topology(world.shape)) {
		geometry shape update: convex_hull((polygon((ball_in_cloud) collect (each.location)))) buffer 10;

		init {
			shape <- convex_hull((polygon((ball_in_cloud) collect (each.location)))) buffer 10;
		}

		reflex capture_nearby_free_balls when: false {
		}

		reflex merge_nearby_groups when: false {
		}

		reflex chase_target when: false {
		}

		reflex self_disaggregate {
		}

		action move2 (float with_heading, float with_speed) {
			ask ball_in_cloud {
				do move2(with_heading, with_speed);
			}

		}

		species ball_in_cloud parent: ball_in_group topology: (world.shape) as topology control: fsm {

			action move2 (float with_heading, float with_speed) {
				float dx <- cos(with_heading) * with_speed;
				float dy <- sin(with_heading) * with_speed;
				location <- {((location.x) + dx), ((location.y) + dy)};
			}

			aspect default {
			}

		}

	}

	group target_group;

	//The cloud try to look for small groups to capture them
	reflex chase_group {
		if ((target_group = nil) or (dead(target_group))) {
			target_group <- one_of(group);
		}

		if (target_group != nil) {
			float direction_target <- self towards (target_group);
			ask group_delegation {
				do move2 with: [with_heading::float(direction_target), with_speed::float(cloud_speed)];
			}

		}

	}

	//Operator to know if a cloud can capture a group overlapping the cloud agent. 
	bool can_capture (group a_group) {
		if (a_group = nil or dead(a_group)) {
			return false;
		}

		if (shape overlaps a_group.shape) {
			return true;
		}

		loop gd over: group_delegation {
			if ((a_group.shape) overlaps gd.shape) {
				return true;
			}

		}

		return false;
	}

	//Reflex to capture group
	reflex capture_group {
		if (can_capture(target_group)) {
			capture target_group as: group_delegation {
				migrate ball_in_group target: ball_in_cloud;
			}

		}

	}

	//Reflex to disaggregate the clouds when they are no more group to capture
	reflex disaggregate when: (empty(list(group))) {
		ask group_delegation {
			migrate ball_in_cloud target: ball_in_group;
		}

		release list(group_delegation) as: group in: world {
			do disaggregate;
		}

		do die;
	}

	aspect default {
		draw shape color: color wireframe: true;
		draw (name + ' with ' + (string(length(members))) + ' groups.') size: 15 color: color at: {location.x - 65, location.y};
	}

}

species group_agents_viewer {

	aspect default {
		draw ('Number of groups: ' + (string(length(group)))) at: {(environment_bounds.x) / 2 - 210, (environment_bounds.y) / 2} color: #blue size: 40;
	}

}


experiment group_experiment type: gui {
	parameter 'Create groups?' var: create_group <- true;
	parameter 'Create clouds?' var: create_cloud <- false;
	output {
		layout horizontal([vertical([1::5000, 0::5000])::5000, 2::5000]) tabs: true editors: false;
		display 'Standard display' {
			species ball aspect: default transparency: 0.5;
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
		layout vertical([horizontal([0::5000, 1::5000])::5000, horizontal([2::5000, 3::5000])::5000]) tabs: true toolbars: true editors: false;
		display 'Standard display'  background: #black{
			species ball aspect: default transparency: 0.5;
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

		display 'Ball display'  {
			species ball;
		}

		display 'Group display'  {
			species group;
			species group_agents_viewer;
		}

		display 'Cloud display'  {
			species cloud;
		}

	}

}

