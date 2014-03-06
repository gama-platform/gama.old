/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */

model si

global { 
    int number_S <- 495;
    int number_I <- 5 ;
    int number_R <- 0 ;
    float survivalProbability <- 1/(70*365) ;
	float beta <- 0.05 ; 	// The parameter Beta
	float nu <- 0.001 ;
	float delta <- 0.01 parameter: "Delta (I->R)"; // The parameter Delta
	int numberHosts <- number_S+number_I+number_R;
	bool local_infection <- true parameter: "Is the infection is computed locally?";
	int neighbours_size <- 2 min:1 max: 5 parameter:"Size of the neighbours";
	int nb_infected <- number_I;
	float R0 ;
	geometry shape <- square(50);
	
	init {
		create Host number: number_S {
        	is_susceptible <- true;
        	is_infected <-  false;
            is_immune <-  false; 
            color <-  rgb("green");
        }
        create Host number: number_I {
            is_susceptible <-  false;
            is_infected <-  true;
            is_immune <-  false; 
            color <-  rgb("red"); 
       }
       R0 <- beta/(delta+nu);
		write "Basic Reproduction Number: "+ R0;
   }
   reflex compute_nb_infected {
   		nb_infected <- Host count (each.is_infected);
   }       
}


entities {
	grid sir_grid width: 50 height: 50 use_individual_shapes: false use_regular_agents: false frequency: 0{
		rgb color <- rgb("black");
		list<sir_grid> neighbours <- (self neighbours_at neighbours_size) ;       
    }
    species Host  {
		bool is_susceptible <- true;
		bool is_infected <- false;
        bool is_immune <- false;
        rgb color <- rgb("green");
        int sic_count <- 0;
        sir_grid myPlace;
        
        init {
        	myPlace <- one_of (sir_grid as list);
        	location <- myPlace.location;
        }        
        reflex basic_move {
        	myPlace <- one_of (myPlace.neighbours) ;
            location <- myPlace.location;
        }
        
        reflex become_infected when: is_susceptible {
        	float rate  <- 0.0;
        	if(local_infection) {
        		int nb_hosts  <- 0;
        		int nb_hosts_infected  <- 0;
        		loop hst over: ((myPlace.neighbours + myPlace) accumulate (Host overlapping each)) {
        			nb_hosts <- nb_hosts + 1;
        			if (hst.is_infected) {nb_hosts_infected <- nb_hosts_infected + 1;}
        		}
        		rate <- nb_hosts_infected / nb_hosts;
        	} else {
        		rate <- nb_infected / numberHosts;
        	}
        	if (flip(beta * rate)) {
	        	is_susceptible <-  false;
	            is_infected <-  true;
	            is_immune <-  false;
	            color <-  rgb("red");    
	        }
        }
        
        reflex become_immune when: (is_infected and flip(delta)) {
        	is_susceptible <- false;
        	is_infected <- false;
            is_immune <- true;
            color <- rgb("blue");
        }
        
        reflex shallDie when: flip(nu) {
			create species(self)  {
				myPlace <- myself.myPlace ;
				location <- myself.location ; 
			}
           	do die;
        }
                
        aspect basic {
	        draw circle(1) color: color; 
        }
    }
}

experiment Simulation type: gui { 
 	parameter "Number of Susceptible" var: number_S ;// The number of susceptible
    parameter "Number of Infected" var: number_I ;	// The number of infected
    parameter "Number of Removed" var:number_R ;	// The number of removed
 	parameter "Survival Probability" var: survivalProbability ; // The survival probability
	parameter "Beta (S->I)" var:beta; 	// The parameter Beta
	parameter "Mortality" var:nu ;	// The parameter Nu
	parameter "Delta (I->R)" var: delta; // The parameter Delta
	parameter "Is the infection is computed locally?" var:local_infection ;
	parameter "Size of the neighbours" var:neighbours_size ;
 	output { 
	    display sir_display {
	        grid sir_grid lines: rgb("black");
	        species Host aspect: basic;
	    }
	        
	    display chart refresh_every: 10 {
			chart "Susceptible" type: series background: rgb("lightGray") style: exploded {
				data "susceptible" value: Host count (each.is_susceptible) color: rgb("green");
				data "infected" value: Host count (each.is_infected) color: rgb("red");
				data "immune" value: Host count (each.is_immune) color: rgb("blue");
			}
		}
			
	}
}
