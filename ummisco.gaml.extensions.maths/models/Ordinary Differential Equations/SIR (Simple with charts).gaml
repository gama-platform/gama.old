/**
* Name: SIR (Simple)
* Author: hqnghi
* Description: A simple example of ODE use into agents with the example of the SIR equation system.
* Tags: equation, math
*/

model simple_ODE_SIR

global {
	init{
		create aSIR number:10{
			S<-S-(int(self)*10000);
		}
	}
	list S_3d->{aSIR collect each.S};
	list I_3d->{aSIR collect each.I};
	list R_3d->{aSIR collect each.R};
	reflex ss when: cycle>=40{
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
   
	equation SIR{ 
		diff(S,t) = (- beta * S * I / N);
		diff(I,t) = (beta * S * I / N) - (alpha * I);
		diff(R,t) = (alpha * I);
	}
                
    reflex solving {
    	solve SIR method: "rk4" step: h ;//cycle_length: 1/h ;
    }    
}


experiment maths type: gui {
		float minimum_cycle_duration<-0.2;
	
	output { 
		display display_charts {
			chart "SIR_agent" type: series background: #white {
				data 'S0' value: (aSIR[0]).S color: #green ;				
				data 'S1' value: (aSIR[1]).S color: #green ;				
				data 'S2' value: (aSIR[2]).S color: #green ;				
				data 'S3' value: (aSIR[3]).S color: #green ;				
				data 'S4' value: (aSIR[4]).S color: #green ;				
				data 'S5' value: (aSIR[5]).S color: #green ;				
				data 'S6' value: (aSIR[6]).S color: #green ;				
				data 'S7' value: (aSIR[7]).S color: #green ;				
				data 'S8' value: (aSIR[8]).S color: #green ;		
				data 'S9' value: (aSIR[9]).S color: #green ;				
						
				data 'I0' value: (aSIR[0]).I color: #red ;				
				data 'I1' value: (aSIR[1]).I color: #red ;				
				data 'I2' value: (aSIR[2]).I color: #red ;				
				data 'I3' value: (aSIR[3]).I color: #red ;				
				data 'I4' value: (aSIR[4]).I color: #red ;				
				data 'I5' value: (aSIR[5]).I color: #red ;				
				data 'I6' value: (aSIR[6]).I color: #red ;				
				data 'I7' value: (aSIR[7]).I color: #red ;				
				data 'I8' value: (aSIR[8]).I color: #red ;		
				data 'I9' value: (aSIR[9]).I color: #red ;				
						
				data 'R0' value: (aSIR[0]).R color: #blue ;				
				data 'R1' value: (aSIR[1]).R color: #blue ;				
				data 'R2' value: (aSIR[2]).R color: #blue ;				
				data 'R3' value: (aSIR[3]).R color: #blue ;				
				data 'R4' value: (aSIR[4]).R color: #blue ;				
				data 'R5' value: (aSIR[5]).R color: #blue ;				
				data 'R6' value: (aSIR[6]).R color: #blue ;				
				data 'R7' value: (aSIR[7]).R color: #blue ;				
				data 'R8' value: (aSIR[8]).R color: #blue ;		
				data 'R9' value: (aSIR[9]).R color: #blue ;			
			}
		}
		
		display display_charts_radar {
			chart "SIR_agent" type: radar background: #white axes:#white {
				data 'S0' value: (aSIR[0]).S[] color: #green ;			
				data 'I0' value: (aSIR[0]).I[] color: #red ;	
				data 'R0' value: (aSIR[0]).R[] color: #blue ;		
			}
		}
		
		
		
		display display_chartsH {
			chart "SIR_agent" type: heatmap background: #white
			reverse_axes: true
			 {
				data 'S0' value: S_3d color: #green ;	
				data 'I0' value: I_3d color: #red ;	
				data 'R0' value: R_3d color: #blue ;	
			}
		}
	}
}
