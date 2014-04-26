/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */ 
 
model SIR_switch 

global { 
	int enviSize<-50;
	 
    int initial_S <- 1495;// parameter: 'Number of Susceptible';  // The number of susceptible
    int initial_I <- 1;// parameter: 'Number of Infected';	// The number of infected
    int initial_R <- 0;// parameter: 'Number of Removed';	// The number of removed 
	int number_Hosts <- initial_S+initial_I+initial_R; // Total number of individuals
	int switch_threshold <- 480 parameter: 'Switch models at'; // threshold for switching models
    SIR_model current_model; // serves as an interface, it is transparent to user if model is maths or IBM
	float is_transparent <- 0.0;
   
	float beta <- 0.01 parameter: 'Beta (S->I)'; 	// The parameter Beta
	float delta <- 0.01 parameter: 'Delta (I->R)'; // The parameter Delta
	float beta_maths;
	
	bool local_infection <- true parameter: 'Is the infection is computed locally?';
	int gridSize <- 1; //size of the grid
	int neighbours_range <- 2 min:1 max: 200 parameter:'Size of the neighbours';
	bool saveLocation <- true parameter: 'Save location of IBM -> EBM';
												
	float neighbourhoodSize <-3.0; // average size of the neighbourhood (in number of cells)	
	bool randomWalk <- true parameter: 'Random Walk';  // 1: agents new positions are determined according to a random walk process, 
														// new position is in the neighbourhood;
						    							// 0: agents new position is selected randomly anywhere in the grid.
	float adjust <- 1.0; // to adjust math model to ABM when using random walk
	bool computeInfectionFromS <- initial_S < initial_I; // if true, use the S list to compute infections. If false, use I list.
										 					 // the purpose is to minimize the number of evaluation by using the smallest list.
	float R0 ;
	
	init {
		
		create new_scheduler;
		/* determine the size of the neighbourhood and the average count of hosts neighbours */
		gridSize <- list(sir_grid) count(true);
		neighbourhoodSize <- sum(sir_grid accumulate(length(each.neighbours))) / gridSize+1; // +1 to count itself in the neighbourhood; // average size of the neighbourhood of one cell
													   //+1 to count itself in the neighbourhood;
	
		
		
		beta_maths <- beta *neighbourhoodSize*number_Hosts/gridSize*adjust;
		do write message: 'Switch will happen at population sizes around ' + string(switch_threshold);	
		
        R0 <- beta/delta;
		do write message: 'Basic Reproduction Number: '+ string(R0) +'\n';

		
		
		create switch_model{
			threshold_to_IBM <- switch_threshold;
			threshold_to_Maths <- switch_threshold;
		}
		
          
       	if (first(switch_model).start_with_IBM){
	//		do write message: 'Starting with IBM model';
			is_transparent <- 0.0;
			create IBM_model {}
			current_model <- first(IBM_model);
		}else{
	//		do write message: 'Starting with Maths model';
			create Math_model{}
			is_transparent <- 1.0;
			current_model <- first(Math_model);
		}	
		current_model.S <- float(initial_S);
		current_model.I <- float(initial_I);
		current_model.R <- float(initial_R);
		current_model.N <- number_Hosts;
        ask current_model  {do initialize;}
        
       
		
		create my_SIR_maths{
			self.S <- float(myself.initial_S);
			self.I <- float(myself.initial_I);
			self.R <- float(myself.initial_R);
			self.N <- number_Hosts;
		}
  	}
  	int aaa<-rnd(99);
  	string outSwitch<-"";
  	string outEBM<-"";
//	reflex saveData when:cycle>=300{
//		save outSwitch to: "outSwitch.csv" type: "csv" rewrite:true;
//		save outEBM to: "outEBM.csv" type: "csv" rewrite:true;
// 		do halt;
//	}
	reflex infection_computation_method{
		outSwitch<-outSwitch+current_model.S  + ","+ current_model.I +","+current_model.R +"\n";
		outEBM<-outEBM+first((my_SIR_maths)).S+","+first((my_SIR_maths)).I +","+first((my_SIR_maths)).R+"\n";  
	/* computing infection from S has a complexity of S*ngb, where ngb is the size of the neighbourhood.
	 * computing infection from I has a complexity of I*ngb.
	 * this reflex determine which method has the lowest cost.
	 * */
//	 write ""+cycle+" in model"+aaa+"  "+current_model.S  + ","+ current_model.I +","+current_model.R;
		computeInfectionFromS <- (Host as list) count (each.is_susceptible) < (Host as list) count (each.is_infected);
	}

   
      
}

