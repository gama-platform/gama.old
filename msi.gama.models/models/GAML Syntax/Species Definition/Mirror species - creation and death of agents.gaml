/***
* Name: Advanced mirror
* Author: Kevin Chapuis and Benoit Gaudou
* Description: This model goes further in the use of mirror species, 
*   in particular by illustrating creation and death of mirrored agents during the simulation.
*   In particular, the mirrored agents move, and interact with their neighbor agents 
*   following a opinion dynamics model. The model is very close to Deffuant et al. JASSS. 
*   In addition the mirror agents are displayed in a so-called social space of opinion:
*   they are displayed vertically, depending on their opinion value.
*   
* Tags: mirror, display, opinion dynamics
***/

model Mirror

global {
	point up_display_threeD <- {0,0,20};
	float threshold <- 0.4;
	float convergence_speed <- 0.4;
	
	bool with_respawn <- false;
	int nb_agent <- 20;
	
	init {
    	create A number:1;    
  	}
  	
  	// At the creation of an agent A, a mirror agent B will also be created.
  	reflex create_agents when: length(A) < nb_agent{
  		create A number: 2;
  	}
  	
  	// Similarly when an agent A is killed, the corresponding mirror agent is killed 
  	// (this is done only at the end of the simulation step).
  	reflex update_population when: (length(A) >= nb_agent) and with_respawn {
  		ask one_of(A) {do die;}
  		create A;  		
  	}
}

species A skills:[moving]{
	float opinion <- rnd(1.0);
	
    reflex move {
        do wander;
    }

    reflex opinion_influence {
  		A neighbor <- (A - self) with_min_of (each distance_to self);

		if (neighbor != nil) and ((abs(neighbor.opinion - opinion)) < threshold) {	
			// x = x + u* (x - x')
			float temp <- opinion;
			opinion <- opinion + convergence_speed * (neighbor.opinion - opinion);
			neighbor.opinion <- neighbor.opinion + convergence_speed * (temp - neighbor.opinion);	
		}  	
    }
    
    aspect base{
        draw circle(1) color:  rgb(255*opinion,255,0) border: #black;
    }
}

// Only the mirror agents for which the target is not dead will be scheluded.
species B mirrors: A schedules: B where(!dead(each.target)){
	
    aspect base {
    	if(!dead(target)) {
			draw sphere(2) at: {-10, 100 * target.opinion, 10} color: rgb(255*target.opinion,255,0) ;		
    	}
    }
}

experiment mirroExp type: gui {
	parameter "Create new agents" var: with_respawn;
	parameter "Macimum number of agents" var: nb_agent;
	
    output {
        display superposedView type: 3d{ 
          	species A aspect: base ;
          	species B aspect: base ;
          
          	graphics g {
          		font f <- font("Arial", 15, #bold);
          		draw "Opinion" at: {-8, -5, 10} color: #black anchor: #top_center font: f;
          		draw "0.0" at: {-17, 0, 10} color: #black font: f;
          		draw "1.0" at: {-17, 100, 10} color: #black font: f;
          		
          	}
        }
    }
}


