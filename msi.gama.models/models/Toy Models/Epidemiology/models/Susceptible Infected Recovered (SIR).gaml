/**
* Name: SIR without ODE
* Author: 
* Description: A simple SIR model without Ordinary Differential Equations showing agents 
* 	moving randomly among a grid and becoming infected then resistant to a disease
* Tags: grid
*/

model si

global { 
	//Number of susceptible host at init
    int number_S <- 495;
    //Number of infected host at init
    int number_I <- 5 ;
    //Number of resistant host at init
    int number_R <- 0 ;
    //Rate for the infection success 
	float beta <- 0.05 ;
	//Mortality rate for the host
	float nu <- 0.001 ;
	//Rate for resistance 
	float delta <- 0.01;
	//Number total of hosts
	int numberHosts <- number_S+number_I+number_R;
	//Boolean to represent if the infection is computed locally
	bool local_infection <- true parameter: "Is the infection is computed locally?";
	//Range of the cells considered as neighbours for a cell
	int neighbours_size <- 2 min:1 max: 5 parameter:"Size of the neighbours";
	
	float R0 ;
	geometry shape <- square(50);
	
	init {
		//Creation of all the susceptible Host
		create Host number: number_S {
        	is_susceptible <- true;
        	is_infected <-  false;
            is_immune <-  false; 
            color <-  rgb(46,204,113);
        }
        //Creation of all the infected Host
        create Host number: number_I {
            is_susceptible <-  false;
            is_infected <-  true;
            is_immune <-  false; 
            color <-  rgb(231,76,60); 
       }
       //Creation of all the resistant Host
       create Host number: number_R {
            is_susceptible <-  false;
            is_infected <-  false;
            is_immune <-  true; 
            color <-  rgb(52,152,219); 
       }
       
       
       R0 <- beta/(delta+nu);
		write "Basic Reproduction Number: "+ R0;
   }
   
   //Reflex to update the number of infected
   reflex compute_nb_infected {
   		number_I <- Host count (each.is_infected);
   }       
}


//Grid used to discretize space 
grid sir_grid width: 50 height: 50 use_individual_shapes: false use_regular_agents: false frequency: 0{
	rgb color <- #white;
	list<sir_grid> neighbours <- (self neighbors_at neighbours_size) ;       
}

//Species host which represent the Host of the disease
species Host  {
	//Booleans to represent the state of the host agent
	bool is_susceptible <- true;
	bool is_infected <- false;
    bool is_immune <- false;
    rgb color <- rgb(46,204,113);
    sir_grid myPlace;
    
    init {
    	//Place the agent randomly among the grid
    	myPlace <- one_of (sir_grid as list);
    	location <- myPlace.location;
    }     
    //Reflex to make the agent move   
    reflex basic_move {
    	myPlace <- one_of (myPlace.neighbours) ;
        location <- myPlace.location;
    }
    //Reflex to make the agent infected if it is susceptible
    reflex become_infected when: is_susceptible {
    	float rate  <- 0.0;
    	//computation of the infection according to the possibility of the disease to spread locally or not
    	if(local_infection) {
    		int nb_hosts  <- 0;
    		int nb_hosts_infected  <- 0;
    		loop hst over: ((myPlace.neighbours + myPlace) accumulate (Host overlapping each)) {
    			nb_hosts <- nb_hosts + 1;
    			if (hst.is_infected) {nb_hosts_infected <- nb_hosts_infected + 1;}
    		}
    		rate <- nb_hosts_infected / nb_hosts;
    	} else {
    		rate <- number_I / numberHosts;
    	}
    	if (flip(beta * rate)) {
        	is_susceptible <-  false;
            is_infected <-  true;
            is_immune <-  false;
            color <-  rgb(231,76,60);    
        }
    }
    //Reflex to make the agent recovered if it is infected and if it success the probability
    reflex become_immune when: (is_infected and flip(delta)) {
    	is_susceptible <- false;
    	is_infected <- false;
        is_immune <- true;
        color <- rgb(52,152,219);
    }
    //Reflex to kill the agent according to the probability of dying
    reflex shallDie when: flip(nu) {
    	//Create another agent
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


experiment Simulation type: gui { 
 	parameter "Number of Susceptible" var: number_S ;// The number of susceptible
    parameter "Number of Infected" var: number_I ;	// The number of infected
    parameter "Number of Resistant" var:number_R ;	// The number of removed
	parameter "Beta (S->I)" var:beta; 	// The parameter Beta
	parameter "Mortality" var:nu ;	// The parameter Nu
	parameter "Delta (I->R)" var: delta; // The parameter Delta
	parameter "Is the infection is computed locally?" var:local_infection ;
	parameter "Size of the neighbours" var:neighbours_size ;
 	output { 
 		layout #split;
	    display sir_display  type:2d antialias:false {
	        grid sir_grid border: #lightgray;
	        species Host aspect: basic;
	    }
	        
	    display chart refresh: every(10#cycles) type: 2d {
			chart "Susceptible" type: series background: #white style: exploded {
				data "susceptible" value: Host count (each.is_susceptible) color: rgb(46,204,113);
				data "infected" value: Host count (each.is_infected) color: rgb(231,76,60);
				data "immune" value: Host count (each.is_immune) color: rgb(52,152,219);
			}
		}
			
	}
}
