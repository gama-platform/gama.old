model tutorial_gis_city_traffic

global {
	file shape_file_lighttrap <- '../includes/Mekong/DongThap_LightTraps.shp' parameter: 'Shapefile for the LightTraps:' category: 'GIS' ;
	file shape_file_grid <- '../includes/Mekong/DongThap_Grid.shp' parameter: 'Shapefile for the grid:' category: 'GIS' ;
	file shape_file_district <- '../includes/Mekong/DONGTHAP_district.shp' parameter: 'Shapefile for the districts:' category: 'GIS' ;
	init {
		create lightrap from: shape_file_lighttrap  {
	
		}
		create grid from: shape_file_grid ;
		
		create district from : shape_file_district;
	}
}
entities {
	species lightrap {
		string type; 
		rgb color <- rgb('green')  ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	species grid  {
		rgb color <- rgb('black') ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	
	species district{
		rgb color <- rgb('red');
		aspect base {
			draw shape: geometry color: color;
		}
	}
}
environment bounds: shape_file_grid ;
output {
	display city_display refresh_every: 1 {
		species lightrap aspect: base ;
		species grid aspect: base ;
		species district aspect: base;
	}
}
