/**
 *  seir
 *  Author: 
 *  Description: A compartmental SEIR model based on Hethcote description
 */

model seir

/* Here is the model definition here */

global { 
 //       var numberHosts type: int init: 100 parameter: 'Number of Hosts';
        int number_S <- 990 parameter: 'Number of Susceptible';  // The number of susceptible
        int number_E <- 0 parameter: 'Number of Latent';	// The number of exposed
        int number_I <- 10 parameter: 'Number of Infected';	// The number of infected
        int number_R <- 0 parameter: 'Number of Removed';	// The number of removed
		float survivalProbability <- 1/(70*365) parameter: 'Survival Probability'; // The survival probability
		float beta <- 0.05 parameter: 'Beta (S->E)'; 	// The parameter Beta
		float delta <- 0.1 parameter: 'Delta (E->I)'; // The parameter Delta
		float sigma <- 1/30 parameter: 'Sigma (I->R)'; // The parameter Gamma
		float nu <- 0.001 parameter: 'Mortality';	// The parameter Nu
//		int timestep <- 100 parameter: "Time step";	// The parameter Beta
		int numberHosts <- number_S+number_E+number_I+number_R;
		float R0;

        init {
                create Host number: number_S {
                	set is_susceptible <- true;
                	set is_latent value: false;
                	set is_infected value: false;
                	set is_immune value: false; 
                	set color value: rgb('green');
                }
                create Host number: number_E {
                	set is_susceptible value: false;
                	set is_latent value: true;
                	set is_infected value: false;
                	set is_immune value: false; 
                	set color value: rgb('yellow');
                }
                create Host number: number_I {
                	set is_susceptible value: false;
                	set is_latent value: false;
                	set is_infected value: true;
                	set is_immune value: false; 
                	set color value: rgb('red');
                }
                create Host number: number_R {
                	set is_susceptible value: false;
                	set is_latent value: false;
                	set is_infected value: false;
                	set is_immune value: true; 
                	set color value: rgb('white');
                }
        }
		reflex ViewR0 {
			set R0 value: (beta*delta)/((delta+nu)*(sigma+nu));
			do action: write {
				arg message value: 'Basic Reproduction Number: '+ string(R0);
			}			
		}
 //   	reflex shouldHalt when: (time > timestep) or (empty (Host as list)) {
 //       		do action: halt;
 //   	}
        
}

environment width: 100 height: 100 {
        grid seir_grid width: 100 height: 100 {
                var color type: rgb init: rgb('black');
                list neighbours of: seir_grid <- self neighbours_at 4;
                
           }
  }

entities {
        species Host skills: [moving] {
				bool is_susceptible <- true;
//                var is_latent type: bool init: false;
                bool is_latent <- false;             
                bool is_infected <- false;
                bool is_immune <- false;
//                var color type: rgb value: rgb('green');
                rgb color value: rgb('green');
                int sic_count <- 0;
                var myPlace type: seir_grid function: {location as seir_grid};
                
//                var age type: int init: 10;
//                var myPlace type: stupid_grid value: location as stupid_grid;
                reflex basic_move {
                        let destination var: destination value: one_of (myPlace.neighbours) ; //where empty(each.agents);
                      	set location value: destination;
                        }        
                reflex become_latent when: (is_susceptible and flip(beta*(length(list(Host) where each.is_infected)/numberHosts))) {
                        set is_susceptible value: false;
                        set is_latent value: true;
                        set is_infected value: false;
                        set is_immune value: false;
                        set color value: rgb('yellow');
                }
                reflex become_infected when: (is_latent and flip(delta)) {
                        set is_susceptible value: false;
                        set is_latent value: false;
                        set is_infected value: true;
                        set is_immune value: false;
                        set color value: rgb('red');
                }
                reflex become_immune when: (is_infected and flip(sigma)) {
                        set is_susceptible value: false;
                        set is_latent value: false;
                        set is_infected value: false;
                        set is_immune value: true;
                        set color value: rgb('white');
                }
        		reflex shallDie when: flip(nu) {
						create species: species(self) number: 1 {
							set myPlace var: myPlace value: myself.myPlace ;
							set location var: location value: myself.location ;
						}
            			do action: die;
        		}
                aspect basic {
//                        draw shape: circle color: color size: 1;
						draw shape: circle;
                }
        }

}

experiment default_expr type: gui {
	output {
	    display seir_display {
	        grid seir_grid;
	        species Host aspect: basic;
	    }
	        

	    display chart refresh_every: 1 {
			chart name: 'Susceptible' type: series background: rgb('lightGray') style: exploded {
				data susceptible value: (Host as list) count (each.is_susceptible) color: rgb('green');
				data latent value: (Host as list) count (each.is_latent) color: rgb('yellow');
				data infected value: (Host as list) count (each.is_infected) color: rgb('red');
				data immune value: (Host as list) count (each.is_immune) color: rgb('white');
			}
		}
			
	}
}
