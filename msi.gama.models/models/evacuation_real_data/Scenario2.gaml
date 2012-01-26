model Scenario2

global {
	// Parameters of the time in a day and the toursit season
	var initial_time type: float parameter: 'Time in day' init: 8.5 min: 0 max: 24;
	var tourist_season type: bool parameter: 'In tourist season?' init: true;
	var fox_rate type: float parameter: 'Percent of fox in the population' init: 0.1 min: 0 max: 1;
	var population_scale type: float init: 0.05 const: true;
	
	// GIS data
	var shape_file_road type: string init: '/gis/roadlines.shp';
	var shape_file_rivers type: string init: '/gis/rivers.shp';
	var shape_file_beach type: string init: '/gis/Beacha.shp';
	var shape_file_roadwidth type: string init: '/gis/roads.shp';
	var shape_file_building type: string init: '/gis/buildr.shp';
	var shape_file_bounds type: string init: '/gis/bounds.shp';
	var shape_file_panel type: string init: '/gis/panel.shp';
	var shape_file_ward type: string init: '/gis/wards.shp';
	var shape_file_zone type: string init: '/gis/zone.shp';

	var sheep_speed type: float init: 1;
	var sheep_size type: float init: 1 const: true;
	var sheep_color type: rgb init: rgb('green');
	
	var fox_speed type: float init: 5;
	var fox_color type: rgb init: rgb('blue');

	var ward_colors type: list of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')] const: true;
	var zone_colors type: list of: rgb init: [rgb('magenta'), rgb('blue'), rgb('yellow')] const: true;
	
	/*
	<var type="string" name="shapeSign" init="'/icons/sign.jpg'" const="true"/>
	<var type="string" name="shapeSign1" init="'/icons/sign1.jpg'" const="true"/>
	 */
	var shapeSign type: string init: '/icons/CaliforniaEvacuationRoute.jpg' const: true;

	var zone1_building_color type: rgb init: rgb('orange');
	var zone2_building_color type: rgb init: rgb('gray');
	var zone3_building_color type: rgb init: rgb('yellow');

	var terminal_panel_ids type: list of: int init: [10];

	var road_graph type: graph;

	init {
		 create species: road from: shape_file_road;
		 create species: beach from: shape_file_beach;
		 
		 create species: ward from: shape_file_ward with: [id :: read('ID'), wardname :: read('Name'), population :: read('Population')] {
		 	do action: init_overlapping_roads;
		 }
		 
	create species: zone from: shape_file_zone with: [id :: read('ID')];
	create species: building from: shape_file_building with: [ floor :: read('STAGE'), x :: read('X'), y :: read('Y')];
	create species: roadwidth from: shape_file_roadwidth;
	create species: panel from: shape_file_panel with: [next_panel_id :: read('TARGET'), id :: read('ID'), priority :: read('PRIORITY')];
	create species: river from: shape_file_rivers;
		 
		set road_graph value: as_edge_graph (list(road) collect (each.shape));

		loop w over: list(ward) {
			/*
			create species: sheep number: int ( (w.population * population_scale) * (1 - fox_rate) ) {
				set location value: any_location_in (one_of (w.roads));
			}

			create species: fox number: int ( (w.population * population_scale) * (fox_rate) ) {
				set location value: any_location_in (one_of (w.roads));
			}
 */
 
		}
		
		create species: fox number: 1 {
			set location value: any_location_in (one_of ((one_of (list(ward)).roads)));
		}
		
		
		do action: write {
			arg message value: 'foxes: ' + (string (length (list (fox)))) + '; sheeps: ' + (string (length (list (sheep))));
		}
	}	 
}

