/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */
model si

global {
	int SimNumber <- 1 parameter : 'number of runs for statistics'; // the number of simulations
	int number_S <- 495 parameter : 'Number of Susceptible'; // The number of susceptible
	int number_I <- 5 parameter : 'Number of Infected'; // The number of infected
	int number_R <- 0 parameter : 'Number of Removed'; // The number of removed 
	float survivalProbability <- 1 / (70 * 365) parameter : 'Survival Probability'; // The survival probability
	float beta <- 0.1 parameter : 'Beta (S->I)'; // The parameter Beta
	float nu <- 0.00 parameter : 'Mortality'; // The parameter Nu
	float delta <- 0.01 parameter : 'Delta (I->R)'; // The parameter Delta
	int number_Hosts <- number_S + number_I + number_R;
	bool local_infection <- true parameter : 'Is the infection is computed locally?';
	int gridSize <- 1; //size of the grid
	float neighbourhoodSize <- 1.0; // average size of the neighbourhood
	int neighbours_size <- 2 min : 1 max : 5 parameter : 'Size of the neighbours';
	float average_ngb_number <- 1.0;
	bool randomWalk <- false parameter : 'Random Walk'; // 1: agents new positions are determined according to a random walk process, new position is in the neighbourhood;
	// 0: agents new position is selected randomly anywhere in the grid.
	float R0;
	float distance <- 0.0; //a distance measure between curves of ABM and EBM
	float adjust <- 1.0 min : 0.1 max : 10.0 parameter : 'Adjust curves'; // parameter to help adjust ABM curve with EBM curve
	float hKR4 <- 0.07;
	int iInit <- 1;
	bool moved <- false;
	init {
		set gridSize <- list(sir_grid) count (each);
		let nbCells type : int <- 0;
		loop cell over : list(sir_grid) {
			set nbCells <- nbCells + (cell.neighbours count (each));
		}

		set neighbourhoodSize <- nbCells / gridSize + 1; // +1 to count itself in the neighbourhood;
		set average_ngb_number <- neighbourhoodSize / gridSize;
		loop id from : 1 to : SimNumber {
			create Host number : number_S {
				set is_susceptible <- true;
				set is_infected <- false;
				set is_immune <- false;
				set color <- rgb('green');
				set simulationID <- id;
			}

			create Host number : number_I {
				set is_susceptible <- false;
				set is_infected <- true;
				set is_immune <- false;
				set color <- rgb('red');
				set simulationID <- id;
			}

			create Host number : number_R {
				set is_susceptible <- false;
				set is_infected <- false;
				set is_immune <- true;
				set color <- rgb('yellow');
				set simulationID <- id;
			}

			create sim_data number : 1 {
				set simulationID <- id;
				set self.total_S <- number_S;
				set self.total_I <- number_I;
			}

		}

		create statistic {
			self.mylist <- list(sim_data);
		}

		set R0 <- beta / (delta + nu);
		do write message : 'Basic Reproduction Number: ' + string(R0);
		create my_SIR_maths {
			self.S <- myself.number_S;
			I <- number_I;
			R <- number_R;
			N <- number_Hosts;
			beta1 <- beta * neighbourhoodSize * N / gridSize * adjust;
			self.alpha <- delta;
		}

	}

	//   reflex min_max  { 	
	//	   set min_S <- list(sim_data) min_of(each.total_S); 		
	//	   set max_S <- list(sim_data) max_of(each.total_S); 		
	//	   set min_I <- list(sim_data) min_of(each.total_I); 	
	//	   set max_I <- list(sim_data) max_of(each.total_I); 		   
	//     		write "in global "+min_S+" "+(Host as list) count (each.is_susceptible)/SimNumber;
	//   }
	reflex compute_distance {
		set distance <- distance + abs(((Host as list) count (each.is_susceptible) / SimNumber) - first(my_SIR_maths).S);
	}

}

environment width : 50 height : 50 {
	grid sir_grid width : 50 height : 50 {
		rgb color <- rgb('black');
		list neighbours of : sir_grid <- (self neighbours_at neighbours_size) of_species sir_grid;
	}

}

