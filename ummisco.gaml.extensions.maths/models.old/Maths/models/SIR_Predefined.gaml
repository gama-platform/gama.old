/**
 *  SIRcompartmentV2
 *  Author: tri and nghi
 *  Description: 
 */
model SIRcompartmentV2

global {
	float mu <- 0.02;
	float alpha <- 35.842;
	float gamma <- 100.0;
	float beta0 <- 1884.95;
	float beta1 <- 0.255;
	float hKR4 <- 0.01;
	init {
	
		create my_SEIR_maths {
			self.clen<-1;
			self.h <- 0.1;
		}

	}

}

entities {
	species my_SEIR_maths {
		float t;
		float a;
		int clen;
		list dI <- [];
		list dE <- [];
		list dS <- [];
		list dT <- [];



//    	float t;    
    	int N<-500;
		float Im <- 1.0; 
		float Sm <- N - Im; 
		float Rm <- 0.0; 
   		float h<-0.1;
   		float beta<-0.4;
   		float delta<-0.01; 


// must be followed with exact order S, I, R, t  and N,beta,delta
		equation eqSIR type:SIR with_vars:[Sm,Im,Rm,t] with_params:[N,beta,delta];
//		equation SIR_classic1{
//			diff(S,t) = (- beta * S * I / N);
//			diff(I,t) = (beta * S * I / N) - (delta * I);
//			diff(R,t) = (delta * I);
//		}
		solve eqSIR method:rk4 step:h;
		
//		solve SIR_classic1 method: rk4 step: h  integrated_times: dT integrated_values: [dS, dE, dI] cycle_length:clen {}
		//solve SEIR method : dp853  min_step : 0.000001 max_step: 1 scalAbsoluteTolerance:0.001 scalRelativeTolerance:0.001 {}

	}

}

experiment mysimulation type : gui {
	output {
		
		display chartdiscret refresh_every : 1 {
			chart 'chartdiscret' type : series background : rgb('lightGray') {
				data "s_mathsd" value : first(my_SEIR_maths).Sm color : rgb('yellow');
				data "i_mathsd" value : first(my_SEIR_maths).Im color : rgb('blue');
				data "r_mathsd" value : first(my_SEIR_maths).Rm color : rgb('red');
			}

		}


	}

}


