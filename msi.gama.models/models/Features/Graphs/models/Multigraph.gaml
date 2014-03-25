/**
 *  multigraph
 *  Author: patricktaillandier
 *  Description: 
 */

model multigraph

global {
	file shape_file_in <- file('../includes/road.shp') ;
	file shape_file_bounds <- file('../includes/bounds.shp') ;
	geometry shape <- envelope(shape_file_bounds);
	graph road_graph; 
	graph friendship_graph <- graph([]);
	
	init {
		create road from: shape_file_in;
		road_graph <- as_edge_graph(road);
		create people number: 50 {
			add node:self to: friendship_graph;
		}
		loop times: 50 {
			people p1 <- one_of(people);
			people p2 <- one_of(list(people) - p1);
			create friendship_link  {
				add edge: (p1::p2)::self to: friendship_graph;
				shape <- link(p1::p2);
			}
		}
	}
}

species people skills: [moving]{
	point location <- any_location_in(one_of(road));
	people target<- one_of(people);
	float size <- 3.0;
	
	action updateSize {
		path friendship_path <- path_between(friendship_graph,self,target);
		if (friendship_path != nil) {
			size <-max([2,length( friendship_path.segments)]) as float;
		}
	}
	reflex movement {
		if (location distance_to target < 5.0) {
			target <- one_of(people);
			do updateSize;
		}
		do goto on:road_graph target:target speed:1 + rnd(2);
	}
	aspect base {
		draw circle(size) color: rgb("red");
	}	
}
	
species friendship_link {
	aspect base {
		draw shape color: rgb("blue");
	}
}
	
species road  {
	aspect base {
		draw shape color: rgb('black') ;
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
