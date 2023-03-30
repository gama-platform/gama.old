/***
* Name: Influence of the integration step
* Author: Tri, Nghi, Benoit
* Description: The aim is to show the influence of the integration method on the result precision.
* 				Note: an integration step of 0.1 is considered as not accurate enough. It is used here 
* 				to highlight the impact of the integration method.
* 
* 				About the expected dynamics: Lotka-Volterra model solutions are known to be periodic. With
* 				a step of 0.1, the numerical solution provided by the Runge-Kutta 4 method for Lotka-Volterra 
* 				model looks periodic (see the phase portrait), while for the Euler the solution is unbounded (which
* 				is wrong). See as time increases how errors accumulate, leading to negative and unbounded values.
* 
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
		layout #split tabs: true;
		display LV_series name: "Time series" toolbar: false  type: 2d {
			chart 'Comparison Euler - RK4 (RK4 is more accurate)' type: series 
			x_serie: first(LVRK4).t[] y_label: "pop" background: rgb(47,47,47) color: #white x_tick_line_visible: false {
				data "x (rk4)" value: first(LVRK4).x[] color: rgb(52,152,219) marker: false thickness: 2;
				data "y (rk4)" value: first(LVRK4).y[] color: rgb(41,128,185) marker: false thickness: 2;
				data "x (Euler)" value: first(LVEuler).x[] color: rgb(243,156,18) marker: false thickness: 2;
				data "y (Euler)" value: first(LVEuler).y[] color: rgb(230,126,34) marker: false thickness: 2;
			}

		}
		display LV_phase_portrait name: "Phase portrait" toolbar: false  type: 2d {
			chart 'Comparison Euler - RK4 (RK4 is more accurate)' type: xy 
			background: rgb(47,47,47) color: #white x_label: "x" y_label: "y" x_tick_line_visible: false y_tick_line_visible: false{
				data "y(x(t)) rk4" value: rows_list(matrix(first(LVRK4).x[],first(LVRK4).y[])) color: rgb(52,152,219) marker: false thickness: 2;
				data "y(x(t)) Euler" value: rows_list(matrix(first(LVEuler).x[],first(LVEuler).y[])) color: rgb(243,156,18) marker: false thickness: 2;
			}

		}

	}

}