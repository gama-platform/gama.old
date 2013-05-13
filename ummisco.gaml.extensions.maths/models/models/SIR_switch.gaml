/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */

model si

global { 
    int initial_S <- 495 parameter: 'Number of Susceptible';  // The number of susceptible
    int initial_I <- 5 parameter: 'Number of Infected';	// The number of infected
    int initial_R <- 0 parameter: 'Number of Removed';	// The number of removed 
	int number_Hosts <- initial_S+initial_I+initial_R; // Total number of individuals
	int switch_threshold <- 45 parameter: 'Switch models at'; // threshold for switching models
    SIR_model current_model; // serves as an interface, it is transparent to user if model is maths or IBM
   
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
		do write message: 'Switch will happen at population sizes around ' + string(switch_threshold);	
		do write message: 'Basic Reproduction Number: '+ string(R0) +'\n';

		
		
		create switch_model{
			set threshold_to_IBM <- switch_threshold;
			set threshold_to_Maths <- switch_threshold;
		}
		
		if (first(switch_model).start_with_IBM){
			do write message: 'Starting with IBM model';
			create IBM_model{
				self.S <- initial_S;
				self.I <- initial_I;
				self.R <- initial_R;
				self.N <- number_Hosts;
				do initialize;
			}
			set current_model <- first(IBM_model);
			set current_model.model_type <- 'IBM';
		}else{
			do write message: 'Starting with Maths model';
			create Math_model{
				self.S <- initial_S;
				self.I <- initial_I;
				self.R <- initial_R;
				self.N <- number_Hosts;
				do initialize;
			}
				set current_model <- first(Math_model);
				set current_model.model_type <- 'Maths';
		}	
          
       
       set R0 <- beta/delta;
		
		create my_SIR_maths{
			self.S <- myself.initial_S;
			self.I <- myself.initial_I;
			self.R <- myself.initial_R;
			self.N <- number_Hosts;
		}
  	}
  	


	reflex infection_computation_method{
	/* computing infection from S has a complexity of S*ngb, where ngb is the size of the neighbourhood.
	 * computing infection from I has a complexity of I*ngb.
	 * this reflex determine which method has the lowest cost.
	 * */
		set computeInfectionFromS <- bool((Host as list) count (each.is_susceptible) < (Host as list) count (each.is_infected));
	}

   
      
}

environment width: 50 height: 50 {
	grid sir_grid width: 50 height: 50 {
		rgb color <- rgb('black');
		list neighbours of: sir_grid <- (self neighbours_at neighbours_range) of_species sir_grid;       
    }
  }

