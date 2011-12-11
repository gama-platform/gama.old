model Grid
// proposed by Patrick Taillandier


global {
	init {    
		create species: but number: 1 {
			set location value: one_of (list(cell));
		}
		create species: people number: 10 {
			set goal value: one_of (but as list);
			set location value: one_of (list(cell));
		}
	}
}
environment bounds: {50,50} {
	grid cell width: 50 height: 50 neighbours: 4 torus: false frequency: 1 {
		var color type: rgb init:'white';
	}
}
entities {
	species but {
		aspect default {
			draw shape: circle color: 'red' size: 0.5 ;
		}
	}
	species people skills: [moving] {
		var goal type: but ;
		var speed type: float init: 3;
		aspect default {
			draw shape: circle color: 'green' size: 0.5;
		}
		reflex {
			
			let neighs type: list of: cell value: nil;
			ask target: cell(location) { set neighs value: self neighbours_at myself.speed;}
			add item: cell(location) to: neighs;
			let followed_path type: path value: self.goto [on::cell, target::goal.location, speed::speed];
			let path_geom type: geometry value: followed_path.segments;
			ask target: (neighs where (each.shape intersects path_geom)) {
				set color value: 'magenta';
			}
		}
	}
}
output {
	display objects_display {
		grid cell lines: 'black';
		species but aspect: default ;
		species people aspect: default ;
	}
}
