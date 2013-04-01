model tuto_sir_03_with_GIS

global {
	// Variables related to graph
	graph graphRoad;
	float emigration_rate <- 0.1;	
	
	// Variables related to the disease
	float alpha <- 0.1;
	float beta <- 0.4;
	int nb_infected <- 1;
	
	// Variables for shapefiles
	file shape_file_cities <- file('../../includes/cities.shp');
	file shape_file_roads <- file('../../includes/highways.shp');
	file shape_file_dept <- file('../../includes/depts.shp');
		
	init {	
		create city from: shape_file_cities with: [population_init::int(read('population'))];
		create road from: shape_file_roads;
		create dept from: shape_file_dept;
		
		set graphRoad <- as_edge_graph(list(road));
		
		ask list(city) {
			create people number: (population_init - nb_infected) returns: listS {
				set state <- "S";
				set ownCity <- myself;
				set location <- myself.location;
				
			}
			set inhabitants <- list(listS);
		}
		
		ask one_of(city) {
			create people number: nb_infected returns: listI {
				set state <- "I";
				set ownCity <- myself;				
				set location <- myself.location;				
			}
		set inhabitants <- inhabitants + list(listI);
		}
	}
}

environment bounds: shape_file_dept {}

entities {
	species people skills: [moving]{
		city ownCity;
		city target;
		string state;   // Can be "S", "I" or "R"		
				
		reflex epidemic when: ((state != "R") and (ownCity != nil) ){
			if(state = "I" and flip(alpha)) {set state <- "R";}
			if(state = "S" and flip(beta*length(ownCity.inhabitants where (each.state = "I"))/ownCity.nbInhabitants)) {
			 set state <- "I";}  	
		}
		
		reflex move when: (target != nil) {
			do goto target: target on: graphRoad speed: 15000;
			if(location = target.location) {
				set ownCity <- target;
				ask ownCity {add myself to: inhabitants;}
				set target <- nil;
			}
		}
		
		aspect display {
			draw circle(20000.0) color: (state = "S" ) ? rgb('green') : ((state = "I") ? rgb('red') : rgb('blue'));
		}
	}
	species city {
		int population_init;
		list inhabitants of: people;
		int nbInhabitants <- population_init update: length(inhabitants);				
		int nbI update: length(inhabitants where (each.state = "I"));
		
		reflex emigration when: flip(emigration_rate){
			ask any(inhabitants) {
				set target <- any(city);
				remove self from: myself.inhabitants;
				set ownCity <- nil;
			}
		}
		
		aspect epidemic_state {
			draw circle((nbI = 0) ? 20000.0 : ln(nbI)*10000 + 20000.0) color: (nbI = 0) ? rgb('green') : rgb('red');
		}
	}
	species road {
		aspect default {
			draw geometry: shape color: rgb('red');
		}
	}
	species dept {
		aspect default {
			draw geometry: shape color: rgb('black');
		}
	}
}

experiment tuto_sir_01 type: gui {
	parameter 'alpha:' var: alpha category: 'Epidemic';
	parameter 'beta:' var: beta category: 'Epidemic';
	parameter 'Number of infected' var: nb_infected category: 'Population';
	parameter 'Emigration rate' var: emigration_rate category: 'City';
	
	
	output {
		display Charts {
			chart name: 'Global happiness and similarity' type: series background: rgb('lightGray') {
				data s_serie color: rgb('green') value: length(list(people) where (each.state = "S"));
				data i_serie color: rgb('red') value: length(list(people) where (each.state = "I"));
				data r_serie color: rgb('blue') value: length(list(people) where (each.state = "R"));
			}			
		}
		
		display people {
			species people aspect: display;
			species city aspect: epidemic_state;
			species road aspect: default;			
		}
		display shpFiles {
			species dept;					
			species city aspect: epidemic_state;		
		}
	}
}
