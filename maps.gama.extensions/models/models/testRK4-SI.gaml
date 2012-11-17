/**
 *  testRK4-SI
 *  Author: bgaudou
 *  Description: 
 */

model testRK4

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

environment {}

entities {
	species node skills: [EDP]{
		float I <- float(iInit); 
		float S <- N - I; 
		
		map equations <- [
			"S"::"(- beta * S * I / N) + (gamma * I)",
			"I"::"(beta * S * I / N) - (gamma * I)"
		];
		map param <- [
			"beta"::beta,
			"gamma"::gamma,
			"N"::N
		];

		reflex go {
			let varValues type: list of: float <- [];
			add S to: varValues;
			add I to: varValues;
		
			let temp type: list of: float <- list(self RK4 [
			  					equations::equations, 
			  					value::varValues, 
			  					param::param, 
			  					h::hKR4
			  				]); 
		  set S value: (temp at 0);
		  set I value: (temp at 1);
		}		
	}}

experiment testRK4 type: gui {
	parameter 'Beta:' var: beta category: 'SI' ;	
	parameter 'Gamma:' var: gamma category: 'SI' ;	
	parameter 'I init' var: iInit category: 'SI';
	
	parameter 'Population by node:' var: N category: 'Population'; 
	
	parameter 'h of RK4' var: hKR4 category: 'Discretization';
	
	output {
		display SI refresh_every: 1 {
			chart "SI" type: series background: rgb('white') {
				data S value: first(list(node)).S color: rgb('green') ;				
				data I value: first(list(node)).I color: rgb('red') ;
			}
		}
	}}
