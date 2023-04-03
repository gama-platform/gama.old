/**
* Name: SIR (Simple)
* Author: hqnghi
* Description: A simple example of ODE use into agents with the example of the SIR equation system.
* Tags: equation, math
*/

model simple_ODE_SIR

global {
	init {
		create agent_with_SIR_dynamic number:1;
	}
}


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
                
    reflex solving {
    	solve SIR method: "rk4"  ;
    }    
}


experiment Simulation type: gui {
	float minimum_cycle_duration <- 0.1#s;
	output { 
		layout #vertical tabs: true;
		display display_charts toolbar: false  type: 2d {
			chart "Time series" type: series background: rgb(47,47,47) color: #white {
				data 'S' value: first(agent_with_SIR_dynamic).S color: rgb(46,204,113) ;				
				data 'I' value: first(agent_with_SIR_dynamic).I color: rgb(231,76,60) ;
				data 'R' value: first(agent_with_SIR_dynamic).R color: rgb(52,152,219) ;
			}
		}
		display display_phase_portrait toolbar: false  type: 2d {
			chart "Phase portrait" type: xy background: rgb(47,47,47) color: #white x_label:"S" y_label:"Y" x_range: {0,1600} y_range: {0,700}{
				data 'I vs S' value: [first(agent_with_SIR_dynamic).S,first(agent_with_SIR_dynamic).I] color: rgb(243,156,18)  ;				
			}
		}
	}
}
