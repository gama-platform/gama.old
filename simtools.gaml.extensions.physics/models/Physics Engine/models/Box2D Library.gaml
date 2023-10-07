/**
* Name: Restitution
* A model to demonstrate the role of the restitution in the collision of objects in the box2D library, which simulates physical interactions in 2 dimensions
* Author: Alexis Drogoul - 2023
* Tags: 2D, physics
*/
model Box2D

global parent: physical_world {
	
	int size <- 250;
	
	float seed <- machine_time;
	string library <- "box2D";
	// The definition of the step plays a crucial role in the dynamics. If the physics engine can kick in at a high frequency, then the simulation is more accurate (but also slower). 
	// The outcome of a model can be completely changed by the step. 
	float step <- 1.0 / 100;
	float wall_restitution <- 1.0 min: 0.0 max: 2.0;
	float ball_restitution <- 0.9 min: 0.0 max: 1.0;
	geometry shape <- box(size, size, 0.001);
	float friction <- 0.0;
	float restitution <- 0.0;
	bool accurate_collision_detection <- true; // expensive but much better
	int max_substeps <- 1;
	point ball_contact <- nil;
	int ball_timer <- 0;
	point wall_contact <- nil;
	int wall_timer <- 0;
	bool disturb <- true;
	list<ball> movers;

	init {
		do register([self]);

		geometry box <- box(size+3, 3, 10);
		create wall from: [box at_location ({size/2, 0}), box rotated_by 90 at_location ({0, size/2}), box at_location ({size/2, size}), box rotated_by 90 at_location ({size, size/2})];
		list<point> starting_places <- [{5,5}, {5,size-5},{size-5,5}, {size-5,size-5}];
		create ball from: starting_places collect (circle(4) at_location each) with: [mass::10, color::#cadetblue, speed::30] returns: balls;
		movers <-balls;
		loop x from: 5 to: size-5 step: 10 {
			loop y from: 5 to: size-5 step: 10 {
				if (x = 5) or (x = size-5) {
					if (y = 5) or (y = size-5) {
						continue;
					}
				} 
				float n <- rnd(1.0, 4.5);
				create ball with: [shape::circle(n) at_location {x,y},mass::n, color::brewer_colors("Set3")[int(n)], speed::n*2] {
					initial_location <- location;
				}
			}

		}
		

	}
	
	reflex when: every(1000#cycle){
		ask movers{
			float s <- speed * 2;
			velocity <- velocity + {(rnd(s) * rnd(-1.0,1.0)), (rnd(s) * rnd(-1.0,1.0))};
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
	float angular_damping <- 0.0;
	float restitution <- ball_restitution;
	float friction <- 0.1;
	rgb color ;//<- one_of(brewer_colors("Set3"));


	reflex go_back when: every(10#cycle) and (abs(velocity.x) between(0.0,1.0)) and (abs(velocity.y) between(0.0,1.0)) {
		do goto target: initial_location;
	}

}

experiment "Disturbance" type: gui {
	


	image_file bang <- image_file("../images/bang.png");
	image_file bam <- image_file("../images/bam.png");
	text "This experiment uses the Box2D library to display particles that are disturbed randomly and try to get back to their original location when this happens. Try stopping the disturbance or increasing or decreasing the restitution to see what happens. Agents are provided also with the moving skill, and it is a good example of mixing a physics-based with a behavior-based dynamics";

	output {
		layout #split;
		display "Restitution" type: 3d antialias: true axes: false {

			species ball {
				draw shape color: color;
				draw line(location, location + velocity) color: #black end_arrow: 1 width: 1;
			}

		}

	}

}