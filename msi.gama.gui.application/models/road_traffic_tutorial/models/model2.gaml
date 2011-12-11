model tutorial_gis_city_traffic
import "platform:/plugin/msi.gama.gui.application/generated/std.gaml"

global {
	var shape_file_buildings type: string init: '../includes/building.shp' parameter: 'Shapefile for the buildings:' category: 'GIS' ;
	var shape_file_roads type: string init: '../includes/road.shp' parameter: 'Shapefile for the roads:' category: 'GIS' ;
	var shape_file_bounds type: string init: '../includes/bounds.shp' parameter: 'Shapefile for the bounds:' category: 'GIS' ;
	var nb_people type: int init: 100 parameter: 'Number of people agents' category: 'People' ;
	init {
		create species: building from: shape_file_buildings with: [type::read('NATURE')] {
			if condition: type='Industrial' {
				set color value: rgb('blue') ;
			}
		}
		create species: road from: shape_file_roads ;
		let residential_buildings type: list of: building value: list(building) where (each.type='Residential');
		create species: people number: nb_people {
			set location value: any_location_in ((one_of (residential_buildings)).shape);
		}
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
	species road {
		var color type: rgb init: rgb('black') ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	species people {
		var color type: rgb init: rgb('yellow') ;
		aspect base {
			draw shape: circle color: color size: 10 ;
		}
	}
}
environment bounds: shape_file_bounds ;
output {
	display city_display refresh_every: 1 {
		species building aspect: base ;
		species road aspect: base ;
		species people aspect: base ;
	}
}
