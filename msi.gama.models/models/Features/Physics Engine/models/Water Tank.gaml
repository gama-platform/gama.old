model Tank

/**
 *  Tank
 * 
 *  Author: Arnaud Grignard
 * 
 *  Description: Ball are falling in a Tank with a floor and 4 walls.
 * 
 *  Floor is a simple rectangle with a null mass.
 *  Wall is a rectangle draw with a depth equal to its height with a null mass.
 *  Ball is a sphere with a given mass.
 * 
 *
 */
global {
	int width_of_environment parameter: 'Dimensions' init: 100;
	int height_of_environment parameter: 'Dimensions' init: 100;
	int nb_balls parameter: 'Number of Agents' min: 1 <- 500;
	int size_of_agents parameter: 'Size of Agents' min: 1 <- 1;
	int wall_height parameter: 'Wall height' min: 1 <- 25;
	geometry shape <- rectangle(width_of_environment, height_of_environment);
	physic_world world2;
	
	init {
		create ball number: nb_balls {
			location <- { rnd(width_of_environment - size_of_agents), rnd(height_of_environment - size_of_agents), rnd(height_of_environment - size_of_agents) };
			radius <- float(size_of_agents);
			collisionBound <- ["shape"::"sphere", "radius"::radius];
		}

		create ground {
			location <- { width_of_environment / 2, height_of_environment / 2, 0 };
			collisionBound <- ["shape"::"floor", "x"::width_of_environment / 2, "y"::height_of_environment / 2, "z"::0];
			shape <- rectangle({ width_of_environment, height_of_environment });
			mass <- 0.0;
		}
		//down wall
		create wall {
			location <- { width_of_environment / 2, height_of_environment, 0 };
			shape <- rectangle({ width_of_environment, 2 });
			collisionBound <- ["shape"::"floor", "x"::width_of_environment / 2, "y"::1, "z"::wall_height];
			mass <- 0.0;
		}
		//upper wall
		create wall {
			location <- { width_of_environment / 2, 0, 0 };
			shape <- rectangle({ width_of_environment, 2 });
			collisionBound <- ["shape"::"floor", "x"::width_of_environment / 2, "y"::1, "z"::wall_height];
			mass <- 0.0;
		}
		//left wall
		create wall {
			location <- { 0, height_of_environment / 2, 0 };
			shape <- rectangle({ 2, height_of_environment });
			collisionBound <- ["shape"::"floor", "x"::1, "y"::height_of_environment / 2, "z"::wall_height];
			mass <- 0.0;
		}
		//right wall
		create wall {
			location <- { width_of_environment, height_of_environment / 2, 0 };
			shape <- rectangle({ 2, height_of_environment });
			collisionBound <- ["shape"::"floor", "x"::1, "y"::height_of_environment / 2, "z"::wall_height];
			mass <- 0.0;
		}

		create physic_world {
			world2 <- self;
		}
		ask world2 {
			registeredAgents <- (ball as list) + (ground as list) + (wall as list);
		}

		world2.gravity <- true;
	}

	reflex computeForces {
		ask world2 {
			do computeForces timeStep : 1;
		}

	}

}

species physic_world parent: Physical3DWorld ;

species ground skills: [physical3D] {
	aspect default {
		draw shape color: rgb(60, 60, 60);
	}

}

species wall skills: [physical3D] {
	rgb color;
	aspect default {
		draw shape color: rgb(40, 40, 40) depth: wall_height;
	}

}

species ball skills: [physical3D] {
	float radius;
	aspect default {
		draw sphere(radius) color: rgb(4, 158, 189);
	}

}

experiment tank type: gui {
	init{
		minimum_cycle_duration <-0.001;
	}
	output {
		display Circle refresh_every: 1 type: opengl ambient_light: 100 background: rgb(230, 230, 230) { species ground;
		species wall;
		species ball;
		}
	}

}

