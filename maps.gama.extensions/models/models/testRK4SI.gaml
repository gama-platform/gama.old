/**
 *  micmactestRK4
 *  Author: bgaudou
 *  Description: 
 */

model micmactestRK4

global {

	float beta <- 0.8 min: 0.0 max: 1.0;
	float gamma <- 0.1 min: 0.0 max: 1.0;
	int N <- 1500 min: 1 max: 3000;
	float hKR4 <- 0.01;
	int iInit <- 1;

	init {
		create node number: 1;
	}

}

entities {
	species node skills: [SIR]{
		float I <- float(iInit); 
		float S <- N - I; 
	
		reflex go {
			let temp type: list of: float <- 
			  list(self RK4SI [S::S, I::I, beta::beta, gamma::gamma, N::N, h::hKR4]); 
		  set S value: (temp at 0);
		  set I value: (temp at 1);
		}		
	}
}

experiment testRK4SI type: gui {
	parameter 'Beta:' var: beta category: 'SIR' ;	
	parameter 'Gamma:' var: gamma category: 'SIR' ;	
	
	parameter 'Population par noeud:' var: N category: 'Population'; 
	
	output {
		display SIR refresh_every: 1 {
			chart "SIR" type: series background: rgb('white') {
				data S value: first(list(node)).S color: rgb('green') ;				
				data I value: first(list(node)).I color: rgb('red') ;
			}
		}
	}
}