/**
* Name: Stairs
* This model demonstrates the use of the physics engine to build a simple model where particles are 
* Author: Alexis Drogoul - 2021
* Tags: physics
*/
model Stairs

global parent: physical_world {
	bool use_native <- true;
	list<string> textures <- ["tennis", "foot", "basket", "ball"];
	list<float> sizes <- [0.07, 0.22, 0.25, 0.5];
	list<float> masses <- [0.1, 0.45, 0.62, 0.01];
	list<float> restitutions <- [0.7, 0.7, 0.8, 1.0];
	list<float> frictions <- [0.1, 0.5, 0.5, 0.0];
	int tennis <- 0;
	int basket <- 1;
	int foot <- 2;
	float step <- 0.001;
	int max_substeps <- 0;
	int number_of_steps <- 8;
	float max_height <- 20.0;
	float dimension <- 40.0;
	geometry shape <- box(dimension * 3, dimension * 3, 1);
	float friction <- 0.7;
	float restitution <- 0.5;

	init {
		do register([self]);
		float step_width <- dimension / (number_of_steps + 2);
		float step_diff <- max_height / (number_of_steps + 1);
		float current_y <- dimension / 2;
		float current_height <- max_height;
		loop i from: 0 to: number_of_steps {
			create steps from: [box(dimension / 2, step_width, current_height) at_location {dimension * 3 / 2, current_y}];
			current_y <- current_y + step_width;
			current_height <- current_height - step_diff;
		}

	}

	reflex when: every(500 #cycle) {
		create ball with: [type::one_of(0, 1, 2, 3)] {
			location <- {dimension * 3 / 2 + rnd(4) - 2, dimension + rnd(4) - 2, max_height + dimension + rnd(4) - 2};
		}

	}

}

species steps skills: [static_body] {
	float restitution <- 1.0;
	float friction <- 0.05;

	aspect default {
		draw shape color: rgb(132, 172, 136, 255);
	}

}

species ball skills: [dynamic_body] {
	int type;
	float radius <- sizes[type] * 10;
	float mass <- masses[type];
	geometry shape <- sphere(radius);
	float restitution <- restitutions[type];
	float friction <- frictions[type];
	float damping <- 0.1;

	init {
		angular_velocity <- {rnd(2) - 1, rnd(2) - 1, rnd(2) - 1};
	}

	//When a ball agent falls from the edges of the world, it is removed from the simulation (and the physical world as well).		
	reflex manage_location when: location.z < -20 {
		do die;
	}

	aspect default {
		draw shape rotate: (rotation) texture: "../images/" + textures[type] + ".jpg";
		//draw aabb wireframe: true color: #blue;
	}

}

experiment Stairs type: gui {
	output {
		display "Climb" type: 3d axes: false {
	
			camera 'default' location: {-47.816,165.8396,88.7059} target: {60.0,60.0,0.0};
			graphics ground refresh:false{
				draw shape color: rgb(132, 172, 136, 255);
				//draw aabb wireframe: true color: #blue;
			}

			species steps refresh: false;
			species ball;
			event #mouse_down {
				ask ball {
					do apply impulse: {rnd(2)-1, rnd(2)-1, 10};
				}
			}
		}

	}

}