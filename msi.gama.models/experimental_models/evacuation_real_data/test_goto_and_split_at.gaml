model test_goto_and_split_at

global {
	float insideRoadCoeff <- 0.1 min: 0.01 max: 0.4 parameter: 'Size of the external parts of the roads:';

	topology road_graph_topology;
	macro_patch_initializer mpi;

	init {
		create species: road with: [ shape:: polyline([{5,5}, {20,20}, {50,5}, {75,80}]) ];
		set road_graph_topology value: topology(as_edge_graph (list(road)));
		
		create macro_patch_initializer returns: rets;
		set mpi value: (rets at 0); 
		
		loop r over: list(road) {
			ask mpi {
				do initialize with: [ the_road :: r ];
			}
		}
	}
}

environment width: 100 height: 100;

entities {
	species road {
		geometry macro_patch;
		geometry extremity1;
		geometry extremity2;

		reflex when: (time = 1) {
			create macro_patch_viewer with: [ shape :: macro_patch ];
			create extremity1_viewer with: [ shape :: extremity1 ];
			create extremity2_viewer with: [ shape :: extremity2 ];
		}
		
		aspect default {
			draw shape: geometry color: rgb('green');
		}
	}
	
	species macro_patch_viewer {
		aspect default {
			draw shape: geometry color: rgb('red');
		}		
	}
	
	species extremity1_viewer {
		aspect default {
			draw shape: geometry color: rgb('blue');
		}
	}
	
	species extremity2_viewer {
		aspect default {
			draw shape: geometry color: rgb('magenta');
		}
	}

	species macro_patch_initializer skills: [moving] {
		action initialize {
			arg the_road type: road;
				
			let inside_road_geom type: geometry value: the_road.shape;
			set speed value: (the_road.shape).perimeter * insideRoadCoeff;
			let point1 type: point value: first(inside_road_geom.points);
			let point2 type: point value: last(inside_road_geom.points);
			set location value: point1;
			
			do action: goto {
				arg target value: point2;
				arg on value: road_graph_topology; 
			}

			let lines1 type: list of: geometry value: (inside_road_geom split_at location);
			set the_road.extremity1 value: lines1  first_with (geometry(each).points contains point1);
			set inside_road_geom value: lines1 first_with (!(geometry(each).points contains point1));
			set location value: point2;
			do action: goto {
				arg target value: point1;
				arg on value: road_graph_topology; 
			}
			let lines2 type: list of: geometry value: (inside_road_geom split_at location);
			
			set the_road.extremity2 value:  lines2 first_with (geometry(each).points contains point2);
			set inside_road_geom value: lines2 first_with (!(geometry(each).points contains point2));
			
			set the_road.macro_patch value: inside_road_geom;
		}
	}	
}

experiment default_expr type: gui {
	output {
		display road_with_macro_patch_viewer {
			species road aspect: default transparency: 0.5;
			species macro_patch_viewer aspect: default;
		}
		
		display road_network_display {
			species road aspect: default;
		}
		
		display 2_extremities_with_macro_patch {
			species extremity1_viewer aspect: default;
			species macro_patch_viewer aspect: default;
			species extremity2_viewer aspect: default;
		}

		display extremit1_display {
			species extremity1_viewer aspect: default;
		}

		display extremity2_display {
			species extremity2_viewer aspect: default;
		}

		display macro_patch_display {
			species macro_patch_viewer aspect: default;
		}
	}
}