entities {
	species Host skills : [moving] {
		bool is_susceptible <- true;
		bool is_infected <- false;
		bool is_immune <- false;
		int simulationID <- 1; // can interact only with agents with same ID
		rgb color <- rgb('green');
		int sic_count <- 0;
		sir_grid myPlace;
		int ngb_number function: { ((myPlace.neighbours + myPlace) accumulate (each.agents)) of_species Host count (each.simulationID = simulationID) - 1 // -1 is because the agent counts itself
		};
		init {
			set myPlace <- one_of(sir_grid as list);
			set location <- myPlace.location;
		}

		reflex basic_move {
			if (randomWalk) {
				set myPlace <- one_of(myPlace.neighbours);
				set location <- myPlace.location;
				//				do wander amplitude:800;
				//				set myPlace <- first(sir_grid overlapping (self)) ;      		

			} else {
				set myPlace <- any(sir_grid);
				set location <- myPlace.location;
			}

			moved <- true;
		}

		reflex become_infected when: is_susceptible {
			let hst_ngb <- (((myPlace.neighbours + myPlace) accumulate (each.agents)) of_species Host) where (each.simulationID = simulationID);
			if (flip(1 - (1 - beta) ^ (hst_ngb count (each.is_infected)))) {
				set is_susceptible <- false;
				set is_infected <- true;
				set is_immune <- false;
				set color <- rgb('red');
			}

		}

		reflex become_immune when: (is_infected and flip(delta)) {
			set is_susceptible value: false;
			set is_infected value: false;
			set is_immune value: true;
			set color value: rgb('yellow');
		}

		reflex shallDie when: flip(nu) {
			create species(self) number: 1 {
				set myPlace <- myself.myPlace;
				set location <- myself.location;
			}

			do die;
		}

		aspect basic {
			draw circle(1) color : color;
		}

	}

	species sim_data {
		int simulationID <- 1;
		int total_S;
		int total_I;
		reflex count {
			let hsts <- list(Host) where (each.simulationID = simulationID);
			set self.total_S <- hsts count (each.is_susceptible);
			set self.total_I <- hsts count (each.is_infected);
			//			write "in sim" + " " + ( Host as list ) count ( each . is_susceptible ) /	SimNumber ;
			write "2";
			//			     		ask statistic{
			//			     		}

		}

	}

	species statistic {
		int min_S <- number_S;
		int max_S <- number_S;
		int min_I <- number_I;
		int max_I <- number_I;
		list<sim_data> mylist;
		action min_max {
		}

		reflex sss {
			set min_S <- mylist min_of (each.total_S);
			set max_S <- mylist max_of (each.total_S);
			set min_I <- mylist min_of (each.total_I);
			set max_I <- mylist max_of (each.total_I);
			write "in statistic " + self.min_S + " " + (Host as list) count (each.is_susceptible) / SimNumber;
		}
		//  	 reflex min_max  { 	
		//	   set min_S <- list(sim_data) min_of(each.total_S); 		
		//	   set max_S <- list(sim_data) max_of(each.total_S); 		
		//	   set min_I <- list(sim_data) min_of(each.total_I); 	
		//	   set max_I <- list(sim_data) max_of(each.total_I); 	
		//	  
		//     		write "in statistic "+self.min_S+" "+(Host as list) count (each.is_susceptible)/SimNumber+"\n";
		//   }
		reflex ss {
			write "1";
			//			ask sim_data{
			//			     		}

		}

	}

	species my_SIR_maths {
		float alpha <- 0.01 min : 0.0 max : 1.0;
		float beta1 <- 0.1 min : 0.0 max : 1000.0;
		int N <- 500 min : 1 max : 3000;
		int iInit <- 1;
		float t;
		float I <- float(iInit);
		float S <- N - I;
		float R <- 0.0;
		equation SIR {
			diff(S, t) = (-beta1 * S * I / N);
			diff(I, t) = (beta1 * S * I / N) - (alpha * I);
			diff(R, t) = (alpha * I);
		}

		solve SIR method : "rk4" step : 0.01 {
			float cycle_length <- 1;
			float t0 <- cycle - 1;
			float tf <- cycle;
		}

	}

}

experiment Simulation type : gui {
	output {
	//	    display sir_display {
	//	        grid sir_grid lines: rgb("black");
	//	        species Host aspect: basic;
	//	    }
		display chart refresh_every : 1 {
			chart 'Susceptible' type : series background : rgb('lightGray') style : exploded {
				data 'susceptible' value : (Host as list) count (each.is_susceptible) / SimNumber color : rgb('green');
				data 'infected' value : (Host as list) count (each.is_infected) / SimNumber color : rgb('red');
				data 'immune' value : (Host as list) count (each.is_immune) / SimNumber color : rgb('yellow');
				data 'min_S ' value : first(statistic).min_S color : rgb('pink');
				data 'max_S ' value : first(statistic).max_S color : rgb('orange');
				//				data min_I value: min_I color: rgb('red');
				//				data max_I value: max_I color: rgb('red');	
				//				data distance value: distance color: rgb('blue');			

			}

		}
		//		display SI refresh_every: 1 {
		//			chart "SI" type: series background: rgb('white') {
		//				data S value: first((my_SIR_maths)).S color: rgb('green') ;				
		//				data I value: first((my_SIR_maths)).I color: rgb('red') ;
		//				data R value: first((my_SIR_maths)).R color: rgb('yellow') ;				
		//			}
		//		}
		//		display Neighbours refresh_every: 1 {
		//			chart "Average neighbours number" type: series background: rgb('white'){
		//				data nb value: average_ngb_number color: rgb('blue');
		//			}
		//			
		//		}

	}

}

experiment Batch type: batch repeat: 1 keep_seed: true until: time = 10 {
	parameter name: 'adjust:' var: adjust min : 0.4 max: 1.6 step : 0.1;
	method hill_climbing iter_max : 20 minimize : distance;
}

experiment BatchExhaustive type: batch repeat : 1 keep_seed : true until: time = 6 {
	parameter name: 'adjust:' var: adjust min: 0.4 max: 1.6 step: 0.2;
	method exhaustive minimize : distance;
}

