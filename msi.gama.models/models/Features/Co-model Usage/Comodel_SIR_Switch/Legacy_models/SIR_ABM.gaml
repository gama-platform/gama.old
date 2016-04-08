model SIR_ABM 

global{
	geometry shape<-envelope(square(100));
	float beta <- 0.5 ; 	// The parameter Beta
	float nu <- 0.001 ;
	float delta <- 0.01;
	init{
		create Host number:495 ;
		create Host number:5{state<-1;}
		
	}
}

species Host skills:[moving]{
	int state<-0;
	list<rgb> color<-[#green, #red, #yellow];
	reflex moving{
		do wander;
	}


    
    reflex become_infected when: state=1 {
    	list n<- self neighbors_at(1);
    	ask n{    		
	    	if (flip(beta)) {
				state<-1;    
	        }
    	}
    }
    
    reflex become_immune when: (state=1 and flip(delta)) {
    	state<-2;
    }
    
            
	aspect base{
		draw circle(1) color: color[state];
	}
}
experiment SIR_ABM_exp type:gui{
	output {
		display gridDisp{
			species Host aspect:base;
		}
	}
}