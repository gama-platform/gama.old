/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */

model model5

global { 
    int initial_S <- 495 parameter: 'Number of Susceptible';  // The number of susceptible
    int initial_I <- 5 parameter: 'Number of Infected';	// The number of infected
    int initial_R <- 0 parameter: 'Number of Removed';	// The number of removed 
	int number_Hosts <- initial_S+initial_I+initial_R; // Total number of individuals
	int switch_threshold <- 120 parameter: 'Switch models at'; // threshold for switching models


   
	float beta <- 0.1 parameter: 'Beta (S->I)'; 	// The parameter Beta
	float delta <- 0.01 parameter: 'Delta (I->R)'; // The parameter Delta
	float beta_maths;
	
	bool local_infection <- true parameter: 'Is the infection is computed locally?';
	int gridSize <- 1; //size of the grid
	int neighbours_range <- 2 min:1 max: 5 parameter:'Size of the neighbours';
	float neighbourhoodSize <-1.0; // average size of the neighbourhood (in number of cells)	
	bool randomWalk <- false parameter: 'Random Walk';  // 1: agents new positions are determined according to a random walk process, 
														// new position is in the neighbourhood;
						    							// 0: agents new position is selected randomly anywhere in the grid.
	float adjust <- 1.0; // to adjust math model to ABM when using random walk
	bool computeInfectionFromS <- bool(initial_S < initial_I); // if true, use the S list to compute infections. If false, use I list.
										 					 // the purpose is to minimize the number of evaluation by using the smallest list.
	float R0 ;
	
	
	
	
	init {

		/* determine the size of the neighbourhood and the average count of hosts neighbours */
		set gridSize <- list(sir_grid) count(each);
		let nbCells type: int <- 0;
		loop cell over: list(sir_grid){
			set nbCells <- nbCells + (cell.neighbours count(each));
		}
		set neighbourhoodSize <- nbCells / gridSize+1; // +1 to count itself in the neighbourhood;
		set beta_maths <- beta *neighbourhoodSize*number_Hosts/gridSize*adjust;
			
		
        set R0 <- beta/delta;
		
		
		
	
	

			create Host number: round(initial_S) {
	        	set is_susceptible <- true;
	        	set is_infected <-  false;
	            set is_immune <-  false; 
	            set color <-  rgb('green');
	        }     
	        create Host number: round(initial_I) {
	            set is_susceptible <-  false;
	            set is_infected <-  true;
	            set is_immune <-  false; 
	            set color <-  rgb('red'); 
	       }
	       create Host number: round(initial_R) {
	            set is_susceptible <-  false;
	            set is_infected <-  false;
	            set is_immune <-  true; 
	            set color <-  rgb('yellow'); 			
			}  
		}
  	


//	reflex infection_computation_method{
//	/* computing infection from S has a complexity of S*ngb, where ngb is the size of the neighbourhood.
//	 * computing infection from I has a complexity of I*ngb.
//	 * this reflex determine which method has the lowest cost.
//	 * */
//		set computeInfectionFromS <- bool((Host as list) count (each.is_susceptible) < (Host as list) count (each.is_infected));
//	}

   
      
}

environment width: 50 height: 50 {
	grid sir_grid width: 50 height: 50 {
		rgb color <- rgb('black');
		list neighbours of: sir_grid <- (self neighbours_at neighbours_range) of_species sir_grid;       
    }
  }

entities {
	
	
		

	
	species Host skills:[moving]  {
		bool is_susceptible <- true;
		bool is_infected <- false;
        bool is_immune <- false;
        rgb color <- rgb('green');
        sir_grid myPlace;
        /* next function computes the number of neighbours of the agent */
        int ngb_number  function:{    		
//        						((myPlace.neighbours + myPlace) collect (each.agents)) of_species Host count(each)-1// -1 is because the agent counts itself
												((self) neighbours_at (2)) of_species Host count(each)-1
        						};
        
        init {
        	set myPlace <- one_of (sir_grid as list);
        	set location <- myPlace.location;
        }   
             
        reflex basic_move {
        	if (!randomWalk){
        		/* random walk among neighbours */
	        	set myPlace <- one_of (myPlace.neighbours) ;
	            set location <- myPlace.location;            
//				do wander amplitude:800;
//				set myPlace <- first(sir_grid overlapping (self)) ;      		
        	}else{
        		/* move agent to a random place anywhere in the grid */
				set myPlace <- any(sir_grid);
				set location <- myPlace.location;		
        	}			 
        }
        


        reflex become_infected when: (is_susceptible and computeInfectionFromS) {
//        		if (flip(1 - (1 - beta)  ^ (((myPlace.neighbours + myPlace) collect (each.agents)) of_species Host as list) count (each.is_infected))) {
	        	if (flip(1 - (1 - beta)  ^ (((self) neighbours_at (2)) of_species Host) count (each.is_infected))) {
	        		set is_susceptible <-  false;
	            	set is_infected <-  true;
	            	set is_immune <-  false;
	            	set color <-  rgb('red');       	
	        }
        }
        
        reflex infecte_others when: (is_infected and not(computeInfectionFromS)) {
//          		loop hst over: ((myPlace.neighbours + myPlace) collect (each.agents)) of_species Host{
          			loop hst over: ((self) neighbours_at (2)) of_species Host{
        			if (Host(hst).is_susceptible){
        				if(flip(beta)){
			 	        	set hst.is_susceptible <-  false;
				            set hst.is_infected <-  true;
				            set hst.is_immune <-  false;
				            set hst.color <-  rgb('red');     		
        				}    				
        			}
        		}
        }
        
        reflex become_immune when: (is_infected and flip(delta)) {
        	set is_susceptible value: false;
        	set is_infected value: false;
            set is_immune value: true;
            set color value: rgb('yellow');
        }
        
//        reflex shallDie when: flip(nu) {
//			create species(self) number: 1 {
//				set myPlace <- myself.myPlace ;
//				set location <- myself.location ; 
//			}
//           	do die;
//        }
                
        aspect basic {
	        draw circle(1) color: color; 
        }
    }
    
}

experiment simulation type: gui { 
 	output { 
	    display 'sir display' {
	        grid sir_grid lines: rgb("black");
	        species Host aspect: basic;
	    }
	
	    display 'Time series' refresh_every: 1 {
			chart 'Susceptible' type: series background: rgb('lightGray') style: exploded {
				data 'susceptible' value: Host count(each.is_susceptible) color: rgb('green');
				data 'infected' value: Host count(each.is_infected) color: rgb('red');
				data 'immune' value: Host count(each.is_immune) color: rgb('yellow');
			}
		
		}
		

			
	}
}
