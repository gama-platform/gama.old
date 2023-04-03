/**
* Name: Boids 3D Analysis
* Author: 
* Description:  This model shows the movement of boids following a goal and creating a flock. 
* Four experiments are proposed : Simple is the 3D display of the boids like a real world, 
* Trajectory Analysis is about the analysis of the trajectories of the boids, Space Time Cube adds two 
* 	displays to see the movement of the boids using the time as the z-axis, and the last one represents the 
* 	differents cameras available in GAMA.
* Tags: gui, skill, 3d, camera, multi_level
*/
model boids

global torus: torus_environment {
//Number of boids to represent
	int number_of_agents parameter: 'Number of agents' <- 10 min: 1 max: 500;
	//Number of obstacles to represent
	int number_of_obstacles parameter: 'Number of obstacles' <- 4 min: 0;
	//Size of the boids
	int boids_size parameter: 'Boids size' <- 20 min: 1;
	//Maximal speed allowed for the boids
	float maximal_speed parameter: 'Maximal speed' <- 15.0 min: 0.1 max: 15.0;
	//Speed radius
	float radius_speed parameter: 'radius speed' <- 0.5 min: 0.1;
	//Cohesion factor of the boid group  in the range of a boid agent
	int cohesion_factor parameter: 'Cohesion Factor' <- 200;
	//Alignment factor used for the boid group in the range of a boid agent
	int alignment_factor parameter: 'Alignment Factor' <- 100;
	//Minimal distance to move
	float minimal_distance parameter: 'Minimal Distance' <- 30.0;
	//Maximal turn done by the boids
	int maximal_turn parameter: 'Maximal Turn' <- 45 min: 0 max: 359;

	//Parameters of the environment and the simulations
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
	//Wind vector 
	point wind_vector <- {0, 0} parameter: 'Direction of the wind';
	int goal_duration <- 30 update: (goal_duration - 1);
	//Goal location
	point goal <- {rnd(width_and_height_of_environment - 2) + 1, rnd(width_and_height_of_environment - 2) + 1};
	list images of: image_file <- [file('../images/bird1.png'), file('../images/bird2.png'), file('../images/bird3.png')];
	string file_path_to_ocean <- '../images/ocean.jpg';
	int xmin <- bounds;
	int ymin <- bounds;
	int xmax <- (width_and_height_of_environment - bounds);
	int ymax <- (width_and_height_of_environment - bounds);
	geometry shape <- square(width_and_height_of_environment);

	// flock's parameter 
	float two_boids_distance const: true init: 30.0;
	int merging_distance const: true init: 30;
	bool create_flock init: false;

	init {
	//Create the boids and place them randomlly
		create boids number: number_of_agents {
			location <- {rnd(width_and_height_of_environment - 2) + 1, rnd(width_and_height_of_environment - 2) + 1};
		}

		//Create an obstacle and place it randomly
		create obstacle number: number_of_obstacles {
			location <- {rnd(width_and_height_of_environment - 2) + 1, rnd(width_and_height_of_environment - 2) + 1};
		}
		//Create a goal and place it at the goal location
		create boids_goal number: 1 {
			location <- goal;
		}

		create aggregatedboids;
	}

	//Reflex to create flock of boids considering the neighbours of each boids 
	reflex create_flocks {
		if create_flock {
		//Create a map using a boid agent as a key and the list of all its neighbours as the value for the key
			map<boids, list<boids>> potentialBoidsNeighboursMap;

			//Search all the boids within the two boids distance from a boid agent and put them in the map
			loop one_boids over: boids {
				list<boids> free_neighbours <- boids overlapping (one_boids.shape + (two_boids_distance));
				remove one_boids from: free_neighbours;
				if !(empty(free_neighbours)) {
					add (one_boids::free_neighbours) to: potentialBoidsNeighboursMap;
				}

			}

			//Sorting of all the boids considered as key in the map by the length of their neighbours
			list<boids> sorted_free_boids <- (potentialBoidsNeighboursMap.keys) sort_by (length(potentialBoidsNeighboursMap at each));
			//Removing of all the boids which has been considered as a key of the map, but  which are already included in a bigger list of neighbours by one of them neighbours
			loop one_boids over: sorted_free_boids {
				list<boids> one_boids_neighbours <- potentialBoidsNeighboursMap at one_boids;
				if (one_boids_neighbours != nil) {
					loop one_neighbour over: one_boids_neighbours {
						remove one_neighbour from: potentialBoidsNeighboursMap;
					}

				}

			}
			//Remove all the duplicates key of potentialBoidsNeighboursMap
			list<boids> boids_neighbours <- (potentialBoidsNeighboursMap.keys);
			loop one_key over: boids_neighbours {
				put (remove_duplicates((potentialBoidsNeighboursMap at (one_key)) + one_key)) at: one_key in: potentialBoidsNeighboursMap;
			}

			//Create a flock of boids considering the key of potentialBoidsNeighboursMap
			loop one_key over: (potentialBoidsNeighboursMap.keys) {
				list<boids> micro_agents <- potentialBoidsNeighboursMap at one_key;
				if ((length(micro_agents)) > 1) {
					create flock number: 1 with: [color::rgb([rnd(255), rnd(255), rnd(255)])] {
						capture micro_agents as: boids_delegation;
					}

				}

			}

		}

	}

}
//Species boids_goal which represents the goal followed by the boids agent, using the skill moving
species boids_goal skills: [moving] {
	float range const: true init: 20.0;
	int radius <- 3;

	//Reflex to make the goal move in circle
	reflex wander_in_circle {
		location <- {world.shape.width / 2 + world.shape.width / 2 * cos(time * radius_speed), world.shape.width / 2 + world.shape.width / 2 * sin(time * radius_speed)};
		goal <- location;
	}

	aspect default {
		draw circle(10) color: rgb('red');
		draw circle(40) color: rgb('orange') wireframe: true;
	}

}

