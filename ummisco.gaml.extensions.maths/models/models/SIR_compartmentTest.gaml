/**
 *  SIRcompartmentV2
 *  Author: tri and nghi
 *  Description: 
 */

model SIRcompartmentV2

global {
	int number_S <- 495 parameter : 'Number of Susceptible' ; // The number of susceptible
	int number_I <- 5 parameter : 'Number of Infected' ; // The number of infected
	int number_R <- 0 parameter : 'Number of Removed' ; // The number of removed 
	int N <- number_S + number_I + number_R ;
	float beta <- 1.0 parameter : 'Beta (S->I)' ; // The parameter Beta
	float delta <- 0.01 parameter : 'Delta (I->R)' ; // The parameter Delta
	float hKR4 <- 0.01 ;
	int iInit <- 1 ;
	
	float R0 ;
	
	init {
		create SIRAgentTOTO {
//			self.number_S <- number_S;
//			self.number_I <- number_I;
//			self.number_R <- number_R;		
//			self.beta <- beta;
//			self.delta <- delta;				
		}
		//        
		create my_SIR_maths number:1{
			self . Sm <- number_S ;
			self . Im <- number_I ;
			self . Rm <- number_R ;
		}
		set R0 <- beta * number_S / ( delta ) ;
		do write message : 'Basic Reproduction Number: ' + string ( R0 ) ;
	}
}
entities {
	
	species SIRAgentTOTO {
//		int number_S  ; // The number of susceptible
//		int number_I  ; // The number of infected
//		int number_R  ; // The number of removed 
//		int N <- number_S + number_I + number_R ;
//		
//		float beta  ; // The parameter Beta
//		float delta ; // The parameter Delta		
		
		init {
//			create S {set Ssize <- number_S ;}
//			create I {set Isize <- number_I ;}
//			create R {set Rsize <- number_R;}			
		}
		
		species S {
//			float Ssize ;
////			float t ;
////			equation evol simultaneously : [ first ( I ) , first ( R ) ] {
////				diff ( first ( S ) . Ssize , t ) = ( - beta * first ( S ) . Ssize * first (
////				I ) . Isize / N ) ;
////			}
////			solve evol method : "rk4" step : 0.01;
		}
//		species I {
//			float Isize ; // number of infected
////			float t ;
////			equation evol simultaneously : [ first ( S ) , first ( R ) ] {
////				diff ( first ( I ) . Isize , t ) = 
////				       ( beta * first ( S ) . Ssize * first ( I ) . Isize / N ) - ( delta * first ( I ) . Isize ) ;
////			}
//		}
//		species R {
//			float Rsize ;
////			float t ;
////			equation evol simultaneously : [ first ( S ) , first ( I ) ] {
////				diff ( first ( R ) . Rsize , t ) = ( delta * first ( I ) . Isize ) ;
////			}
//		}
	}

	species my_SIR_maths {
		float t ;
		float Im ;
		float Sm ;
		float Rm ;
		equation SIR {
			diff ( Sm , t ) = ( - beta * Sm * Im / N ) ; 
			diff ( Im , t ) = ( beta * Sm * Im / N ) - ( delta * Im ) ; 
			diff ( Rm , t ) = ( delta * Im ) ;
		}
		solve SIR method : "rk4" step : 0.01 ;
	}
}

experiment Simulation type : gui {
	output {
//		display chart1 refresh_every : 1 {
//			chart 'Susceptible' type : series background : rgb ( 'lightGray' ) {
////				data susceptible value : first ( SIRAgent ) . Ssize color : rgb ( 'green' ) ;
////				data infected value : first ( SIRAgent ) . Isize color : rgb ( 'red' ) ;
////				data recovered value : first ( SIRAgent ) . Rsize color : rgb ( 'blue' ) ;				
//			}
//		}
		display chart2 refresh_every : 1 {
			chart 'Susceptible' type : series background : rgb ( 'lightGray' ) {
				data 'susceptible_maths' value : first ( my_SIR_maths ) . Sm color : rgb ('green' ) ;
				data 'infected_maths' value : first ( my_SIR_maths ) . Im color : rgb ( 'red' ) ;
				data 'recovered_maths' value : first ( my_SIR_maths ) . Rm color : rgb ( 'blue' ) ;				
			}
		}
	}
}


