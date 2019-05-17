/**
* Name: SIR (Simple)
* Author: hqnghi
* Description: A simple example of ODE use into agents with the example of the SIR equation system.
* Tags: equation, math
*/

model simple_ODE_SIR

global {
	
	 	string mm <- "rk4";
	
	list S_3d -> aSIR collect each.S;
	list I_3d -> aSIR collect each.I;
	list R_3d -> aSIR collect each.R;	
	
	init {
		create aSIR number: 10 {
			S<-S-(int(self)*10000);
		}
	}
	
	reflex ss when: cycle>=40 {
    	do pause;	
	}
}


species aSIR {
	int N <- 150000 ;
	int iInit <- 1000;		

    float t;  
	float S <- N - float(iInit); 	      
	float I <- float(iInit); 
	float R <- 0.0; 
	
	float alpha <- 0.2 min: 0.0 max: 1.0;
	float beta <- 0.8  min: 0.0 max: 1.0;
 
	float h <- 0.1;
   
	equation SIR { 
		diff(S,t) = (- beta * S * I / N);
		diff(I,t) = (beta * S * I / N) - (alpha * I);
		diff(R,t) = (alpha * I);
	}
                
    reflex solving {
     	solve SIR method: #rk4 step_size: h ;
    }    
}


experiment maths type: gui {
	float minimum_cycle_duration<-0.2;
	
	output { 
		display display_charts {
			chart "SIR_agent" type: series background: #white {
				loop i from: 0 to: length(aSIR) - 1 {
					data 'S'+i value: (aSIR[i]).S color: #green ;					
				}

				loop i from: 0 to: length(aSIR) - 1 {
					data 'I'+i value: (aSIR[i]).I color: #red ;					
				}
						
				loop i from: 0 to: length(aSIR) - 1 {
					data 'R'+i value: (aSIR[i]).R color: #blue ;					
				}
			}
		}
		
		display display_charts_radar {
			chart "SIR_agent" type: radar background: #white axes:#white {
				data 'S0' value: (aSIR[0]).S[] color: #green ;			
				data 'I0' value: (aSIR[0]).I[] color: #red ;	
				data 'R0' value: (aSIR[0]).R[] color: #blue ;						
			}
		}	
		
//		display display_chartsH {
//			chart "SIR_agent" type: heatmap background: #white
//				reverse_axes: true
//			 {
//				data 'S0' value: S_3d color: #green ;	
//				data 'I0' value: I_3d color: #red ;	
//				data 'R0' value: R_3d color: #blue ;	
//			}
//		}
	}
}