//Species flock which represents the flock of boids agents, managing the boids agents captured
species flock {
//Represent the cohesion index of the flock
	float cohesionIndex <- two_boids_distance update: (two_boids_distance + (length(members)));
	rgb color <- rgb([64, 64, 64]);
	geometry shape update: !(empty(members)) ? ((polygon(members collect
	(boids_delegation(each)).location)) + 2.0) : (polygon([{rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)}]));

	//Species that will represent the boids agents captured or inside a flock
	species boids_delegation parent: boids topology: topology(world.shape) {
		list<boids> others -> ((boids_delegation overlapping (shape + range))) - self;

		//Action to compute the mass center of the flock
		action compute_mass_center type: point {
			loop o over: others {
				if dead(o) {
					write 'in ' + name + ' agent with others contains death agents';
				}

			}

			return (length(others) > 0) ? (mean(others collect (each.location))) : location;
		}

		reflex separation when: apply_separation {
		}

		reflex alignment when: apply_alignment {
		}
		//Reflex to apply the cohesion on the boids agents
		reflex cohesion when: apply_cohesion {
			point acc <- compute_mass_center() - location;
			acc <- acc / cohesion_factor;
			velocity <- velocity + acc;
		}

		reflex avoid when: apply_avoid {
		}

	}
	//Reflex to capture boids agents and release captured boids agents
	reflex capture_release_boids {
		list<boids_delegation> removed_components <- boids_delegation where ((each distance_to location) > cohesionIndex);
		if !(empty(removed_components)) {
			release removed_components;
		}

		list<boids> added_components <- boids where ((each distance_to location) < cohesionIndex);
		if !(empty(added_components)) {
			capture added_components as: boids_delegation;
		}

	}
	//Reflexe to kill the flock if the boids agents contained is lower than 2
	reflex dispose when: ((length(members)) < 2) {
		release list<agent>(members);
		do die;
	}
	//Reflex to merge the flocks too close from each other
	reflex merge_nearby_flocks {
		list<flock> nearby_flocks <- (flock overlapping (shape + merging_distance));
		if !(empty(nearby_flocks)) {
			nearby_flocks <- nearby_flocks sort_by (length(each.members));
			flock largest_flock <- nearby_flocks at ((length(nearby_flocks)) - 1);
			remove largest_flock from: nearby_flocks;
			list<boids> added_components;
			loop one_flock over: nearby_flocks {
				release list<agent>(one_flock.members) returns: released_boids;
				loop rb over: released_boids {
					add boids(rb) to: added_components;
				}

			}

			if !(empty(added_components)) {
				ask largest_flock {
					capture added_components as: boids_delegation;
				}

			}

		}

	}

	aspect default {
		draw shape color: color;
	}

}

