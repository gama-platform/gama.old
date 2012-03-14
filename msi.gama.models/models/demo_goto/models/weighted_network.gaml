model demo_weighted_graph

global {
	file shape_file_roads parameter: 'Shapefile for the roads:' category: 'GIS' <- '../includes/gis/simpleRoad.shp' ;
	file shape_file_bounds parameter: 'Shapefile for the roads:' category: 'GIS' <- '../includes/gis/BoundsSimpleRoad.shp' ;
	
	graph the_graph;
	
	init { 
		let cpt type: int <- 1;
		create road from: shape_file_roads;
		let roadsList type: list of: road <- (road as list) sort_by ((each.location).x);
		ask roadsList {
			set destruction_coeff <- cpt;
			set color <- rgb([min([255, int(255*(destruction_coeff/ 4.0))]),max ([0, int(255 - (255*(destruction_coeff/4.0)))]),0]) ;
			set cpt <- cpt * 2;
		}
		
		let weights_map type: map <- (list (road)) as_map [each:: each.destruction_coeff];
		set the_graph <- as_edge_graph(list(road))  with_weights weights_map;
		
		create people number: 1 {
			set speed <- 2 ;
			set location <- first(((first (roadsList)).shape).points); 
			set target <- last(((last (roadsList)).shape).points); 
		}
	}
}
entities {
	species road  {
		float destruction_coeff;
		rgb color  ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	species people skills: [moving]{
		rgb color <- rgb('yellow') ;
		point target <- nil ;
		
		reflex move when: target != nil {
			do goto target: target on: the_graph ; 
			switch target { 
				match location {set target value: nil ;}
			}
		}
		aspect base {
			draw shape: circle color: color size: 5 ;
		}
	}
}
environment bounds: shape_file_bounds ;
output {
	display city_display refresh_every: 1 {
		species road aspect: base ;
		species people aspect: base ;
	}
}



