model multi_topologies

global {
	file shape_file_buildings <- file('gis/building.shp') parameter: 'Shapefile for the buildings:' category: 'GIS' ;
	file shape_file_roads <- file('gis/road.shp') parameter: 'Shapefile for the roads:' category: 'GIS' ;
	file shape_file_bounds <- file('gis/bounds_new.shp') parameter: 'Shapefile for the bounds:' category: 'GIS' ;
	int nb_people <- 30 parameter: 'Number of people agents' category: 'People' ;
	float min_speed <- 1.0 parameter: 'minimal speed' category: 'People' ;
	float max_speed <- 10.0 parameter: 'maximal speed' category: 'People' ;
	
	int nb_balls <- 10;

	topology graph_topo;

	init {
		create building from: shape_file_buildings;
		
		create road from: shape_file_roads ;
		set graph_topo value: topology(as_edge_graph(list(road)));		

		create people number: nb_people {
			set location value: any_location_in(one_of(list(road)));
			set speed <- min_speed + rnd (max_speed - min_speed) ;
		}
		
		create macro_circle;
		
		create ball number: nb_balls;
		create ball_agents_counter;
	}
}

environment bounds: shape_file_bounds;


entities {
	
	species ball skills: moving {
		geometry shape <- square(40) value: square(40) at_location (location);
		float speed <- 30;
		
		reflex move_around {
			do wander;
		}
			
		aspect default {
			draw shape: geometry color: rgb('pink');
		}
	}
	
	species macro_circle frequency: 2 {
		geometry shape <- circle(600) at_location {700, 800};
		bool is_capturing <- true;
		
		reflex capture_balls when: is_capturing {
			let to_be_captured type: ball value: one_of(ball overlapping(self));
			if (to_be_captured != nil) { 
				capture to_be_captured as: ball_in_rectangle {
					set speed value: 5;
				}
			}
			
			if (length(members) = nb_balls) { set is_capturing value: false; }
		}
		
		reflex release_balls when: ( (!is_capturing) ) {
			release one_of(members) as: ball in: world {
				set speed value: 30;
			}
			
			if (empty(members)) { set is_capturing value: true; }
		}
		
		species ball_in_rectangle parent: ball {
			geometry shape <- circle(10.0) value: circle(10.0) at_location location;
			
			aspect default {
				draw shape: geometry color: rgb('green');
			}
		}	
		
		aspect default {
			draw shape: geometry color: rgb('blue');
			draw text: 'Balls in circle: ' + (string(length(members))) at: {location.x - 260, location.y} size: 80 color: rgb('black') style: bold;
		}
	}
	
	species building {
		string type; 
		rgb color <- rgb('gray')  ;
		aspect base {
			draw geometry: shape color: color ;
		}
	}
	
	species road  {
		rgb color <- rgb('black') ;
		aspect base {
			draw geometry: shape color: color ;
		}
	}

	species people skills: [moving] {
		rgb color <- rgb('yellow') ;
		point the_target <- nil ;
		
		reflex move {
			
			if (the_target = nil) {
				set the_target value: (one_of(list(building))).location;
			}
			
			do goto target: the_target on: graph_topo ;
			 
			switch the_target { 
				match location {set the_target value: nil ;}
			}
		}
		
		aspect base {
			draw shape: circle color: color size: 20 ;
		}
	}
	
	species ball_agents_counter {
		aspect default {
			draw text: 'Balls: ' + (string(length (world.members of_species ball))) at: {10, 1750} style: bold size: 150 color: rgb('pink');
		}
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			species road;
			species building;
			species people aspect: base;
			species ball;
			
			species macro_circle transparency: 0.5 {
				species ball_in_rectangle;
			}
		
			species ball_agents_counter;	
		}
	}
}