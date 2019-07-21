model SIR_ABM 

global{
	geometry shape<-envelope(square(100));
	list<rgb> the_color<-[#green, #red, #yellow];
	float beta <- 0.8 ; 	 
	float alpha <- 0.06;
	init{
		create Host number:495 ;
		create Host number:5{state<-1;}
		
	}
}

species Host skills:[moving]{
	int state<-0;
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
    
    reflex become_immune when: (state=1 and flip(alpha)) {
    	state<-2;
    }
    
            
	aspect base{
		if(!dead(self)){			
			draw circle(1) color: the_color[state];
		}
	}
}
experiment SIR_ABM_exp type:gui{
	output {
		display "HostDisp" {
			species Host aspect:base;
		}
	}
}