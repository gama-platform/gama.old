/**
 *  model5
 *  This model illustrates EDO
 */ 
model model5 

global {
	file roads_shapefile <- file("../includes/road.shp");
	file buildings_shapefile <- file("../includes/building.shp");
	geometry shape <- envelope(roads_shapefile);
	graph road_network;
	float alpha <- 0.1;
	float beta <- 0.4;
	
	init {
		create roads from: roads_shapefile;
		road_network <- as_edge_graph(roads);
		create buildings from: buildings_shapefile;
		create people number:1000 {
			buildings init_place <- one_of(buildings);
			location <- any_location_in(init_place) add_z init_place.height;
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

species buildings skills: [EDP]{
	float height <- 10.0+ rnd(10);
	int population_init;
	int nbInhabitants update: length(members);				
	list<people_in_building> membersS <- [] update: members where (!(each as people_in_building).is_infected);
	list<people_in_building> membersI <- [] update: members where ((each as people_in_building).is_infected);
	int nbI update: length(membersI);
	
	aspect geom {
		draw shape color: rgb("gray") depth: height;
	}
	species people_in_building parent: people schedules: [] {
		int leaving_time;
		aspect circle{}
	}
	reflex aggregate {
		list<people> entering_people <- (people inside self);
		if !(empty (entering_people)) {
			capture entering_people as: people_in_building returns: people_captured;
			ask people_captured {
				leaving_time <- time + 25 + rnd(25);
			}
 		}
	}
	reflex disaggregate  {
		list<people_in_building> leaving_people <- (list (members)) where (time >= (people_in_building (each)).leaving_time);
		if !(empty (leaving_people)) {
			release leaving_people as: people in: world;
		}
	}
	reflex epidemic {
		ask (membersI where flip(alpha)) {
			is_infected <- false;
		}
		ask (membersS where flip(beta*nbI/nbInhabitants)) {
			 is_infected <- true;
			 }  		
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