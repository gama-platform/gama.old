/**
 *  simple_ODE_LotkaVolterra.gaml
 *  Author: hqnghi
 *  Description: Simple example of a Lotka and Volterra prey-predator system.
 */

model simple_ODE_LotkaVolterra

global {

	float alpha <- 0.8 min: 0.0 max: 1.0;
	float beta <- 0.3 min: 0.0 max: 1.0;
	float gamma <- 0.2 min: 0.0 max: 1.0;
	float delta <- 0.85 min: 0.0 max: 1.0;
	
	float xInit <- 2.0 min: 0.0;
	float yInit <- 2.0 min: 0.0;
	
	init{
		create my_maths number:1 with:[x::xInit, y::yInit];
	}
}

entities {
	species my_maths {
	    float t;  
		float x ;
		float y ;
  
		equation lotka_volterra { 
			diff(x,t) =   x * (alpha - beta * y);
			diff(y,t) = - y * (delta - gamma * x);
        }
                
        solve lotka_volterra method: "rk4" step:0.01 ;        
	}
}

experiment maths type: gui {
	output { 
		display LV refresh_every: 1 {
			chart "SIR" type: series background: rgb('white') {
				data 'x' value: first(my_maths).x color: rgb('green') ;				
				data 'y' value: first(my_maths).y color: rgb('red') ;
			}
		}
		display LVphase refresh_every: 1 {			
			chart "SIR" type: xy background: rgb('white') {
				data 'x' value: first(my_maths).x color: rgb('green') ;				
				data 'y' value: first(my_maths).y color: rgb('red') ;
			}
		}			
	}
}
