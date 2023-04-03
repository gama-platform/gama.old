/**
* Name: ODE_LotkaVolterra
* Author: Huynh Quang Nghi & Nathalie Corson
*  Description: Lotka Volterra Predator Prey Model - ODE solved with Runge-Kutta 4 method with different integration time step with only one agent
* Tags: equation, math
*/

model ODE_LotkaVolterra

global {

	float prey_birth_rate<- 0.05 ; 		// natural birth rate of preys
	float predation_rate <- 0.001; 			// death rate of preys due to predators
	float predator_death_rate<- 0.03 ; 	// natural death rate of predators
	float predation_efficiency<- 0.0002 ; 	// birth rate of predators due to prey consumption

	float nb_prey_init <- 250.0; 			// initial number of preys
	float nb_predator_init <- 45.0 ; 		// initial number of predators

	float integration_time_step <- 0.01; 	// integration time step used in the Runge Kutta 4 method
	float t; 						// simulation time : t = n * integration_time_step  where n is the number of already computed time step

	init{
		create LotkaVolterra_agent number:1 ; 	// creation of an agent containing the ODE model
	}
}

species LotkaVolterra_agent {

	float nb_prey <- nb_prey_init ; 			// number of preys initialized with the values given by the user
	float nb_predator <- nb_predator_init ; 		// number of predators initialized with the values given by the user

	equation lotka_volterra {
		diff(nb_prey,t) =   nb_prey * (prey_birth_rate - predation_rate * nb_predator); 					// evolution of the number of preys duting an integration time step
		diff(nb_predator,t) = - nb_predator * (predator_death_rate - predation_efficiency * nb_prey); 		// evolution of the number of predator during an integration time step
      }
      reflex solving {
       	solve lotka_volterra method: "rk4" step_size:integration_time_step ;					 			// use of runge kutta 4 method with an integration time step of value integration_time_step
       }
}


experiment maths type: gui {

	parameter "Prey birth rate" var: prey_birth_rate <- 0.05 min: 0.0 max: 1.0 category: "Prey";						// the user defines the value of parameter prey_birth_rate on the interface, the default value is 0.05 and this value must be between 0 and 1
	parameter "Predation rate" var: predation_rate <- 0.001 min: 0.0 max: 1.0 category: "Prey"; 						// the user defines the value of parameter prey_birth_rate on the interface, the default value is 0.001 and this value must be between 0 and 1
	parameter "Predator death rate" var: predator_death_rate <- 0.03 min: 0.0 max: 1.0 category: "Predator";	 		// the user defines the value of parameter predator_death_rate on the interface, the default value is 0.03 and this value must be between 0 and 1
	parameter "Predation efficiency" var: predation_efficiency <- 0.0002 min: 0.0 max: 1.0 category: "Predator";		// the user defines the value of parameter predation_efficiency on the interface, the default value is 0.0002 and this value must be between 0 and 1

	parameter "Initial number of prey" var: nb_prey_init <- 250.0 min: 1.0 category: "Prey"; 							// the user defines the value of parameter predation_efficiency on the interface, the default value is 250, the minimum possible value is 1
	parameter "Initial number of predator" var: nb_predator_init <- 45.0 min: 1.0 category: "Predator"; 				// the user defines the value of parameter predation_efficiency on the interface, the default value is 45, the minimum possible value is 1

	parameter "Integration time step" var: integration_time_step <- 0.01 min: 0.0 max:0.1 category: "Integration method";  // the user defines the value of the integration step, the default value is 0.01 and this value must be between 0 and 1

	output {
 		display TimeSeries  type: 2d  {																	// creation of a display to show time series of the model, values are plotted at every step
			chart "Lotka Volterra Time Series" type: series background: #white {  						// the chart, of type 'serie', is named Lotka Volterra Time Series, it shows quantities according to time, and the background is white
				data 'Number of preys' value: first(LotkaVolterra_agent).nb_prey color: #green ;			// number of preys is plotted in green
				data 'Number of predators' value: first(LotkaVolterra_agent).nb_predator color: #red ; 	// number of predators is plotted in red
			}
		}
		display PhasePortrait  type: 2d  {																// creation of a display to show the phase portait, values are plotted at every time step
			chart "Lotka Volterra Phase Portrait" type: xy background: #white {							// the chart, os type 'xy', is named Lotka Volterra Phase portrait, it shows a quantity according to another one, and the background is white
			data ' ' value: {predator_death_rate / predation_efficiency, prey_birth_rate/predation_rate} color: #blue; // equilibrium point
			data 'Number of preys according to number of predators' value:{first(LotkaVolterra_agent).nb_prey, first(LotkaVolterra_agent).nb_predator} color: #black ;	// number of predators according to the number of preys plotted in black
			}
		}
	}
}
