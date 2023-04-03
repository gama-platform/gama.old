/**
* Name: Corridor Multi-Level Architecture
* Author: Vo Duc An; Ngoc Anh; JD Zucker; A. Drogoul
* Description: This model shows how to use multi-level architecture. A corridor can capture pedestrians going from left to right side if 
*	they are inside the corridor. This will result in changing their species from pedestrian to captured_pedestrian which will not be 
*	displayed. Once they pass enought time to consider they reach the exit of the corridor, they will be released by the corridor agent 
*	as pedestrians, letting them been displayed and going to their target. 
* Tags: multi_level, agent_movement
*/
model corridor

global {
//Capture pedestrians parameter to define if wall will capture pedestrians
	bool capture_pedestrians <- false;
	int environment_width init: 8000;
	int environment_height init: 3000;
	geometry shape <- rectangle(environment_width, environment_height);

	//Pedestrians parameters
	rgb pedestrian_color <- #green;
	float pedestrian_speed <- 10.0;

	//Wall parameters
	float corridor_width <- environment_width / 1.5;
	int corridor_wall_height <- 800;
	geometry corridor_wall_0_shape <- rectangle({corridor_width, corridor_wall_height}) at_location {environment_width / 2, corridor_wall_height / 2};
	geometry corridor_wall_1_shape <- rectangle({corridor_width, corridor_wall_height}) at_location {environment_width / 2, environment_height - (corridor_wall_height / 2)};
	

	//Corridor parameters
	float corridor_left_bounds <- (location.x - (corridor_width / 2));
	float corridor_right_bounds <- (location.x + (corridor_width / 2));

	init {
		create corridor;
	}

	reflex change_color when: every(200 #cycle) {
		pedestrian_color <- rnd_color(255);
	}

	reflex generate_pedestrians when: every(4 #cycle) {
		create pedestrian number: 30 with: [color::pedestrian_color] {
			do init_location({0, rnd(environment_height)});
		}
	}
}

//Species pedestrian which will move from one side of the experiment to another and destroy itself once the other side is reached
species pedestrian skills: [moving] topology: (topology(shape - (corridor_wall_0_shape + corridor_wall_1_shape))) {
	point target_location;
	rgb color;

	action init_location (point loc) {
		location <- loc;
		target_location <- {environment_width, loc.y};
		speed <- rnd(pedestrian_speed - 5) + 5.0;
	}
	
	
	reflex change_speed when: every(rnd(200) #cycle) {
			speed <- rnd(pedestrian_speed - 5) + 5.0;
	}

	//Reflex to make the agent move to its target_location
	reflex move {
		point previous_location <- location;

		if (location.y < corridor_wall_height) and (location.x <= (environment_width / 2)) {
			do move heading: self towards {(environment_width / 2) - (corridor_width / 2), corridor_wall_height};
		} else if (location.y > environment_height - corridor_wall_height) and (location.x <= (environment_width / 2)) {
			do move heading: self towards {(environment_width / 2) - (corridor_width / 2), environment_height - corridor_wall_height};
		} else {
			do move heading: self towards target_location;
		}
		if (location.x = previous_location.x) { // No move detected
			do move heading: self towards {environment_width, world.shape.location.y};
		}
	}

	reflex arrived when: location.x >= target_location.x {
		do die;
	}

}

//Species which represents the corridor
species corridor {
	geometry shape <- ((rectangle({corridor_width, environment_height})) at_location world.location) - (corridor_wall_0_shape + corridor_wall_1_shape);

	//Subspecies for the multi-level architectures : captured pedestrians in this case
	species captured_pedestrian parent: pedestrian schedules: [] {
		float release_time;
	}

	//Reflex to capture pedestrians if the parameter is checked
	reflex aggregate when: capture_pedestrians {
	//If we have pedestrians inside the corridor, we capture them
	//We update the time during which a pedestrian is captured according to the time the pedestrian
	// should need to pass through the corridor if it wasn't captured
		capture (pedestrian where (p: p.location.x between (corridor_left_bounds, corridor_right_bounds))) as: captured_pedestrian {
			release_time <- time + ((corridor_width - (location.x - ((environment_width / 2) - (corridor_width / 2)))) / (pedestrian_speed - 2.5));
		} }

		//Reflex to release pedestrians which have already passed enough time in the corridor
	// which means if they weren't captured by the corridor, they would have finish passing through it
	reflex disaggregate {
		list tobe_released_pedestrians <- captured_pedestrian where (time >= each.release_time);
		if !(empty(tobe_released_pedestrians)) {
			release tobe_released_pedestrians as: pedestrian in: world {
				location <- {((environment_width / 2) + (corridor_width / 2)), (location).y};
			}

		}
	}
}

experiment "Corridor" type: gui autorun: true {
	point button_location;
	bool button_hover;
	geometry corridor_wall_0_display <- rectangle({corridor_width-30, corridor_wall_height-30}) at_location {environment_width / 2, corridor_wall_height / 2};
	geometry corridor_wall_1_display <- rectangle({corridor_width-30, corridor_wall_height-30}) at_location {environment_width / 2, environment_height - (corridor_wall_height / 2)};
	
	init {
		button_location <- {simulation.corridor_left_bounds + 100, 100};  
	}
	output {
		display defaut_display type: 2d background: #black fullscreen: true toolbar: false {
			graphics back {
				draw shape color: #black wireframe: false;
				draw corridor_wall_0_display color: #gray wireframe: true;
				draw corridor_wall_1_display color: #gray wireframe: true ;
			}

			species corridor {
				draw shape color: #black;
			}
			
			agents "Captured" value: list(corridor(0).captured_pedestrian) transparency: 0.5 {
				draw square(30) wireframe: false color: #white;
			}

			species pedestrian {
				draw square(20) wireframe: false color: color;
			}

			graphics front {
				draw (capture_pedestrians ? "Capturing":"Not capturing") anchor: #left_center at: {corridor_left_bounds + 200, 100} color: !capture_pedestrians ? #darkred : #darkgreen font: font("Helvetica", 20 * #zoom, 0);
				draw ("Captured: " + length(corridor(0).captured_pedestrian)) anchor: #left_center at: {corridor_left_bounds + 200, 250} color: #white font: font("Helvetica", 20 * #zoom, 0);
				draw ("Pedestrians: " + length(pedestrian)) anchor: #left_center at: {corridor_left_bounds + 200, 400} color: #white font: font("Helvetica", 20 * #zoom, 0);
				draw ("Step duration (ms): " + (duration copy_between (0, 4))) anchor: #left_center at: {corridor_left_bounds + 200, 550} color: #white font: font("Helvetica", 20 * #zoom, 0);
			}

			graphics button {
				draw circle(50) color: #darkgray at: button_location;
				draw circle(40) color: !capture_pedestrians ? (button_hover ? #yellow : #red) : (button_hover ? #lightgreen : #darkgreen) at:  button_location;
			}

			event #mouse_down {
				if (button_location distance_to #user_location <= 50) {
					capture_pedestrians <- !capture_pedestrians;
				}
			}
			event #mouse_move {
				button_hover <- (button_location distance_to #user_location <= 50);
			}
		}
	}

}
