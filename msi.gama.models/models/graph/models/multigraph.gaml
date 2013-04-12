/**
 *  multigraph
 *  Author: patricktaillandier
 *  Description: 
 */

model multigraph

global {
	file shape_file_in <- file('../includes/road.shp') ;
	file shape_file_bounds <- file('../includes/bounds.shp') ;
	
	graph road_graph; 
	
	//SOL 1
	/*graph<people, friendship_link> friendship_graph <- empty_graph(friendship_link, people);
	init {
		create road from: shape_file_in;
		create people number: 100 {
			add vertex: self to: friendship_graph;
		}
		loop times: 200 {
			people p1 <- one_of(people);
			people p2 <- one_of(list(people) - p1);
			
			add edge: p1::p2 to: friendship_graph;
		}
		
	}*/
	
	//SOL 2 
	graph friendship_graph <- graph([]);
	init {
		create road from: shape_file_in;
		create people number: 10 {
			add vertex: self to: friendship_graph;
		}
		loop times: 10 {
			people p1 <- one_of(people);
			people p2 <- one_of(list(people) - p1);
			if flip(0.5) {
				create friendship_link returns: fls;
				add edge: (p1::p2)::first(fls) to: friendship_graph;
			
			}
			else {add edge: p1::p2 to: friendship_graph;}
		}
		
	}
}

environment bounds: shape_file_bounds;

entities {
	species people skills: [moving]{
		point location <- any_location_in(one_of(road));
		point target<- any_location_in(one_of(road));
		path my_path; 
	
		reflex movement {
			write "in_edges_of : " + friendship_graph in_edges_of self;
			if (location = target) {
				target <- any_location_in(one_of(road));
			}
			do goto on:road_graph target:target speed:1;
		}
		aspect base {
			/*loop fd over: friendship_graph neighbours_of self {
				draw line([location, people(fd).location]) color: rgb("black");
			}*/
			draw circle(5) color: rgb("red");
		}
		
	}
	
	species friendship_link {
		geometry shape  update: new_shape();
		action new_shape {
			return line([people(friendship_graph source_of(self)).location, people(friendship_graph target_of(self)).location]);
		}
		aspect base {
			draw shape color: rgb("blue");
		}
	}
	
	species road  {
		aspect base {
			draw geometry: shape color: rgb('black') ;
		}
	} 
}

experiment multigraph type: gui {
	output {
		display friendship {
			species road aspect: base;
			species friendship_link aspect: base;
			species people aspect: base;
		}
	}
}
