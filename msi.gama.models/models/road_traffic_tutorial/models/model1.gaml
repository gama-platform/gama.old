model tutorial_gis_city_traffic

global {
	file shape_file_buildings <- file('../includes/building.shp');
	file shape_file_roads <- file('../includes/road.shp');
	file shape_file_bounds <- file('../includes/bounds.shp');
	init {
		create building from: shape_file_buildings with: [type::read ('NATURE')] {
			if type='Industrial' {
				set color <- rgb('blue') ;
			}
		}
		create road from: shape_file_roads ;
	}
}
entities {
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
}

environment bounds: shape_file_bounds ;
	
experiment road_traffic type: gui {
	parameter 'Shapefile for the buildings:' var: shape_file_buildings category: 'GIS' ;
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS' ;
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS' ;
	output {
		display city_display refresh_every: 1 {
			species building aspect: base ;
			species road aspect: base ;
		}
	}
}