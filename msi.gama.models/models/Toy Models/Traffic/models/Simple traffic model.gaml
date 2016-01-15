/**
 *  trafic
 *  Author: Patrick Taillandier
 *  Description: A simple road network model: the speed on a road depends on the number of people on this road (the highest, the slowest)
 */

model trafic

global {
	file building_shapefile <- file("../includes/buildings.shp");
	file road_shapefile <- file("../includes/roads.shp");
	geometry shape <- envelope(road_shapefile);
	float step <- 10 #s;
	graph road_network;
	map<road,float> road_weights;
	
	init {
		create building from: building_shapefile;
		create road from: road_shapefile;
		create people number: 1000{
			location <- any_location_in(one_of(building));
      	}
      	road_weights <- road as_map (each::each.shape.perimeter);
      	road_network <- as_edge_graph(road);
	}
	
	reflex update_road_speed  {
		road_weights <- road as_map (each::each.shape.perimeter / each.speed_coeff);
		road_network <- road_network with_weights road_weights;
	}
}

species people skills: [moving]{
	point target;
	float leaving_proba <- 0.05; 
	float speed <- 5 #km/#h;
	rgb color <- rnd_color(255);
	
	reflex leave when: (target = nil) and (flip(leaving_proba)) {
		target <- any_location_in(one_of(building));
	}
	
	reflex move when: target != nil {
		do goto target: target on: road_network recompute_path: false move_weights: road_weights;
		if (location = target) {
			target <- nil;
		}	
	}
	
	aspect default {
		draw circle(5) color: color;
	}
}

species building {
	aspect default {
		draw shape color: #gray;
	}
}

species road {
	float capacity <- 1 + shape.perimeter/30;
	int nb_people <- 0 update: length(people at_distance 1);
	float speed_coeff <- 1.0 update:  exp(-nb_people/capacity) min: 0.1;
	
	aspect default {
		draw (shape + 3 * speed_coeff) color: #red;
	}
}
experiment trafic type: gui {
	float minimum_cycle_duration <- 0.01;
	output {
		display carte type: opengl{
			species building refresh: false;
			species road ;
			species people ;
		}
	}
}
