/**
 *  testRK4-LotkaVolterra
 *  Author: bgaudou
 *  Description: 
 */

model testRK4LotkaVolterra

global {
	float alpha <- 0.8 min: 0.0 max: 1.0;
	float beta <- 0.3 min: 0.0 max: 1.0;
	float gamma <- 0.2 min: 0.0 max: 1.0;
	float delta <- 0.85 min: 0.0 max: 1.0;
	
	float xInit <- 2.0 min: 0.0;
	float yInit <- 2.0 min: 0.0;
	
	float hKR4 <- 0.01;
	float nbSteps <- 1.0;

	init {
		create LotkaVolterra number: 1;
	}
}

environment {}

entities {
	species LotkaVolterra skills: [EDP]{
		float x <- xInit;
		float y <- yInit;
		
		map equations <- [
			"x"::"x * (alpha - beta * y)",
			"y"::"- y * (delta - gamma * x)"
		];
		map param <- [
			"alpha"::alpha,
			"beta"::beta,
			"gamma"::gamma,
			"delta"::delta
		];
	
		reflex go {
			let varValues type: list of: float <- [];
			add x to: varValues;
			add y to: varValues;
		
			let temp type: list of: float <- list(self RK4iterated [
			  				equations::equations,  
			  				value::varValues, 
			  				param::param, 
			  				h::hKR4,
			  				nbSteps::nbSteps
			  			]); 
		  set x value: (temp at 0);
		  set y value: (temp at 1);
		}		
	}
}

experiment testRK4LotkaVolterra type: gui {
	parameter 'Xinit:' var: xInit category: 'Populations' ;	
	parameter 'Yinit:' var: yInit category: 'Populations' ;	
	
	parameter 'Alpha:' var: alpha category: 'parametres' ;	
	parameter 'Beta:' var: beta category: 'parametres' ;	
	parameter 'Gamma:' var: gamma category: 'parametres' ;	
	parameter 'Delta:' var: delta category: 'parametres' ;
	
	parameter 'h of RK4' var: hKR4 category: 'Discretization';
	parameter 'nbSteps' var: nbSteps category: 'Discretization';
	
	output {
		display LV refresh_every: 1 {
			chart "SIR" type: series background: rgb('white') {
				data 'x' value: first(list(LotkaVolterra)).x color: rgb('green') ;				
				data 'y' value: first(list(LotkaVolterra)).y color: rgb('red') ;
			}
		}
		display LVphase refresh_every: 1 {
			chart "SIR" type: xy background: rgb('white') {
				data 'x' value: first(list(LotkaVolterra)).x color: rgb('green') ;				
				data 'y' value: first(list(LotkaVolterra)).y color: rgb('red') ;
			}
		}		
	}}
