/***
* Name: NetlogoTrafficmodel2
* Author: Benoit Gaudou
* Description: This model is a variation of the "Netlogo - Traffic model - 1 road.gaml" in order to be able 
*     to visualise the impacts of parameters on the congestion and compare 2 scenarios of acceleration-deceleration. 
*     It is based on the Netlogo model "Traffic model".
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
	int pavement_height <- 14;
	
	geometry shape <- rectangle(pavement_width * 2, pavement_height * 2);	
	image_file voit_image_file <- image_file("../includes/voit.png");
	image_file voit_red_image_file <- image_file("../includes/voit_red.png");
	image_file voit_blue_image_file <- image_file("../includes/voit_blue.png");

	int y_road1 <- 4;
	int y_road2 <- 10;
	list<car> cars_on_road1;
	list<car> cars_on_road2;
	car sample_car1;
	car sample_car2;	
	list<pavement> road1;
	list<pavement> road2;
	
	int nb_cars <- 25;
	float acceleration1 <- 0.0045 min: 0.0 max: 0.01 parameter: true;
	float deceleration1 <- 0.026 min: 0.0 max: 0.1 parameter: true;	
	float acceleration2 <- 0.0005 min: 0.0 max: 0.01 parameter: true;
	float deceleration2 <- 0.004 min: 0.0 max: 0.1 parameter: true;	
	float max_speed <- 50.0;
	
	init {
		road1 <- pavement where(each.grid_y = y_road1);
//		cars_on_road1 <- create_cars_on_road(nb_cars, road1);
		cars_on_road1 <- create_cars_on_road(1, road1, acceleration1, deceleration1);
		sample_car1 <- pick_sample(cars_on_road1, #red, voit_red_image_file);
		
		road2 <- pavement where(each.grid_y = y_road2);		
//		cars_on_road2 <- create_cars_on_road(nb_cars, road2);
		cars_on_road2 <- create_cars_on_road(1, road2, acceleration2, deceleration2);		
		sample_car2 <- pick_sample(cars_on_road2, #blue, voit_blue_image_file);		
	}
	
	reflex create_cars when: (length(car) < nb_cars * 2) and every(10 #cycles) {
		do create_cars_on_road(1, road1, acceleration1, deceleration1);
		do create_cars_on_road(1, road2, acceleration2, deceleration2);		
	}
	
	list<car> create_cars_on_road(int num_cars, list<pavement> road, float acc, float dec){
		create car number: num_cars returns: cars_on_road {
			pavement free_pavement <- one_of(road where(empty(car inside self)));
			if(free_pavement != nil) {
				my_pavement <- free_pavement;
				location <- my_pavement.location;
				acceleration <- acc;
				deceleration <- dec;
			} else {
				do die;
			}
			heading <- 0.0;
		}
		
		return cars_on_road;		
	}
	
	car pick_sample(list<car> cars, rgb col, image_file _icon) {
		car a_car <- one_of(cars);
		ask a_car {
			color <- col;
			icon <- _icon;
		}	
		return a_car;	
	}

	
}

grid pavement height: pavement_height width: pavement_width {
	init {
		if( (grid_y >= y_road1 - 1) and (grid_y <= y_road1 + 1) ) or 
		  ( (grid_y >= y_road2 - 1) and (grid_y <= y_road2 + 1)) {
			color <- #white;
		} else {
			color <- #black;
		}
	}
}

species car skills: [moving] {
	float speed_limit;
	float speed_min;
	float acceleration;
	float deceleration;
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
		display road type:2d antialias:false{
			grid pavement /*border: #black*/;
			species car aspect: icon;
			
		}

		display sp  type: 2d {
			chart "speed" type: series {
				data "red car" value: sample_car1.speed * max_speed color: sample_car1.color;
				data "blue car" value: sample_car2.speed * max_speed color: sample_car2.color;
			}
		}
	}
}
