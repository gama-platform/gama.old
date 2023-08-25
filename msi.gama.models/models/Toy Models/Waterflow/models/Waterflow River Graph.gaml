/***
* Name: Water flow in a river graph, using water flow in rivers
* Author: Benoit Gaudou and Patrick Taillandier
* Description: In this model, the flow of water is modeled through the exchange of water between elements of rivers.
* 	The only water input comes (every 20 steps) from the source points. Then the water flows toward the outlet point.
* Tags: shapefile, gis, graph, gui, hydrology, water flow
***/

model Waterflowrivergraph

global {
	file river_shape_file <- shape_file("../includes/rivers.shp");
	file poi_file <- shape_file("../includes/poi.shp");

	geometry shape <- envelope(river_shape_file) + 500;
	
	graph the_river;
	poi outlet;

	init {
		create river from: river_shape_file;
		create poi from: poi_file;
		outlet <- poi first_with(each.type = "outlet");
		the_river <- as_edge_graph(river);
	
		ask poi - outlet{
			closest_river <- river closest_to self;
			path path_to_outlet <- path_between(the_river,self,outlet);
			loop i from: 0 to: length(path_to_outlet.edges) - 2 {
				river(path_to_outlet.edges[i]).next_river <- river(path_to_outlet.edges[i+1]);
			}
		} 
	}
	
	reflex water_flow {
		// Every 20 simulation steps, the source points provide water to the closest river.
		if (every(10#cycles)) {
			ask poi - outlet {
				do give_water;
			}	
		//  For a visualisation purpose, the water flow is not executed when the sources provide water.
		} else {		
			ask river {
				do water_flow;
			}
			ask river {
				do update_water_level;
			}			
		}
	}
}

species poi {
	string type;
	river closest_river ;
	
	action give_water {
		closest_river.water_volume <- 200.0;
	}
	
	aspect default {
		draw circle(500) color: (type="source") ? #green : #red border: #black;		
	}	
}

species river {
	river next_river ;
	float water_volume;
	float water_volume_from_other;
	
	action water_flow {
		if (next_river != nil) {
			next_river.water_volume_from_other <- next_river.water_volume_from_other + 0.9 * water_volume;
		}
	}
	
	action update_water_level {
		water_volume <- 0.1 * water_volume + water_volume_from_other;
		water_volume_from_other <- 0.0;
	}
	
	aspect default {
		draw shape color: #blue;	
		draw shape + water_volume color: #blue;
		
			
	}
}

experiment flow type: gui {
	output {
	 	display "Water Unit" type:2d { 
			species river ; 
			species poi;			
		}
	}
}
