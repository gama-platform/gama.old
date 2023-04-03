/**
* Name: SIR_split_in_agents
* Author: hqnghi 
* Description: This model illustrates the possibility to split an equation system into several agents. 
*       All the equations are solved together thanks to the `simultaneously` facet of the equation statement. 
*       We also compare the split model with the simple SIR one. 
* Tags: equation, math
*/

model SIR_split_in_agents

global {
	int number_S <- 495 ; // The number of susceptible
	int number_I <- 5   ; // The number of infected
	int number_R <- 0   ; // The number of removed 

	float beta  <- 1.0  ; // The parameter Beta
	float delta <- 0.01 ; // The parameter Delta
	
	// Note that N will remain constant as demography is not taken into account in the SIR model.
	int N <- number_S + number_I + number_R ;
	float hKR4 <- 0.07 ;

	init {
		//Creation of the representation of the susceptible agents
		create S_agt {
			Ssize <- float(number_S) ;
			self.beta <- myself.beta ;
		}
		//Creation of the representation of the infected agents
		create I_agt {
			Isize <- float(number_I) ;
			self.beta <- myself.beta ;
			self.delta <- myself.delta ;
		}
		//Creation of the representation of the recovered agents
		create R_agt {
			Rsize <- float(number_R) ;
			self.delta <- myself.delta ;
		}
		//Creation of the representation of the SIR agent representing the non split system
		create SIR_agt {
			self.Sm <- float(number_S) ;
			self.Im <- float(number_I) ;
			self.Rm <- float(number_R) ;
			
			self.beta <- myself.beta ;
			self.delta <- myself.delta ;
		}
	}
}

//Species which represent the susceptible agents compartiment
species S_agt {
	float t ;		
	float Ssize ; //number of susceptible
	
	float beta ;
	//Equation that will be solved simultaneously with the two other equations systems
	equation evol simultaneously: [  ( I_agt ) ,  ( R_agt ) ] {
		diff ( first ( S_agt ) . Ssize , t ) = 
			( - beta * first ( S_agt ) . Ssize * first (	I_agt ) . Isize / N ) ;
	}
	
	reflex solving {solve evol method: "rk4" step_size: 0.01 ;}
}
//Species which represent the infected agents compartiment
species I_agt {
	float t ;
	float Isize ; // number of infected
	
	float beta ;
	float delta ;

	//Equation that will be solved simultaneously with the two other equations systems
	equation evol simultaneously: [  ( S_agt ) ,  ( R_agt ) ] {
		diff ( first ( I_agt ) . Isize , t ) = 
			( beta * first ( S_agt ) . Ssize * first ( I_agt ) . Isize / N ) 
			- ( delta * first ( I_agt ) . Isize ) ;
	}
}
//Species which represent the resistant agents compartiment
species R_agt {
	float t ;		
	float Rsize ; //number of resistant
	
	float delta ;

	//Equation that will be solved simultaneously with the two other equations systems
	equation evol simultaneously: [ ( S_agt ) + ( I_agt ) ] {
		diff ( first ( R_agt ) . Rsize , t ) = 
			( delta * first ( I_agt ) . Isize ) ;
	}
}

//Species which represent the ordinary differential equations system
species SIR_agt {
	float t ;
	float Im ;
	float Sm ;
	float Rm ;
	
	float beta ;
	float delta ;
	
	equation SIR {
		diff ( Sm , t ) = ( - beta * Sm * Im / N ) ; 
		diff ( Im , t ) = ( beta * Sm	* Im / N ) - ( delta * Im ) ; 
		diff ( Rm , t ) = ( delta * Im ) ;
	}
	
	reflex solving {solve SIR method: "rk4" step_size: 0.01 ;}
}


experiment Simulation type: gui {
	parameter 'Number of Susceptible' type: int var: number_S <- 495 category: "Initial population"; // The initial number of susceptibles
	parameter 'Number of Infected'    type: int var: number_I <- 5   category: "Initial population";
	parameter 'Number of Removed'     type: int var: number_R <- 0   category: "Initial population";

	parameter 'Beta (S->I)'  type: float var: beta <- 1.0   category: "Parameters";
	parameter 'Delta (I->R)' type: float var: delta <- 0.01 category: "Parameters";
	
	output {
		layout #split;
		display "split system"  type: 2d {
			chart 'Susceptible' type: series background: #white {
				data 'susceptible' value: first ( S_agt ) . Ssize color: rgb(46,204,113) ;
				data 'infected' value: first ( I_agt ) . Isize color: rgb(231,76,60) ;
				data 'removed' value: first ( R_agt ) . Rsize color: rgb(52,152,219) ;
			}
		}
		display "unified system"  type: 2d {
			chart 'Susceptible' type: series background: #white {
				data 'susceptible_maths' value: first( SIR_agt ).Sm color: rgb(46,204,113) ;
				data 'infected_maths' value: first( SIR_agt ).Im color: rgb(231,76,60) ;
				data 'removed_maths' value: first( SIR_agt ).Rm color: rgb(52,152,219) ;
			}
		}
	}
}
