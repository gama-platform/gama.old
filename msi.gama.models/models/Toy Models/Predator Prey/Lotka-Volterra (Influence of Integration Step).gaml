/**
* Name: ODE_LotkaVolterra
* Author: Huynh Quang Nghi & Nathalie Corson
*  Description: Lotka Volterra Predator Prey Model - ODE solved with Runge-Kutta 4 method with different integration time step
* Tags: equation, math
*/

model ODE_LotkaVolterra_InfluenceTimeStep

global {

	float prey_birth_rate ; 		// natural birth rate of preys
	float predation_rate ; 			// death rate of preys due to predators
	float predator_death_rate ; 	// natural death rate of predators
	float predation_efficiency ; 	// birth rate of predators due to prey consumption
	
	float nb_prey_init ; 			// initial number of preys
	float nb_predator_init  ; 		// initial number of predators
	
	float integration_step ; 	// integration time step of the Runge Kutta 4 method
	float t;   					// simulation time : t = n * integration_time_step  where n is the number of already computed time step
	
	
	float integration_time_step1  <- 1.0;  // first integration time step to compare 
	float integration_time_step2  <- 0.1;  // second integration time step to compare 
	float integration_time_step3  <- 0.01;  // third integration time step to compare 
	
	list<LotkaVolterra_agent> LV_agents;
	
	init{
		create LotkaVolterra_agent number: 1 with:[integration_time_step::integration_time_step1]; 	// creation of an agent containing the ODE model with an integration time step of value integration_time_step1
		create LotkaVolterra_agent number: 1 with:[integration_time_step::integration_time_step2]; 	// creation of an agent containing the ODE model with an integration time step of value integration_time_step2
		create LotkaVolterra_agent number: 1 with:[integration_time_step::integration_time_step3]; 	// creation of an agent containing the ODE model with an integration time step of value integration_time_step3
		LV_agents <- list(LotkaVolterra_agent);
	}
}

species LotkaVolterra_agent {
	
    float nb_prey <- nb_prey_init ; 				// number of preys initialized with the values given by the user
	float nb_predator <- nb_predator_init ; 		// number of predators initialized with the values given by the user
	
	float integration_time_step ; 					// integration time step used in the Runge Kutta 4 method
  
	equation lotka_volterra { 
		diff(nb_prey,t) =   nb_prey * (prey_birth_rate - predation_rate * nb_predator); 					// evolution of the number of preys duting an integration time step
		diff(nb_predator,t) = - nb_predator * (predator_death_rate - predation_efficiency * nb_prey); 		// evolution of the number of predator during an integration time step
      }
      reflex solving {        
       	solve lotka_volterra method: "rk4" step_size:integration_time_step;									// use of runge kutta 4 method with an integration time step of value integration_time_step
       }
}