entities {
	
	species switch_model{
		int threshold_to_IBM <- 45; // threshold under which the model swith to IBM
		int threshold_to_Maths <- 50; // threshold under which the model swith to Maths model 
		bool start_with_IBM function:{(initial_S < threshold_to_IBM or initial_I < threshold_to_IBM)};
		
		
		reflex switch_to_IBM when: (current_model.model_type = 'Maths'){
//			do write message: 'now is Maths';
			if (current_model.S < threshold_to_IBM or current_model.I < threshold_to_IBM){
				do write message: 'Switch to IBM model at cycle ' + string(cycle);	
				create IBM_model{
	//				do write message: 'current_model.S: ' + string(current_model.S);
					self.S <- current_model.S;
					self.I <- current_model.I;
					self.R <- current_model.R;
					self.N <- current_model.N;
					do initialize;
				}
				ask current_model {do remove_model;}
				set current_model <- first(IBM_model);
				set current_model.model_type <- 'IBM';			
			}
		}
		
	
		reflex switch_to_Maths when: (current_model.model_type = 'IBM'){
//			do write message: 'now is IBM';
			if (current_model.S > threshold_to_Maths and current_model.I > threshold_to_Maths){			
				do write message: 'Switch to Maths model at cycle ' + string(cycle);		
				create Math_model{
					self.S <- current_model.S;
					self.I <- current_model.I;
					self.R <- current_model.R;
					self.N <- current_model.N;
					do initialize;
				}
				ask current_model {do remove_model;}
				set current_model <- first(Math_model);
				set current_model.model_type <- 'Maths';		
			}
		}		
		
	}
	
	species SIR_model{
		float S;
		float I;
		float R;
		int N;
		string model_type <- 'none';
		
		action remove_model{
	//		do write message: 'removing model';
			do die;
		}
	}
	
	species IBM_model parent: SIR_model{
		
		 action initialize{
			do write message: 'Init IBM model with S='  + string(round(S)) + ', I='+ string(round(I)) + ', R='+ string(round(R)) + '\n';

			create Host number: round(S) {
	        	set is_susceptible <- true;
	        	set is_infected <-  false;
	            set is_immune <-  false; 
	            set color <-  rgb('green');
	        }     
	        create Host number: round(I) {
	            set is_susceptible <-  false;
	            set is_infected <-  true;
	            set is_immune <-  false; 
	            set color <-  rgb('red'); 
	       }
	       create Host number: round(R) {
	            set is_susceptible <-  false;
	            set is_infected <-  false;
	            set is_immune <-  true; 
	            set color <-  rgb('yellow'); 			
			}
			//force evaluation at first step;
			do count;
		}	
		
		reflex count{
			do count;
		}
		
		action count{
			set S <- Host count(each.is_susceptible);
			set I <- Host count(each.is_infected);
			set R <- Host count(each.is_immune);			
		}
		
		action remove_model{
			ask Host {do die;}
			do die;
		}
	}
	
	species Math_model parent: SIR_model{
		float t;
		
		action initialize{
			do write message: 'Init Maths model with S='  + string(S) + ', I='+ string(I) + ', R='+ string(R) + '\n';	
		}
		
		equation SIR{ 
			diff(S,t) = (- beta_maths * S * I / N);
			diff(I,t) = (beta_maths * S * I / N) - (delta * I);
			diff(R,t) = (delta * I);
		}
                
    	solve SIR method: "rk4" step:0.01 { 
    	
    	}
		
	}
	
	
	species Host skills:[moving]  {
		bool is_susceptible <- true;
		bool is_infected <- false;
        bool is_immune <- false;
        rgb color <- rgb('green');
        sir_grid myPlace;
        /* next function computes the number of neighbours of the agent */
        int ngb_number  function:{    		
        						((myPlace.neighbours + myPlace) accumulate (each.agents)) of_species Host count(each)-1// -1 is because the agent counts itself
        						};
        
        init {
        	set myPlace <- one_of (sir_grid as list);
        	set location <- myPlace.location;
        }   
             
        reflex basic_move {
        	if (randomWalk){
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
        		if (flip(1 - (1 - beta)  ^ (((myPlace.neighbours + myPlace) accumulate (each.agents)) of_species Host as list) count (each.is_infected))) {
	        		set is_susceptible <-  false;
	            	set is_infected <-  true;
	            	set is_immune <-  false;
	            	set color <-  rgb('red');       	
	        }
        }
        
        reflex infecte_others when: (is_infected and not(computeInfectionFromS)) {
          		loop hst over: ((myPlace.neighbours + myPlace) accumulate (each.agents)) of_species Host{
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
    
    species my_SIR_maths {
		int N <- 500 min: 1 max: 3000;
    	float t;    
		float I <- 1.0; 
		float S <- N - I; 
		float R <- 0.0; 
   
		equation SIR{ 
			diff(S,t) = (- beta_maths * S * I / N);
			diff(I,t) = (beta_maths * S * I / N) - (delta * I);
			diff(R,t) = (delta * I);
		}
                
    	solve SIR method: "rk4" step:0.01 { 
    	
    	}
    	
        
	}



}

experiment Simulation type: gui { 
 	output { 
	    display sir_display {
	        grid sir_grid lines: rgb("black");
	        species Host aspect: basic;
	    }
	        
//	    display chart refresh_every: 1 {
//			chart 'Susceptible' type: series background: rgb('lightGray') style: exploded {
//				data susceptible value: (Host as list) count (each.is_susceptible) color: rgb('green');
//				data infected value: (Host as list) count (each.is_infected) color: rgb('red');
//				data immune value: (Host as list) count (each.is_immune) color: rgb('yellow');
//			}			
//		}
	    display chart refresh_every: 1 {
			chart 'Susceptible' type: series background: rgb('lightGray') style: exploded {
				data 'susceptible' value: current_model.S color: rgb('green');
				data 'infected' value: current_model.I color: rgb('red');
				data 'immune' value: current_model.R color: rgb('yellow');
			}			
		}
		display SI refresh_every: 1 {
			chart "SI" type: series background: rgb('white') {
				data 'S' value: first((my_SIR_maths)).S color: rgb('green');				
				data 'I' value: first((my_SIR_maths)).I color: rgb('red') ;
				data 'R' value: first((my_SIR_maths)).R color: rgb('yellow') ;				
			}
		}

			
	}
}
