/**
* Name: Multigraph
* Author: Patrick Taillandier
* Description: This model shows how to build a graph on which people agents will move with GIS Shapefile, but also to generate 
* an other graph representing the friendship between the people agents, people agents trying to be closer spatially to each other
* Tags: graph, load_file, skill
*/

model multigraph

global {
	file shape_file_in <- file('../includes/road.shp') ;
	file shape_file_bounds <- file('../includes/bounds.shp') ;
	geometry shape <- envelope(shape_file_bounds);
	
	//spatial graph representing the road network
	graph road_graph; 
	
	//social graph (not spatial) representing the friendship links between people
	graph friendship_graph <- graph([]);
	
	init {
		create road from: shape_file_in;
		
		//creation of th road graph from the road agents
		road_graph <- as_edge_graph(road);
		
		//creation of 50 people agent, and add each people agent as a node in the friendship graph
		create people number: 50 {
			add node(self) to: friendship_graph;
		}
		
		//creation of 50 friendship link between people agents
		loop times: 50 {
			people p1 <- one_of(people);
			people p2 <- one_of(list(people) - p1);
			create friendship_link  {
				add edge (p1, p2, self) to: friendship_graph;
				shape <- link(p1,p2);
			}
		}
	}
}

species people skills: [moving]{
	float size <- 3.0;
	people target_people;
	point target;
	
	init {
		location <- any_location_in(one_of(road));
		target_people <- one_of(people - self);
		target <- target_people.location;
	
	}
	
	//action that make recompute the size of the agents as the distance between it and its target people in the friendship graph (the farthest, the biggest)
	action updateSize {
		path friendship_path <- path_between(friendship_graph,self,target_people);
		if (friendship_path != nil) {
			size <-max([2,length( friendship_path.edges)]) as float;
		}
	}
	
	//the agent moves toward its target, when reaching it, it chooses another target as the location of one of the people agent
	reflex movement {
		if (location distance_to target < 5.0) {
			target_people <- one_of(people - self);
			target <- target_people.location;
			do updateSize;
		}
		do goto on:road_graph target:target speed:1 + rnd(2.0);
	}
	aspect default {
		draw circle(size) color: #red;
	}	
}
	
species friendship_link {
	
	aspect default {
		draw shape color: #blue;
	}
}
	
species road  {
	aspect default {
		draw shape color:#black ;
	}
} 


experiment multigraph type: gui {
	output {
		display friendship type: 3d{
			species road ;
			species friendship_link ;
			species people;
		}
	}
}
