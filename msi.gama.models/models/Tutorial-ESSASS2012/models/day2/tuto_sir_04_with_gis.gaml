model tuto_sir_04_with_GIS

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
			create people_in_city number: (population_init - nb_infected) returns: listS {
				set state <- "S";
			} 
		}
		
		ask one_of(city) {
			create people_in_city number: nb_infected returns: listI {
				set state <- "I";			
			}
		}
	}
}

environment bounds: shape_file_dept {}

entities {
	species people skills: [moving]{
		city target;
		string state;   // Can be "S", "I" or "R"		
		
		reflex move when: (target != nil) {
			do goto target: target on: graphRoad speed: 15000;
			if(location = target.location) {
				ask target {
					capture myself as: people_in_city;
				}
			}
		}
		
		aspect display {
			draw circle(20000.0) color: (state = "S" ) ? rgb('green') : ((state = "I") ? rgb('red') : rgb('blue'));
		}
	}
	species city {
		int population_init;
		int nbInhabitants update: length(members);				
		int nbI update: length(members where ((each as people_in_city).state = "I"));
		list membersS of: people_in_city <- [] update: members where ((each as people_in_city).state = "S");
		list membersI of: people_in_city <- [] update: members where ((each as people_in_city).state = "I");
		list membersR of: people_in_city <- [] update: members where ((each as people_in_city).state = "R");		
		
		reflex emigration when: flip(emigration_rate){	
			release agent(any(members)) as: people in: world {
				set target <- any(city);
			}
		}

		reflex epidemic {
			ask (membersI where flip(alpha)) {
				set state <- "R";
			}
			ask (membersS where flip(beta*nbI/nbInhabitants)) {
			 	set state <- "I";
			 }  		
		}
		
		aspect epidemic_state {
			draw circle((nbI = 0) ? 20000.0 : ln(nbI)*10000 + 20000.0) color: (nbI != 0) ? rgb('red') : rgb('green') ;
		}
		
		species people_in_city parent: people schedules: [] { }
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

experiment tuto_sir type: gui {
	parameter 'alpha:' var: alpha category: 'Epidemic';
	parameter 'beta:' var: beta category: 'Epidemic';
	parameter 'Number of infected' var: nb_infected category: 'Population';
	parameter 'Emigration rate' var: emigration_rate category: 'City';
	
	output {
		monitor 'Number of people agents' value: length(list(people));
		
		display people {
			species people aspect: display;
			species city aspect: epidemic_state {}
			species road;			
		}
		display shpFiles {
			species dept;					
			species city aspect: epidemic_state;	
		}		
	}
}
