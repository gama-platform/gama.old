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
		create people number: 100 {
			add vertex: self to: friendship_graph;
		}
		loop times: 200 {
			people p1 <- one_of(people);
			people p2 <- one_of(list(people where (each.my_color=p1.my_color)) - p1);
			if flip(0.5) {
				create friendship_link  {
					my_color<-p1.my_color;
					add edge: (p1::p2)::self to: friendship_graph;
					shape <- new_shape();
				}
				
			
			}
			else {add edge: p1::p2 to: friendship_graph;}
		}
		ask people {
			 do updateSize;
		}
		
	}
}

entities {
	species people skills: [moving]{
		rgb my_color<-rgb(rnd(2)*120,rnd(1)*250,rnd(2)*120);
		point location <- any_location_in(one_of(road));
		people target<- one_of(people);
		float size ;
		action updateSize {
			path friendship_path <- friendship_graph path_between(self:: target); 
			size <-max([8,5+length( friendship_path.edges)]);
		}
		reflex movement {
			if (location distance_to target < 5.0) {
				target <- one_of(people);
				do updateSize;
			}
			do goto on:road_graph target:target speed:1 + rnd(2);
		}
		aspect base {
			draw circle(size) color:my_color;
		}
		
	}
	
	species friendship_link {
		rgb my_color;
		geometry shape update: new_shape();
		action new_shape {
			return line([people(friendship_graph source_of(self)).location, people(friendship_graph target_of(self)).location]);
		}
		aspect base {
			draw shape color: my_color;
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
