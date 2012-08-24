model tutorial_gis_city_traffic

global {

	file shape_file_roads <- file('../includes/road.shp');
	file shape_file_bounds <- file('../includes/bounds.shp');

	graph the_graph;
	
	init {

		create road from: shape_file_roads ;
		let weights_map type: map <- (list (road)) as_map [weight];
		set the_graph <- as_edge_graph(list(road))  with_weights weights_map;
	
	}
	
	reflex update_graph{
		let weights_map type: map <- (list (road)) as_map [weight];
		set the_graph <- the_graph  with_weights weights_map;
	}
}
entities {

	species road  {
		float weight <- 1.0 ;
		int colorValue <- int(255*(weight - 1)) update: int(255*(weight - 1));
		rgb color <- rgb([min([255, colorValue]),max ([0, 255 - colorValue]),0])  update: rgb([min([255, colorValue]),max ([0, 255 - colorValue]),0]) ;
		aspect base {
			draw geometry: shape color: color ;
		}
	}

}
environment bounds: shape_file_bounds ;

experiment road_traffic type: gui {

	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS' ;
	
	output {
		display city_display refresh_every: 1 type: opengl{
			species road aspect: base ;
		}
	}
}




