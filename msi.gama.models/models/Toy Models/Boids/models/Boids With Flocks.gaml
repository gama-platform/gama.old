/**
* Name: Boids With Flocks
* Author: 
* Description: This model shows the movement of boids following a goal and creating a flock . 
* Tags: gui, skill, 3d, multi_level, clustering
*/
model boids_flock
//Import the boids model
import "Boids.gaml"
global {
	//Size of the boids
	float boids_size <- float(3);
	//Shape of the boids
	geometry boids_shape <- circle(boids_size);
	//Separation between boids
	float boids_separation <- 4 * boids_size;
	//Distance to allow creation of the flock 
	int flock_creation_distance <- int(boids_separation + 1);
	//Minimum number of member among a flock
	int min_group_member <- 3;
	//Frequency of update for the flock
	int update_frequency <- 10;
	//Frequency of merge for the flock
	int merge_frequency <- 10;
	//Allow the creation of flock
	bool create_flocks <- false;
	//Perception range of the boids
	int base_perception_range <- int(xmax / 100) min: 1;
	
	init {
		//Creation of the different agents viewer
		create boids_agents_viewer;
		create flock_agents_viewer;
		create boids_in_flock_viewer;
	}
	//Reflex to create the flocks if it is available
	reflex create_flocks when: create_flocks {
		
		if (length(boids) > 1) {
			//Clustering by distance of the boids to determine the satisfying boids groups
			list<list<boids>> satisfying_boids_groups <- (boids.population simple_clustering_by_distance flock_creation_distance) where ((length(each)) > min_group_member);
			loop one_group over: satisfying_boids_groups {
				
				geometry potential_flock_polygon <- convex_hull(solid(polygon(one_group collect boids(each).location)) + (base_perception_range + 5));
				//If there is no obstacle between the boids of a potential flock, then the flock is created and all the boids become boids in flock
				if (empty(obstacle overlapping potential_flock_polygon)) {
					create flock {
						capture one_group as: boids_in_flock;
					}
				}

			}

		}

	} 

}

//Species flock which represent the flock of boids, using the skill moving
species flock skills: [moving] {
	rgb color <- rgb(rnd(255), rnd(255), rnd(255));
	geometry shape <- any_point_in(host);
	//Range of perception of the flock
	float perception_range <- float(base_perception_range + (rnd(5)));
	//Speed of the flock
	float speed update: mean(boids_in_flock collect each.speed);
	//Reflex to disaggregate the flock if there is a obstacle in the flock
	reflex disaggregate {
		geometry buffered_shape <- shape + perception_range;
		if !(empty(obstacle overlapping buffered_shape)) {
			release list<agent>(members) as: boids in: world;
			do die;
		}

	}
	//Reflex to capture the boids nearby in the range of perception with an update_frequency
	reflex capture_nearby_boids when: ((cycle mod update_frequency) = 0) {
		geometry buffered_shape <- shape + perception_range;
		list<boids> nearby_boids <- (boids overlapping buffered_shape);
		if (!(empty(nearby_boids))) {
			geometry new_polygon <- convex_hull(solid(shape + polygon(nearby_boids collect (each.location))));
			if (empty(obstacle overlapping new_polygon)) {
				capture nearby_boids as: boids_in_flock;
			}

		}

	}
	//Reflex to merge the intersecting flocks
	reflex merge_nearby_flocks when: ((cycle mod merge_frequency) = 0) 
	{
		loop f over: (flock) {
			if (f != self and (shape intersects f.shape)) {
				geometry new_shape <- convex_hull(polygon(shape.points + f.shape.points));
				if empty(obstacle overlapping new_shape) {
					list<boids> released_boids;
					ask f {
						release list<agent>(members) as: boids in: world returns: released_coms;
						released_boids <- released_coms;
						do die;
					}

					if (!empty(released_boids)) {
						capture released_boids as: boids_in_flock;
					}

					shape <- convex_hull(polygon(members collect (boids_in_flock(each).location)));
				}

			}

		}

	}
	//Reflex to make the flock follow the goal
	reflex chase_goal {
		float direction_to_nearest_ball <- (self towards (first(boids_goal)));
		float step_distance <- speed * step;
		float dx <- step_distance * (cos(direction_to_nearest_ball));
		float dy <- step_distance * (sin(direction_to_nearest_ball));
		geometry envelope <- shape.envelope;
		float min_y <- (envelope.points with_min_of (each.y)).y;
		float min_x <- (envelope.points with_min_of (each.x)).x;
		float max_x <- (envelope.points with_max_of (each.x)).x;
		float max_y <- (envelope.points with_max_of (each.y)).y;
		if (((dx + min_x) < xmin) and min_x > xmin) or (((dx + max_x) > xmax) and max_x < xmax) {
			dx <- 0.0;
		}
		if (((dy + min_y) < ymin) and min_y > ymin) or (((dy + max_y) > ymax) and max_y < ymax) {
			dy <- 0.0;
		}
		
		loop com over: boids_in_flock {
			com.location <- com.location + { dx, dy };
		}

		shape <- convex_hull(polygon(list(boids_in_flock) collect (each.location)));
	}

	aspect default {
		draw shape color: color;
	}
	//Species boids_in_flock which represents the boids agents captured by the flock
	species boids_in_flock parent: boids {
		float my_age <- 1.0 update: my_age + 0.01;
		reflex separation when: apply_separation {
		}

		reflex alignment when: apply_alignment {
		}

		reflex cohesion when: apply_cohesion {
		}

		reflex avoid when: apply_avoid {
		}

		reflex follow_goal  {
		}

		reflex wind when: apply_wind {
		}

		action do_move {
		}

		reflex movement {
			do do_move;
		}

		aspect default {
			draw circle(my_age) color: (host.color).darker;
		}

	}

}
//Species flock agents viewer which draw the flock information
species flock_agents_viewer {
	aspect default {
		draw "Flocks: " + (string(length(list(flock)))) at: { width_and_height_of_environment - 810, (width_and_height_of_environment) - 5 } color: #blue size: 80 ;
	}

}
//Species boids agents viewer which draw the boids information
species boids_agents_viewer {
	aspect default {
		draw "Boids: " + (string(length(list(boids)))) at: { width_and_height_of_environment - 810, (width_and_height_of_environment) - 165 } color: #blue size: 80 ;
	}

}

//Species boids_in_flock_viewer which draw the boids in flock information
species boids_in_flock_viewer {
	aspect default {
		draw "Boids in flocks: " + (string(number_of_agents - (length(list(boids))))) at: { width_and_height_of_environment - 810, width_and_height_of_environment - 85 } color:
		#blue size: 80 ;
	}

}


experiment boids_flocks type: gui {
	parameter "Create flock?" var: create_flocks <- true;
	parameter "Number of boids" var: number_of_agents <- 300;
	parameter "Environment size" var: width_and_height_of_environment <- 1600;
	parameter "Moving obstacles?" var: moving_obstacles <- true;
	parameter "Torus environment?" var: torus_environment <- false;
	parameter "Number of obstacles" var: number_of_obstacles <- 5;
	output {
		display default_display {
			species boids_goal;
			species boids aspect: image;
			species obstacle;
			species flock aspect: default transparency: 0.5 {
				species boids_in_flock aspect: default;
			}

			species flock_agents_viewer;
			species boids_agents_viewer;
			species boids_in_flock_viewer;
		}

	}

}