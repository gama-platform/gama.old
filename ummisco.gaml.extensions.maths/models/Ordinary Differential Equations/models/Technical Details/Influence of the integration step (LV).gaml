/***
* Name: Influence of the integration step
* Author: Tri, Nghi, Benoit
* Description: The aim is to show the influence of the integration step on the result precision.
* 			   Notice that a step of value 1.0 is not realistic, but this value is necessary with the RK4 method to show the influence of the integration step.
* Tags: equation, math
***/

model LVInfluenceoftheIntegrationstep

global {
	init {
		create userLV with: [h::0.01,x::2.0,y::2.0];	
		create userLV with: [h::1.0,x::2.0,y::2.0];									
	}
}

species userLV {
	float t;
	float x ; 
	float y ; 
	float h;
	float alpha <- 0.8 ;
	float beta  <- 0.3 ;
	float gamma <- 0.2 ;
	float delta <- 0.85;
	
	equation eqLV { 
		diff(x,t) =   x * (alpha - beta * y);
		diff(y,t) = - y * (delta - gamma * x);
    }		
    
	reflex solving {
		solve eqLV method: #rk4 step_size: h;
	}
}

experiment examples type: gui {
	output {		
		display LV  {
			chart 'examplesUserLV' type: series background: #lightgray {
				data "x" value: first(userLV).x color: #yellow;
				data "y" value: first(userLV).y color: #blue;
				data "x1" value: last(userLV).x color: #red;
				data "y1" value: last(userLV).y color: #green;				
			}			
		}						
	}
}