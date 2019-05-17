/**
* Name: simple_ODE_SIR_Predefined
* Author: hqnghi 
* Description: A simple example of ODE use into agents with the example of the SIR equation system.
* Tags: math, equation
*/
 
model simple_ODE_SIR_Predefined

global {
	init {
		create predefined_ODE_SIR_agent number: 1;
	}
}

//Species which represent the Ordinary Differential Equations System 
species predefined_ODE_SIR_agent {
	//Variable to represent the discrete time for integration
	float t;
 	//Total Population 
   	int N <- 500;
   	//Number of infected
	float I <- 1.0; 
	//Number of susceptible
	float S <- N - I; 
	//Number of recovered
	float R <- 0.0; 

	//Rate of transmission success for each infected
  	float beta <- 0.4;
  	//Rate of passing to resistant
   	float gamma <- 0.01; 
   		
   	float h <- 0.1;
   	string mm <- "Euler";

	// Parameters must follow exact order S, I, R, t  and N,beta,gamma		
	equation eqSIR type:SIR vars: [S,I,R,t] params: [N,beta,gamma] ;

	reflex solving {solve eqSIR method: mm step_size:h ;}//cycle_length:int(1/h);}
}


experiment mysimulation type: gui {
	output {	
		display display_charts {
			chart 'SIR_agent' type: series background: #lightgray {
				data "S" value: first(predefined_ODE_SIR_agent).S color: #green;
				data "I" value: first(predefined_ODE_SIR_agent).I color: #red;
				data "R" value: first(predefined_ODE_SIR_agent).R color: #blue;
			}
		}
	}
}
