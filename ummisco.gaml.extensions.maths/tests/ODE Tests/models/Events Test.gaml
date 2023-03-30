/**
* Name: ODE Solver Test
* Author: Tri Nguyen-Huu,Huynh Quang Nghi
* 
* This model tests if ODE solvers react as expected to events. 
* The 'normal' experiment tests the reaction to the modification of a variable governed by an equation 
* (here the number of infected')
* The 'dynamical_creation' tests if the ODE system adapts to the creation/destruction of agents, adding and 
* removing equations dynamically.
* The 'test' experiment do both and quantify the error. An error of 0 is expected, otherwise the model does not
* behave as expected.
* 
* Tags: math, equation
*/



model SplitTest


global{
	bool with_viz <- true;
	bool dynamics_creation <- false;

	int final_cycle <- 20;

	float beta <- 1.0/#s; 
	float delta <- 0.01/#s;
	
	float error <- 0.0;

	init {
		create SIR_model {
			Sm <- 495.0;
			Im <- 0.0;
			Rm <- 0.0;
		}
	
		create S {
			Ssize <- 495.0;
		}
		if !dynamics_creation {
			create I {Isize <- 0.0;}
		}
		create R {
			Rsize <- 0.0;
		}
		
		if !with_viz{
			loop test_t from: 0 to: 20{
				do compute_err;
				if (test_t = 5) {do add_infected;}
				if (test_t = 15) {do remove_infected;}
				ask SIR_model {do solveEquation;}
				ask S {do solveEquation;}
				write error;
			}
		}
	}
	
	reflex add_infected when: (cycle = 5) {
		do add_infected;
	}	
	
	action add_infected {
		first(SIR_model).Im <- 5.0;
		if !dynamics_creation{
			first(I).Isize <- 5.0;
		} else {
			create I {Isize <- 5.0;}
		}
	}
	
	reflex remove_infected when: (cycle = 15){
		do remove_infected;
	}
	
	action remove_infected {
		first(SIR_model).Im <- 0.0;
		if !dynamics_creation{
			first(I).Isize <- 0.0;
		} else {
			ask I {do die;}
		}
	}
	
	reflex compute_err when: with_viz{
		do compute_err;
	}
	
	action compute_err {
		error <- max([error, abs(first(SIR_model).Im - sum(I collect each.Isize))]);
	}
	
	float event_monitor {
		return (cycle = 4 or cycle = 14)?500.0:0.0;
	}
	
	float i_count {
		return sum(I collect each.Isize);
	}
	
	reflex endSim when:  (cycle >= final_cycle) {
		write "Distance between the split system\nand the regular system: "+error;		
		do pause;
	}		
}


	species SIR_model{	
		float t ;
		float Sm;
		float Im;
		float Rm;

		equation SIR {
			diff ( Sm , t ) = - beta * Sm * Im / 500; 
			diff ( Im , t ) =   beta * Sm * Im / 500  - delta * Im; 
			diff ( Rm , t ) =   delta * Im;
		}
		
		reflex solveEquation{
			do solveEquation;
		}
		
		action solveEquation{
			solve SIR method: "rk4";
		}
		
	}

	species S {
		float Ssize;
		float t ;
		
		equation evol simultaneously: [I, R] {
			diff (self . Ssize , t) = - beta * first (S).Ssize * sum(I collect each.Isize) / 500;
		}
		
		reflex solveEquation{
			do solveEquation;
		}
		
		action solveEquation{
			solve evol method: "rk4";
		}
	}
	
	species I {
		float Isize; 
		float t ;
		
		equation evol simultaneously: [S , R] {
			diff (self. Isize , t) =  beta * first (S) . Ssize * self. Isize / 500  - delta * self . Isize ;
		}

	}
	species R {
		float Rsize;
		float t ;
		equation evol simultaneously: [S , I ] {
			diff (self.Rsize , t) = delta * sum(I collect each.Isize) ;
		}
	}
	

	

experiment normal type: gui {
	output {
		display "Equation Tests" refresh: every(1 #cycle)  type: 2d {
			chart '5 infected added at cycle=5, all infected at cycle=1' type: series background: #white y_range: {0,500} {
				data "S" value: (first(SIR_model).Sm) color: #green marker: true line_visible: false; 
				data "I" value: (first(SIR_model).Im) color: #red marker: true line_visible: false;
				data "R" value: (first(SIR_model).Rm) color: #blue marker: true line_visible: false;	
				data "S (split)" value: (first(S).Ssize) color: #green marker: false; 
				data "I (split)" value: (first(I).Isize) color: #red marker: false;
				data "R (split)" value: (first(R).Rsize) color: #blue marker: false;	
				data "Events" value: world.event_monitor() color: #black marker: false style: bar;
			}
		}
	}
}


experiment dynamical_creation type: gui {
	action _init_ {
		create SplitTest_model with: [dynamics_creation::true];
	}
	
	output {
		display "Equation Tests" refresh: every(1 #cycle)  type: 2d  {
			chart '5 infected added at cycle=5, all infected at cycle=1' type: series background: #white y_range: {0,500} {
				data "S" value: (first(SIR_model).Sm) color: #green marker: true line_visible: false; 
				data "I" value: (first(SIR_model).Im) color: #red marker: true line_visible: false;
				data "R" value: (first(SIR_model).Rm) color: #blue marker: true line_visible: false;	
				data "S (split)" value: (first(S).Ssize) color: #green marker: false; 
				data "I (split)" value: world.i_count() color: #red marker: false;
				data "R (split)" value: (first(R).Rsize) color: #blue marker: false;
				data "Events" value: world.event_monitor() color: #black marker: false style: bar;	
			}
		}
	}
}


experiment test type: test {
	test "Equations" {
		create SplitTest_model with: [with_viz::false];
		write "Distance between the static split system\nand the static regular system: "+error;
		assert error = 0;
		create SplitTest_model with: [with_viz::false, dynamics_creation::true];
		write "Distance between the dynamics split system\nand the dynamics regular system: "+error;
		assert error = 0;
	}	
}