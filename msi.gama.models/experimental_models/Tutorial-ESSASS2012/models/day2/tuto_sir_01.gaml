model tuto_sir_01

global {
	float alpha <- 0.2;
	float beta <- 0.8;
	int nb_agents <- 1000;
	int nb_infected <- 10;
		
	init {
		create people number: (nb_agents - nb_infected) {
			set state <- "S";
		}
		create people number: nb_infected {
			set state <- "I";
		}
		
	}
}

environment {}

entities {
	species people {
		string state;   // Can be "S", "I" or "R"		
				
		reflex epidemic when: (state != "R"){
			if(state = "I" and flip(alpha)) {set state <- "R";}
			if(state = "S" and 
				flip(beta*length(list(people) where (each.state = "I"))/nb_agents)) {
				set state <- "I";
			}  	
		}
		
		aspect display {
			draw circle(2) 
				color: (state = "S" ) ? rgb('green') : 
							((state = "I") ? rgb('red') : rgb('blue'));
		}
	}
}

experiment tuto_sir_01 type: gui {
	parameter 'alpha:' var: alpha category: 'Epidemic';
	parameter 'beta:' var: beta category: 'Epidemic';
	parameter 'Number of agents' var: nb_agents category: 'Population';
	parameter 'Number of infected' var: nb_infected category: 'Population';
	
	output {
		display Charts {
			chart name: 'Global happiness and similarity' type: series background: rgb('lightGray') {
				data 's_serie' color: rgb('green') value: length(list(people) where (each.state = "S"));
				data 'i_serie'color: rgb('red') value: length(list(people) where (each.state = "I"));
				data 'r_serie' color: rgb('blue') value: length(list(people) where (each.state = "R"));
			}			
		}
		
		display people {
			species people aspect: display;
		}
	}
}
