model tutorial_gis_city_traffic


global {
	file shape_file_buildings <- file('../includes/building.shp');
	file shape_file_roads <- file('../includes/road.shp');
	file shape_file_bounds <- file('../includes/bounds.shp');
	geometry shape <- envelope(shape_file_bounds);
	int nb_people <- 100;
	
	init {
		create building from: shape_file_buildings with: [type::string(read ('NATURE'))] {
			if type='Industrial' {
				color <- rgb('blue') ;
			}
		}
		create road from: shape_file_roads ;
		list<building> residential_buildings <- building where (each.type='Residential');
		create people number: nb_people {
			location <- any_location_in (one_of (residential_buildings));
		}
	}
}
entities {
	species building {
		string type; 
		rgb color <- rgb('gray')  ;
		aspect base {
			draw shape  color: color ;
		}
	}
	species road  {
		rgb color <- rgb('black') ;
		aspect base {
			draw shape color: color ;
		}
	}
	species people {
		rgb color <- rgb('yellow') ;
		aspect base {
			draw circle(10) color: color;
		}
	}
}

experiment road_traffic type: gui {
	parameter 'Shapefile for the buildings:' var: shape_file_buildings category: 'GIS' ;
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS' ;
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS' ;
	parameter 'Number of people agents' var: nb_people category: 'People' ;
	output {
		display city_display refresh_every: 1 {
			species building aspect: base ;
			species road aspect: base ;
			species people aspect: base ;
		}
	}
}

