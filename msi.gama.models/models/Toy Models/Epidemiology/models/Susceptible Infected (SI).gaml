/**
* Name: SI without ODE
* Author: 
* Description: A simple SI model without Ordinary Differential Equations showing agents 
* 	moving randomly among a grid and becoming infected
* Tags: grid
*/

model si

global { 
	
    int number_S <- 495;  // The number of susceptible
    int number_I <- 5 ;	// The number of infected
    float survivalProbability <- 1/(70*365) ; // The survival probability
	float beta <- 0.05 ; 	// The parameter Beta
	float nu <- 0.001 ;	// The parameter Nu
	int numberHosts <- number_S+number_I; //Total number of hosts
	bool local_infection <- true ; //Infection spread locally or not
	int neighbours_size <- 2 ; //Size of the neighbourhood
	geometry shape <- square(50);
	init { 
		//Creation of all the susceptible hosts
		create Host number: number_S {
        	is_susceptible <- true;
        	is_infected <-  false;
            is_immune <-  false; 
            color <-  #green;
        }
        //Creation of all the infected hosts
        create Host number: number_I {
            is_susceptible <-  false; 
            is_infected <-  true;
            is_immune <-  false; 
            color <-  #red;  
       }
   }
   //Reflex to update the number of infected hosts
   reflex compute_nb_infected {
   		number_I <- Host count (each.is_infected);
   }  
}

//Grid to discretize space
grid si_grid width: 50 height: 50 use_individual_shapes: false use_regular_agents: false frequency: 0{
	rgb color <- #black;
	list<si_grid> neighbours <- (self neighbors_at neighbours_size) ;       
}
//Species host which represent the possible hosts of a disease
species Host  {
	//Booleans to represent the state of the agent
	bool is_susceptible <- true;
	bool is_infected <- false;
    bool is_immune <- false;
    rgb color <- #green;
    int sic_count <- 0;
    si_grid myPlace;
    
    //The agent is placed randomly among the grid
    init {
    	myPlace <- one_of (si_grid as list);
    	location <- myPlace.location;
    }        
    //Reflex to move the agents in its neighbourhood
    reflex basic_move {
    	myPlace <- one_of (myPlace.neighbours) ;
        location <- myPlace.location;
    }
    //Reflex to infect the agent if it is susceptible and according to the other infected agents
    reflex become_infected when: is_susceptible {
    	float rate <- 0.0;
    	if(local_infection) {
    		int nb_hosts <- 0;
    		int nb_hosts_infected <- 0;
    		loop hst over: ((myPlace.neighbours + myPlace) accumulate (Host overlapping each)) {
    			nb_hosts <- nb_hosts + 1;
    			if (hst.is_infected) {
    				nb_hosts_infected <- nb_hosts_infected + 1;
    			}
    		}
    		rate <- nb_hosts_infected / nb_hosts;
    	} else {
    		rate <- number_I / numberHosts;
    	}
    	if (flip(beta * rate)) {
        	is_susceptible <-  false;
            is_infected <-  true;
            is_immune <-  false;
            color <-  #red;    
        }
    }
    //Reflex to kill the agent according to the death rate
    reflex shallDie when: flip(nu) {
		create species(self) {
			myPlace <- myself.myPlace ;
			location <- myself.location ; 
		}
       	do die;
    }
            
    aspect basic {
        draw circle(1) color: color; 
    }
}


experiment Simulation type: gui { 
 	parameter "Number of Susceptible" var: number_S ;// The number of susceptible
    parameter "Number of Infected" var: number_I ;	// The number of infected
    parameter "Survival Probability" var: survivalProbability ; // The survival probability
	parameter "Beta (S->I)" var:beta; 	// The parameter Beta
	parameter "Mortality" var:nu ;	// The parameter Nu
	parameter "Is the infection is computed locally?" var:local_infection ;
	parameter "Size of the neighbours" var:neighbours_size ;
	
 	output {
 		layout #split; 
	    display si_display {
	        grid si_grid lines: #black;
	        species Host aspect: basic;
	    }
	        
	    display chart refresh: every(10#cycles) {
			chart "Susceptible" type: series background: #lightgray style: exploded {
				data "susceptible" value: Host count (each.is_susceptible) color: #green;
				data "infected" value: Host count (each.is_infected) color: #red;
			}
		}
			
	}
}
