model debug_goto

global {
	
	// GIS data
	var shape_file_road type: string init: '/gis/roadlines.shp';
	var shape_file_bounds type: string init: '/gis/bounds.shp';
	var shape_file_panel type: string init: '/gis/panel.shp';

	var fox_speed type: float init: 0.1;
	var fox_color type: rgb init: rgb('green') const: true;
	
	var shapeSign type: string init: '/icons/CaliforniaEvacuationRoute.jpg' const: true;

	var terminal_panel_ids type: list of: int init: [10];

	var road_graph type: graph;

	init {
		create species: road from: shape_file_road;
		create species: panel from: shape_file_panel with: [next_panel_id :: read('TARGET'), id :: read('ID')];
		set road_graph value: as_edge_graph (list(road) collect (each.shape));

		create species: fox number: 1 {
			set location value: ( one_of ( (list (panel)) where (each.id = 1) )).location;
		}
	}	 
}

entities {
	species road {
	 	aspect base {
	 		draw shape: geometry color: rgb('yellow');
	 	}
	}
	
	species panel {
		var next_panel_id type: int;
		var id type: int;
		
		var is_terminal type: bool init: false;
		
		init {
			if condition: (terminal_panel_ids contains id) {
				set is_terminal value: true;
			}
		}
		
		aspect base {
			draw image: shapeSign at: location size: 50;
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
}

environment bounds: shape_file_bounds;

experiment default_expr type: gui {
	output {
		display pedestrian_road_network {
		 	species road aspect: base transparency: 0.1;
		 	species panel aspect: base transparency: 0.01;
 			species fox aspect: base transparency: 0.1;
		}
	}
}