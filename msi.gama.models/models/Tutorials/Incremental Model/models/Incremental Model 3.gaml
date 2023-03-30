/**
* Name: Integration of GIS Data
* Author:
* Description: third part of the tutorial : Incremental Model
* Tags: gis, chart, tutorial
*/
model model3

global {
	int nb_people <- 500;
	float agent_speed <- 5.0 #km / #h;
	float step <- 1 #minutes;
	float infection_distance <- 2.0 #m;
	float proba_infection <- 0.05;
	int nb_infected_init <- 5;
	file roads_shapefile <- file("../includes/road.shp");
	file buildings_shapefile <- file("../includes/building.shp");
	geometry shape <- envelope(roads_shapefile);
	int nb_people_infected <- nb_infected_init update: people count (each.is_infected);
	int nb_people_not_infected <- nb_people - nb_infected_init update: nb_people - nb_people_infected;
	float infected_rate update: nb_people_infected / nb_people;

	init {
		create road from: roads_shapefile;
		create building from: buildings_shapefile;
		create people number: nb_people {
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

species people skills: [moving] {
	bool is_infected <- false;

	reflex move {
		do wander;
	}

	reflex infect when: is_infected {
		ask people at_distance infection_distance {
			if flip(proba_infection) {
				is_infected <- true;
			}
		}
	}

	aspect default {
		draw circle(5) color: is_infected ? #red : #green;
	}
}

species road {

	aspect default {
		draw shape color: #black;
	}
}

species building {

	aspect default {
		draw shape color: #gray border: #black;
	}
}

experiment main_experiment type: gui {
	parameter "Infection distance" var: infection_distance;
	parameter "Proba infection" var: proba_infection min: 0.0 max: 1.0;
	parameter "Nb people infected at init" var: nb_infected_init;
	output {
		monitor "Current hour" value: current_date.hour;
		monitor "Infected people rate" value: infected_rate;
		display map {
			species road;
			species building;
			species people;
		}

		display chart refresh: every(10 #cycles)  type: 2d {
			chart "Disease spreading" type: series style: spline {
				data "susceptible" value: nb_people_not_infected color: #green marker: false;
				data "infected" value: nb_people_infected color: #red marker: false;
			}
		}
	}
}
