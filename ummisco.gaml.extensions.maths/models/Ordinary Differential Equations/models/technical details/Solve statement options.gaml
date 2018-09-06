/**
* Name: Predefined equestions
* Author: Benoit Gaudou
* Description: Presentation of all the predefined equation systems.
* Comparaison with hand-written systems to test them.
* Tags: equation, math
*/
model all_predefined_equations

global {
	float mu <- 0.02;
	float alpha <- 35.842;
	float gamma <- 100.0;
	float beta0 <- 1884.95;
	float beta1 <- 0.255;
	float hKR4 <- 0.01;
	
	init {
		create userSIR with: [h::0.1,N::500,I::1.0];
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
		list i_list;
		list v_list ;
		solve eqSIR method: "rk4" step: h integrated_times: i_list integrated_values: v_list;
		write "i_list: " + i_list;
		write "v_list:" + v_list;
	}

}


experiment examples type: gui {
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
