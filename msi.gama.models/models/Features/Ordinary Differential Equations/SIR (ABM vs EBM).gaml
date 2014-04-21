/**
 *  comparison_ABM_EBM_SIR.gaml
 *  Author: Benoit Gaudou and 
 *  Description: Comparison between an agent-based and an equation-based model of the SIR model.
 */
model comparison_ABM_EBM_SIR

global {
	int number_S <- 499; // The number of susceptible
	int number_I <- 1; // The number of infected
	int number_R <- 0; // The number of removed
	float survivalProbability <- 1 / (70 * 365); // The survival probability
	float beta <- 0.1; // The parameter Beta
	float nu <- 0.00; // The parameter Nu
	float gamma <- 0.01; // The parameter Delta
	bool local_infection <- true;
	int neighbours_size <- 2;
	int N <- number_S + number_I + number_R;
	int nb_hosts <- number_S + number_I + number_R;
	int nb_infected <- number_I;
	float hKR4 <- 0.7;
	geometry shape <- square(50);
	init {
		create Host number: number_S {
			is_susceptible <- true;
			is_infected <- false;
			is_immune <- false;
			color <- rgb('green');
		}

		create Host number: number_I {
			is_susceptible <- false;
			is_infected <- true;
			is_immune <- false;
			color <- rgb('red');
		}

		create node_agent number: 1 {
			S <- float(number_S);
			I <- float(number_I);
			R <- float(number_R);
		}

		write 'Basic Reproduction Number: ' + string(beta / (gamma + nu));
	}

	reflex compute_nb_infected_hosts {
		nb_infected <- Host count (each.is_infected);
		nb_hosts <- length(Host);
	}

}

grid sir_grid width: 50 height: 50 {
		rgb color <- rgb('black');
		list<sir_grid> neighbours <- (self neighbours_at neighbours_size) of_species sir_grid;
	}
species Host {
	bool is_susceptible <- true;
	bool is_infected <- false;
	bool is_immune <- false;
	rgb color <- rgb('green');
	int sic_count <- 0;
	sir_grid myPlace;
	init {
		myPlace <- one_of(sir_grid as list);
		location <- myPlace.location;
	}

	reflex basic_move {
		myPlace <- one_of(myPlace.neighbours);
		location <- myPlace.location;
	}

	reflex become_infected when: is_susceptible {
		float rate <- 0.0;
		if (local_infection) {
			int my_nb_hosts <- 0;
			int nb_hosts_infected <- 0;
			loop hst over: ((myPlace.neighbours + myPlace) accumulate (Host overlapping each)) {
				my_nb_hosts <- my_nb_hosts + 1;
				if (hst.is_infected) {
					nb_hosts_infected <- nb_hosts_infected + 1;
				}

			}

			rate <- nb_hosts_infected / nb_hosts;
		} else {
			rate <- nb_infected / nb_hosts;
		}

		if (flip(beta * rate)) {
			is_susceptible <- false;
			is_infected <- true;
			is_immune <- false;
			color <- rgb('red');
		}

	}

	reflex become_immune when: (is_infected and flip(gamma)) {
		is_susceptible <- false;
		is_infected <- false;
		is_immune <- true;
		color <- rgb('yellow');
	}

	reflex shallDie when: flip(nu) {
		create species(self) {
			myPlace <- myself.myPlace;
			location <- myself.location;
		}

		do die;
	}

	aspect basic {
		draw circle(1) color: color;
	}

}

species node_agent {
	float t;
	float I;
	float S;
	float R;
	equation eqSIR type: SIR vars: [S, I, R, t] params: [N, beta, gamma];
	reflex solving {solve eqSIR method: rk4 step: 0.5 cycle_length: 2;}
	
}

experiment Simulation type: gui {
	parameter 'Number of Susceptible' type: int var: number_S <- 495 category: "Initial population";
	parameter 'Number of Infected' type: int var: number_I <- 5 category: "Initial population";
	parameter 'Number of Removed' type: int var: number_R <- 0 category: "Initial population";
	parameter 'Beta (S->I)' type: float var: beta <- 0.1 category: "Parameters";
	parameter 'Gamma (I->R)' type: float var: gamma <- 0.01 category: "Parameters";
	parameter 'Mortality' type: float var: nu <- 0.00 category: "Parameters";
	parameter 'Survival Probability' type: float var: survivalProbability <- 1 / (70 * 365) category: "Parameters";
	parameter 'Is the infection is computed locally?' type: bool var: local_infection <- true category: "Infection";
	parameter 'Size of the neighbours' type: int var: neighbours_size <- 2 min: 1 max: 5 category: "Infection";
	output {
		display sir_display { grid sir_grid lines: rgb("black");
		species Host aspect: basic;
		}
		display ABM refresh_every: 1 { chart 'Susceptible' type: series background: rgb('lightGray') style: exploded {
			data 'susceptible' value: (Host as list) count (each.is_susceptible) color: rgb('green');
			data 'infected' value: (Host as list) count (each.is_infected) color: rgb('red');
			data 'immune' value: (Host as list) count (each.is_immune) color: rgb('blue');
		}

		}
		display EBM refresh_every: 1 { chart "SIR" type: series background: rgb('white') {
			data 'S' value: first(node_agent).S color: rgb('green');
			data 'I' value: first(node_agent).I color: rgb('red');
			data 'R' value: first(node_agent).R color: rgb('blue');
		}

		}
	}

}
