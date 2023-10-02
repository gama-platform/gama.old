/**
* Name: Restitution
* A model to demonstrate the role of the restitution in the collision of objects in the box2D library, which simulates physical interactions in 2 dimensions
* Author: Alexis Drogoul - 2023
* Tags: 2D, physics
*/
model Box2D

global parent: physical_world {
	
	float seed <- machine_time;
	string library <- "box2D";
	// The definition of the step plays a crucial role in the dynamics. If the physics engine can kick in at a high frequency, then the simulation is more accurate (but also slower). 
	// The outcome of a model can be completely changed by the step. 
	float step <- 1.0 / 100;
	float wall_restitution <- 0.9 min: 0.0 max: 2.0;
	float ball_restitution <- 0.5 min: 0.0 max: 1.0;
	geometry shape <- box(200, 200, 0.001);
	float friction <- 0.0;
	float restitution <- 0.0;
	bool accurate_collision_detection <- true; // expensive but much better
	int max_substeps <- 1;
	point ball_contact <- nil;
	int ball_timer <- 0;
	point wall_contact <- nil;
	int wall_timer <- 0;
	bool disturb <- true;

	init {
		do register([self]);
		geometry box <- box(203, 3, 10);
		create wall from: [box at_location ({100, 0}), box rotated_by 90 at_location ({0, 100}), box at_location ({100, 200}), box rotated_by 90 at_location ({200, 100})];
		loop x from: 5 to: 195 step: 10 {
			loop y from: 5 to: 195 step: 10 {
				create ball from: [circle(3) at_location {x, y}] {
					initial_location <- location;
				}
			}

		}

	}

	reflex r1 when: ball_timer > 0 {
		ball_timer <- ball_timer - 1;
		if (ball_timer = 0) {
			ball_contact <- nil;
		}

	}

	reflex r2 when: wall_timer > 0 {
		wall_timer <- wall_timer - 1;
		if (wall_timer = 0) {
			wall_contact <- nil;
		}

	}

	reflex r3 when: every(500 #cycles) and disturb {
		ask one_of(ball) {
			do apply impulse: {(rnd(500) - 250), (rnd(500) - 250)};
		}

	}

}

species wall skills: [static_body] {
	float restitution <- wall_restitution;
	float friction <- 0.1;
}

species ball skills: [dynamic_body, moving] {
	point initial_location;
	float contact_damping <- 0.1;
	float damping <- 0.1;
	float angular_damping <- 0.1;
	float mass <- 5.0;
	float restitution <- ball_restitution;
	float friction <- 0.1;
	rgb color <- rnd_color(255);
		
	//bool going_back <- false update: (velocity.x < 2) and (velocity.y < 2) and distance_to(location,initial_location) > 1 ;

	action contact_added_with (agent other) {
		if (other is ball) {
			ball_contact <- location;
			ball_timer <- 20;
		} else if (other is wall) {
			wall_contact <- location;
			wall_timer <- 20;
		}

	}

	
	reflex go_back when:  (velocity.x < 2) and (velocity.y < 2) {
		do apply impulse: (initial_location - location)  /2 ;
	}

}

experiment "Test Restitution !" type: gui {
	


	image_file bang <- image_file("../images/bang.png");
	image_file bam <- image_file("../images/bam.png");
	text "This experiment uses the Box2D library to display particles that are disturbed randomly and try to get back to their original when this happens. Try stopping the disturbance or increasing or decreasing the restitution to see what happens";
	parameter "Disturb" var: disturb labels: ["Yes","No"];

	parameter "Restitution of the walls" var: wall_restitution {
			ask wall {
				restitution <- wall_restitution;
			}

	}

	parameter "Restitution of the balls" var: ball_restitution {
		ask simulations {
			ask ball {
				restitution <- ball_restitution;
			}

		}

	}

	output {
		layout #split;
		display "Restitution" type: 3d antialias: true {
			graphics "Bang" {
				if (ball_contact != nil) {
					draw bang at: ball_contact size: {20, 20};
				}
				if (wall_contact != nil) {
					draw bam at: wall_contact size: {20, 20};
				}

			}

			species wall refresh: false {
				draw aabb color: #cadetblue;
			}

			species ball {
				draw circle(3) color: color;
				draw line(location, location + velocity) color: #black end_arrow: 1 width: 1;
			}

		}

	}

}