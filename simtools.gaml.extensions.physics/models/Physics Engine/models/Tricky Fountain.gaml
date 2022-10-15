/**
* Name: Tricky fountain
* Author: Arnaud Grignard - Alexis Drogoul 2021
* Description: This is a model that shows how the physics engine works using a tank, with a floor and 4 walls, and balls of water
* falling into it. The model is exploiting the viewpoint of the user (thanks to the camera_location and camera_target) to give the illusion
* of a fake gravity (in a completely unrealistic way, just for the demo !). 
* Tags: physics_engine, skill, 3d, spatial_computation
*/
model Tank

/**
 * The model is inheriting from 'physical_world' a special model species that provides access to the physics engine -- and the possibility
 * to manage physical agents. In this model, the world itself is not a physical agent
 */
global parent: physical_world {
	bool use_native_library <- false;
	//Dimensions of the environment
	int dim <- 100;
	//Step (in #sec) passed to the physics engine. The same step is used for the simulation and the physics engine. The accuracy and synchronization
	//between the two can be controlled by max_substeps 
	float step <- 0.1; 
	//When this variable is true (the default), all the agents that inherit from dynamic_body or static_body are automatically registered as
	//physical agents of this world. Otherwise, they have to be registered manually (using the 'register' action)
	bool automated_registration <- true;
	//The shape of the environment. Since it is not part of the physical world, could be anything
	geometry shape <- rectangle(dim, dim);
	
	

	init {
		//The floor is a large flat box in the middle of the world.
		create pillarAndFloor {
			shape <- box({dim * 2, dim * 2, 1}) at_location {dim / 2, dim / 2, -5};
		}
		//On which we create the bottom of the fountain 
		create wall {
			shape <- box({dim, dim, 10}) at_location {dim / 2, dim / 2, -5};
		}
		float depth <- dim/6;
		//We then create the walls of the fountain itself as four vertical flat boxes
		create wall from: [
			box({dim, 2, depth}) at_location {dim / 2, dim, 0}, 
			box({dim, 2, depth}) at_location {dim / 2, 0, 0}, 
			box({2, dim, depth}) at_location {0, dim / 2, 0}, 
			box({2, dim, depth}) at_location {dim, dim / 2, 0}];
		//And finally, the pillar, a vertical cylinder
		create pillarAndFloor {
			shape <- cylinder(4, dim - 10) at_location {dim / 2, dim / 2, 0};
		}

	}

	//Every 5 steps the world creates 5 water agents at the same place. No need to provide them with a velocity or
	//an impulse: the immediate resolution of the physical forces make them spring 
	reflex flow when: every(5 #cycle) {
		create water number: 5 {
				location <- {dim/2, dim/2, dim};
		}
	}
	
	
	//Here comes the trick of the model. The orientation of the view is estimated (very roughly) by the position and
	//target of the camera. Whenever the user changes it (by rotating or tilting the view, for the moment only around the x-axis),  
	//the gravity is adjusted in order for it to remain oriented towards the 'bottom' of the screen. The full control (which would involve Euler angles) 
	//is of course not implemented here, but left as a future exercise ! 
	reflex compute_gravity {
		point p <- #camera_location - #camera_target;
		p <- {p.x = 0 ? 1 : p.x, p.y = 0 ? -1 : -p.y, p.z = 0 ? 1 : p.z};
		point g <- {0, -1 / (p.y) * signum(p.z), -2 / abs(p.z)};
		gravity <- g / norm(g) * 9.81;
	}
}


/**
 * Species that represent the walls of the tank. They are static physical objects with no behavior
 */

species wall skills: [static_body];

species pillarAndFloor skills: [static_body];


/**
 * Species that represents the balls falling from the fountain, using the skill dynamic_body
 */
species water skills: [dynamic_body] {
	//The shape of water drops is a sphere between 1 and 2 of radius
	geometry shape <- sphere(rnd(2.0) + 1.0);
	//They are provided with a mass (otherwise they would 'float')
	float mass <- 3.0;
	rgb color <- one_of(brewer_colors("Blues")); 
	//This provides some 'bounciness' when they hit other agents
	float restitution <- 0.5;
	//This provides some stability
	float angular_damping <- 0.9;
	float contact_damping <- 0.9;
	
	//When water drops fall from the ground, they are eliminated (from the simulation and the physical world)
	reflex when: location.z < -20 {
		do die;
	}
} 

experiment "3D View" type: gui {
	output { 
		//The initial orientation of the display makes water drops 'fall' slightly towards the user... calling (hopefully) from immediate action!
		display Flow type: 3d background: #black axes: false  {
			camera #default location: {50,300,150} target: {dim/2,dim/2,10};
			species water {draw shape color: color;}
			species wall refresh: false {draw shape texture: image_file("../images/marble2.jpg");}
			species pillarAndFloor refresh: false {draw shape texture: image_file("../images/marble.jpg");}
		}

	}

}

