/**
 *  model3
 *  This model illustrates how to use spatial operator
 */ 
model model3 
 
global {
	file roads_shapefile <- file("../includes/road.shp");
	file buildings_shapefile <- file("../includes/building.shp");
	geometry shape <- envelope(roads_shapefile);
	graph road_network;
	init {
		create roads from: roads_shapefile;
		road_network <- as_edge_graph(roads);
		create buildings from: buildings_shapefile;
		create people number:1000 {
			location <- any_location_in(one_of(buildings));
			target <- any_location_in(one_of(buildings));
		}
	}
}

species people skills:[moving]{		
	float speed <- 5.0 + rnd(5);
	bool is_infected <- flip(0.01);
	point target;
	reflex move {
		do goto target:target on: road_network;
		if (location = target) {
			target <- any_location_in(one_of(buildings));
		}
	}
	reflex infect when: is_infected{
		ask people at_distance 10 {
			if flip(0.01) {
				is_infected <- true;
			}
		}
	}
	aspect circle{
		draw circle(5) color:is_infected ? rgb("red") : rgb("green");
	}
}

species roads {
	aspect geom {
		draw shape color: rgb("black");
	}
}

species buildings {
	aspect geom {
		draw shape color: rgb("gray");
	}
}

experiment main_experiment type:gui{
	output {
		display map {
			species roads aspect:geom;
			species buildings aspect:geom;
			species people aspect:circle;			
		}
	}
}