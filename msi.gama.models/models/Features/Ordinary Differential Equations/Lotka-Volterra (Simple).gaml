/**
 *  ODE_LotkaVolterra
 *  Author: Nguyen Quang Nghi & Nathalie Corson
 *  Description: Lotka Volterra Predator Prey Model - ODE solved with Runge-Kutta 4 method
 */

model ODE_LotkaVolterra

global {

	float prey_birth_rate ; 		// natural birth rate of preys
	float predation_rate ; 			// death rate of preys due to predators
	float predator_death_rate ; 	// natural death rate of predators
	float predation_efficiency ; 	// birth rate of predators due to prey consumption
	
	float nb_prey_init ; 			// initial number of preys
	float nb_predator_init  ; 		// initial number of predators
	
	float integration_time_step ; 	// integration time step of the Runge Kutta 4 method
	
	init{
		create LotkaVolterra_agent number:1 with:[nb_prey::nb_prey_init, nb_predator::nb_predator_init]; 	// creation of an agent containing the ODE model
	}

}

species LotkaVolterra_agent {
    float t;   					// time (equals to ( 1 / integration time step)
	float nb_prey ; 			// number of preys
	float nb_predator ; 		// number of predators
  
	equation lotka_volterra { 
		diff(nb_prey,t) =   nb_prey * (prey_birth_rate - predation_rate * nb_predator); 					// evolution of the number of preys duting an integration time step
		diff(nb_predator,t) = - nb_predator * (predator_death_rate - predation_efficiency * nb_prey); 		// evolution of the number of predator during an integration time step
      }
      reflex solving {        
       	solve lotka_volterra method: "rk4" step:integration_time_step cycle_length:1/step; 			// use of runge kutta 4 method with an integration time step of value integration_time_step
       }
}


experiment maths type: gui {
		
	parameter "Prey birth rate" var: prey_birth_rate <- 0.05 min: 0.0 max: 1.0 category: "Prey";
	parameter "Predation rate" var: predation_rate <- 0.001 min: 0.0 max: 1.0 category: "Prey";
	parameter "Predator death rate" var: predator_death_rate <- 0.03 min: 0.0 max: 1.0 category: "Predator";
	parameter "Predation efficiency" var: predation_efficiency <- 0.0002 min: 0.0 max: 1.0 category: "Predator";
	
	parameter "Initial number of prey" var: nb_prey_init <- 250.0 min: 1.0 category: "Prey";
	parameter "Initial number of predator" var: nb_predator_init <- 45.0 min: 1.0 category: "Predator";
	
	parameter "Integration time step" var: integration_time_step <- 0.01 min: 0.00001 max:0.1 category: "Integration method";
	
	output {		
		monitor "Equilibrium point" value: {predator_death_rate / predation_efficiency, prey_birth_rate/predation_rate};
 		display TimeSeries refresh_every: 1 {
			chart "Lotka Volterra Time Series" type: series background: rgb('white') {
				data 'x' value: first(LotkaVolterra_agent).nb_prey color: rgb('green') ;				
				data 'y' value: first(LotkaVolterra_agent).nb_predator color: rgb('red') ;
			}
		}
		display PhasePortrait refresh_every: 1 {			
			chart "Lotka Volterra Phase Portrait" type: xy background: rgb('white') {
			data 'm' value: {predator_death_rate / predation_efficiency, prey_birth_rate/predation_rate} color: °blue; // equilibrium point
			data 'xy' value:{first(LotkaVolterra_agent).nb_prey, first(LotkaVolterra_agent).nb_predator} color: rgb('black') ;				
			}
		}
	}
}
