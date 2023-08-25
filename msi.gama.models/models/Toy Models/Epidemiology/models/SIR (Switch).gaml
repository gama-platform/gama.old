/**
* Name: SIR_switch
* Author: tri and hqnghi 
* Description: A model which show how to implement ODE system, IBM model, and to switch 
* 	from one to another using a threshold. Another interesting point seen in this model is the 
* 	the minimization of the execution time by reducing the number of agents to compute infections.
* Tags: equation, math, grid
*/
model SIR_switch

global {
	// Parameters
	int initial_S; // The number of susceptible
	int initial_I; // The number of infected
	int initial_R; // The number of removed 

	float beta; // The parameter Beta 
	float delta; // The parameter Delta	
	
	int switch_threshold <- 120 ; // threshold for switching models
	bool local_infection <- true ;
	int neighbours_range <- 2 ;
	bool local_random_walk <- true ; 
	
	
	// Global variables
	int grid_size <- 50;
	geometry shape <- square(grid_size);
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
		
		//Creation of the switch_model agent that will manage the switch between the mathematical and the individual based models
		create switch_model {
			threshold_to_IBM <- switch_threshold;
			threshold_to_Maths <- switch_threshold;
		}
		//Creation of the model according to the one to begin with
		if (first(switch_model).start_with_IBM) {
		//		write 'Starting with IBM model';
			create IBM_model;
			current_model <- first(IBM_model);
		} else {
		//		write 'Starting with Maths model';
			create Math_model;
			current_model <- first(Math_model);
		}
		//Initialization of the Susceptible, Infected, Resistant and Total Compartiment
		current_model.S <- float(initial_S);
		current_model.I <- float(initial_I);
		current_model.R <- float(initial_R);
		current_model.N <- number_Hosts;
		
		//Ask to the model to initialize itself according to the value initialized
		ask current_model {
			do initialize;
		}
		
		//Create the SIR maths with ODE to compare
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
//Grid which represent the discretized space for the host agents
	grid sir_grid width: grid_size height: grid_size {
		rgb color <- #white;
		list<sir_grid> neighbours <- (self neighbors_at neighbours_range) of_species sir_grid;
	}


//Species which allows the execution of only Host, IBM_model, Math_model and switch_model at each cycle
species new_scheduler schedules: (Host + IBM_model + Math_model + switch_model) ;

//Species which represent the manager between IBM and Math model
species switch_model schedules: [] {
	int threshold_to_IBM <- 45; // threshold under which the model swith to IBM
	int threshold_to_Maths <- 50; // threshold under which the model swith to Maths model 
	bool start_with_IBM function:  (initial_S < threshold_to_IBM or initial_I < threshold_to_IBM) ;

	//Switch the model used to IBM when the threshold is higher than the population
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
	//Switch the model used to Maths when the threshold is lower than the population
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
//Species which represent the SIR model used by the IBM and the Math models 
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

//Species IBM Model which represent the Individual based model, derivated from SIR_model
species IBM_model schedules: [] parent: SIR_model {
	string model_type <- 'IBM';
	
	//Action to initialize the Model with SIR compartiments
	action initialize {
		
		write 'Initializing IBM model with S=' + round(S) + ', I=' + round(I) + ', R=' + round(R) + '\n';
		//Creation of the host agents
		create Host number: round(S) {
			is_susceptible <- true;
			is_infected <- false;
			is_immune <- false;
			color <- rgb(46,204,113);
		}

		create Host number: round(I) {
			is_susceptible <- false;
			is_infected <- true;
			is_immune <- false;
			color <- rgb(231,76,60);
		}

		create Host number: round(R) {
			is_susceptible <- false;
			is_infected <- false;
			is_immune <- true;
			color <- rgb(52,152,219);
		}
		do count;
	}

	reflex count {
		do count;
	}
	//Action to update the different compartiments
	action count {
		S <- float(Host count (each.is_susceptible));
		I <- float(Host count (each.is_infected));
		R <- float(Host count (each.is_immune));
	}
	//Action to remove the model and kill all the agents it contains
	action remove_model {
		ask Host {
			do die;
		}

		do die;
	}

}

//Species Math Model which represent the mathematical Ordinary Differential Equations model, derivated from SIR_model
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

	reflex solving {solve SIR method: "rk4" step_size: 0.01 ;}
}
//Species host used by the Individual Based Model which move from one cell to another
species Host schedules: [] skills: [moving] {
	bool is_susceptible <- true;
	bool is_infected <- false;
	bool is_immune <- false;
	rgb color <- #green;
	sir_grid myPlace;
	
	/* next function computes the number of neighbours of the agent */
	int ngb_number function: 
		length(((self) neighbors_at (2)) of_species Host) - 1 // -1 is because the agent counts itself
	;
	
	init {
		myPlace <- one_of(sir_grid as list);
		location <- myPlace.location;
	}

	//Reflex to move the agents among the cells
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
	//Reflex to make the agent infected when the infection is computed from S for a better execution time
	reflex become_infected when: (is_susceptible and computeInfectionFromS) {
		if (flip(1 - (1 - beta) ^ (((self) neighbors_at (2)) of_species Host) count (each.is_infected))) {
			is_susceptible <- false;
			is_infected <- true;
			is_immune <- false;
			color <- rgb(231,76,60);
		}

	}
	//Reflex to make the agent infect others when the infection is not computed from S for a better execution time
	reflex infecte_others when: (is_infected and not (computeInfectionFromS)) {
		loop hst over: ((self) neighbors_at (2)) {
			if (hst.is_susceptible) {
				if (flip(beta)) {
					hst.is_susceptible <- false;
					hst.is_infected <- true;
					hst.is_immune <- false;
					hst.color <- rgb(231,76,60);
				}
			}
		}
	}
	//Reflex to make the agent resistant
	reflex become_immune when: (is_infected and flip(delta)) {
		is_susceptible <- false;
		is_infected <- false;
		is_immune <- true;
		color <- rgb(52,152,219);
	}

	aspect basic {
		draw circle(1) color: color;
	}

}
//Species which represent the SIR mathematical model 
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
	
	reflex solving {solve SIR method:"rk4" step_size:0.01;}

}



