/**
* Name: Restitution
* A model to demonstrate the role of the restitution in the collision of objects and some other techniques (display of contacts between objects, creation of comparisons
* between native and Java Bullet libraries...).
* Author: Alexis Drogoul - 2021
* Tags: 3D, physics
*/


model Restitution

global parent: physical_world {
	string library_name <- use_native ? "Native":"Java";
	float wall_restitution <- 1.0  min: 0.0 max: 1.0 ;
	float ball_restitution <- 0.8  min: 0.0 max: 1.0 ;
	point ball_impulse <- {100,100,0} ;
	geometry shape <- box(100,100,0.001);
	float friction <- 0.0;
	float restitution <- 0.0;
	// The definition of the step plays a crucial role in the dynamics. If the physics engine can kick in at a high frequency, then the simulation is more accurate (but also slower). 
	// The outcome of a model can be completely changed by the step. See the "Steps.gaml" model for instance
	float step <- 1.0/60;
	bool accurate_collision_detection <- true;
	point ball_contact <- nil;
	int ball_timer <- 0;
	point wall_contact <- nil;
	int wall_timer <- 0;
	
	init {
		do register([self]);
		geometry box <- box(103,3,10);
		create wall from: [box at_location({50,0}), box rotated_by 90 at_location({0,50}), box at_location({50,100}), box rotated_by 90 at_location({100, 50})];
		create ball from: [sphere(6) at_location {50,50}, sphere(6) at_location {20,20}];
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
}

species wall skills: [static_body] {
	float restitution <- wall_restitution;
	float friction <- 0.0; 
}

species ball skills: [dynamic_body] {
	float contact_damping <- 0.0;
	float damping <- 0.0;
	float angular_damping <- 0.1;
	float mass <- 1.0;
	float restitution <- ball_restitution;
	float friction <- 0.0;
	
	action contact_added_with(agent other) {
		if (other is ball) {
			ball_contact <- location;
			ball_timer  <- 20;
		} else if (other is wall) {
			wall_contact <- location;
			wall_timer <- 20;
		}
	}

	reflex manage_location when: location.z < -20 {
		do die;
	}
	
	reflex remove when: cycle = 10 {
		location <- {rnd(100), rnd(100)};
	}
	
}


experiment "Test it !" type: gui {
	
	
	image_file bang <- image_file("../images/bang.png");
	image_file bam <- image_file("../images/bam.png");
	
	// Ensure that the simulation does not go too fast
	float minimum_cycle_duration <- 1.0/120;
	
	font custom <- font("Helvetica", 12, #bold);
	
	parameter "Impulse" var: ball_impulse;
	
	user_command "  Reset balls" color: #darkgray {
				ask simulations {
					ask ball { 
						do die;
					}
					create ball from: [sphere(5) at_location {50,50,5}, sphere(5) at_location {20,20,5}];
				}
	}
	
	action _init_ {
		bool prev0 <- gama.pref_experiment_expand_params;
		bool prev1 <- gama.pref_append_simulation_name;
 		gama.pref_append_simulation_name <- true;
		gama.pref_experiment_expand_params <- true; 
		bool native <- user_confirm("Native", "Compare using native library ? ");
		create simulation with: [seed:: 1.0, use_native :: native, step::1/60];
		create simulation with: [seed:: 1.0, use_native :: native, step::1/30];
		create simulation with: [seed:: 1.0, use_native :: native, step::1/15];
		create simulation with: [seed:: 1.0, use_native :: native, step::1/10];
		gama.pref_experiment_expand_params <- prev0;
		gama.pref_append_simulation_name <- prev1;
	}



	output { 
		layout #split;
		display "Restitution" type: 3d antialias: true {
			graphics "Title"  refresh: false {
				draw "Step " + step + " (click to move the balls)" font: custom color: #cadetblue at: {5, 0, 20} depth: 5 precision: 0.001;
				draw shape color: #khaki;
			}
			graphics "Bang" {
				if (ball_contact != nil) {
					draw  bang at: ball_contact size:{20,20};
				}
				if (wall_contact != nil) {
					draw  bam at: wall_contact size:{20,20};
				}
			}
			species wall refresh: false {draw shape color: #cadetblue;}
			species ball {
				draw shape texture: image_file("../images/ball.jpg") rotate: rotation color: #darkseagreen;
			}

			event "mouse_down" {
				point target <- #user_location;
				// When the user hits the mouse, we apply an impulse to the while ball, in the direction of the target. 'velocity' could also be used here
				ask simulations {
					ask ball {
						point direction <- (target - location) / 100;
						do apply impulse: {ball_impulse.x * direction.x, ball_impulse.y * direction.y};
						angular_velocity <- {rnd(10), rnd(10), rnd(10)};
					}

				}

			}
		}
	}
}