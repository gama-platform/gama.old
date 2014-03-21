model pool3D

/**
 *  pool3D
 * 
 *  Author: Arnaud Grignard
 * 
 *
 */
global {
	int width_of_environment <- 200;
	int height_of_environment <- 300;
	float speed_of_agents <- 2.0;
	int size_of_agents <- 10;
	rgb colorwood <- rgb([178, 112, 62]);
	physic_world world2;
	geometry shape <- rectangle(width_of_environment, height_of_environment);
	init {
		create ball {
			location <- { width_of_environment / 2, 4 * height_of_environment / 5, 5.0 };
			mass <- 3.0;
			//a random velocity between 500 and 1000
			velocity <- [0.0, -float(500 + rnd(500)), 0.0];
			collisionBound <- ["shape"::"sphere", "radius"::5];
		}

		int i <- 0;
		int deltaI <- 0;
		int initX <- 75;
		int initY <- int(height_of_environment / 8);
		create ball number: 15 {
			location <- { initX + (i - deltaI) * 10, initY, 5.0 };
			heading <- 90;
			speed <- 0.0;
			mass <- 3.0;
			collisionBound <- ["shape"::"sphere", "radius"::5];
			i <- i + 1;
			if ((i mod 2) = 0) {
				color <- rgb('red');
			} else {
				color <- rgb('yellow');
			}

			if (i = 5) {
				initX <- initX + 5;
				initY <- initY + 9;
				deltaI <- 5;
			}

			if (i = 9) {
				initX <- initX + 5;
				initY <- initY + 9;
				deltaI <- 9;
			}

			if (i = 12) {
				initX <- initX + 5;
				initY <- initY + 9;
				deltaI <- 12;
			}

			if (i = 14) {
				initX <- initX + 5;
				initY <- initY + 9;
				deltaI <- 14;
			}

		}

		create ground {
			location <- { width_of_environment / 2, height_of_environment / 2, 0 };
			shape <- rectangle({ width_of_environment - 24, height_of_environment - 24 });
			collisionBound <- ["shape"::"floor", "x"::width_of_environment / 2 - 12, "y"::height_of_environment / 2 - 12, "z"::0];
			mass <- 0.0;
		}

		create ground {
			location <- { width_of_environment / 2, 6, 0 };
			shape <- rectangle({ width_of_environment - 24, 12 });
			collisionBound <- ["shape"::"floor", "x"::width_of_environment / 2 - 12, "y"::6, "z"::0];
			mass <- 0.0;
		}

		create ground {
			location <- { width_of_environment / 2, height_of_environment - 6, 0 };
			shape <- rectangle({ width_of_environment - 24, 12 });
			collisionBound <- ["shape"::"floor", "x"::width_of_environment / 2 - 12, "y"::6, "z"::0];
			mass <- 0.0;
		}

		create ground {
			location <- { 6, height_of_environment / 4 + 3, 0 };
			shape <- rectangle({ 12, height_of_environment / 2 - 18 });
			collisionBound <- ["shape"::"floor", "x"::6, "y"::height_of_environment / 4 - 9, "z"::0];
			mass <- 0.0;
		}

		create ground {
			location <- { 6, 3 * height_of_environment / 4 - 3, 0 };
			shape <- rectangle({ 12, height_of_environment / 2 - 18 });
			collisionBound <- ["shape"::"floor", "x"::6, "y"::height_of_environment / 4 - 9, "z"::0];
			mass <- 0.0;
		}

		create ground {
			location <- { width_of_environment - 6, height_of_environment / 4 + 3, 0 };
			shape <- rectangle({ 12, height_of_environment / 2 - 18 });
			collisionBound <- ["shape"::"floor", "x"::6, "y"::height_of_environment / 4 - 9, "z"::0];
			mass <- 0.0;
		}

		create ground {
			location <- { width_of_environment - 6, 3 * height_of_environment / 4 - 3, 0 };
			shape <- rectangle({ 12, height_of_environment / 2 - 18 });
			collisionBound <- ["shape"::"floor", "x"::6, "y"::height_of_environment / 4 - 9, "z"::0];
			mass <- 0.0;
		}

		//down wall
		create wall {
			location <- { width_of_environment / 2, height_of_environment, 0 };
			shape <- rectangle({ width_of_environment, 2 });
			collisionBound <- ["shape"::"floor", "x"::width_of_environment / 2, "y"::1, "z"::10];
			mass <- 0.0;
			color <- colorwood;
		}
		//upper wall
		create wall {
			location <- { width_of_environment / 2, 0, 0 };
			shape <- rectangle({ width_of_environment, 2 });
			collisionBound <- ["shape"::"floor", "x"::width_of_environment / 2, "y"::1, "z"::10];
			mass <- 0.0;
			color <- colorwood;
		}
		//left wall
		create wall {
			location <- { 0, height_of_environment / 2, 0 };
			shape <- rectangle({ 2, height_of_environment });
			collisionBound <- ["shape"::"floor", "x"::1, "y"::height_of_environment / 2, "z"::10];
			mass <- 0.0;
			color <- colorwood;
		}
		//right wall
		create wall {
			location <- { width_of_environment, height_of_environment / 2, 0 };
			shape <- rectangle({ 2, height_of_environment });
			collisionBound <- ["shape"::"floor", "x"::1, "y"::height_of_environment / 2, "z"::10];
			mass <- 0.0;
			color <- colorwood;
		}

		create physic_world {
			gravity <- true;
			world2 <- self;
		}

		ask world2 {
			registeredAgents <- (ball as list) + (ground as list) + (wall as list);
		}

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
		draw shape color: rgb([10, 114, 63]) border: rgb([10, 114, 63]);
	}

}

species wall skills: [physical3D] {
	rgb color;
	aspect default {
		draw shape color: color depth: 10;
	}

}

species ball skills: [physical3D] {
	rgb color;
	int size <- size_of_agents;
	float speed <- speed_of_agents;
	int heading <- rnd(359);
	aspect default {
		draw circle(10) color: color depth: 1;
	}

	aspect sphere {
		draw sphere(5);
	}

}

experiment pool type: gui {
	output {
		display Circle refresh_every: 1 type: opengl tesselation: true ambient_light: 100 background: rgb('black') draw_env: false { species ground aspect: default;
		species wall aspect: default;
		species ball aspect: sphere;
		}
	}

}

