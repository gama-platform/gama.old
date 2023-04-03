/**
* Name: stochastic_differential_equations
* Author: Jean-Claude Régnault 
* Description: A simple example of an SDE simulation solved with an Euler-Maruyama Method for a 
* 				geometric brownian motion, which is based on a classical Euler forward method for ODEs.
* 
* 				This toy model simulates the evolution of some cryptocurrencies. Use at your own risk.
*
* Tags: math, equation
*/
 
model stochastic_differential_equations

global {	
	string view <- "Month" among: ["Day","Week","Month","3 Months","Year","Lifetime"];
	int time_window_size <- 1;
	int elapsed_steps <- 1;
	float step <- 1.2;
	// step for integration	
	float dt <- 0.01;
	int nb_steps <- int(floor(1/dt));
	
	//parameters of the geometric brownian motion
  	float mu <- 0.03;
   	float sigma <- 0.25; 
   	
   	//initial condition
   	float X0 <- 1.0;
   	  	
	init {
		create SDE_agent number: 4{
			X <- X0;
		}
	}
	
	
	reflex update_time_window{
		switch view{
			match "Day"{
				time_window_size <-1;
			}
			match "Week"{
				time_window_size <-30;
			}
			match "Month"{
				time_window_size <-30;
			}
			match "3 Months"{
				time_window_size <-90;
			}
			match "Year"{
				time_window_size <-365;
			}
			match "Lifetime"{
				time_window_size <-length(first(SDE_agent).t[]);
			}
		}
	}
}

//Species which represents the SDE System 
species SDE_agent {
	//Variable to represent the discrete time for integration
	float t;
 	//Main variable 
   	float X; 	

	equation SDE simultaneously: [SDE_agent]{
		diff(X,t) = mu * X + sigma * X * sqrt(dt) * gauss(0,1)/dt;
	}
	
	reflex solving when: int(self)=0{
		solve SDE method: "Euler" step_size: dt;
		elapsed_steps <- length(t[]);
	}
}


experiment mysimulation type: gui {
	float minimum_cycle_duration <- 0.2#s;
	//parameter "Window" var: time_window_size category: 'Choix';
	parameter "Window" var: view category: 'Choix';
	output {
		layout 	#split tabs: true;
		display display_charts  type: 2d  {
			chart 'BTC price' type: series  
				x_range: min(elapsed_steps,time_window_size*nb_steps) background: #white 
				x_tick_line_visible: false{
				data "BTC" value: first(SDE_agent).X[] color: rgb(239,142,25) marker: false;
			}
		}
		display display_charts2  type: 2d  {
			chart 'SOL price' type: series 
				x_range: min(elapsed_steps,time_window_size*nb_steps)  background: #white 
				x_tick_line_visible: false{
				data "SOL" value: SDE_agent[1].X[] color: rgb(0,0,0) marker: false;
			}
		}
		display display_charts3  type: 2d  {
			chart 'ETH price' type: series 
				x_range: min(elapsed_steps,time_window_size*nb_steps) background: #white 
				x_tick_line_visible: false{
				data "ETH" value: SDE_agent[2].X[] color: rgb(33, 92,175) marker: false;
			}
		}
		display display_charts4  type: 2d {
			chart 'XRP price' type: series 
				x_range: min(elapsed_steps,time_window_size*nb_steps) background: #white 
				x_tick_line_visible: false{
				data "XRP" value: SDE_agent[3].X[] color: rgb(31,135,178) marker: false;
			}
		}
	}
}


