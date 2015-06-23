/**
 *  SIR_switch.gaml
 *  Author: tri and nghi
 *  Description: A compartmental SI model 
 */
model SIR_switch

global {
	// Parameters
	int initial_S <- 495 ; // The number of susceptible
	int initial_I <- 5   ; // The number of infected
	int initial_R <- 0   ; // The number of removed 

	float beta <- 0.1   ; // The parameter Beta 
	float delta <- 0.01 ; // The parameter Delta	
	
	int switch_threshold <- 120 ; // threshold for switching models
	bool local_infection <- true ;
	int neighbours_range <- 2 ;
	bool local_random_walk <- true ; 
	// true: agents new positions are determined according to a random walk process, 
	// new position is in the neighbourhood;
	// false: agents new position is selected randomly anywhere in the grid.
	
	
	// Global variables
	int grid_size <- 50;
	int number_Hosts <- initial_S + initial_I + initial_R; // Total number of individuals
	SIR_model current_model; // serves as an interface, it is transparent to user if model is maths or IBM

	float beta_maths;
	int gridSize <- 1; //size of the grid
	float neighbourhoodSize <- 1.0; // average size of the neighbourhood (in number of cells)	
	float adjust <- 0.721; // to adjust math model to ABM when using random walk
	bool computeInfectionFromS <- initial_S < initial_I; // if true, use the S list to compute infections. If false, use I list.
	// the purpose is to minimize the number of evaluation by using the smallest list.
	
	init {
		create new_scheduler;
		/* determine the size of the neighbourhood and the average count of hosts neighbours */
		gridSize <- length(sir_grid);
		int nbCells <- 0;
		
		loop cell over: sir_grid {
			nbCells <- nbCells + length(cell.neighbours);
		}

		neighbourhoodSize <- nbCells / gridSize + 1; // +1 to count itself in the neighbourhood;
		beta_maths <- beta * neighbourhoodSize * number_Hosts / gridSize * adjust;
		
		write 'Switch will happen at population sizes around ' +switch_threshold;
		write 'Basic Reproduction Number (R0): ' + string(beta / delta) + '\n';
		
		create switch_model {
			threshold_to_IBM <- switch_threshold;
			threshold_to_Maths <- switch_threshold;
		}

		if (first(switch_model).start_with_IBM) {
		//		write 'Starting with IBM model';
			create IBM_model;
			current_model <- first(IBM_model);
		} else {
		//		write 'Starting with Maths model';
			create Math_model;
			current_model <- first(Math_model);
		}

		current_model.S <- float(initial_S);
		current_model.I <- float(initial_I);
		current_model.R <- float(initial_R);
		current_model.N <- number_Hosts;
		
		ask current_model {
			do initialize;
		}

		create my_SIR_maths {
			self.S <- float(myself.initial_S);
			self.I <- float(myself.initial_I);
			self.R <- float(myself.initial_R);
			self.N <- number_Hosts;
			self.beta1 <- beta * neighbourhoodSize * (N / gridSize)* adjust;
			self.alpha <- delta;
		}

	}

	reflex infection_computation_method {
	/* computing infection from S has a complexity of S*ngb, where ngb is the size of the neighbourhood.
	 * computing infection from I has a complexity of I*ngb.
	 * this reflex determine which method has the lowest cost.
	 * */
		computeInfectionFromS <- (Host count (each.is_susceptible)) < (Host count (each.is_infected));
	}

}

environment width: grid_size height: grid_size {
	grid sir_grid width: grid_size height: grid_size {
		rgb color <- #black;
		list<sir_grid> neighbours <- (self neighbours_at neighbours_range) of_species sir_grid;
	}

}


species new_scheduler schedules: (Host + IBM_model + Math_model + switch_model) ;

species switch_model schedules: [] {
	int threshold_to_IBM <- 45; // threshold under which the model swith to IBM
	int threshold_to_Maths <- 50; // threshold under which the model swith to Maths model 
	bool start_with_IBM function: { (initial_S < threshold_to_IBM or initial_I < threshold_to_IBM) };

	// task switch_to_IBM weight:1 when: (current_model.model_type = 'Maths'){
	reflex switch_to_IBM when: (current_model.model_type = 'Maths') {
		if (current_model.S < threshold_to_IBM or current_model.I < threshold_to_IBM) {
			write 'Switch to IBM model at cycle ' + string(cycle);
			create IBM_model {
				self.S <- current_model.S;
				self.I <- current_model.I;
				self.R <- current_model.R;
				self.N <- current_model.N;
				do initialize;
			}

			ask current_model {
				do remove_model;
			}

			current_model <- first(IBM_model);
		}

	}

	reflex switch_to_Maths when: (current_model.model_type = 'IBM') {
		if (current_model.S > threshold_to_Maths and current_model.I > threshold_to_Maths) {
			write 'Switch to Maths model at cycle ' + cycle;
			create Math_model {
				self.S <- current_model.S;
				self.I <- current_model.I;
				self.R <- current_model.R;
				self.N <- current_model.N;
				do initialize;
			}

			ask current_model {
				do remove_model;
			}

			current_model <- first(Math_model);
		}

	}

}

species SIR_model schedules: [] {
	float S;
	float I;
	float R;
	int N;
	string model_type <- 'none';
	
	action remove_model {
		do die;
	}

	action initialize ;

}

