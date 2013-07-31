/**
 *  SIRcompartmentV2
 *  Author: tri and nghi
 *  Description: 
 */
model SIRcompartmentV2

global {
	int strain_number <- 2;
	int number_S <- 495 parameter : 'Number of Susceptible'; // The number of susceptible
	int number_I <- 5 parameter : 'Number of Infected'; // The number of infected
	int number_R <- 0 parameter : 'Number of Removed'; // The number of removed 
	int N <- number_S + number_I * strain_number + number_R;
	float _beta <- 1.0 parameter : 'Beta (S->I)'; // The parameter Beta
	float _delta <- 0.01 parameter : 'Delta (I->R)'; // The parameter Delta
	float hKR4 <- 0.01;
	float R0;
	init {
		create S {
			Ssize <- number_S;
		}

		create I number : strain_number {
			Isize <- number_I;
			set self.beta <- _beta; // + rnd ( 100 ) / 200 ;
			set self.delta <- _delta; // + rnd ( 100 ) / 1000 ;

		}

		create R {
			Rsize <- number_R;
		}

		create my_SIR_maths {
			self.Sm <- number_S;
			self.Im <- number_I;
			self.Rm <- number_R;
		}

		R0 <- _beta * number_S / (_delta);
		write 'Basic Reproduction Number: ' + string(R0);
	}

}

entities {
	species S {
		float Ssize;
		float t;
		equation evol simultaneously : [I, R] {
			diff(self.Ssize, t) = (-sum(I accumulate [each.beta * each.Isize]) * self.Ssize / N);
		}

		solve evol method : "rk4" step : hKR4 {
		}

	}

	species I {
		float Isize; // number of infected
		float t;
		float beta;
		float delta;
		equation evol simultaneously : [S, R] {
			diff(self.Isize, t) = (beta * first(S).Ssize * self.Isize / N) - (delta * self.Isize);
		}

	}

	species R {
		float Rsize;
		float t;
		equation evol simultaneously : [I] {
			diff(self.Rsize, t) = (sum(I collect (each.delta * each.Isize)));
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

		solve SIR method : "rk4" step : hKR4;
	}

}

experiment Simulation type : gui {
	output {
		display chart_3system_eq refresh_every : 1 {
			chart 'Susceptible' type : series background : rgb('lightGray') {
				data 'susceptible' value : first(S).Ssize color : rgb('green');
				//				data infected value : sum(I accumulate (each.beta * each. Isize)) color : rgb ( 'red' ) ;
				data 'infected0' value : first(I).beta * first(I).Isize color : rgb('red');
				data 'infected1' value : last(I).beta * last(I).Isize color : rgb('yellow');
				data 'recovered' value : first(R).Rsize color : rgb('blue');
			}

		}

		display chart_1system_eq refresh_every : 1 {
			chart 'Susceptible' type : series background : rgb('lightGray') {
				data 'susceptible_maths' value : first(my_SIR_maths).Sm color : rgb('green');
				data 'infected_maths' value : first(my_SIR_maths).Im color : rgb('red');
				data 'recovered_maths' value : first(my_SIR_maths).Rm color : rgb('blue');
			}

		}

	}

}