experiment mysimulation type: gui {
	parameter 'Number of Susceptible' type: int var: initial_S <- 495 category: "Initial population"; 
	parameter 'Number of Infected'    type: int var: initial_I <- 5   category: "Initial population";
	parameter 'Number of Removed'     type: int var: initial_R <- 0   category: "Initial population";

	parameter 'Beta (S->I)'  type: float var: beta <- 0.1   category: "Parameters";
	parameter 'Delta (I->R)' type: float var: delta <- 0.01 category: "Parameters";	
	
	parameter 'Is the infection is computed locally?' type: bool var: local_infection <- true category: "Infection";
	parameter 'Size of the neighbours' type: int var: neighbours_range <- 2 min:1 max: 5 category: "Infection";

	parameter 'Local Random Walk' type: bool var: local_random_walk <- true category: "Agents";	
	
	parameter 'Switch models at' type: int var: switch_threshold <- 120 category: "Model";
	
	output {
		layout #split;
		display 'sir display'  type:2d antialias:false {
			grid sir_grid border: #lightgray;
			species Host aspect: basic;
		}
	
		display 'Switch model' type: 2d {
			chart 'Susceptible' type: series background: #white style: exploded {
				data 'susceptible' value: current_model.S color: rgb(46,204,113);
				data 'infected' value: current_model.I color: rgb(231,76,60);
				data 'immune' value: current_model.R color: rgb(52,152,219);
			}

		}

		display SI_maths type: 2d {
			chart "SI" type: series background: #white {
				data 'S' value: first((my_SIR_maths)).S color: rgb(46,204,113);
				data 'I' value: first((my_SIR_maths)).I color: rgb(231,76,60);
				data 'R' value: first((my_SIR_maths)).R color: rgb(52,152,219);
			}

		}

	}

}
