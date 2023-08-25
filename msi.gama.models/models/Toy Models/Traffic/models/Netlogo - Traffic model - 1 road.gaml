/***
* Name: NetlogoTrafficmodel
* Author: Benoit Gaudou (for the reimplementation), Wilensky, U. (for the original model)
* Description: This model is a reimplementation of the Netlogo model "Traffic model"
*     Wilensky, U. (1997). NetLogo Traffic Basic model. http://ccl.northwestern.edu/netlogo/models/TrafficBasic. 
*     Center for Connected Learning and Computer-Based Modeling, Northwestern University, Evanston, IL.
*  It has been implemented with the Netlogo platform:
*     Wilensky, U. (1999). NetLogo. http://ccl.northwestern.edu/netlogo/. 
*     Center for Connected Learning and Computer-Based Modeling, Northwestern University, Evanston, IL.
* Tags: traffic, transport, congestion, netlogo
***/

model NetlogoTrafficmodel

global torus: true {
	int pavement_width <- 50;
	int pavement_height <- 9;
	
	geometry shape <- rectangle(pavement_width * 2, pavement_height * 2);	
	image_file voit_image_file <- image_file("../includes/voit.png");
	image_file voit_red_image_file <- image_file("../includes/voit_red.png");

	int y_road <- 4;
	car sample_car;
	
	int nb_cars <- 25 parameter: true;
	float acceleration <- 0.0045 min: 0.0 max: 0.01 parameter: true;
	float deceleration <- 0.026 min: 0.0 max: 0.1 parameter: true;	
	
	float max_speed <- 50.0;
	
	init {
		list<pavement> road <- pavement where(each.grid_y = y_road);
		create car number: nb_cars {
			pavement free_pavement <- one_of(road where(empty(car inside self)));
			if(free_pavement != nil) {
				my_pavement <- free_pavement;
				location <- my_pavement.location;
			} else {
				do die;
			}
			heading <- 0.0;
		}
		
		sample_car <- one_of(car);
		ask sample_car {
			color <- #red;
			icon <- voit_red_image_file;
		}
	}
}

grid pavement height: pavement_height width: pavement_width {
	init {
		if( (grid_y >= 3) and (grid_y <=5) ) {
			color <- #white;
		} else {
			color <- #black;
		}
	}
}

species car skills: [moving] {
	float speed_limit;
	float speed_min;
	rgb color;
	image_file icon;
	pavement my_pavement;
	
	init {
		color <- #blue;
		icon <- voit_image_file;
		speed <- 0.1 +rnd(0.9);
		speed_limit <- 1.0;
		speed_min <- 0.0;
	}
	
	reflex patch_ahead {
		my_pavement <- pavement first_with(each overlaps self);
		pavement next_pavement <- pavement first_with( 
										(each.grid_y = my_pavement.grid_y) and 
										(each.grid_x = (my_pavement.grid_x + signum(cos(heading))) mod pavement_width)
		);
		car car_ahead <- first(car inside next_pavement);
		if(car_ahead != nil) {
			do slow_down(car_ahead);
		} else {
			do speed_up;
		}
		
		do move heading: heading;
	}

	action slow_down(car car_ahead) {
		speed <- max(speed_min, car_ahead.speed - deceleration) ;
	}
	
	action speed_up {
		speed <- min(speed + acceleration, speed_limit);
	}

	aspect rect {
		draw rectangle(1.5,1) rotated_by heading color: color border: #black;
	}
	
	aspect icon {
		draw icon at: location size: 3 rotate: heading ;
	}	
}

experiment NetlogoTrafficmodel type: gui {
	float minimum_cycle_duration <- 0.01;
	
	output {
		layout #vertical;
		
		display road type: 2d antialias:false{
			grid pavement;
			species car aspect: icon;
			
		}
		
		display sp  type: 2d {
			chart "speed" type: series {
				data "red car" value: sample_car.speed * max_speed color: #red;
				data "min speed" value: car min_of(each.speed) * max_speed color: #blue;
				data "max speed" value: car max_of(each.speed) * max_speed color: #green;
			}
		}
	}
}
