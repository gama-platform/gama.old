model Grid
// proposed by Patrick Taillandier

global {
	init {    
		create goal{
			location <- point(one_of (list(cell)));
		}
		create people number: 10 {
			target <- one_of (goal as list);
			location <- point(one_of (list(cell)));
		}
	} 
}

entities {
	grid cell width: 100 height: 100 neighbours: 4 torus: false {
		rgb color <- rgb('white');
	} 
	species goal {
		aspect default { 
			draw circle(0.5) color: rgb('red');
		}
	} 
	
	  
	species people skills: [moving] {
		goal target;
		float speed <- float(3);
		aspect default {
			draw circle(0.5) color: rgb('green');
		}
		reflex move {
			list<cell> neighs <- (cell(location) neighbours_at speed) + cell(location);
			path followed_path <- self goto (on:cell, target:target, speed:speed, return_path:true);
			geometry path_geom <- geometry(followed_path.segments);
			ask (neighs where (each.shape intersects path_geom)) { color <- rgb('magenta');}
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