environment width: enviSize height: enviSize {
	grid sir_grid width: enviSize height: enviSize {
		rgb color <- rgb('white');
		list neighbours of: sir_grid <- (self neighbours_at neighbours_range) of_species sir_grid;       
    }
  }

entities {
	
	species new_scheduler schedules:(Host+IBM_model+Math_model+switch_model) {}
	
	species switch_model schedules: [] {
		int threshold_to_IBM <- 45; // threshold under which the model swith to IBM
		int threshold_to_Maths <- 50; // threshold under which the model swith to Maths model 
		bool start_with_IBM function:{(initial_S < threshold_to_IBM or initial_I < threshold_to_IBM)};
		

//		task switch_to_IBM weight:1 when: (current_model.model_type = 'Maths'){

		list<sir_grid> savedLocS<-[];
		list<sir_grid> savedLocI<-[]; 
		list<sir_grid> savedLocR<-[];  

		reflex switch_to_IBM when: (current_model.model_type = 'Maths'){
			if (current_model.S < threshold_to_IBM or current_model.I < threshold_to_IBM){
				do write message: 'Switch to IBM model at cycle ' + string(cycle);	
				ask world{do pause;}
				if(saveLocation){
					loop times:length(savedLocS)-current_model.S{
						sir_grid tmp <- any(savedLocS);
						savedLocS>-tmp;
						savedLocI<+tmp;
					}				
					loop times:current_model.R-length(savedLocR){
						sir_grid tmp <- any(savedLocI);
						savedLocI>-tmp;
						savedLocR<+tmp;
					}	
				}
				
				
				create IBM_model{
	//				do write message: 'current_model.S: ' + string(current_model.S);
					self.S <- current_model.S;
					self.I <- current_model.I;
					self.R <- current_model.R;
					self.N <- current_model.N;
					do initialize;
				}
				ask current_model {do remove_model;}
				current_model <- first(IBM_model);
				is_transparent <- 0.0;		
			}
		}
		
		reflex beforeswitch_to_Maths when: (current_model.model_type = 'IBM'){
			if (current_model.S > threshold_to_Maths-50 and current_model.I > threshold_to_Maths-50){			
					ask world{do pause;}					
			}
		}
		reflex switch_to_Maths when: (current_model.model_type = 'IBM'){
			
//			do write message: 'now is IBM';
			if (current_model.S > threshold_to_Maths and current_model.I > threshold_to_Maths){			
				do write message: 'Switch to Maths model at cycle ' + string(cycle);	
				ask world{do pause;}	
				if(saveLocation){
					savedLocS<-(Host where (each.is_susceptible)) collect each.myPlace;						
					savedLocI<-(Host where (each.is_infected)) collect each.myPlace;						
					savedLocR<-(Host where (each.is_immune)) collect each.myPlace;
				}
				create Math_model{
					self.S <- current_model.S;
					self.I <- current_model.I;
					self.R <- current_model.R;
					self.N <- current_model.N;
					do initialize;
				}
				ask current_model {do remove_model;}
				current_model <- first(Math_model);
				is_transparent <- 1.0;
				 	
			}
		}
		

		
	}
	
	species SIR_model schedules: []{
		float S;
		float I;
		float R;
		int N;
		string model_type <- 'none';
		
		action remove_model{
	//		do write message: 'removing model';
			do die;
		}
		
		action initialize{}
	}
	
	species IBM_model schedules: [] parent: SIR_model{
		 string model_type <- 'IBM';
		
		
		 action initialize{
			do write message: 'Initializing IBM model with S='  + string(round(S)) + ', I='+ string(round(I)) + ', R='+ string(round(R)) + '\n';
			

			create Host number: round(S) {
	        	is_susceptible <- true;
	        	is_infected <-  false;
	            is_immune <-  false; 
	            color <-  rgb('green');
	            if(length(first(switch_model).savedLocS)>0){	            	
	          		myPlace <- one_of (first(switch_model).savedLocS);
	            }else{
		            myPlace <- one_of (sir_grid as list);
	            }
        		location <- myPlace.location;
	        }     
	        create Host number: round(I) {
	            is_susceptible <-  false;
	            is_infected <-  true;
	            is_immune <-  false; 
	            color <-  rgb('red');
	            if(length(first(switch_model).savedLocI)>0){	            	
	          		myPlace <- one_of (first(switch_model).savedLocS);
	            }else{
		            myPlace <- one_of (sir_grid as list);
	            }
        		location <- myPlace.location; 
	       }
	       create Host number: round(R) {
	            is_susceptible <-  false;
	            is_infected <-  false;
	            is_immune <-  true; 
	            color <-  rgb('yellow');
	            if(length(first(switch_model).savedLocR)>0){	            	
	          		myPlace <- one_of (first(switch_model).savedLocS);
	            }else{
		            myPlace <- one_of (sir_grid as list);
	            }
        		location <- myPlace.location; 			
			}
			//force evaluation at first step;
			do count;
		}	
		
		reflex count{
			do count;
		}
		
		action count{
			S <- float(Host count(each.is_susceptible));
			I <- float(Host count(each.is_infected));
			R <- float(Host count(each.is_immune));		
		}
		
		action remove_model{
			ask Host {do die;}
			do die;
		}
	}
	
	species Math_model schedules: [] parent: SIR_model{
		string model_type <- 'Maths';
		float t;
		
		action initialize{
			do write message: 'Initializing Maths model with S='  + string(S) + ', I='+ string(I) + ', R='+ string(R) + '\n';	
		}
		
		equation SIR1{ 
			diff(S,t) = (- beta_maths * S * I / N);
			diff(I,t) = (beta_maths * S * I / N) - (delta * I);
			diff(R,t) = (delta * I);
		}
                
        reflex solving{        	
	    	solve SIR1 method: "rk4" step:1;
        }
    	
		
	}
	
	
	species Host schedules: [] skills:[moving]  {
		bool is_susceptible <- true;
		bool is_infected <- false;
        bool is_immune <- false;
        rgb color <- rgb('green');
        sir_grid myPlace;
        /* next function computes the number of neighbours of the agent */
       int ngb_infected_number function: {self neighbours_at(neighbours_range) count(each.is_infected)};
        
//        init {
//        	myPlace <- one_of (sir_grid as list);
//        	location <- myPlace.location;
//        }   
             
        reflex basic_move {
        	if (randomWalk){
        		/* random walk among neighbours */
	        	myPlace <- one_of (myPlace.neighbours) ;
	            location <- myPlace.location;            
//				do wander amplitude:800;
//				myPlace <- first(sir_grid overlapping (self)) ;      		
        	}else{
        		/* move agent to a random place anywhere in the grid */
				myPlace <- any(sir_grid);
				location <- myPlace.location;		
        	}			 
        }
        


        reflex become_infected when: (is_susceptible and computeInfectionFromS) {
        		if (flip(1 - (1 - beta)  ^ ngb_infected_number)) {
	        		is_susceptible <-  false;
	            	is_infected <-  true;
	            	is_immune <-  false;
	            	color <-  rgb('red');       	
	        }
        }
        
        reflex infecte_others when: (is_infected and not(computeInfectionFromS)) {
 
      			loop hst over: (self neighbours_at(neighbours_range) where (each.is_susceptible)) {
 
        				if(flip(beta)){
			 	        	hst.is_susceptible <-  false;
				            hst.is_infected <-  true;
				            hst.is_immune <-  false;
				            hst.color <-  rgb('red');     		
        				}    				
        		}
        }
        
        reflex become_immune when: (is_infected and flip(delta)) {
        	is_susceptible <- false;
        	is_infected <- false;
            is_immune <- true;
            color <- rgb('yellow');
        }
        
//        reflex shallDie when: flip(nu) {
//			create species(self) number: 1 {
//				myPlace <- myself.myPlace ;
//				location <- myself.location ; 
//			}
//           	do die;
//        }
                
        aspect basic {
	        draw square(1) color: color; 
        }
    }
    
    species my_SIR_maths {
		int N <- 500 min: 1 max: 3000;
    	float t;    
		float I <- 1.0; 
		float S <- N - I; 
		float R <- 0.0; 
		float adj2 <- 0.6;
   
		equation SIR{ 
			diff(S,t) = (-beta_maths * S * I / N);
			diff(I,t) = (beta_maths * S * I / N) - (delta * I);
			diff(R,t) = (delta * I);
		}
		
		reflex solving{
    		solve SIR method: "rk4" step:1;
		}
    	
        
	}




}
//experiment SIRsimulation2 type: gui { 
// 	init{
//		initial_S<-595;
//	}
// 	output {  
// 	
// 	
// 	}	
// 	}
experiment SIRsimulation type: gui { 
 	output { 
	    display 'sir display' {

	        	 chart 'Distribution' type : pie background :rgb('white'){
		    		data "Susceptible" value: current_model.S color: rgb('green');
					data "Infected" value: current_model.I color: rgb('red');
					data "Immune" value: current_model.R color: rgb('yellow');
				}
			grid sir_grid lines: rgb("white") transparency: is_transparent;
	        species Host aspect: basic transparency: is_transparent;
	    }
	    
	    
	    
	    display 'Mixed time series' refresh_every: 1 {
			chart 'Susceptible' type: series background: rgb('lightGray') style: exploded {
				
				data "Susceptible" value: current_model.S color: rgb('green');
				data "Infected" value: current_model.I color: rgb('red');
				data "Immune" value: current_model.R color: rgb('yellow');
				data "S EBM" value: first((my_SIR_maths)).S color: rgb (100,200,100);				
				data "I EBM" value: first((my_SIR_maths)).I color: rgb ('orange');
				data "R EBM" value: first((my_SIR_maths)).R color: rgb(100,100,20) ;	
			}
		
		}		
	    
//	    display distribution refresh_every: 1{
//	    		chart 'Distribution' type : pie background :rgb('lightGray'){
//		    		data "Susceptible" <- current_model.S color: rgb('green');
//					data "Infected" <- current_model.I color: rgb('red');
//					data "Immune" <- current_model.R color: rgb('yellow');
//				}
//				
//	    }
//	        
//
//	    display 'Time series' refresh_every: 1 {
//			chart 'Susceptible' type: series background: rgb('lightGray') style: exploded {
//				data "Susceptible" <- current_model.S color: rgb('green');
//				data "Infected" <- current_model.I color: rgb('red');
//				data "Immune" <- current_model.R color: rgb('yellow');	
//			}
//		
//		}
//		
//		
//		display "100% EBM" refresh_every: 1 {
//			chart "100% EBM" type: series background: rgb('white') {
//				data "S" <- first((my_SIR_maths)).S color: rgb('green');				
//				data "I" <- first((my_SIR_maths)).I color: rgb('red') ;
//				data "R" <- first((my_SIR_maths)).R color: rgb('yellow') ;				
//			}
//		}

			
	}
}
