model test_one_road

global {
	topology road_topo;

	init {
		create road with: [ shape :: polyline([ {5, 5} , {10, 10} ] ) ];
		
		set road_topo value: topology(as_edge_graph(list(road)));
	}
}

environment width: 100 height: 100;
//environment bounds: one_road_bounds;

entities {
	species pedestrian skills: moving {
		point goal;
		float speed <- 1;
		
		reflex when: (location != goal) {
			do goto {
				arg on value: road_topo;
				arg speed value: speed;
				arg target value: goal;
			}
		}
		
		aspect base {
			draw shape: geometry color: rgb('red');
		}
	}
	
	species road {
		init {
			create pedestrian with: [ location :: first(shape.points), goal :: last(shape.points) ];
		}

		reflex when: (time = 1) {
			do write {
				arg message value: name + ' with perimeter: ' + (string(perimeter));
			}
		} 
		
		aspect base {
			draw shape: geometry color: rgb('green');
		}		
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			species road aspect: base;
			species pedestrian aspect: base;
		}
	}
}