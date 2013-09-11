model tutorial_gis_city_traffic

global {
	file shape_file_bounds <- file('../includes/bounds.shp');
	geometry shape <- envelope(shape_file_bounds);
	init {
	}
}
entities {

}

experiment road_traffic type: gui {
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS' ;
	output {

	}
}