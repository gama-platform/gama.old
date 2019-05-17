/***
* Name: SIRInfluenceofSimulationStep
* Author: Tri, Nghi and Benoit 
* Description: This model illustrates the impact of the simulation step on the integration speed.
* Tags: equation, math, step
***/


model SIRInfluenceofSimulationStep

global {	
	init {
		write name + "" + step;
		create userSIR with: [h::0.1,N::500,I::1.0];
	}
	
	reflex w {
		write name + " - c = " + cycle + " - s = " + step + " - t = " + time;		
	}
}

species userSIR {
	float t;
	int N;
	float I ; 
	float S <- N - I; 
	float R <- 0.0; 
	float h;
	float beta<-0.4;
	float gamma<-0.01; 				
	
	equation eqSIR {
		diff(S,t) = (- beta * S * I / N);
		diff(I,t) = (beta * S * I / N) - (gamma * I);
		diff(R,t) = (gamma * I);
	}		
	
	reflex solving {		
		solve eqSIR method: #rk4 step_size: h ;
	}

}


experiment examples type: gui {
	
	init {
		create simulation with: [step::2#s,name::"s2s"]   ;
		create simulation with: [step::10#s,name::"s10s"] ;		
	}
	
	output {		
		display SIR  {
			chart 'examplesUserSIR' type: series background: #lightgray {
				data "S" value: first(userSIR).S color: #green;
				data "I" value: first(userSIR).I color: #red;
				data "R" value: first(userSIR).R color: #blue;
			}			
		}				
	}
}
