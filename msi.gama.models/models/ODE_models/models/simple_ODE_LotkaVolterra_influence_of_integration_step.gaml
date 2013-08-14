/**
 *  simple_ODE_LotkaVolterra_influence_of_integration_step.gaml.gaml
 *  Author: hqnghi
 *  Description: Use of the simple Lotka and Volterra prey-predator system to illustrate the influence of the integration step.
 *  In the example, I took 3 different steps: 1, 0.1 and 0.01.
 */

model simple_ODE_LotkaVolterra_influence_of_integration_step

global {

	float alpha <- 0.8 min: 0.0 max: 1.0;
	float beta <- 0.3 min: 0.0 max: 1.0;
	float gamma <- 0.2 min: 0.0 max: 1.0;
	float delta <- 0.85 min: 0.0 max: 1.0;
	
	float xInit <- 2.0 min: 0.0;
	float yInit <- 2.0 min: 0.0;
	
	init{
		create agt_LV with:[x::xInit, y::yInit, h::1, my_length::1];
		create agt_LV with:[x::xInit, y::yInit, h::0.1, my_length::10];
		create agt_LV with:[x::xInit, y::yInit, h::0.01, my_length::100];				
	}
}

entities {
	species agt_LV {
	    float t;  
		float x ;
		float y ;
		
		float h;
		float my_length;
  
		equation lotka_volterra { 
			diff(x,t) =   x * (alpha - beta * y);
			diff(y,t) = - y * (delta - gamma * x);
        }
                
        solve lotka_volterra method: "rk4" step:h cycle_length: my_length ;        
	}
}

experiment maths type: gui {
	output { 
		display LV refresh_every: 1 {
			chart "LV_h1" type: series background: rgb('white') position: {0,0} size:{1,0.33} {
				data 'x' value: first(agt_LV where [each.h = 1]).x color: rgb('green') ;				
				data 'y' value: first(agt_LV where [each.h = 1]).y color: rgb('red') ;
			}
			chart "LV_h0.1" type: series background: rgb('white') position: {0,0.33} size:{1,0.33} {
				data 'x' value: first(agt_LV where [each.h = 0.1]).x color: rgb('green') ;				
				data 'y' value: first(agt_LV where [each.h = 0.1]).y color: rgb('red') ;
			}
			chart "LV_h0.01" type: series background: rgb('white') position: {0,0.66} size:{1,0.33} {
				data 'x' value: first(agt_LV where [each.h = 0.01]).x color: rgb('green') ;				
				data 'y' value: first(agt_LV where [each.h = 0.01]).y color: rgb('red') ;
			}			
		}
		display LVphase refresh_every: 1 {			
			chart "LV_h1" type: xy background: rgb('white') position: {0,0} size:{0.33,1} {
				data 'x' value: first(agt_LV where [each.h = 1]).x color: rgb('green') ;				
				data 'y' value: first(agt_LV where [each.h = 1]).y color: rgb('red') ;
			}
			chart "LV_h0.1" type: xy background: rgb('white') position: {0.33,0} size:{0.33,1} {
				data 'x' value: first(agt_LV where [each.h = 0.1]).x color: rgb('green') ;				
				data 'y' value: first(agt_LV where [each.h = 0.1]).y color: rgb('red') ;
			}
			chart "LV_h0.01" type: xy background: rgb('white') position: {0.66,0} size:{0.33,1} {
				data 'x' value: first(agt_LV where [each.h = 0.01]).x color: rgb('green') ;				
				data 'y' value: first(agt_LV where [each.h = 0.01]).y color: rgb('red') ;
			}	
		}			
	}
}
