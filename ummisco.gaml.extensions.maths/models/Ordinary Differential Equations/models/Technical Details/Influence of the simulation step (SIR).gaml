/***
* Name: SIRInfluenceofSimulationStep
* Author: Tri, Nghi and Benoit 
* Description: This model illustrates the impact of the simulation step on the integration speed.
* Tags: equation, math, step
***/


model SIRInfluenceofSimulationStep

global {
	string step_string;
	init {
		write name + "" + step;
		step_string <- string(step)+"s";
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
	float minimum_cycle_duration <- 0.1#s;
	
	action _init_ {
		create simulation with: [step::2#s,name::"s2s"]   ;
		create simulation with: [step::10#s,name::"s10s"] ;		
	}
	
	output {
		layout #split tabs: true;	
		display SIR toolbar: false  type: 2d {
			chart 'Time Teries ('+step_string+' per cycle)' type: series 
			background: rgb(47,47,47) color: #white y_label: "pop" x_tick_line_visible: false{
				data "S" value: first(userSIR).S[] color: rgb(46,204,113) marker: false thickness: 2;
				data "I" value: first(userSIR).I[] color: rgb(231,76,60) marker: false thickness: 2;
				data "R" value: first(userSIR).R[] color: rgb(52,152,219) marker: false thickness: 2;
			}			
		}
		display "Phase Portrait" toolbar: false  type: 2d  {
		chart 'Phase Portrait ('+step_string+' per cycle)' type: xy 
		background: rgb(47,47,47) color: #white y_label: "y" x_label: "x"
			x_range: {0,500} y_range: {0,450}{
				data "I vs S" value: rows_list(matrix(first(userSIR).S[],first(userSIR).I[])) color: rgb(52,152,219) marker: false;			
			}		
		}					
	}
}