species IBM_model schedules: [] parent: SIR_model {
	string model_type <- 'IBM';
	
	action initialize {
		write 'Initializing IBM model with S=' + round(S) + ', I=' + round(I) + ', R=' + round(R) + '\n';
		create Host number: round(S) {
			is_susceptible <- true;
			is_infected <- false;
			is_immune <- false;
			color <- #green;
		}

		create Host number: round(I) {
			is_susceptible <- false;
			is_infected <- true;
			is_immune <- false;
			color <- #red;
		}

		create Host number: round(R) {
			is_susceptible <- false;
			is_infected <- false;
			is_immune <- true;
			color <- #yellow;
		}
		//force evaluation at first step;
		do count;
	}

	reflex count {
		do count;
	}

	action count {
		S <- float(Host count (each.is_susceptible));
		I <- float(Host count (each.is_infected));
		R <- float(Host count (each.is_immune));
	}

	action remove_model {
		ask Host {
			do die;
		}

		do die;
	}

}

species Math_model schedules: [] parent: SIR_model {
	string model_type <- 'Maths';
	float t;
	
	action initialize {
		write 'Initializing Maths model with S=' + S + ', I=' + I + ', R=' + R + '\n';
	}

	equation SIR {
		diff(S, t) = (-beta_maths * S * I / N);
		diff(I, t) = (beta_maths * S * I / N) - (delta * I);
		diff(R, t) = (delta * I);
	}

	reflex solving {solve SIR method: "rk4" step: 0.01 ;}
}

species Host schedules: [] skills: [moving] {
	bool is_susceptible <- true;
	bool is_infected <- false;
	bool is_immune <- false;
	rgb color <- #green;
	sir_grid myPlace;
	
	/* next function computes the number of neighbours of the agent */
	int ngb_number function: {
		length(((self) neighbours_at (2)) of_species Host) - 1 // -1 is because the agent counts itself
	};
	
	init {
		myPlace <- one_of(sir_grid as list);
		location <- myPlace.location;
	}

	reflex basic_move {
		if (!local_random_walk) {
		/* random walk among neighbours */
			myPlace <- one_of(myPlace.neighbours);
			location <- myPlace.location;
		} else {
		/* move agent to a random place anywhere in the grid */
			myPlace <- any(sir_grid);
			location <- myPlace.location;
		}

	}

	reflex become_infected when: (is_susceptible and computeInfectionFromS) {
		if (flip(1 - (1 - beta) ^ (((self) neighbours_at (2)) of_species Host) count (each.is_infected))) {
			set is_susceptible <- false;
			set is_infected <- true;
			set is_immune <- false;
			set color <- #red;
		}

	}

	reflex infecte_others when: (is_infected and not (computeInfectionFromS)) {
		loop hst over: ((self) neighbours_at (2)) {
			if (hst.is_susceptible) {
				if (flip(beta)) {
					hst.is_susceptible <- false;
					hst.is_infected <- true;
					hst.is_immune <- false;
					hst.color <- #red;
				}
			}
		}
	}

	reflex become_immune when: (is_infected and flip(delta)) {
		is_susceptible <- false;
		is_infected <- false;
		is_immune <- true;
		color <- #yellow;
	}

	aspect basic {
		draw circle(1) color: color;
	}

}

species my_SIR_maths {
	float t;
	float I <- float(iInit);
	float S <- N - I;
	float R <- 0.0;
			
	float alpha <- 0.01 min: 0.0 max: 1.0;
	float beta1 <- 0.1 min: 0.0 max: 1000.0;
	int N <- 500 min: 1 max: 3000;
	int iInit <- 1;

	equation SIR {
		diff(S, t) = (-beta1 * S * I / N);
		diff(I, t) = (beta1 * S * I / N) - (alpha * I);
		diff(R, t) = (alpha * I);
	}
	
	reflex solving {solve SIR method:"rk4" step:0.01;}

}



experiment mysimulation type: gui {
	parameter 'Number of Susceptible' type: int var: initial_S <- 495 category: "Initial population"; 
	parameter 'Number of Infected'    type: int var: initial_I <- 5   category: "Initial population";
	parameter 'Number of Removed'     type: int var: initial_R <- 0   category: "Initial population";

	parameter 'Beta (S->I)'  type: float var: beta <- 1.0   category: "Parameters";
	parameter 'Delta (I->R)' type: float var: delta <- 0.01 category: "Parameters";	
	
	parameter 'Is the infection is computed locally?' type: bool var: local_infection <- true category: "Infection";
	parameter 'Size of the neighbours' type: int var: neighbours_range <- 2 min:1 max: 5 category: "Infection";

	parameter 'Local Random Walk' type: bool var: local_random_walk <- true category: "Agents";	
	
	parameter 'Switch models at' type: int var: switch_threshold <- 120 category: "Model";
	
	output {
		display 'sir display' {
			grid sir_grid lines: #black;
			species Host aspect: basic;
		}
	
		display 'Switch model' {
			chart 'Susceptible' type: series background: #lightgray style: exploded {
				data 'susceptible' value: current_model.S color: #green;
				data 'infected' value: current_model.I color: #red;
				data 'immune' value: current_model.R color: #yellow;
			}

		}

		display SI_maths  {
			chart "SI" type: series background: #white {
				data 'S' value: first((my_SIR_maths)).S color: #green;
				data 'I' value: first((my_SIR_maths)).I color: #red;
				data 'R' value: first((my_SIR_maths)).R color: #yellow;
			}

		}

	}

}
