model simple_hybrid_model

global {
	/** GIS data */
	var shape_file_road type: string init: '/gis/two_roadlines.shp';
	var shape_file_bounds type: string init: '/gis/two_roadlines_bounds.shp';
	var shape_file_destination type: string init: '/gis/two_roadlines_destination.shp';
	var shape_file_people type: string init: '/gis/two_roadlines_people.shp';

	const building_colors type: list init: ['orange', 'red', 'blue', 'black', 'gray', 'magenta'];
	var the_graph type: graph;
	
	init {
		create species: road from: shape_file_road with: [ fid :: read('FID') ] returns: the_roads;
		create species: destination from: shape_file_destination with: [fid :: read ('IND')];
		set the_graph value: as_edge_graph (list(road));
//		set the_graph value: as_edge_graph (list(road) collect (each.shape));
		
		create species: people from: shape_file_people { 
			set goal value: one_of (destination as list) ;
		}
	}
}

entities {
	species road skills: situated {
		var fid type: int;
		
		aspect default {
			draw shape: geometry color: 'yellow';
		}
	}

	species destination skills: situated {
		var fid type: int;
		var color type: rgb init: rgb (one_of (building_colors));
		
		aspect default {
			draw shape: geometry color: color;
		}
	}

	species people skills: [moving] {
		var goal type: destination ;
		var my_path type: path;
	
		aspect default {
			draw shape: circle color: 'green' size: 0.0001 ;
		}

		reflex move when: (goal != nil) {
			let followedPath type: path value: self.goto [on::the_graph, target::goal.location, speed::0.000005];
			let segments type: list of: geometry value: followedPath.segments;
			
			/*
			do action: write {
				arg name: message value: 'length segments: ' + (length (segments));
			}
			*/
			
			loop line over: segments {
				let ag type: road value: followedPath agent_from_geometry line;
				if condition: (ag != nil) {
					do action: write {
						arg name: message value: 'ag: ' + (ag) ;
					}
					
					else {
						do action: write {
							arg name: message value: '(followedPath agent_from_geometry line) returns NIL';
						}
					}
				}
			}
			
			if condition: (goal.location = location) {
				set goal value: nil ;
			}
		}
	}
}

environment bounds: shape_file_bounds;

experiment default_expr type: gui {
	output {
		display default_display {
			species road aspect: default transparency: 0.1;
			species destination aspect: default transparency: 0.1;
			species people aspect: default;
		}
	}
}
