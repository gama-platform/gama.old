/**
* Name: SIR (Split in Agents, Multiple Strains)
* Author: tri and nghi
* Description: This model is an extension of the model SIR_split_in_agents.gaml. 
* It creates several agents of one compartment type (here typically several I_agt).
* Tags: equation, math
*/
model SIR_split_in_agents_multiple_strains

global {
	// Parameters
	int number_S <- 495 ; // The number of susceptible
	int number_I <- 5 ; // The number of infected
	int number_R <- 0 ; // The number of removed 

	float _beta <- 0.8 ; // The parameter Beta
	float _delta <- 0.2 ; // The parameter Delta
	
	// Global variables
	int strain_number <- 2;
	int N <- number_S + number_I * strain_number + number_R;	
	float hKR4 <- 0.01;
	
	init {
		create S_agt {
			Ssize <- float(number_S);
		}

		create I_agt number: strain_number {
			Isize <- float(number_I);
			self.beta <- _beta*(0.8+rnd(0.4)); 
			write self.beta;
			self.delta <- _delta; 
		}

		create R_agt {
			Rsize <- float(number_R);
		}

		create my_SIR_maths {
			self.Sm <- float(number_S);
			self.Im <- float(number_I) * strain_number;
			self.Rm <- float(number_R);
		}

		write 'Basic Reproduction Number (R0): ' + string(_beta * number_S / (_delta));
	}

}


species S_agt {
	float t;		
	float Ssize;
	
	equation evol simultaneously: [I_agt, R_agt] {
		diff(self.Ssize, t) = (- sum(I_agt accumulate [each.beta * each.Isize]) * self.Ssize / N);
	}

	reflex solving {
		solve evol method: #rk4 step_size: hKR4 ;
	}
}

species I_agt {
	float t;		
	float Isize;
	 
	float beta;
	float delta;
	
	equation evol simultaneously: [S_agt, R_agt] {
		diff(self.Isize, t) = (beta * first(S_agt).Ssize * self.Isize / N) - (delta * self.Isize);
	}
}

species R_agt {
	float t;		
	float Rsize;

	equation evol simultaneously: [I_agt] {
		diff(self.Rsize, t) = (sum(I_agt collect (each.delta * each.Isize)));
	}
}

species my_SIR_maths {
	float t;
	float Im;
	float Sm;
	float Rm;
	
	equation SIR {
		diff(self.Sm, t) = (-_beta * Sm * Im / N);
		diff(self.Im, t) = (_beta * Sm * Im / N) - (_delta * Im);
		diff(self.Rm, t) = (_delta * Im);
	}

	reflex solving {
		solve SIR method: #rk4 step_size: hKR4;
	}
}



experiment Simulation type: gui {
	float minimum_cycle_duration <- 0.1#s;
	parameter 'Number of Susceptible' type: int var: number_S <- 495 category: "Initial population"; 
	parameter 'Number of Infected'    type: int var: number_I <- 5   category: "Initial population";
	parameter 'Number of Removed'     type: int var: number_R <- 0   category: "Initial population";

	parameter 'Beta (S->I)'  type: float var: _beta <- 0.8   category: "Parameters";
	parameter 'Delta (I->R)' type: float var: _delta <- 0.2 category: "Parameters";	
	
	output {
		layout #split tabs: true;
		display chart_3system_eq name: "Split system" toolbar: false  type: 2d {
			chart 'Split system' type: series background: rgb(47,47,47) color: #white x_tick_line_visible: false{
				data 'susceptible' value: first(S_agt).Ssize color: rgb(46,204,113) marker_shape: marker_circle;
				data 'infected 1' value: first(I_agt).beta * first(I_agt).Isize color: rgb(231,76,60)+120 marker_shape: marker_diamond;
				data 'infected 2' value: last(I_agt).beta * last(I_agt).Isize color: rgb(231,76,60)+100 marker_shape: marker_diamond;
				data 'infected 1+2' value: sum(I_agt accumulate (each.beta * each. Isize)) color: rgb(231,76,60) marker_shape: marker_circle;				
				data 'recovered' value: first(R_agt).Rsize color: rgb(52,152,219) marker_shape: marker_circle;
			}

		}

		display chart_1system_eq name: "Unified system" toolbar: false  type: 2d  {
			chart 'Unified system' type: series background: rgb(47,47,47) color: #white x_tick_line_visible: false{
				data 'susceptible (maths)' value: first(my_SIR_maths).Sm color: rgb(46,204,113) marker_shape: marker_circle;
				data 'infected (maths)' value: first(my_SIR_maths).Im color: rgb(231,76,60) marker_shape: marker_circle;
				data 'recovered (maths)' value: first(my_SIR_maths).Rm color: rgb(52,152,219) marker_shape: marker_circle;
			}
		}
	}
}


