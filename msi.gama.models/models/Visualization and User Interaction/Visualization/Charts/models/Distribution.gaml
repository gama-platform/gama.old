/**
* Name: Distribution Examples
* Author: Philippe Caillou
* Description: A demonstration of charts using the distribution of values in a list
* Tags: gui, chart
*/
model distribution


global {
	map<string, list> gauss_distrib;
	map<string, list> gamma_distrib;
	map<string, list> lognormal_distrib;
	map<string, list> weibull_distrib;
	
	list<float> gauss_test;
	list<float> gamma_test;
	list<float> lognormal_test;
	list<float> weibull_test;
	
	init {
		gauss_test <- [1, 2, 4, 1, 2, 5, 10.0];
		gauss_distrib <- distribution_of(gauss_test, 5);
		
		gamma_test <- [1, 2, 4, 1, 2, 5, 10.0];
		gamma_distrib <- distribution_of(gamma_test, 5);
		
		lognormal_test <- [1, 2, 4, 1, 2, 5, 10.0];
		lognormal_distrib <- distribution_of(lognormal_test, 5);
		
		weibull_test <- [1, 2, 4, 1, 2, 5, 10.0];		
		weibull_distrib <- distribution_of(weibull_test, 5);
	}

	reflex update_distrib {
		add gauss_rnd(10,10) to: gauss_test; 
		gauss_distrib <- distribution_of(gauss_test, 10);
		
		add gamma_rnd(10,0.5) to: gamma_test; 
		gamma_distrib <- distribution_of(gamma_test, 10);
		
		add lognormal_rnd(10,0.5) to: lognormal_test; 
		lognormal_distrib <- distribution_of(lognormal_test, 10);
		
		add weibull_rnd(10,10) to: weibull_test; 
		weibull_distrib <- distribution_of(weibull_test, 10);						
	}
}

experiment "Example of Distribution" type: gui {
	output {
		layout #split;
		display "Gauss Distribution" {
			chart "Gauss Distribution" type: histogram {
				datalist (gauss_distrib at "legend") value: (gauss_distrib at "values");
			}
		}
		display "Gamma Distribution" {
			chart "Gamma Distribution" type: histogram {
				datalist (gamma_distrib at "legend") value: (gamma_distrib at "values");
			}
		}
		display "LogNormal Distribution" {
			chart "LogNormal Distribution" type: histogram {
				datalist (lognormal_distrib at "legend") value: (lognormal_distrib at "values");
			}
		}
		display "Weibull Distribution" {
			chart "Weibull Distribution" type: histogram {
				datalist (weibull_distrib at "legend") value: (weibull_distrib at "values");
			}
		}						
	}
}
	
