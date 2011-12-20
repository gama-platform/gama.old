model tutorial_gis_city_traffic
import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	var shape_file_buildings type: string init: '../includes/building.shp' parameter: 'Shapefile for the buildings:' category: 'GIS' ;
	var shape_file_roads type: string init: '../includes/road.shp' parameter: 'Shapefile for the roads:' category: 'GIS' ;
	var shape_file_bounds type: string init: '../includes/bounds.shp' parameter: 'Shapefile for the bounds:' category: 'GIS' ;
	init {
		create species: building from: shape_file_buildings with: [type::read ('NATURE')] {
			if condition: type='Industrial' {
				set color value: rgb('blue') ;
			}
		}
		create species: road from: shape_file_roads ;
	}
}
entities {
	species building {
		var type type: string ;
		var color type: rgb init: rgb('gray')  ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	species road  {
		var color type: rgb init: rgb('black') ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
}
environment bounds: shape_file_bounds ;
output {
	display city_display refresh_every: 1 {
		species building aspect: base ;
		species road aspect: base ;
	}
}
