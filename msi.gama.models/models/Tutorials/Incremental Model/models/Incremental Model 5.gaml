/**
* Name: 3D visualization
* Author: GAMA team
* Description: 5th part of the tutorial : Incremental Model
* Tags: tutorial, chart, graph, 3d, light
*/

model model5 
 
global {
	int nb_people <- 500;
    float agent_speed <- 5.0 #km/#h;			
	float step <- 1 #minutes;
	float infection_distance <- 2.0 #m;
	float proba_infection <- 0.05;
	int nb_infected_init <- 5;
	file roads_shapefile <- file("../includes/road.shp");
	file buildings_shapefile <- file("../includes/building.shp");
	geometry shape <- envelope(roads_shapefile);
	graph road_network;
	float staying_coeff update: 10.0 ^ (1 + min([abs(current_date.hour - 9), abs(current_date.hour - 12), abs(current_date.hour - 18)]));
	int nb_people_infected <- nb_infected_init update: people count (each.is_infected);
	int nb_people_not_infected <- nb_people - nb_infected_init update: nb_people - nb_people_infected;
	bool is_night <- true update: current_date.hour < 7 or current_date.hour > 20;	
	float infected_rate update: nb_people_infected / nb_people;
	
	init {
		create road from: roads_shapefile;
		road_network <- as_edge_graph(road);
		create building from: buildings_shapefile;
		create people number:nb_people {
			speed <- agent_speed;
			location <- any_location_in(one_of(building));
		}
		ask nb_infected_init among people {
			is_infected <- true;
		}
	}
	reflex end_simulation when: infected_rate = 1.0 {
		do pause;
	}
}

species people skills:[moving]{		
	bool is_infected <- false;
	point target;
	int staying_counter;
	reflex stay when: target = nil {
		staying_counter <- staying_counter + 1;
		if flip(staying_counter / staying_coeff) {
			target <- any_location_in (one_of(building));
		}
	}
		
	reflex move when: target != nil{
		do goto target:target on: road_network;
		if (location = target) {
			target <- nil;
			staying_counter <- 0;
		} 
	}
	reflex infect when: is_infected{
		ask people at_distance infection_distance {
			if flip(proba_infection) {
				is_infected <- true;
			}
		}
	}
	aspect default{
		draw circle(5) color:is_infected ? #red : #green;
	}
	aspect sphere3D{
		draw sphere(3) at: {location.x,location.y,location.z + 3} color:is_infected ? #red : #green;
	}
}

species road {
	geometry display_shape <- shape + 2.0;
	aspect default {
		draw display_shape color: #black depth: 3.0;
	}
}

species building {
	float height <- rnd(10#m, 20#m) ;
	
	aspect default {
		draw shape color: #gray border: #black depth: height;
	}
}

experiment main_experiment type:gui{
	parameter "Infection distance" var: infection_distance;
	parameter "Proba infection" var: proba_infection min: 0.0 max: 1.0;
	parameter "Nb people infected at init" var: nb_infected_init ;
	output {
		monitor "Current hour" value: current_date.hour;
		monitor "Infected people rate" value: infected_rate;
		display map_3D type: 3d {
			light #ambient intensity: 20;
			light #default intensity:(is_night ? 127 : 255);
			image "../includes/soil.jpg";
			species road ;
			species people aspect:sphere3D;			
			species building  transparency: 0.5;
		}
		display chart refresh: every(10#cycles)  type: 2d {
			chart "Disease spreading" type: series style: spline {
				data "susceptible" value: nb_people_not_infected color: #green marker: false;
				data "infected" value: nb_people_infected color: #red marker: false;
			}
		}
	}
}