//Species to represent the boids aggregated
species aggregatedboids {

	reflex updateLocation {
		location <- mean(boids collect (each.location));
	}

	aspect base {
		draw circle(10) color: #white;
	}

}
//Species to represent the boids agent using the skill moving
species boids skills: [moving] {
//Speed of the agent
	float speed max: maximal_speed <- maximal_speed;
	//Range of movement for the neighbours
	float range <- minimal_distance * 2;
	//Velocity of the agent
	point velocity <- {0, 0};
	float hue <- rnd(360) / 360;

	//List of the neighbours boids
	list<boids> others update: ((boids overlapping (circle(range))) - self);

	//Point of the mass center of the "flock" considered as the neighbours agents
	point mass_center update: (length(others) > 0) ? (mean(others collect (each.location))) : location;

	//Reflex to do the separation of the agents with the other boids in the minimal distance
	reflex separation when: apply_separation {
		point acc <- {0, 0};
		loop boid over: (boids at_distance minimal_distance) {
			acc <- acc - ((boid.location) - location);
		}

		velocity <- velocity + acc;
	}

	//Reflex to do the alignement of the boids
	reflex alignment when: apply_alignment {
		point acc <- mean(others collect (each.velocity)) - velocity;
		velocity <- velocity + (acc / alignment_factor);
	}

	//Reflex to apply the cohesion using the mass center of the "flock"
	reflex cohesion when: apply_cohesion {
		point acc <- mass_center - location;
		acc <- acc / cohesion_factor;
		velocity <- velocity + acc;
	}
	//Reflex to avoid the obstacles
	reflex avoid when: apply_avoid {
		point acc <- {0, 0};
		list<obstacle> nearby_obstacles <- (obstacle overlapping (circle(range)));
		loop obs over: nearby_obstacles {
			acc <- acc - ((location of obs) - my (location));
		}

		velocity <- velocity + acc;
	}
	//action to represent the bounding of the movement of the boids
	action bounding {
		if !(torus_environment) {
			if (location.x) < xmin {
				velocity <- velocity + {bounds, 0};
			} else if (location.x) > xmax {
				velocity <- velocity - {bounds, 0};
			}

			if (location.y) < ymin {
				velocity <- velocity + {0, bounds};
			} else if (location.y) > ymax {
				velocity <- velocity - {0, bounds};
			}

		}

	}
	//Reflex to follow the goal 
	reflex follow_goal when: apply_goal {
		velocity <- velocity + ((goal - location) / cohesion_factor);
	}
	//Reflex to apply the wind vector
	reflex wind when: apply_wind {
		velocity <- velocity + wind_vector;
	}
	//action to move  
	action do_move {
		if (((velocity.x) as int) = 0) and (((velocity.y) as int) = 0) {
			velocity <- {(rnd(4)) - 2, (rnd(4)) - 2};
		}

		point old_location <- location;
		do goto target: location + velocity;
		velocity <- location - old_location;
	}

	//Reflex to do the movement, calling both bounding and do_move actions
	reflex movement {
		do bounding;
		do do_move;
	}

	aspect basic {
		draw triangle(boids_size) color: rgb('black');
	}

	aspect image {
		draw (images at (rnd(2))) size: boids_size rotate: heading color: rgb('black');
	}

	aspect dynamicColor {
		rgb cc <- hsb(float(heading) / 360.0, 1.0, 1.0);
		draw triangle(20) size: 15 rotate: 90 + heading color: cc border: cc depth: 5;
	}

}

//Species which represents the obstacles using the skill moving
species obstacle skills: [moving] {
	float speed <- 0.1;

	aspect default {
		draw triangle(20) color: rgb('yellow') depth: 5;
	}

}

experiment "Simple" type: gui {
	float minimum_cycle_duration <- 0.05;
	output {
		display RealBoids type: 3d {
			image file_path_to_ocean refresh: false;
			species boids aspect: dynamicColor position: {0, 0, 0.1} trace: 30;
			species boids_goal transparency: 0.2 position: {0, 0, 0.1};
			species obstacle position: {0, 0, 0.1};
		}

	}

}

