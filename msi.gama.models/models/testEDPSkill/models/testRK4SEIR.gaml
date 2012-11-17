/**
 *  testRK4SEIR
 *  Author: bgaudou
 *  Description: 
 */

model testRK4SEIR

global {

	float alpha <- 0.2 min: 0.0 max: 1.0;
	float beta <- 0.8 min: 0.0 max: 1.0;
	float a <- 0.3 min: 0.0 max: 1.0;
	float b <- 0.0 min: 0.0 max: 1.0;
	float d <- 0.0 min: 0.0 max: 1.0;
	int N <- 1500 min: 1 max: 3000;
	float hKR4 <- 0.01;
	int iInit <- 1;

	init {
		create node number: 1;
	}

}

entities {
	species node skills: [EDP]{
		float I <- float(iInit);
		float E <- 0.0; 
		float S <- N - I; 
		float R <- 0.0; 
	
		reflex go {
			let temp type: list of: float <- list(self RK4SEIR [
				S::S, E::E, I::I, R::R, alpha::alpha, beta::beta, a::a,b::b, d::d, N::N, h::hKR4
			]); 
		  set S value: (temp at 0);
		  set E value: (temp at 1);
		  set I value: (temp at 2);
		  set R value: (temp at 3);
		}		
	}
}

experiment testRK4SEIR type: gui {
	parameter 'Alpha:' var: alpha category: 'SIR' ;
	parameter 'Beta:' var: beta category: 'SIR' ;	
	parameter 'a:' var: a category: 'SIR' ;	
	parameter 'I init' var: iInit category: 'SI';
	
	parameter 'birth' var: b category: 'demography';
	parameter 'death' var: d category: 'demography';
	
	parameter 'Population par noeud:' var: N category: 'Population'; 

	parameter 'h of RK4' var: hKR4 category: 'Discretization';	
	
	output {
		display SIR refresh_every: 1 {
			chart "SIR" type: series background: rgb('white') {
				data S value: first(list(node)).S color: rgb('green') ;		
				data E value: first(list(node)).E color: rgb('black') ;								
				data I value: first(list(node)).I color: rgb('red') ;
				data R value: first(list(node)).R color: rgb('blue') ;
			}
		}
	}
}