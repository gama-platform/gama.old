/**
 *  simple_ODE_SIR_Predefined.gaml
 *  Author: hqnghi
 *  Description: A simple example of ODE use into agents with the example of the SIR equation system.
 */
 
model simple_ODE_SIR_Predefined

global {
	init {
		create predefined_ODE_SIR_agent number: 1;
	}
}

entities {
	species predefined_ODE_SIR_agent {
		float t;
 
    	int N <- 500;
		float I <- 1.0; 
		float S <- N - I; 
		float R <- 0.0; 

   		float beta <- 0.4;
   		float gamma <- 0.01; 
   		
   		float h <- 0.01;

		// Followinf predefined equation system is equivalent to following system:
		//		equation SIR_classic{
		//			diff(S,t) = (- beta * S * I / N);
		//			diff(I,t) = (beta * S * I / N) - (gamma * I);
		//			diff(R,t) = (gamma * I);
		//		}
		
		// Parameters must follow exact order S, I, R, t  and N,beta,gamma		
		equation eqSIR type:SIR vars: [S,I,R,t] params: [N,beta,gamma]{}

		solve eqSIR method:rk4 step:h;
	}
}

experiment mysimulation type : gui {
	output {	
		display display_charts refresh_every : 1 {
			chart 'SIR_agent' type : series background : rgb('lightGray') {
				data "S" value : first(predefined_ODE_SIR_agent).S color : rgb('green');
				data "I" value : first(predefined_ODE_SIR_agent).I color : rgb('red');
				data "R" value : first(predefined_ODE_SIR_agent).R color : rgb('blue');
			}
		}
	}
}
