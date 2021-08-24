/**
* Name: Pool using Physic Engine
* Author: Arnaud Grignard (2012) --revised by Alexis Drogoul (2021)
* Description: This is a model that allows the user to play a (simplistic) game of pool in order to show how the physics engine works. It also
* demonstrates the effect of different physical properties (friction, restitution, etc.) 
*
* Tags: physics_engine, skill, 3d, spatial_computation, obstacle
*/
model pool3D

/**
 * The model is inheriting from 'physical_world' a special model species that provides access to the physics engine -- and the possibility
 * to manage physical agents. In this model, the world itself is not a physical agent but all the other agents (balls and walls) are 
 * automatically registered (thanks to the default value of 'true' of the variable 'automated_registration').
 */
global parent: physical_world {
	// The dynamics of the agents is a bit different if we use the native (3.0.x) or Java version (2.8.x) of Bullet
	bool use_native_library <- false;
	string library <- "box2D";
	//All the physical characteristics of the balls can be accessed here and modified at will by the user
	float ball_damping <- 0.05 min: 0.0 max: 1.0 on_change: {ask ball {damping<-ball_damping;}};
	float ball_restitution <- 0.8  min: 0.0 max: 1.0 on_change: {ask ball {restitution<-ball_restitution;}};
	float ball_friction <- 0.2  min: 0.0 max: 1.0 on_change: {ask ball {friction<-ball_friction;}};
	float wall_restitution <- 0.7  min: 0.0 max: 1.0 on_change: {ask wall {restitution<-wall_restitution;}};
	float wall_friction <- 0.2  min: 0.0 max: 1.0 on_change: {ask wall {friction<-wall_friction;}};
	float ground_friction <- 0.6  min: 0.0 max: 1.0 on_change: {ask ground {friction<-ground_friction;}};
	float strength <- 120.0  min: 0.0 max: 200.0;
	int width <- 200;
	int height <- 300;
	//Given that very few agents inhabit this world, the step is really small, so as to prevent the physical world to go too fast. 
	//The simulation itself is aligned with this number (see experiment.minimum_cycle_duration)
	float step <-1.0/120;
	//Gives access (or not) to an improved (but slower) detection collision algorithm
	bool better_collision_detection <- false;
	// Artificially high gravity to make sure that the balls stay on the ground
	point gravity <- {0,0,-20};


	//Physical Engine
	geometry shape <- rectangle(width, height);
	ball white;
	point target;

	init {

		float floor <- -4.0;
		float depth <- 4.0;
		create ground from: [
			box({width - 20, height - 24, depth}) at_location {width / 2, height / 2, floor}, 
			box({width - 20, 20, depth}) at_location {width / 2, 6, floor}, 
			box({width - 20, 20, depth}) at_location {width / 2, height - 6, floor}, 
			box({20, height / 2 - 18, depth}) at_location {6, height / 4 + 3, floor}, 
			box({20, height / 2 - 18, depth}) at_location {6, 3 * height / 4 - 3, floor}, 
			box({20, height / 2 - 18, depth}) at_location {width - 6, height / 4 + 3, floor}, 
			box({20, height / 2 - 18, depth}) at_location {width - 6, 3 * height / 4 - 3, floor}
		];
				
		float section <- 10.0;
		float z <- section + section/4;
		create wall from: [
			line([{0,height + section/2,z}, {width,height + section/2,z}], section/2), //down
			line([{0,-section/2,z}, {width,-section/2,z}], section/2), // up
			line([{-section/2,-section/2,z}, {-section/2,height + section/2, z}], section/2), // left
			line([{width+section/2,-section/2,z}, {width+section/2,height + section/2, z}], section/2) // right

		];
		
		create wall with: [inside::true] from: [
			box(width+3*section/2, section/2, section) at_location {width / 2, height + section/2, 0}, // down
			box(width+3*section/2, section/2, section) at_location {width / 2,  -section/2, 0}, // up
			box(section/2, height +  section, section) at_location {-section/2, height / 2, 0}, // left
			box(section/2, height + section, section) at_location {width + section/2, height / 2, 0} // right
		];

		
		do create_white_ball;

		int deltaI <- 0;
		int initX <- 75;
		int initY <- int(height / 8);
		int i <- 0;

		//Create the other balls for the pool
		create ball number: 15 {
			location <- {initX + (i - deltaI) * 10, initY, 0};
			i <- i + 1;
			color <- (i mod 2) = 0 ? #red :  #yellow;
			if (i in [5, 9, 12, 14]) {
				initX <- initX + 5;
				initY <- initY + 9;
				deltaI <- i;
			}

		}

	}
	
	action create_white_ball {
		create ball {
			location <- {width / 2, 4 * height / 5, 0};
			white <- self;
		}
	}

}