experiment "Trajectory Analysis" type: gui {
	float minimum_cycle_duration <- 0.05;
	output {
		layout #split;
		display RealBoids type: 3d {
			image file_path_to_ocean refresh: false;
			species boids aspect: dynamicColor transparency: 0.5 position: {0, 0, 0.1};
			species boids_goal transparency: 0.2 position: {0, 0, 0.1};
			species obstacle position: {0, 0, 0.1};
		}

		display AggregatedBoidsTrajectory type: 3d {
			image file_path_to_ocean refresh: false;
			species aggregatedboids aspect: base trace: 100 fading: true;
			species boids_goal aspect: default;
		}

	}

}

experiment "Space & Time Cube" type: gui {
	float minimum_cycle_duration <- 0.05;
	output {
		layout #split;
		display RealBoids type: 3d {
			image file_path_to_ocean refresh: false;
			species boids aspect: dynamicColor transparency: 0.5 position: {0, 0, 0.1};
			species boids_goal transparency: 0.2 position: {0, 0, 0.1};
			species obstacle position: {0, 0, 0.1};
		}

		display SpaceTimeCubeAll type: 3d camera: #from_up_front {
			image file_path_to_ocean refresh: false;
			species boids trace: 100 {
				draw triangle(20) size: 15 rotate: heading color: hsb(float(heading) / 360.0, 1.0, 1.0) border: hsb(float(heading) / 360.0, 1.0, 1.0) depth: 5 at:
				{location.x, location.y, location.z + time};
			}

			species boids_goal trace: 100 {
				draw sphere(10) color: rgb('yellow') at: {location.x, location.y, location.z + time};
			}

		}

		display SpaceTimeCubeAggregated type: 3d camera: #from_up_front {
			image file_path_to_ocean refresh: false;
			species aggregatedboids trace: 500 {
				draw sphere(10) color: rgb('red') at: {location.x, location.y, location.z + time};
			}

			species boids_goal trace: 500 {
				draw sphere(10) color: rgb('yellow') at: {location.x, location.y, location.z + time};
			}

		}

	}

}

experiment "Multiple views" type: gui {
	float minimum_cycle_duration <- 0.05;
	output synchronized: true {
		layout #split;
		display RealBoids type: 2d antialias: false {
			image file_path_to_ocean refresh: false;
			species boids aspect: dynamicColor transparency: 0.5 position: {0, 0, 0.1};
			species boids_goal transparency: 0.2 position: {0, 0, 0.25};
			species obstacle;
			species boids aspect: dynamicColor transparency: 0.2 position: {0, 0, 0.24};
		}

		display ThirdPerson type: 3d antialias: false {
			camera "default" dynamic: true location: {int(first(boids).location.x), int(first(boids).location.y), 500} target:
			{int(first(boids).location.x), int(first(boids).location.y), 0};
			overlay position: {5, 5} size: {width_and_height_of_environment/3,width_and_height_of_environment/3} transparency: 0.2 rounded: false {
			 	ask boids {
					draw triangle(20) size: 15 rotate: 90 + heading color: int(self)=0 ? #red: #gray depth: 5 at: location/3;
			 	}
			}
			image file_path_to_ocean;
			species obstacle;
			species boids aspect: dynamicColor transparency: 0.2;
			species boids_goal transparency: 0.2;
		}

		display FirstPerson type: 3d antialias: false {
			camera "default" dynamic: true location: {int(first(boids).location.x), int(first(boids).location.y), 5} target:
			{cos(first(boids).heading) * first(boids).speed + int(first(boids).location.x), sin(first(boids).heading) * first(boids).speed + int(first(boids).location.y), 5};
			image file_path_to_ocean;
			species obstacle;
			species boids aspect: dynamicColor transparency: 0.2;
			species boids_goal transparency: 0.2;
			overlay position: {5, 5} size: {width_and_height_of_environment/3,width_and_height_of_environment/3} transparency: 0.2 rounded: false {
			 	ask boids {
					draw triangle(20) size: 15 rotate: 90 + heading color: int(self)=0 ? #red: #gray depth: 5 at: location/3;
			 	}
			}

		}

	}

}