/**
 *  model6
 *  This model illustrates EDO
 */ 
model model6 

global {
	file roads_shapefile <- file("../includes/road.shp");
	file buildings_shapefile <- file("../includes/building.shp");
	geometry shape <- envelope(roads_shapefile);
	graph road_network;
	
	float beta <- 0.4;
	
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
	int nb_I -> {members count (people_in_building(each).is_infected)};
	int nbInhabitants update: length(members);				
	list<people_in_building> membersS <- [] update: list<people_in_building>(members) where (!each.is_infected);
	list<people_in_building> membersI <- [] update: list<people_in_building>(members) where (each.is_infected);
	float t;    
	float S update: length(membersS) as float; 
   	float I update: length(membersI) as float;
   	float I_to_1 <- 0.0;
   	float h<-0.1;
   	
	aspect geom {
		draw shape color: empty(members) ? rgb("gray") : (nb_I/length(members) > 0.5 ? rgb("red") : rgb("green")) depth: height;
	}
	species people_in_building parent: people schedules: [] {
		int leaving_time;
		aspect circle{}
	}
	reflex let_people_enter {
		list<people> entering_people <- (people inside self);
		if !(empty (entering_people)) {
			capture entering_people as: people_in_building returns: people_captured;
			ask people_captured {
				leaving_time <- int(time + 50 + rnd(50));
			}
 		}
	}
	reflex let_people_leave  {
		list<people_in_building> leaving_people <- list<people_in_building>(members) where (time >= each.leaving_time);
		if !(empty (leaving_people)) {
			release leaving_people as: people in: world;
		}
	}
			
	equation SIR{ 
		diff(S,t) = (- beta * S * I / nbInhabitants) ;
		diff(I,t) = (  beta * S * I / nbInhabitants) ;
	}

	reflex epidemic when:(S>0 and I>0){ 	
		float I0 <- I;
    	solve SIR method: "rk4" step: h ;
    	I_to_1 <- I_to_1 + (I - I0);
    	if(I_to_1 > 1) {
    		ask(membersS){
    			is_infected <- true;
    			myself.I_to_1 <- myself.I_to_1 - 1;
    		}
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