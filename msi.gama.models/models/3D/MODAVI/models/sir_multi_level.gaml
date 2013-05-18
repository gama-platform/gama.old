model sir

global {
    int number_S <- 100 parameter: 'Number of Susceptible';  // The number of susceptible
    int number_I <- 50 parameter: 'Number of Infected';	// The number of infected
    int number_R <- 0 parameter: 'Number of Removed';	// The number of removed
    float survivalProbability <- 1/(70*365) parameter: 'Survival Probability'; // The survival probability
	float beta <- 0.05 parameter: 'Beta (S->I)'; 	// The parameter Beta
	float nu <- 0.0 parameter: 'Mortality';	// The parameter Nu
	float delta <- 0.01 parameter: 'Delta (I->R)'; // The parameter Delta
	int numberHosts <- number_S+number_I+number_R;
	bool local_infection <- true parameter: 'Is the infection is computed locally?';
	int neighbours_size <- 10 min:1 max: 5 parameter:'Size of the neighbours';
	int nb_infected <- number_I;
	
	rgb susceptible_color <- rgb("green");
	rgb infected_color <- rgb("red");
	rgb immune_color <- rgb("yellow");
	
	int SUSCEPTIBLE <- 1 const: true;
	int INFECTED <- 2 const: true;
	int IMMUNE <- 3 const: true;
	
	macro_host susceptible_macro;
	macro_host infected_macro;
	macro_host immune_macro;
	
	init {
		
		create macro_host returns: susceptible_hosts;
		set susceptible_macro value: susceptible_hosts at 0;
		set susceptible_macro.location value: {25, 10};
		ask susceptible_macro {
			do create_hosts with: [ number :: number_S, _status :: SUSCEPTIBLE ];
			set color value: susceptible_color;
		}
		
		create macro_host returns: infected_hosts;
		set infected_macro value: infected_hosts at 0;
		set infected_macro.location value: {10, 40};
		ask infected_macro as: macro_host {
			do create_hosts with: [ number :: number_I, _status :: INFECTED ];
			set color value: infected_color;
		}
		
		create macro_host returns: immune_hosts;
		set immune_macro value: immune_hosts at 0;
		set immune_macro.location value: {40, 40};
		set immune_macro.color value: immune_color;

	}
}

environment width: 50 height: 50 {
	grid sir_grid width: 50 height: 50 {
		rgb color <- rgb('black');
		list neighbours of: sir_grid <- (self neighbours_at neighbours_size) of_species sir_grid;       
    }
}

entities {
	species macro_host {
		geometry shape value: circle (length(members));
		rgb color;
		string type;
		
		action create_hosts {
			arg number type: int;
			arg _status type: int;
			
			create Host number: number {
				set status value: _status;
			}
		}
		
		species Host topology: topology(world.shape) {

	        int status;
	        rgb color;
	        sir_grid myPlace;
	        
	        init {
	        	set myPlace <- one_of (sir_grid as list);
	        	set location <- myPlace.location;
	        	
	        	switch status {
	        		match SUSCEPTIBLE {  set color value: susceptible_color; }
	        		
	        		match INFECTED { set color value: infected_color; }

	        		match IMMUNE { set color value: immune_color; }
	        	}
	        }        
	        
	        reflex basic_move {
	        	set myPlace <- one_of (myPlace.neighbours) ;
	            set location <- myPlace.location;
	        }
	        
	        reflex become_infected when: (status = SUSCEPTIBLE) {
	        	let rate type: float <- 0;
	        	if(local_infection) {
	        		let nb_hosts type: int <- 0;
	        		let nb_hosts_infected type: int <- 0;
	        		loop hst over: ((myPlace.neighbours + myPlace) accumulate (Host overlapping each)) of_species Host{
	        			set nb_hosts <- nb_hosts + 1;
	        			if (Host(hst).status = INFECTED) {set nb_hosts_infected <- nb_hosts_infected + 1;}
	        		}
	        		set rate <- nb_hosts_infected / nb_hosts;
	        	} else {
	        		set rate <- nb_infected / numberHosts;
	        	}
	        	if (flip(beta * rate)) {
		            set status <- INFECTED;
		            set color <-  susceptible_color;
		            
		            // change host
		            ask infected_macro {
		            	capture myself as: Host {
		            		set color value: infected_color;
		            	}
		            }    
		        }
	        }
	        
	        reflex become_immune when: ( (status = INFECTED) and flip(delta)) {
	            set status <- IMMUNE;
	            set color value: immune_color;
	            
	            // change host
	            ask immune_macro {
	            	capture myself as: Host {
	            		set color value: immune_color;
	            	}
	            }
	        }
	        
	        reflex shallDie when: flip(nu) {
				create species(self) number: 1 {
					set myPlace <- myself.myPlace ;
					set location <- myself.location ; 
				}
	           	do die;
	        }
	        
	        aspect basic {
		        //draw shape: circle color: color size: 1 depth:1; 
		        draw geometry: geometry (point(self.location)) color: color depth:neighbours_size/4;
		        draw circle(neighbours_size) color: color empty: true;
	        }
	    }
	    
	    aspect null { }

	    aspect default {
	    	
	    }
	}
}

experiment sir_multi_level type: gui {

	output {
 		 display modavi_display type:opengl	{
 		 	species macro_host aspect: null  {
	 		 	species Host aspect: basic;						
 		 	}
		}
		
		display macro_display {
			species macro_host aspect: default transparency: 0.5;
		}
	}	
}