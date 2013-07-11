/**
 *  model6
 *  Author: hqnghi
 *  Description: 
 * SIR model , people are contacting when travelling on roads_graph ( running in about 5000 simulation step)
 */ model model6  
 global {
	file shape_file_bounds <- file('../includes/bounds.shp');
	file shape_file_buildings <- file('../includes/building.shp');
	file shape_file_roads <- file('../includes/road.shp');
	geometry shape <- envelope(shape_file_bounds);
	graph roads_graph;
	int initial_S <- 495 parameter: 'Number of Susceptible';  // The number of susceptible
    int initial_I <- 5 parameter: 'Number of Infected';	// The number of infected
    int initial_R <- 0 parameter: 'Number of Removed';	// The number of removed 
	
	bool computeInfectionFromS <- bool(initial_S < initial_I);
	float beta <- 0.1 parameter: 'Beta (S->I)'; 	// The parameter Beta
	
	float delta <- 0.01 parameter: 'Delta (I->R)'; // The parameter Delta
	
	init {
		create Buildings from: shape_file_buildings with: [type:: string(read('NATURE')), company::string(read('COMPANY'))] {
			if type = 'Industrial' {
				my_color <- rgb('blue');
			} else if type = 'Residential' {
				my_color <- rgb('red');
			}

		}

		create Roads from: shape_file_roads;
		roads_graph <- as_edge_graph(Roads as list);
		create Workers number: round(initial_S) {
			is_susceptible <- true;
	        is_infected <-  false;
	        is_immune <-  false; 
	        my_color <-  rgb('green');
			do wanna_go_to_work;
			do moving;
		}
		
		create Workers number: round(initial_I) {
			is_susceptible <- false;
	        is_infected <-  true;
	        is_immune <-  false; 
	        my_color <-  rgb('red');
			do wanna_go_to_work;
			do moving;
		}
		
		create Workers number: round(initial_R) {
			is_susceptible <- false;
	        is_infected <-  false;
	        is_immune <-  true; 
	        my_color <-  rgb('yellow');
			do wanna_go_to_work;
			do moving;
		}

	}

}

entities {
	species Buildings {
		string type;
		string company;
		rgb my_color;
		aspect asp1 {
			draw shape color: my_color;
		}

	}

	species Roads {
		aspect normal {
			draw shape color: rgb('black');
		}

	}

	species Workers skills: [moving] {
		bool is_susceptible <- true;
		bool is_infected <- false;
        bool is_immune <- false;
		rgb my_color;
		Buildings my_target;
		int transportation <- 50+rnd(10); // moving speed depend on type of transportation
 		int threshold_time <- 200+rnd(500);
		int flag_time <- 0;
		aspect asp1 {
			draw circle(5) color: my_color;
		}

		action wanna_go_to_work {
			my_target <- any(Buildings where (each.type = 'Industrial'));
		}

		action wanna_go_home {
			my_target <- any(Buildings where (each.type = 'Residential'));
		}

		action moving {
			do goto target: my_target on: roads_graph speed: 5 + transportation;
			if (location = my_target.location and flag_time = 0) {
				flag_time <- cycle;
			}

		}

		reflex day_life {
			do moving;
			if (flag_time > 0 and cycle - flag_time >= threshold_time) {
				flag_time <- 0;
								
				if (my_target.type = 'Industrial') {
					do wanna_go_home;
				} else {
					do wanna_go_to_work;
				}				
				threshold_time <- 200+rnd(500);
			}

		}
		reflex become_infected when: (is_susceptible and flag_time=0) {
//        		if (flip(1 - (1 - beta)  ^ (((myPlace.neighbours + myPlace) collect (each.agents)) of_species Host as list) count (each.is_infected))) {
//				write ""+agents_overlapping(self) of_species Workers where (each.is_infected);
	        	if (flip(1 - (1 - beta)  ^ ((agents_at_distance (1)) of_species Workers) count (each.is_infected) * 100)) {
	        		is_susceptible <-  false;
	            	is_infected <-  true;
	            	is_immune <-  false;
	            	my_color <-  rgb('red');       	
	        }
        }
        
        reflex infecte_others when: (is_infected and flag_time=0) { // flag_time =0 : peoples are not in building (not in working time and relaxing time) 
//          		loop hst over: ((myPlace.neighbours + myPlace) collect (each.agents)) of_species Host{
          			loop hst over: ( agents_at_distance (1)) of_species Workers{
        			if (Workers(hst).is_susceptible){
        				if(flip(beta)){
			 	        	hst.is_susceptible <-  false;
				            hst.is_infected <-  true;
				            hst.is_immune <-  false;
				            hst.my_color <-  rgb('red');     		
        				}    				
        			}
        		}
        }
        
        reflex become_immune when: (is_infected and flip(delta) and flag_time=0) {
        	is_susceptible <- false;
        	is_infected <- false;
            is_immune <- true;
            my_color <- rgb('yellow');
        }

	}

}

experiment exp1 type: gui {
	output {
		display disp1 refresh_every: 1 {
			species Buildings aspect: asp1;
			species Roads aspect: normal;
			species Workers aspect: asp1;
		}
		
		
	    display 'Time series' refresh_every: 1 {
			chart 'Susceptible' type: series background: rgb('lightGray') style: exploded {
				data 'susceptible' value: Workers count(each.is_susceptible) color: rgb('green');
				data 'infected' value: Workers count(each.is_infected) color: rgb('red');
				data 'immune' value: Workers count(each.is_immune) color: rgb('yellow');
			}
		
		}
		
		

	}

}