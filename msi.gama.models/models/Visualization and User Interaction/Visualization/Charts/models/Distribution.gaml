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
	map<string, list> weibull_trunc_distrib;
	map<string, list> lognormal_trunc_distrib;
	map<string, list> gamma_trunc_distrib;
	map<string, list> gauss_trunc_distrib;	
	map<string, list> exp_distrib;
	
	list<float> gauss_test;
	list<float> gamma_test;
	list<float> lognormal_test;
	list<float> weibull_test;
	list<float> weibull_trunc_test;
	list<float> lognormal_trunc_test;
	list<float> gamma_trunc_test;
	list<float> gauss_trunc_test;
	list<float> exp_test;
	
	init {
		gauss_test <- [1, 2, 4, 1, 2, 5, 10.0];
		gauss_distrib <- distribution_of(gauss_test, 5);
		
		gamma_test <- [1, 2, 4, 1, 2, 5, 10.0];
		gamma_distrib <- distribution_of(gamma_test, 5);
		
		lognormal_test <- [1, 2, 4, 1, 2, 5, 10.0];
		lognormal_distrib <- distribution_of(lognormal_test, 5);
		
		weibull_test <- [1, 2, 4, 1, 2, 5, 10.0];		
		weibull_distrib <- distribution_of(weibull_test, 5);

		gauss_trunc_test <- [1, 2, 4, 1, 2, 5, 10.0];
		gauss_trunc_distrib <- distribution_of(gauss_trunc_test, 5);
		
		gamma_trunc_test <- [1, 2, 4, 1, 2, 5, 10.0];
		gamma_trunc_distrib <- distribution_of(gamma_trunc_test, 5);
		
		lognormal_trunc_test <- [1, 2, 4, 1, 2, 5, 10.0];
		lognormal_trunc_distrib <- distribution_of(lognormal_trunc_test, 5);
		
		weibull_trunc_test <- [1, 2, 4, 1, 2, 5, 10.0];		
		weibull_trunc_distrib <- distribution_of(weibull_trunc_test, 5);	
		
		exp_test <- [1, 2, 4, 1, 2, 5, 10.0];
		exp_distrib <- distribution_of(exp_test, 5);
			
	}

	reflex update_distrib {
		add gauss_rnd(10,10) to: gauss_test; 
		gauss_distrib <- distribution_of(gauss_test, 15);
		
		add gamma_rnd(10,0.5) to: gamma_test; 
		gamma_distrib <- distribution_of(gamma_test, 15);
		
		add lognormal_rnd(10,0.5) to: lognormal_test; 
		lognormal_distrib <- distribution_of(lognormal_test, 15);
		
		add weibull_rnd(10,10) to: weibull_test; 
		weibull_distrib <- distribution_of(weibull_test, 15);	
		
		
		add truncated_gauss(10,10) to: gauss_trunc_test; 
		gauss_trunc_distrib <- distribution_of(gauss_trunc_test, 15);
		
		add gamma_trunc_rnd(10,0.5,6,true) to: gamma_trunc_test; 
		gamma_trunc_distrib <- distribution_of(gamma_trunc_test, 15);
		
		add lognormal_trunc_rnd(10,0.5,17000,false) to: lognormal_trunc_test; 
		lognormal_trunc_distrib <- distribution_of(lognormal_trunc_test, 15);
		
		add weibull_trunc_rnd(10,10,7,12) to: weibull_trunc_test; 
		weibull_trunc_distrib <- distribution_of(weibull_trunc_test, 15);		
		
		add exp_rnd(10) to: exp_test; 
		exp_distrib <- distribution_of(exp_test, 15);
							
	}
}

experiment "Example of Distribution" type: gui {
	output {
		layout #split;
		display "Gauss Distribution"  type: 2d {
			chart "Gauss Distribution" type: histogram {
				datalist (gauss_distrib at "legend") value: (gauss_distrib at "values");
			}
		}
		display "Gamma Distribution"  type: 2d {
			chart "Gamma Distribution" type: histogram {
				datalist (gamma_distrib at "legend") value: (gamma_distrib at "values");
			}
		}
		display "LogNormal Distribution"  type: 2d {
			chart "LogNormal Distribution" type: histogram {
				datalist (lognormal_distrib at "legend") value: (lognormal_distrib at "values");
			}
		}
		display "Weibull Distribution"  type: 2d {
			chart "Weibull Distribution" type: histogram {
				datalist (weibull_distrib at "legend") value: (weibull_distrib at "values");
			}
		}	
		
		
		display "Truncated Gauss Distribution"  type: 2d {
			chart "Truncated Gauss Distribution truncated_gauss(10,10)" type: histogram {
				datalist (gauss_trunc_distrib at "legend") value: (gauss_trunc_distrib at "values");
			}
		}
		display "Truncated Gamma Distribution"  type: 2d {
			chart "Truncated Gamma Distribution gamma_trunc_rnd(10,0.5,6,true)" type: histogram {
				datalist (gamma_trunc_distrib at "legend") value: (gamma_trunc_distrib at "values");
			}
		}
		display "Truncated LogNormal Distribution"  type: 2d {
			chart "Truncated LogNormal Distribution lognormal_trunc_rnd(10,0.5,17000,false)" type: histogram {
				datalist (lognormal_trunc_distrib at "legend") value: (lognormal_trunc_distrib at "values");
			}
		}
		display "Truncated Weibull Distribution"  type: 2d {
			chart "Truncated Weibull Distribution weibull_trunc_rnd(10,10,7,12)" type: histogram {
				datalist (weibull_trunc_distrib at "legend") value: (weibull_trunc_distrib at "values");
			}
		}	
		display "Exponential Distribution"  type: 2d {
			chart "Exponential Distribution exp_rnd(10)" type: histogram {
				datalist (exp_distrib at "legend") value: (exp_distrib at "values");
			}
		}								
		
									
	}
}
	
