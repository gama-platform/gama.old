/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */

model si

global { 
    int number_S <- 495 parameter: 'Number of Susceptible';  // The number of susceptible
    int number_I <- 5 parameter: 'Number of Infected';	// The number of infected
    int number_R <- 0 parameter: 'Number of Removed';	// The number of removed
    float survivalProbability <- 1/(70*365) parameter: 'Survival Probability'; // The survival probability
	float beta <- 0.05 parameter: 'Beta (S->I)'; 	// The parameter Beta
	float nu <- 0.001 parameter: 'Mortality';	// The parameter Nu
	float delta <- 0.01 parameter: 'Delta (I->R)'; // The parameter Delta
	int numberHosts <- number_S+number_I+number_R;
	bool local_infection <- true parameter: 'Is the infection is computed locally?';
	int neighbours_size <- 2 min:1 max: 5 parameter:'Size of the neighbours';
	int nb_infected <- number_I;
	float R0 ;
	
	init {
		create Host number: number_S {
        	set is_susceptible <- true;
        	set is_infected <-  false;
            set is_immune <-  false; 
            set color <-  rgb('green');
        }
        create Host number: number_I {
            set is_susceptible <-  false;
            set is_infected <-  true;
            set is_immune <-  false; 
            set color <-  rgb('red'); 
       }
       set R0 <- beta/(delta+nu);
	do write message: 'Basic Reproduction Number: '+ string(R0);
   }
   reflex compute_nb_infected {
   		set nb_infected <- (Host as list) count (each.is_infected);
   }       
}

environment width: 50 height: 50 {
	grid sir_grid width: 50 height: 50 {
		rgb color <- rgb('black');
		list neighbours of: sir_grid <- (self neighbours_at neighbours_size) of_species sir_grid;       
    }
  }

entities {
	species Host  {
		bool is_susceptible <- true;
		bool is_infected <- false;
        bool is_immune <- false;
        rgb color <- rgb('green');
        int sic_count <- 0;
        sir_grid myPlace;
        
        init {
        	set myPlace <- one_of (sir_grid as list);
        	set location <- myPlace.location;
        }        
        reflex basic_move {
        	set myPlace <- one_of (myPlace.neighbours) ;
            set location <- myPlace.location;
        }
        
        reflex become_infected when: is_susceptible {
        	let rate type: float <- 0;
        	if(local_infection) {
        		let nb_hosts type: int <- 0;
        		let nb_hosts_infected type: int <- 0;
        		loop hst over: ((myPlace.neighbours + myPlace) accumulate (each.agents)) of_species Host{
        			set nb_hosts <- nb_hosts + 1;
        			if (Host(hst).is_infected) {set nb_hosts_infected <- nb_hosts_infected + 1;}
        		}
        		set rate <- nb_hosts_infected / nb_hosts;
        	} else {
        		set rate <- nb_infected / numberHosts;
        	}
        	if (flip(beta * rate)) {
	        	set is_susceptible <-  false;
	            set is_infected <-  true;
	            set is_immune <-  false;
	            set color <-  rgb('red');    
	        }
        }
        
        reflex become_immune when: (is_infected and flip(delta)) {
        	set is_susceptible value: false;
        	set is_infected value: false;
            set is_immune value: true;
            set color value: rgb('yellow');
        }
        
        reflex shallDie when: flip(nu) {
			create species(self) number: 1 {
				set myPlace <- myself.myPlace ;
				set location <- myself.location ; 
			}
           	do die;
        }
                
        aspect basic {
	        draw circle(1) color: color; 
        }
    }
}

experiment simulation type: gui { 
 	output { 
	    display sir_display {
	        grid sir_grid lines: rgb("black");
	        species Host aspect: basic;
	    }
	        
	    display chart refresh_every: 10 {
			chart 'Susceptible' type: series background: rgb('lightGray') style: exploded {
				data susceptible value: (Host as list) count (each.is_susceptible) color: rgb('green');
				data infected value: (Host as list) count (each.is_infected) color: rgb('red');
				data immune value: (Host as list) count (each.is_immune) color: rgb('yellow');
			}
		}
			
	}
}
