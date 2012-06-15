model Grid
// proposed by Patrick Taillandier

global {
	init {    
		create goal number: 1 {
			set location <- point(one_of (list(cell)));
		}
		create people number: 10 {
			set target <- one_of (goal as list);
			set location <- point(one_of (list(cell)));
		}
	} 
}
environment bounds: {50,50} { 
	grid cell width: 50 height: 50 neighbours: 4 torus: false {
		rgb color <- rgb('white');
	} 
}
entities {
	species goal {
		aspect default { 
			draw shape: circle color: rgb('red') size: 0.5 ;
		}
	} 
	
	  
	species people skills: [moving] {
		goal target;
		float speed <- float(3);
		aspect default {
			draw shape: circle color: rgb('green') size: 0.5;
		}
		reflex reflex1 {
			let neighs type: list of: cell <- (cell(location) neighbours_at speed) + cell(location);
			let followed_path type: path <- self goto [on::cell, target::target, speed::speed, return_path::true];
			let path_geom type: geometry <- geometry(followed_path.segments);
			ask (neighs where (each.shape intersects path_geom)) {set color <- rgb('magenta');}
		}
	}
}

experiment goto_grid type: gui {
	output {
		display objects_display {
			grid cell lines: rgb('black');
			species goal aspect: default ;
			species people aspect: default ;
		}
	}
}
