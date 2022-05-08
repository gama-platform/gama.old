/***
* Name: Influence of the integration step
* Author: Tri, Nghi, Benoit
* Description: The aim is to show the influence of the integration method on the result precision.
* 				Note: an integration step of 0.1 is considered as not accurate enough. It is used here to highlight clearly the impact of the integration method.
* Tags: equation, math
***/
model LVInfluenceoftheIntegrationMethod

global {
	init {
		create LVRK4 with: [x::2.0, y::2.0];
		create LVEuler with: [x::2.0, y::2.0];
	}

}

species LVRK4 {
	float t;
	float x;
	float y;
	float h <- 0.1;
	float alpha <- 0.8;
	float beta <- 0.3;
	float gamma <- 0.2;
	float delta <- 0.85;

	equation eqLV {
		diff(x, t) = x * (alpha - beta * y);
		diff(y, t) = -y * (delta - gamma * x);
	}

	reflex solving {
		solve eqLV method: #rk4 step_size: h;
	}

}


species LVEuler {
	float t;
	float x;
	float y;
	float h <- 0.1;
	float alpha <- 0.8;
	float beta <- 0.3;
	float gamma <- 0.2;
	float delta <- 0.85;

	equation eqLV {
		diff(x, t) = x * (alpha - beta * y);
		diff(y, t) = -y * (delta - gamma * x);
	}

	reflex solving {	
		solve eqLV method: #Euler step_size: h;    
	}
	
	reflex end_simulation when: cycle > 126{
		ask world{do pause;}
	}

}

experiment examples type: gui {
	float minimum_cycle_duration <- 0.1#s;
	output {
		display LV synchronized:false {
			chart 'Comparison Euler - RK4 (RK4 is more accurate)' type: xy x_serie:first(LVRK4).t[] background: #white {
				data "RK4" value: [first(LVRK4).x,first(LVRK4).y] color: #blue marker: false;
				data "Euler" value: [first(LVEuler).x,first(LVEuler).y] color: #red marker: false;
			}

		}

	}

}