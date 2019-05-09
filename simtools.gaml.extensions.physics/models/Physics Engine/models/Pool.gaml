/**
* Name: Pool using Physic Engine
* Author: Arnaud Grignard
* Description: This is a model that shows how the physics engine works using a pool with balls, collided by a white ball. 
* The balls use the skill Physical3D.
* Tags: physics_engine, skill, 3d, spatial_computation, obstacle
*/
model pool3D

global {
	//Parameters for the environment
	int width_of_environment <- 200;
	int height_of_environment <- 300;
	
	//Parameters for the balls
	float speed_of_agents <- 2.0;
	int size_of_agents <- 10;
	
	rgb colorwood <- rgb([178, 112, 62]);
	
	//Physical Engine
	physic_world world2;
	geometry shape <- rectangle(width_of_environment, height_of_environment);
	init {
		
		//Creation of the white ball
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
		
		//Create the other balls for the pool
		create ball number: 15 {
			location <- { initX + (i - deltaI) * 10, initY, 5.0 };
			heading <- 90;
			speed <- 0.0;
			mass <- 3.0;
			collisionBound <- ["shape"::"sphere", "radius"::5];
			i <- i + 1;
			
			if ((i mod 2) = 0) {
	
				color <- #red;
			} else {
				color <- #yellow;
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
		
		//Create the physic engine with gravity
		create physic_world {
			gravity <- 9.81;
			world2 <- self;
		}

		//Add the agents inside the registered agents in the physic engine
		ask world2 {
			agents <- (ball as list) + (ground as list) + (wall as list);
		}

	}

	//Reflex to compute the forces at each step
	reflex computeForces {
		ask world2 {
			do compute_forces step: 1.0;
		}

	}

}

//Species corresponding to the physics engine, derivated from the built-in species Physical3DWorld
species physic_world parent: physical_world ;

//Species representing the ground agents used for the computation of the forces, using the skill physical3D
species ground skills: [physics] {
	aspect default {
		draw shape color: rgb([10, 114, 63]) border: rgb([10, 114, 63]);
	}

}

//Species representing the wall agents of the pool using the skill physical3D
species wall skills: [physics] {
	rgb color;
	aspect default {
		draw shape color: color depth: 10;
	}

}

//Species representing the ball agents of the pool using the skill physical3D
species ball skills: [physics] {
	rgb color<-#white;
	int size <- size_of_agents;
	float speed <- speed_of_agents;
	int heading <- rnd(359);

	aspect sphere {
		draw sphere(5) color:color;
	}

}

experiment pool type: gui {
	output {
		display Circle type: opengl  background: #white draw_env: false synchronized: true { species ground aspect: default;
		species wall aspect: default;
		species ball aspect: sphere;
		}
	}

}

