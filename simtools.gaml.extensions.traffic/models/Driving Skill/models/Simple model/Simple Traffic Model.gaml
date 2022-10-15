/**
* Name: Traffic
* Description: define species for traffic simulation
* Author: Patrick Taillandier & Duc Pham
* Tags: driving skill, graph, agent_movement, skill, transport
*/

model simple_traffic_model

global {
	shape_file nodes_shape_file <- shape_file("../../includes/rouen/nodes.shp");
	shape_file roads_shape_file <- shape_file("../../includes/rouen/roads.shp");
	
	geometry shape <- envelope(roads_shape_file);
	graph road_network;
	init {
		create intersection from: nodes_shape_file;
		
		create road from: roads_shape_file {
			// Create another road in the opposite direction
			create road {
				num_lanes <- myself.num_lanes;
				shape <- polyline(reverse(myself.shape.points));
				maxspeed <- myself.maxspeed;
				linked_road <- myself;
				myself.linked_road <- self;
			}
		}
		
		
		road_network <- as_driving_graph(road, intersection);
		
		create vehicle number: 1000 with: (location: one_of(intersection).location);
	}

}

species road skills: [skill_road] {
	rgb color <- #white;
	
	aspect base {
		draw shape color: color end_arrow: 1;
	}
}

species intersection skills: [skill_road_node] ;

species vehicle skills: [advanced_driving] {
	rgb color <- rnd_color(255);
	init {
		vehicle_length <- 1.9 #m;
		max_speed <- 100 #km / #h;
		max_acceleration <- 3.5;
	}

	reflex select_next_path when: current_path = nil {
		// A path that forms a cycle
		do compute_path graph: road_network target: one_of(intersection);
	}
	
	reflex commute when: current_path != nil {
		do drive;
	}
	aspect base {
		draw triangle(5.0) color: color rotate: heading + 90 border: #black;
	}
}

experiment city type: gui {
	output synchronized: true {
		display map type: 3d background: #gray {
			species road aspect: base;
			species vehicle aspect: base;		}
	}
}