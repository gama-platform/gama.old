/**
 *  multigraph
 *  Description: 
 */

model multigraph

global {
	file shape_file_in <- file('../includes/road.shp') ;
	file shape_file_bounds <- file('../includes/bounds.shp') ;
	geometry shape <- envelope(shape_file_bounds);
	graph road_graph; 
  
  graph friendship_graph;
	
	init {
		//Road graph creation
		create road from: shape_file_in;
		road_graph <- as_edge_graph(road);
		
		//Friendship graph creation 
		friendship_graph <- generate_barabasi_albert(people,friendship_link,100,1);		
	}
}

entities {
	species people skills: [moving]{
		rgb my_color<-rgb(rnd(2)*120,rnd(1)*250,rnd(2)*120);
		point location <- any_location_in(one_of(road));
		people target<- one_of(people);
		float size ;
		action updateSize {
			size <- 10*friendship_graph degree_of (self);
		}
		reflex movement {
			if (location distance_to target < 5.0) {
				target <- one_of(people);
				do updateSize;
			}
			do goto on:road_graph target:target speed:1 + rnd(2);
		}
		aspect base {
			draw circle((friendship_graph degree_of (self))) color:my_color;
		}
	}
	
	species friendship_link {
		rgb my_color;
		aspect base {
			draw shape color: rgb('black') ;			
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
