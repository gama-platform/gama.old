/**
* Name: comparison_ABM_EBM_SIR
* Author: Benoit Gaudou 
* Description: Comparison between an agent-based and an equation-based model of the SIR model. 
* 	The ABM use a grid to place the agents, and each cell will be the location of an agent, while the EBM 
* 	is only using a ODE System and no geographical representation.
* Tags: equation, math, grid
*/
model comparison_ABM_EBM_SIR

global {
	//Number of susceptible individuals
	int number_S <- 499; 
	//Number of infectious individuals
	int number_I <- 1; 
	//Number of Resistant individuals
	int number_R <- 0; 
	//Beta parameter used for the infection of susceptible individuals
	float beta <- 0.1; 
	//Gamma parameter used for the resistance gained by the infectious individuals
	float gamma <- 0.01;
	//Size of the neighbours
	int neighbours_size <- 2;
	//Total number of individuals
	int N <- number_S + number_I + number_R;
	//Number of hosts (for ABM)
	int nb_hosts <- number_S + number_I + number_R update: length(Host);
	//Number of infected hosts (for ABM)
	int nb_infected <- number_I update:  Host count (each.is_infected);
	float hKR4 <- 0.7;
	geometry shape <- square(50);
	init {
		//Create the number of hosts susceptibles
		create Host number: number_S {
			is_susceptible <- true;
			is_infected <- false;
			is_immune <- false;
			color <- rgb(46,204,113);
		}
		//Create the number of hosts infectious
		create Host number: number_I {
			is_susceptible <- false;
			is_infected <- true;
			is_immune <- false;
			color <- rgb(231,76,60);
		}
		//Create the node agent for the SIR ODE System
		create node_agent number: 1 {
			S <- float(number_S);
			I <- float(number_I);
			R <- float(number_R);
		}
	}

}
//Grid that will be used to discretize space
grid sir_grid width: 50 height: 50 {
		rgb color <- #white;
		list<sir_grid> neighbours <- (self neighbors_at neighbours_size) of_species sir_grid;
	}
	
//Species host which represents the host of the disease
species Host {
	
	//Different booleans to know in which state is the host
	bool is_susceptible <- true;
	bool is_infected <- false;
	bool is_immune <- false;
	
	//Color of the host
	rgb color <- rgb(46,204,113);
	
	//Location of the agent among the grid
	sir_grid myPlace;
	//Count of neighbors infected 
    int ngb_infected_number function: self neighbors_at(neighbours_size) count(each.is_infected);
	
	init {
		//The location is chosen randomly
		myPlace <- one_of(sir_grid);
		location <- myPlace.location;
	}
	//Reflex to move the agent in the neighbours cells
	reflex basic_move {
		myPlace <- one_of(myPlace.neighbours);
		location <- myPlace.location;
	}
	//Reflex to pass the agent to the state infected 
	reflex become_infected when: is_susceptible {
			//Probability of being infected according to the number of infected among the neighbours
    		if (flip(1 - (1 - beta)  ^ ngb_infected_number)) {
        		is_susceptible <-  false;
	            	is_infected <-  true;
	            	is_immune <-  false;
	            	color <-  rgb(231,76,60);       			
			}    				
	}
	//Reflex to pass the agent to the state immune
	reflex become_immune when: (is_infected and flip(gamma)) {
		is_susceptible <- false;
		is_infected <- false;
		is_immune <- true;
		color <- rgb(52,152,219);
	} 
	
	aspect basic {
		draw circle(1) color: color;
	}

}
//Species node agent that will represent the SIR Ordinary differential equations system
species node_agent {
	float t;
	float I;
	float S;
	float R;
	equation eqSIR {
		diff(S,t) = -beta * S * I / N ;
		diff(I,t) = beta * S * I / N - gamma* I;
		diff(R,t) = gamma* I;
    }
	reflex solving {solve eqSIR method:#rk4 step_size: 1;}
	
}

experiment Simulation_ABM_EBM type: gui {
	parameter 'Number of Susceptible' type: int var: number_S <- 495 category: "Initial population";
	parameter 'Number of Infected' type: int var: number_I <- 5 category: "Initial population";
	parameter 'Number of Removed' type: int var: number_R <- 0 category: "Initial population";
	parameter 'Beta (S->I)' type: float var: beta <- 0.1 category: "Parameters";
	parameter 'Gamma (I->R)' type: float var: gamma <- 0.01 category: "Parameters";
	parameter 'Size of the neighbours' type: int var: neighbours_size <- 1 min: 1 max: 5 category: "Infection";
	output {
		layout #split;
		display sir_display type:2d antialias:false { 
			grid sir_grid border: #lightgray;
			species Host aspect: basic;	
		}
		display ABM  type: 2d  { 
			chart 'Population' type: series background: #white style: exploded {
				data 'susceptible' value: (Host as list) count (each.is_susceptible) color: rgb(46,204,113) marker_size: 0.5;
				data 'infected' value: (Host as list) count (each.is_infected) color: rgb(231,76,60) marker_size: 0.5;
				data 'immune' value: (Host as list) count (each.is_immune) color: rgb(52,152,219) marker_size: 0.5;
			}
		}
		display EBM  type: 2d  { 
			chart "Population" type: series background: #white {
				data 'S' value: first(node_agent).S color: rgb(46,204,113) marker: false;
				data 'I' value: first(node_agent).I color: rgb(231,76,60) marker: false;
				data 'R' value: first(node_agent).R color: rgb(52,152,219) marker: false;
			}
		}
		display ABM_EBM   type: 2d  { 
			chart 'Population' type: series background: #white style: exploded {
				data 'susceptible' value: (Host as list) count (each.is_susceptible) color: rgb(39,174,96) marker_size: 0.5;
				data 'infected' value: (Host as list) count (each.is_infected) color: rgb(192,57,43) marker_size: 0.5;
				data 'immune' value: (Host as list) count (each.is_immune) color: rgb(41,128,185) marker_size: 0.5;
				data 'S' value: first(node_agent).S color: rgb(46,204,113) marker: false;
				data 'I' value: first(node_agent).I color: rgb(231,76,60) marker: false;
				data 'R' value: first(node_agent).R color: rgb(52,152,219) marker: false;
			}
		}
	}

}
