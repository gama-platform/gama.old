model demo_weighted_graph

global {
	file shape_file_roads  <- file('../includes/gis/simpleRoad.shp') ;
	file shape_file_bounds  <- file('../includes/gis/BoundsSimpleRoad.shp') ;
	
	geometry shape <- envelope(shape_file_bounds);
	graph the_graph;
	
	init { 
		int cpt <- 1;
		create road from: shape_file_roads;
		list<road> roadsList <- road sort_by ((each.location).x);
		ask roadsList {
			 destruction_coeff <- float(cpt);
			 color <- rgb(min([255, int(255*(destruction_coeff/ 4.0))]),max ([0, int(255 - (255*(destruction_coeff/4.0)))]),0) ;
			 cpt <- cpt * 2;
		}
		
		map<road,float> weights_map <- road as_map (each:: (each.destruction_coeff * each.shape.perimeter));
		the_graph <- as_edge_graph(road) with_weights weights_map;
		
		create people  {
			 speed <- 2.0 ; 
			 location <- first(((first (roadsList)).shape).points); 
			 target <- last(((last (roadsList)).shape).points); 
		}
	}
}

species road  {
	float destruction_coeff;
	rgb color  ;
	aspect base {
		draw shape color: color ;
	}
}

species people skills: [moving]{
	rgb color <- rgb('yellow') ;
	point target <- nil ;
		
	reflex move  {
		do goto target: target on: the_graph ;
	}
	aspect base {
		draw circle(5) color: color;
	}
}

experiment goto_weighted_network type: gui {
	output {
		display object_display refresh_every: 1 {
			species road aspect: base ;
			species people aspect: base ;
		}
	}
}



