/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */
model si
global {
	int number_S <- 495 parameter : 'Number of Susceptible' ; // The number of susceptible
	int number_I <- 5 parameter : 'Number of Infected' ; // The number of infected
	int number_R <- 0 parameter : 'Number of Removed' ; // The number of removed 
	int N <- number_S + number_I + number_R ;
	float beta <- 1.0 parameter : 'Beta (S->I)' ; // The parameter Beta
	float delta <- 0.01 parameter : 'Delta (I->R)' ; // The parameter Delta
	float R0 ;
	float hKR4 <- 0.07 ;
	int iInit <- 1 ;
	init {
		create S {
			set Ssize <- number_S ;
			set self . beta <- myself . beta ;
		}
		create I {
			set Isize <- number_I ;
			set self . beta <- myself . beta ;
			set self . delta <- myself . delta ;
		}
		create R {
			set Rsize <- number_R ;
			set self . delta <- myself . delta ;
		}
		//        
		create my_SIR_maths {
			self . Sm <- number_S ;
			self . Im <- number_I ;
			self . Rm <- number_R ;
		}
		set R0 <- beta * number_S / ( delta ) ;
		do write message : 'Basic Reproduction Number: ' + string ( R0 ) ;
	}
}
entities {
	species S {
		float Ssize ;
		float beta ;
		float t ;
		equation evol simultaneously : [ first ( I ) , first ( R ) ] {
			diff ( first ( S ) . Ssize , t ) = ( - beta * first ( S ) . Ssize * first (	I ) . Isize / N ) ;
		}
		solve evol method : "rk4" step : 0.01 { }
	}
	species I {
		float Isize ; // number of infected
		float beta ;
		float delta ;
		float t ;
		equation evol simultaneously : [ first ( S ) , first ( R ) ] {
			diff ( first ( I ) . Isize , t ) = ( beta * first ( S ) . Ssize * first ( I
			) . Isize / N ) - ( delta * first ( I ) . Isize ) ;
		}
//				solve evol method : "rk4" step : 0.01 {		}
	}
	species R {
		float Rsize ;
		float delta ;
		float t ;
		equation evol simultaneously : [ first ( S ) , first ( I ) ] {
			diff ( first ( R ) . Rsize , t ) = ( delta * first ( I ) . Isize ) ;
		}
//				solve evol method : "rk4" step : 0.01 {	}
	}
	species my_SIR_maths {
		float t ;
		float Im ;
		float Sm ;
		float Rm ;
		equation SIR {
			diff ( Sm , t ) = ( - beta * Sm * Im / N ) ; 
			diff ( Im , t ) = ( beta * Sm	* Im / N ) - ( delta * Im ) ; 
			diff ( Rm , t ) = ( delta * Im ) ;
		}
		solve SIR method : "rk4" step : 0.01 {
		//			float cycle_length <- 1 ;
		//			float t0 <- cycle - 1 ;
		//			float tf <- cycle ;
		}
	}
}
experiment Simulation type : gui {
	output {
		display chart1 refresh_every : 1 {
			chart 'Susceptible' type : series background : rgb ( 'lightGray' ) {
				data 'susceptible' value : first ( S ) . Ssize color : rgb ( 'green' ) ;
				data 'infected' value : first ( I ) . Isize color : rgb ( 'red' ) ;
//				data susceptible_maths value : first ( my_SIR_maths ) . Sm color : rgb ('blue' ) ;
//				data infected_maths value : first ( my_SIR_maths ) . Im color : rgb ( 'orange' ) ;
			}
		}
		display chart2 refresh_every : 1 {
			chart 'Susceptible' type : series background : rgb ( 'lightGray' ) {
//				data susceptible value : first ( S ) . Ssize color : rgb ( 'green' ) ;
//				data infected value : first ( I ) . Isize color : rgb ( 'red' ) ;
				data 'susceptible_maths' value : first ( my_SIR_maths ) . Sm color : rgb ('blue' ) ;
				data 'infected_maths' value : first ( my_SIR_maths ) . Im color : rgb ( 'orange' ) ;
			}
		}
	}
}