experiment maths type: gui {
		
	parameter "Prey birth rate" var: prey_birth_rate <- 0.05 min: 0.0 max: 1.0 category: "Prey";						// the user defines the value of parameter prey_birth_rate on the interface, the default value is 0.05 and this value must be between 0 and 1
	parameter "Predation rate" var: predation_rate <- 0.001 min: 0.0 max: 1.0 category: "Prey"; 						// the user defines the value of parameter prey_birth_rate on the interface, the default value is 0.001 and this value must be between 0 and 1
	parameter "Predator death rate" var: predator_death_rate <- 0.03 min: 0.0 max: 1.0 category: "Predator";	 		// the user defines the value of parameter predator_death_rate on the interface, the default value is 0.03 and this value must be between 0 and 1
	parameter "Predation efficiency" var: predation_efficiency <- 0.0002 min: 0.0 max: 1.0 category: "Predator";		// the user defines the value of parameter predation_efficiency on the interface, the default value is 0.0002 and this value must be between 0 and 1
	
	parameter "Initial number of prey" var: nb_prey_init <- 250.0 min: 1.0 category: "Prey"; 							// the user defines the value of parameter predation_efficiency on the interface, the default value is 250, the minimum possible value is 1
	parameter "Initial number of predator" var: nb_predator_init <- 45.0 min: 1.0 category: "Predator"; 				// the user defines the value of parameter predation_efficiency on the interface, the default value is 45, the minimum possible value is 1
	
	parameter "Integration time step of the first chart " var:  integration_time_step1 <- 1.0  min: 0.0 max:1.0 category: "Integration time steps"; 	// the user defines the value of the first integration step he wants to compare, the default value is 1 and this value must be between 0 and 1
	parameter "Integration time step of the second chart " var:  integration_time_step2 <- 0.1  min: 0.0 max: 1.0 category: "Integration time steps"; 	// the user defines the value of the second integration step he wants to compare, the default value is 0.1 and this value must be between 0 and 1
	parameter "Integration time step of the third chart " var:  integration_time_step3 <- 0.01  min: 0.0 max: 1.0 category: "Integration time steps"; 	// the user defines the value of the third integration step he wants to compare, the default value is 0.01 and this value must be between 0 and 1
		
	output {		
 		display TimeSeries  type: 2d  {	// creation of a display to show time series of the model, values are plotted at every step. Since there is more than one chart plotted in one display, every chart has a position and a size
			chart "Lotka Volterra Time Series - Integration time step = 1 " type: series background: #white position: {0,0} size:{1,0.33} x_range: 1000 { 		// one chart, of type 'serie', is named Lotka Volterra Time Series - Integration time step = 1, it shows quantities according to time, and the background is white
				data 'Number of preys' value: first(LotkaVolterra_agent where (each.integration_time_step = 1.0)).nb_prey color: #green ;			// number of preys in the case where the integration time step is 1 is plotted in green		
				data 'Number of predators' value: first(LotkaVolterra_agent where (each.integration_time_step = 1.0)).nb_predator color: #red ; 	// number of predators in the case where the integration time step is 1 is plotted in red	
			}
			chart "Lotka Volterra Time Series - Integration time step = 0.1 " type: series background: #white position: {0,0.33} size:{1,0.33} x_range: 1000{
				data 'Number of preys' value: first(LotkaVolterra_agent where (each.integration_time_step = 0.1)).nb_prey color: #green ;				
				data 'Number of predators' value: first(LotkaVolterra_agent where (each.integration_time_step = 0.1)).nb_predator color: #red ;
			}
			chart "Lotka Volterra Time Series - Integration time step = 0.01 " type: series background: #white position: {0,0.66} size:{1,0.33}x_range: 1000{
				data 'Number of preys' value: first(LotkaVolterra_agent where (each.integration_time_step = 0.01)).nb_prey color: #green ;				
				data 'Number of predators' value: first(LotkaVolterra_agent where (each.integration_time_step = 0.01)).nb_predator color: #red ;
			}
		}
		display PhasePortrait  type: 2d {			
			chart "Lotka Volterra Phase Portrait - Integration time step = 1" type: xy background: #white position: {0,0} size:{1,0.33} {		// creation of a display to show phase portrait of the model, values are plotted at every step. Since there is more than one chart plotted in one display, every chart has a position and a size
			data 'Number of preys according to number of predators' value:{LV_agents[0].nb_prey, LV_agents[0].nb_predator} color: #black ;	// number of predators are plotted in black according to the number of preys in the case where the integration time step is 1		
			}
			chart "Lotka Volterra Phase Portrait - Integration time step = 0.1" type: xy background: #white position: {0,0.33} size:{1,0.33}{
			data 'Number of preys according to number of predators' value:{LV_agents[1].nb_prey, LV_agents[1].nb_predator} color: #black ;				
			}
			chart "Lotka Volterra Phase Portrait - Integration time step = 0.01" type: xy background: #white position: {0,0.66} size:{1,0.33} {
			data 'Number of preys according to number of predators' value:{LV_agents[1].nb_prey, LV_agents[1].nb_predator} color: #black ;				
			}
		}
	}
}
