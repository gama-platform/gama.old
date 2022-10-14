/**
* Name: Perfect Gas
* Author: Arnaud Grignard - Alexis Drogoul 2021
* Description: This is a model that shows how the physics engine works with and without gravity. Particles, provided with an initial impulse,
* collide each other and the walls. Without gravity and friction, and with a perfect restitution, this movement can go on forever.
* The user can apply gravity or not, as well as remove one (or several) of the walls to alter this behavior.
* Tags: physics_engine, skill, 3d, spatial_computation
*/
model Gas

/**
 * The model is inheriting from 'physical_world' a special model species that provides access to the physics engine -- and the possibility
 * to manage physical agents. In this model, the world itself is not a physical agent
 */
global parent: physical_world {
	bool use_native <- true;
	int width parameter: 'Dimensions' init:500 ; 
	point gravity <- {0.0, 0.0, 0.0};
	bool withGravity <- false parameter: "Enable gravity" on_change: {gravity <- withGravity ? {0.0, 0.0, -9.81} : {0.0, 0.0, 0.0};};
	bool accurate_collision_detection <- false parameter: "Finer collision detection";
	bool show_walls <- true parameter: "Show walls";
	geometry shape <- rectangle(width, width);
	float step <- 0.01;
 
	init {
		//10000 particles are created, randomly located in a virtual box in the center of the world
		create particles number: 10000 {
			location <-  {rnd(width/2) + width/4,rnd(width/2) + width/4,rnd(width/2)  + width/4};
		}
		//We create walls, large boxes that prevent the particles from moving outside
		create wall from: [box(width, 3*width, width) at_location {-width/2, width/2, 0},box(width, 3*width, width) at_location {3*width/2, width/2, 0},box(width, width, width) at_location {width/2, -width/2, 0},box(width, width, width) at_location {width/2, 3*width/2,  0}];
		create wall from: [box({3*width,3*width, width}) at_location {width/2,width/2,width}, box({3*width,3*width, width}) at_location {width/2,width/2, -width}];
	}	

} 


/**
 * The walls are static physical bodies that offer no friction or restitution whatsoever.
 */
species wall skills: [static_body]{
	float friction <- 0.0;
	float restitution <- 1.0;
    aspect default {
		if (show_walls) {draw shape color: #black wireframe:true;}
	}
}
 	
/**
 * Particles are dynamic bodies that wander around. They provide a perfect restitution (i.e. bounciness) and no friction.
 */
species particles skills: [dynamic_body] {  
	geometry shape <- sphere(2);
	rgb color <- one_of(brewer_colors("Greens"));
	// No friction exerted on other particles
	float friction <- 0.0;
	// No damping, which woud slow down their move
	float damping <- 0.0;
	float angular_damping <- 0.0;
	// Perfect restitution ('bouncing')
	float restitution <- 1.0;	
	
	// An initial velocity is provided to the agents
	init {
		float amp <-100.0;
	    velocity <- {rnd(amp) - amp/2, rnd(amp) - amp/2, rnd(amp) - amp/2};
	}
	
	// A (commented out) callback action can be defined, for instance to exchange the colors of the particles when they collide
//	action contact_added_with(agent other) {
//		if (other is particles) {
//			color <- particles(other).color;
//		}
// 	}
}


experiment "Gas Chamber" type: gui {
	
	// Allows to play with the step of the simulation (and physics step)
	parameter "Physics resolution step (in sec)" var: step min: 0.0001 max: 1.0 ;
	// With this command, the user can destroy one of the walls at random
	user_command "Open one wall" color: #red {
		ask (one_of(wall)) {
			do die;
		}
	}
	output {
		display Cube type:3d background:#white axes:false camera: "1" {
			camera "1" location: {236.9814,1275.9964,799.6291} target: {250.0,250.0,150.0};
			camera "2" location: {236.9814,-1275.9964,799.6291} target: {250.0,250.0,150.0};
			species wall;
	    	species particles {draw shape color: color;}			
		}
	}
}

