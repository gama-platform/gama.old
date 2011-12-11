model tutorial_gis_city_traffic
// gen by Xml2Gaml
import "platform:/plugin/msi.gama.gui.application/generated/std.gaml"

global {
	var shape_file_buildings type: string init: '../includes/building.shp' parameter: 'Shapefile for the buildings:' category: 'GIS' ;
	var shape_file_roads type: string init: '../includes/road.shp' parameter: 'Shapefile for the roads:' category: 'GIS' ;
	var shape_file_bounds type: string init: '../includes/bounds.shp' parameter: 'Shapefile for the bounds:' category: 'GIS' ;
	var nb_people type: int init: 200 parameter: 'Number of people agents' category: 'People' ;
	var day_time type: int value: time mod 144 ;
	var min_work_start type: int init: 36 parameter: 'Earliest hour to start work' category: 'People' ;
	var max_work_start type: int init: 60 parameter: 'Latest hour to start work' category: 'People' ;
	var min_work_end type: int init: 84 parameter: 'Earliest hour to end work' category: 'People' ;
	var max_work_end type: int init: 132 parameter: 'Latest hour to end work' category: 'People' ;
	var min_speed type: float init: 50 parameter: 'minimal speed' category: 'People' ;
	var max_speed type: float init: 100 parameter: 'maximal speed' category: 'People' ;
	var destroy type: float init: 0.02 parameter: 'Value of destruction when a people agent takes a road' category: 'Road' ;
	var repair_time type: int init: 6 parameter: 'Number of steps between two road repairs' category: 'Road' ;
	var the_graph type: graph;
	init {
		create species: building from: shape_file_buildings with: [type::read('NATURE')] {
			if condition: type='Industrial' {
				set color value: rgb('blue') ;
			}
		}
		create species: road from: shape_file_roads ;
		let weights_map type: map value: (list (road)) as_map [each:: each.destruction_coeff];
		set the_graph value: as_edge_graph(list(road))  with_weights weights_map;
		
		let residential_buildings type: list value: (building as list) where (each.type='Residential') ;
		let industrial_buildings type: list value: (building as list) where (each.type='Industrial') ;
		create species: people number: nb_people {
			set speed value: min_speed + rnd (max_speed - min_speed) ;
			set start_work value: min_work_start + rnd (max_work_start - min_work_start) ;
			set end_work value: min_work_end + rnd (max_work_end - min_work_end) ;
			set living_place value: one_of(residential_buildings) ;
			set working_place value: one_of(industrial_buildings) ;
			set location value: any_location_in (living_place.shape); 
		}
	}
	reflex repair_road when: (time mod repair_time) = 0 {
		let the_road_to_repair type: road value: (road as list) with_max_of (each.destruction_coeff) ;
		ask target: the_road_to_repair {
			set destruction_coeff value: 1 ;
		}
	}
	reflex update_graph{
		let weights_map type: map value: (list (road)) as_map [each:: each.destruction_coeff];
		set the_graph value: the_graph  with_weights weights_map;
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
		var destruction_coeff type: float init: 1 ;
		var color type: rgb init: [min([255, int(255*(destruction_coeff - 1))]),max ([0, int(255 - (255*(destruction_coeff - 1)))]),0]  value: [min([255, int(255*(destruction_coeff - 1))]),max ([0, int(255 - (255*(destruction_coeff - 1)))]),0] ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	species people skills: [moving]{
		var color type: rgb init: rgb('yellow') ;
		var living_place type: building init: nil ;
		var working_place type: building init: nil ;
		var start_work type: int ;
		var end_work type: int ;
		var objectif type: string ;
		var the_target type: point init: nil ;
		aspect base {
			draw shape: circle color: color size: 10 ;
		}
		reflex time_to_work when: day_time = start_work {
			set objectif value: 'working' ;
			set the_target value: any_location_in (working_place.shape);
		}
		reflex time_to_go_home when: day_time = end_work {
			set objectif value: 'go home' ;
			set the_target value: any_location_in (living_place.shape); 
		}
		reflex move when: the_target != nil { 
			let path_followed type: path value: self.goto [target::the_target, on::the_graph];
			let segments type: list of: geometry value: path_followed.segments;
			loop line over: segments {
				let dist type: float value: line.perimeter;
				let ag type: road value: path_followed agent_from_geometry line; 
				ask target: road(ag) {
					set destruction_coeff value: destruction_coeff + (destroy * dist / shape.perimeter);
				}
			}
			if condition: the_target = location {
				set the_target value: nil ;
			}
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
	display chart_display refresh_every: 10 {
		chart name: 'Road Status' type: series background: rgb('lightGray') size: {0.9, 0.4} position: {0.05, 0.05} {
			data name:'Mean road destruction' value: mean ((road as list) collect each.destruction_coeff) style: line color: rgb('green') ;
			data name:'Max road destruction' value: (road as list) max_of (each.destruction_coeff) style: line color: rgb('red') ;
		}
		chart name: 'People Objectif' type: pie background: rgb('lightGray') style: exploded size: {0.9, 0.4} position: {0.05, 0.55} {
			data name:'Working' value: length ((people as list) where (each.objectif='working')) color: rgb('green') ;
			data name:'Staying home' value: length ((people as list) where (each.objectif='go home')) color: rgb('blue') ;
		}
	}
}
