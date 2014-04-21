/**
 *  simple_ODE_SIR.gaml
 *  Author: hqnghi
 *  Description: A simple example of ODE use into agents with the example of the SIR equation system.
 */

model simple_ODE_SIR

global {
	init{
		create agent_with_SIR_dynamic number:1;
	}
}

entities {
	species agent_with_SIR_dynamic {
		int N <- 1500 ;
		int iInit <- 1;		

	    float t;  
		float S <- N - float(iInit); 	      
		float I <- float(iInit); 
		float R <- 0.0; 
		
		float alpha <- 0.2 min: 0.0 max: 1.0;
		float beta <- 0.8 min: 0.0 max: 1.0;

		float h <- 0.01;
	   
		equation SIR{ 
			diff(S,t) = (- beta * S * I / N);
			diff(I,t) = (beta * S * I / N) - (alpha * I);
			diff(R,t) = (alpha * I);
		}
	                
	    reflex solving {solve SIR method: "rk4" step: h cycle_length: 1/h ;	    }    
	}
}

experiment maths type: gui {
	output { 
		display display_charts refresh_every: 1 {
			chart "SIR_agent" type: series background: rgb('white') {
				data 'S' value: first(list(agent_with_SIR_dynamic)).S color: rgb('green') ;				
				data 'I' value: first(list(agent_with_SIR_dynamic)).I color: rgb('red') ;
				data 'R' value: first(list(agent_with_SIR_dynamic)).R color: rgb('blue') ;
			}
		}
	}
}