//Species representing the ground agents used for the computation of the forces, using the skill static_body
species ground skills: [static_body] {
	float friction <- ground_friction;
}

//Species representing the wall agents of the pool using the skill static_body
species wall skills: [static_body] {
	bool inside;
	float friction <- wall_friction;
	float restitution <- wall_restitution;	
	
	aspect default {
		if (inside) {
			draw shape color: (#darkgreen);
		} else {
			draw shape texture: "../images/wood.jpg";
		}
		
		draw aabb wireframe: true border: #lightblue;

	}
}

//Species representing the ball agents, provided with dynamic_body capabilities (a mass, a velocity, damping ...)
species ball skills: [dynamic_body] {
	rgb color <- #white;
	float mass <-2.0;
	geometry shape <- sphere(5);
	float friction <- ball_friction;
	float restitution <- ball_restitution;
	float damping <- ball_damping;
    float angular_damping <- 0.0;
	float contact_damping <- 0.0;
	
	// If any ball falls or goes away, it is destroyed, except the white ball, replaced on the table
	reflex manage_location when: location.z < -20 {
		if (self = white) {
			ask world {
				do create_white_ball;
			}
			target <- nil;
		}
		do die;
	}

	aspect default {
		draw shape color: color;
		draw aabb wireframe: true border: #lightblue;

	}

}

experiment "Play !" type: gui autorun: true {
	parameter "Ball Restitution" var: ball_restitution category: "Ball properties" ;
	parameter "Ball Damping (natural deceleration)" var: ball_damping category: "Ball properties" ;
	parameter "Ball Friction" var: ball_friction category: "Ball properties" ;
	parameter "Wall Restitution" var: wall_restitution category: "Wall properties" ;
	parameter "Wall Friction" var: wall_friction category: "Wall properties" ;
	parameter "Ground Friction" var: ground_friction category: "Ground properties";
	parameter "Strength" var: strength category: "Player properties";
	
	// Ensure that the simulation does not go too fast
	float minimum_cycle_duration <- 1.0/120;
	
	action _init_ {
		// A trick to make sure the parameters are expanded and visible when the simulation is launched.
		bool previous <- gama.pref_experiment_expand_params;
		gama.pref_experiment_expand_params <- true;
		create simulation;
		gama.pref_experiment_expand_params <- previous;
	}
	
	output {
		display Pool type: opengl  background: #white draw_env: false  camera_location: {100.0,400.0,300.0} camera_target: {width/2,height/2,-20.0} camera_orientation: {0.0,1.0,0.0} {
			graphics user {
				if (white != nil) and (target != nil) {
					draw line(white, target) color: #white end_arrow: 3;
				}
				if target = nil {
					draw "Choose a target" color: #white font: font("Helvetica", 24, #bold) at: location + {0, 0, 10} perspective: false anchor: #center;
				}
			}

			event "mouse_down"  {
				target <- #user_location;
				float divisor <- distance_to(target, white.location);
				point direction <- (target - white.location) /divisor;
				// When the user hits the mouse, we apply an impulse to the while ball, in the direction of the target. 'velocity' could also be used here
				ask white {
					do apply impulse: {strength * direction.x * 4, strength * direction.y * 4, 0};
				}
			}
			event "mouse_move" {
				target <- #user_location;	
			}
			species ground refresh: false {
				draw shape texture: image_file("../images/mat.jpg");
				draw aabb wireframe: true border: #lightblue;
				
			}
			species wall refresh: false;
			species ball;
		}

	}

}

