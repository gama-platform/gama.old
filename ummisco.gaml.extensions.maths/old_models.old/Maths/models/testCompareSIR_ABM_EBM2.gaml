/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */

model si

global { 
	int toto;
    int number_S <- 495 parameter: 'Number of Susceptible';  // The number of susceptible
    int number_I <- 5 parameter: 'Number of Infected';	// The number of infected
    int number_R <- 0 parameter: 'Number of Removed';	// The number of removed 
     
    float survivalProbability <- 1/(70*365) parameter: 'Survival Probability'; // The survival probability
	float beta <- 0.1;// parameter: 'Beta (S->I)'; 	// The parameter Beta
	float nu <- 0.00 parameter: 'Mortality';	// The parameter Nu
	float delta <- 0.01 parameter: 'Delta (I->R)'; // The parameter Delta
	int number_Hosts <-500;// number_S+number_I+number_R;
	
	bool local_infection <- true parameter: 'Is the infection is computed locally?';
	int gridSize <- 1; //size of the grid
	float neighbourhoodSize <-1.0;// average size of the neighbourhood
	int neighbours_size <- 2 min:1 max: 5 parameter:'Size of the neighbours';
	float average_ngb_number <- 1.0;
	bool randomWalk <- 0; // 1: agents new positions are determined according to a random walk process, new position is in the neighbourhood;
						  // 0: agents new position is selected randomly anywhere in the grid.

	float R0 ;
	
	float hKR4 <- 0.07;
	int iInit <- 1;
//	float betaParam1<-beta*neighbourhoodSize*number_Hosts/gridSize;
	float betaParam<-29.411764705882355;
	init {
//		write ":"+betaParam1;
		set gridSize <- list(sir_grid) count(each);
		let nbCells type: int <- 0;
		loop cell over: list(sir_grid){
			set nbCells <- nbCells + (cell.neighbours count(each));
		}
		set neighbourhoodSize <- nbCells / gridSize+1; // +1 to count itself in the neighbourhood;
		set average_ngb_number <- neighbourhoodSize/gridSize;
		
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
       create Host number: number_R {
            set is_susceptible <-  false;
            set is_infected <-  false;
            set is_immune <-  true; 
            set color <-  rgb('yellow'); 
       }
       
       set R0 <- beta/(delta+nu);
		do write message: 'Basic Reproduction Number: '+ string(R0);
		create my_SIR_maths{
			self.S<-myself.number_S;
			self.I<-number_I;
			self.R<-number_R;
			self.N<-number_Hosts;
			self.beta1 <- betaParam;
			self.alpha<-delta;
		}
   }
//   reflex compute_nb_infected {
//			set number_I <- (Host as list) count (each.is_infected);
//   } 
	int SIABM_intersect function:{(((Host as list) count (each.is_susceptible)< (Host as list) count (each.is_infected)))?cycle:0};
	int SIEBM_intersect function:{(first((my_SIR_maths)).S< first((my_SIR_maths)).I)?cycle:0};
//	reflex ws{
//		write ""+SIABM_intersect+" "+SIEBM_intersect;
//	}
//		reflex SIintersect when: (Host as list) count (each.is_susceptible)< (Host as list) count (each.is_infected) {
//        	write "AA "+cycle;
//        }
//  	   reflex SIintersect1 when: first((my_SIR_maths)).S< first((my_SIR_maths)).I {
//        	write "EE "+cycle;
//        }
   
   reflex compute_average_ngb_number {
   		let nb type: int <- 0;	
   	    loop hst over: (Host as list) {
   		    	set nb <- nb + hst.ngb_number;
        }
        set average_ngb_number <- nb / number_Hosts;
   }      
}

environment width: 50 height: 50 {
	grid sir_grid width: 50 height: 50 {
		rgb color <- rgb('black');
		list neighbours of: sir_grid <- (self neighbours_at neighbours_size) of_species sir_grid;       
    }
  }

entities {
	species Host skills:[moving]  {
		bool is_susceptible <- true;
		bool is_infected <- false;
        bool is_immune <- false;
        rgb color <- rgb('green');
        int sic_count <- 0;
        sir_grid myPlace;
        int ngb_number  function:{    		
        						((myPlace.neighbours + myPlace) accumulate (each.agents)) of_species Host count(each)-1// -1 is because the agent counts itself
        						};
        
        init {
        	set myPlace <- one_of (sir_grid as list);
        	set location <- myPlace.location;
        }        
        reflex basic_move {
        	set myPlace <- one_of (myPlace.neighbours) ;
            set location <- myPlace.location;
            
//			do wander amplitude:800;
			 
//			set myPlace <- any(sir_grid);
//			set location <- myPlace.location;
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
        	} else {
        		//set nb_hosts_infected <- number_I/number_Hosts;
        	}
        	if (flip(1 - (1 - beta)  ^ (((myPlace.neighbours + myPlace) accumulate (each.agents)) of_species Host as list) count (each.is_infected))) {
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
    
    species my_SIR_maths {
		float alpha <- 0.01 min: 0.0 max: 1.0;
		float beta1 <- 0.1 min: 0.0 max: 1000.0;
		int N <- 500 min: 1 max: 3000;
		int iInit <- 1;
    	float t;    
		float I <- float(iInit); 
		float S <- N - I; 
		float R <- 0.0; 
   
		equation SIR{ 
			diff(S,t) = (- beta1 * S * I / N);
			diff(I,t) = (beta1 * S * I / N) - (alpha * I);
			diff(R,t) = (alpha * I);
		}
                
    	solve SIR method: "rk4" step:0.01 { 
    		float cycle_length<-1;
    		float t0<-cycle-1; 
    		float tf<-cycle;
    	
    	}
        
	}
}

experiment Simulation type: gui { 
 	output { 
	    display sir_display {
	        grid sir_grid lines: rgb("black");
	        species Host aspect: basic;
	    }
	        
	    display chart refresh_every: 1 {
			chart 'Susceptible' type: series background: rgb('lightGray') style: exploded {
				data 'susceptible' value: (Host as list) count (each.is_susceptible) color: rgb('green');
				data 'infected' value: (Host as list) count (each.is_infected) color: rgb('red');
				data 'immune' value: (Host as list) count (each.is_immune) color: rgb('yellow');
			}
//			chart 'Susceptible' type: xy background: rgb('lightGray') style: 3d {
//				data susceptible value: (Host as list) count (each.is_susceptible) color: rgb('green');
//				data infected value: (Host as list) count (each.is_infected) color: rgb('red');
//			}			
		}
		display SI refresh_every: 1 {
			chart "SI" type: series background: rgb('white') {
				data 'S' value: first((my_SIR_maths)).S color: rgb('green') ;				
				data 'I' value: first((my_SIR_maths)).I color: rgb('red') ;
				data 'R' value: first((my_SIR_maths)).R color: rgb('yellow') ;				
			}
		}
		display Neighbours refresh_every: 1 {
			chart "Average neighbours number" type: series background: rgb('white'){
				data 'nb' value: average_ngb_number color: rgb('blue');
			}
			
		}
			
	}
}



experiment calibration type: batch repeat: 1 keep_seed: true until: ( SIABM_intersect>0 and SIEBM_intersect>0 ) {	
	parameter "betaEBM" var: betaParam  min: 29.3 max: 29.8 step: 0.1;        
	method exhaustive minimize : abs(SIABM_intersect-SIEBM_intersect);

}