entities {
	species road {
	 	aspect base {
	 		draw shape: geometry color: rgb('yellow');
	 	}
	}
	
	species zone {
	  	var id type: int;
	  	var color type: rgb init: rgb ( (zone_colors at (id - 1)) );
	  	
	  	aspect base {
	  		draw shape: geometry color: color;
	  	}
	  	
	}
	
	species ward {
	  	var id type: int;
	  	var population type: int min: 0;
	  	var wardname type: string;
	  	var color type: rgb init: one_of(ward_colors);
	  	var roads type: list of: road;
	  	
	  	action init_overlapping_roads {
	  		set roads value: road overlapping shape;
	  	}
	  	
	  	
	  	aspect base {
	  		draw shape: geometry color: color;
	  	}
	}

	species roadwidth {
	   	aspect base {
	   		draw shape: geometry color: rgb('yellow');
	   	}
	}
	   
	species building {
	   	var floor type: int;
	   	var x type: float;
	   	var y type: float;
	   	var zone_id type: int;
	   	var color type: rgb init: zone3_building_color;
	   	
	   	init {
	   		let overlapping_zone type: list of: zone value: zone overlapping shape;
	   		if condition: !(empty (overlapping_zone)) {
	   			
	   			set zone_id value: (first(overlapping_zone)).id;
	   			
	   			if condition: (zone_id = 1) {
	   				set color value: zone1_building_color;
	   				
	   				else {
	   					if condition: (zone_id = 2) {
	   						set color value: zone2_building_color;
	   					}
	   				}
	   			}
	   		}
	   	}
	   	
	   	aspect base {
	   		draw shape: geometry color: color;
	   	}
	}

	species beach {
	   	aspect base {
	   		draw shape: geometry color: rgb('green');
	   	}
	}
		
	species river {
		aspect base {
			draw shape: geometry color: rgb('blue');
		}
	}
	
	species panel {
		var next_panel_id type: int;
		var id type: int;
		var priority type: int;
		
		var is_terminal type: bool init: false;
		
		init {
			if condition: (terminal_panel_ids contains id) {
				set is_terminal value: true;
			}
		}
		
		aspect base {
			draw image: shapeSign at: location size: 0.0005;
		}
	}		

	species bounds {
		aspect base {
			draw shape: geometry color: rgb('gray');
		}
	}

	species fox skills: moving {
		var color type: rgb init: rgb('red');
		var capacity type: int init: 20;
		var current_panel type: panel init: nil;
		var born_step type: int;
		
		init {
			set current_panel value: (list (panel)) closest_to shape;
		}
		
		reflex move when: ( (current_panel != nil) and (location != (current_panel.location)) ) {
			do action: goto {
				arg target value: current_panel;
				arg on value: road_graph;
				arg speed value: fox_speed;
			}
			
			if condition: (current_panel.id = 9) {
				do action: write {
					arg message value: name + ' moves with current_panel with id: ' + (string (current_panel.id));
				}
			}
		}
		
		reflex switch_panel_or_die when: (location = (current_panel.location)) {
			if condition: !(current_panel.is_terminal) {
				do action: write {
					arg message value: name + ' switches panel from ' + current_panel + ' with id: ' + (string (current_panel.id)) + ' to ';
				}
				
				set current_panel value: one_of ( (list (panel)) where (each.id =  current_panel.next_panel_id) ) ;
				
				do action: write {
					arg message value: (string (current_panel)) + ' with id: ' + (string (current_panel.id)) + '\\n';
				}
				
				else {
					do action: write {
						arg message value: name + ' reaches terminal panel: ' + current_panel + ' with id: ' + (string (current_panel.id)) + ' and is_terminal: ' + (string (current_panel.is_terminal));
					}
					
					do action: die;
				}
			}
		}
		
		aspect base {
			draw shape: geometry color: fox_color;
		}
	}

	species sheep skills: moving {
		var color type: rgb init: rgb('green');
		var safe_building type: building;
		
		var reach_target type: bool init: false;
		
		init {
			set safe_building value: ((list (building)) where (each.floor >= 3)) closest_to shape;
		}
		
 		aspect base {
//     			draw shape: circle color: sheep_color size: 0.1;
 			draw shape: geometry color: sheep_color;
 		}
	}
}

environment bounds: shape_file_bounds;

experiment default_expr type: gui {
	output {
		display pedestrian_road_network {
		 	species road aspect: base transparency: 0.1;
//		 	species roadwidth aspect: base transparency: 0.1;
//		 	species building aspect: base transparency: 0.1;
//		 	species beach aspect: base transparency: 0.9;
//		 	species zone aspect: base transparency: 0.9;
//		 	species river aspect: base transparency: 0.5;
// 			species panel aspect: base transparency: 0.01;
//		 	species ward aspect: base transparency: 0.9;
 			species fox aspect: base transparency: 0.1;
		 	species sheep aspect: base transparency: 0.1;
		}
		
		monitor length_sheep value: length(list(sheep));
		monitor length_sheep_reach_target value: length(list(sheep) where (each.reach_target));
		monitor length_sheep_NOT_reach_target value: length(list(sheep) where !(each.reach_target));
	}
}