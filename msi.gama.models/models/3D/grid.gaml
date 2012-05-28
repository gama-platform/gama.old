
model Grid
// proposed by Patrick Taillandier



global {
	init {    
		create goal number: 1 {
			set location <- one_of (list(cell));
		}
		create people number: 10 {
			set target <- one_of (goal as list);
			set location <- one_of (list(cell));
		}
	} 
}
environment bounds: {50,50} { 
	grid cell width: 50 height: 50 neighbours: 4 torus: false frequency: 1 {
		rgb color <- 'white';
	} 
}

  

entities {
	species goal {
		aspect default { 
			draw shape: circle color: 'red' size: 0.5 ;
		}
	} 
	species people skills: [moving] {
		goal target;
		float speed <- 3;
		aspect default {
			draw shape: circle color: 'green' size: 0.5;
		}
		reflex {
			let neighs type: list of: cell <- nil;
			ask cell(location) { set neighs <- (self neighbours_at myself.speed);}
			add cell(location) to: neighs;
			let followed_path type: path <- self goto [on::cell, target::target, speed::speed, return_path::true];
			let path_geom type: geometry <- followed_path.segments;
			ask (neighs where (each.shape intersects path_geom)) {set color <- 'magenta';}
		}
	}
}


output {


		display objects_displaygl type:opengl {
		grid cell lines: 'black';
		species goal aspect: default ;
		species people aspect: default ;
	}
}
