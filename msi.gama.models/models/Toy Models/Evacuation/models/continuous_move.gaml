model continuous_move 
global { 
	file building_shapefile <- file("../includes/building.shp");
	geometry shape <- envelope(building_shapefile);
	int maximal_turn <- 90; //in degree
	int cohesion_factor <- 10;
	float people_size <- 2.0;
	geometry free_space;
	int nb_people <- 500;
	point target_point <- {world.location.x, 0};
	init { 
		free_space <- copy(shape);
		create building from: building_shapefile {
			free_space <- free_space - (shape + people_size);
		}
		free_space <- free_space simplification(1.0);
		create people number: nb_people {
			location <- any_location_in(free_space);
			target_loc <-  target_point;
		} 		 	
	}	
}

species building {
	float height <- 3.0 + rnd(5);
	aspect default {
		draw shape color: #gray depth: height;
	}
}
	
species people skills:[moving]{
	point target_loc;
	float speed <- 0.5 + rnd(1000) / 1000;
	point velocity <- {0,0};
	int heading max: heading + maximal_turn min: heading - maximal_turn;
		
	float size <- people_size;
	rgb color <- rgb(rnd(255),rnd(255),rnd(255));
		
	
	reflex end when: location distance_to target_loc <= 2 * people_size{
		write name + " is arrived";
		do die;
	}
	
	reflex follow_goal  {
		velocity <- velocity + ((target_loc - location) / cohesion_factor);
	}
	reflex separation {
		point acc <- {0,0};
		ask (people at_distance size)  {
			acc <- acc - (location - myself.location);
		}  
		velocity <- velocity + acc;
	}
	
	reflex avoid { 
		point acc <- {0,0};
		list<building> nearby_obstacles <- (building at_distance people_size);
		loop obs over: nearby_obstacles {
			acc <- acc - (obs.location - location);
		}
		velocity <- velocity + acc; 
	}
	reflex move {
		point old_location <- copy(location);
		do goto target: location + velocity ;
		if (not empty(building overlapping self )) {
			location <- point((location closest_points_with free_space)[1]);
		}
		velocity <- location - old_location;
	}	
	aspect default {
		draw pyramid(size) color: color;
		draw sphere(size/3) at: {location.x,location.y,size} color: color;
	}
}

experiment main type: gui {
	parameter "nb people" var: nb_people min: 1 max: 1000;
	output {
		display map type: opengl ambient_light: 150 camera_pos: {world.location.x,-world.shape.height*1.5,70}
                        camera_look_pos:{world.location.x,0,0}    {
			image '../images/soil.jpg';
			species building refresh: false;
			species people;
			graphics "exit" refresh: false {
				draw sphere(2 * people_size) at: target_point color: #green;	
			}
		}
	}
}

