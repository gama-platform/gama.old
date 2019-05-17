/**
 *  continuous curves
 *  Author: tri and nghi
 *  Description: 
 */
model ContinuousCurves


global {
	float step <- 1 # s;
	
	init {
		create my_SEIR_maths;
	}
}

species my_SEIR_maths { 
	float ttt;
	float Sm <- 0.0658;
	float Em <- 0.007;
	float Im <- 0.002;
	float mu <- 0.02;
	float alpha <- 35.842;
	float gamma <- 100.0;
	float beta0 <- 1884.95;
	float beta1 <- 0.255;
	
	equation SEIR {
		diff(Sm, ttt) = (mu - beta0 * (1 + beta1 * cos(2 * 3.14 * ttt)) * self.Sm * self.Im - mu * self.Sm);
		diff(Em, ttt) = (beta0 * (1 + beta1 * cos(2 * 3.14 * ttt)) * self.Sm * self.Im - (mu + alpha) * self.Em);
		diff(Im, ttt) = (alpha * self.Em - (mu + gamma) * self.Im);
	}

	reflex solving {
		solve SEIR method: #rk4 step_size: 0.01;
	}
}

experiment mysimulation type: gui {
	output {
		display chartcontinuous {
			chart 'chartcontinuous' type: series background: rgb('lightGray') 
				x_serie: (my_SEIR_maths[0]).ttt[]
				size: { 1.0, 0.5 } position: { 0.0, 0.0 }
			{
				data "s_mathsc" value: (my_SEIR_maths[0]).Sm[] color: # red marker: false;
				data "e_mathsc" value: (my_SEIR_maths[0]).Em[] color: # yellow marker: false;
				data "i_mathsc" value: (my_SEIR_maths[0]).Im[] color: # blue marker: false;
			}

			chart 'chartdiscret' type: series background: rgb('white') size: { 1.0, 0.5 } position: { 0.0, 0.5 } {
				data "s_mathsd" value: first(my_SEIR_maths).Sm color: rgb('red') marker: false;
				data "e_mathsd" value: first(my_SEIR_maths).Em color: rgb('yellow') marker: false;
				data "i_mathsd" value: first(my_SEIR_maths).Im color: rgb('blue') marker: false;
			}
		}
	}
}
