/**
 *  sis
 *  Author: 
 *  Description: A compartmental SI model 
 */

model si

import "./modavi.gaml"

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
	float R0 ;
	
	init {
		create Host number: number_S {
        	set is_susceptible <- true;
        	set is_infected <-  false;
            set is_immune <-  false; 
            set status <-1;
            set color <-  rgb('green');
        }
        create Host number: number_I {
            set is_susceptible <-  false;
            set is_infected <-  true;
            set is_immune <-  false;
            set status <-2; 
            set color <-  rgb('red'); 
       }
       set R0 <- beta/(delta+nu);
	do write message: 'Basic Reproduction Number: '+ string(R0);
	
	
	//Dynamic Interaction graph initialization
   
   set nbClass <-3;
   set nbAgent <-number_S + number_I;
   
   //Susceptible macro Node
   create macroNode {	
			set class <-1;
			set nbAggregatedNodes <- number_S;
			set color <-  rgb('green');
			set location <- {0,0,0};	
			add self to: macroNodes;
			//do updatemyNodes;
	}
	
	//Infected macro node
	create macroNode {	
			set class <-2;
			set nbAggregatedNodes <- number_I;
			set color <-  rgb('red');
			set location <- {25,50,0};	
			add self to: macroNodes;
			//do updatemyNodes;
	}
	
	
	//Recovered macro node
	create macroNode {	
			set class <-3;
			set nbAggregatedNodes <- 0;
			set color <-  rgb('yellow');
			set location <- {50,0,0};
			add self to: macroNodes;
			//do updatemyNodes;
	}
	
	set my_macroGraph <- graph(macroNodes);
	
	 //Link between S and I
	 create macroEdge{
    	set nbAggregatedLinks <- 100;
        set src <- macroNodes at (0);
        set dest <- macroNodes at (1);	
        add self to: macroEdges;
        set my_macroGraph <- my_macroGraph add_edge (src::dest);
	 }
	 
	 //Link between I and R
	 create macroEdge{
    	set nbAggregatedLinks <- 200;
        set src <- macroNodes at (1);
        set dest <- macroNodes at (2);
        add self to: macroEdges;	
        set my_macroGraph <- my_macroGraph add_edge (src::dest);
	 }
	 
	 //Link between S and R
	 create macroEdge{
    	set nbAggregatedLinks <- 100;
        set src <- macroNodes at (0);
        set dest <- macroNodes at (2);
        add self to: macroEdges;	
        set my_macroGraph <- my_macroGraph add_edge (src::dest);
	 }
	

	/* macroGraph is created in last to be sure that all the agent update their state before that the macroGraph does something
	 * A another possibility can be to define a scheduler like:
	 * species scheduler schedules : shuffle (list(node) + list(edge) +list(macroNode) + list(macroEdge));
	 * without forgetting to disable the scheduling of each species (e.g species node schedules [])
	 */
	create macroGraph;
	

	//FIXME: If this is call at the beginning of the init block there is some null value in the matrix.
	set interactionMatrix <- 0 as_matrix({3,3});
	
   }
   
   reflex initMatrix{
   	    write 'init matrix';
		set interactionMatrix <- 0 as_matrix({nbClass,nbClass});	
   }
	
   reflex updateMacroNode{
   	
		ask macroNodes as list{
			
			  if	(class = 1) {
				set nbAggregatedNodes <- (Host as list) count (each.is_susceptible);
			  }
			  if	(class = 2) {
				set nbAggregatedNodes <- (Host as list) count (each.is_infected);
			  }
			  if	(class = 3) {
				set nbAggregatedNodes <- (Host as list) count (each.is_immune);
			  }	
			  
			  write "class:" + class + " nbAggregatedNodes:" + nbAggregatedNodes; 	 	 
		    }
	}
	
	
	
	
	reflex updateInteractionMatrix{
		ask Host as list{		
			let neighbours <- (each neighbours_at (neighbours_size));
			ask neighbours as list{
				write "host:" + self.name + "status" + each.status + "<->" + myself.status;
				let tmp <- interactionMatrix  at {each.status-1,myself.status-1};
				write "interactionMatrix  at {" + (each.status-1) + "," + (myself.status-1) +"} = " + tmp;	
				put (int(tmp)+1) at: {each.status-1,myself.status-1} in: interactionMatrix;
   			}
		}
	}
	
	reflex updateMacroEdge{
		ask macroEdges as list{
			let tmp <- interactionMatrix  at {src.class-1,dest.class-1};
			write "macroEdge: " 	+ src.class + "<->" + dest.class + "interactionMatrix final  at {" + (src.class-1) + "," + (dest.class-1) +"} = " + tmp;		
			set nbAggregatedLinks <-tmp;
		}
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
	
	species node2 mirrors: list(Host) parent: graph_node edge_species:edge {
		point location <- target.location update: target.location;
		
		bool related_to(node2 other){
			//return flip(0.1);
			//write "related to called between " + target + " and " + other.target;
			//write " self location " + location;
			//write " other location " + other.location;
			using topology(target){
			//write " computed distance " + target distance_to  other.target + " < " + distance;
				return (target distance_to other.target) < neighbours_size;
			}
		}
		
		aspect sphere{
		  draw sphere(1) color: rgb('blue');
		}
		
	} 
	
	species graphedge parent: base_edge{	

   	aspect base{
   		draw shape color: rgb("blue");
   	}
   }
	species Host  {
		bool is_susceptible <- true;
		bool is_infected <- false;
        bool is_immune <- false;
        int status <- -1;
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
        		loop hst over: ((myPlace.neighbours + myPlace) accumulate (Host overlapping each)) {
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
	            set status <-2;
	            set color <-  rgb('red');    
	        }
        }
        
        reflex become_immune when: (is_infected and flip(delta)) {
        	set is_susceptible value: false;
        	set is_infected value: false;
            set is_immune value: true;
            set status <-3;
            set color value: rgb('yellow');
        }
        
        reflex shallDie when: flip(nu) {
			create species(self) number: 1 {
				set myPlace <- myself.myPlace ;
				set location <- myself.location ; 
			}
           	do die;
        }
        
        reflex updateStatus{
        	if (is_susceptible){
        	  set status <- 1;
        	}
        	if(is_infected){
        	  set status <- 2;	
        	}
        	if(is_immune){
        	  set status <- 3;	
        	}
        }
                
        aspect basic {
	        draw (point(self.location)) color: color depth:neighbours_size/4;
	        draw circle(neighbours_size) color: color empty: true;
        }
    }
}

experiment Simulation type: gui { 
 	output {
 		
 		 display modavi_display type:opengl ambient_light: 0.2	{
 		 	species Host aspect: basic;	
 		 	species node2 aspect: sphere z:0.2;
	        species edge aspect: base z:0.2;
 		 	
			species macroNode aspect:sphere z:0.4;
			species macroEdge aspect:base z:0.4;	
					
		}
 		 

	    
	   	
	}
}
