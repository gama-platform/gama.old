/**
 *  model4
 *  This model illustrates 3D visualization
 */ 
model model4 

global {
	file roads_shapefile <- shape_file("../includes/road.shp");
	file buildings_shapefile <- file("../includes/building.shp");
	geometry shape <- envelope(roads_shapefile);
	graph road_network;
	init {
		create roads from: roads_shapefile;
		road_network <- as_edge_graph(roads);
		create buildings from: buildings_shapefile;
		create people number:1000 {
			buildings init_place <- one_of(buildings);
			location <- any_location_in(init_place) + {0,0, init_place.height};
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
		draw sphere(5) color:is_infected ? rgb("red") : rgb("green");
	}
}

species roads {
	aspect geom {
		draw shape color: rgb("black");
	}
}

species buildings {
	float height <- 10.0+ rnd(10);
	aspect geom {
		draw shape color: rgb("gray") depth: height;
	}
}

experiment main_experiment type:gui{
	output {
		display map type: opengl ambient_light: 150{
			species roads aspect:geom;
			species buildings aspect:geom;
			species people aspect:circle;			
		}
	}
}