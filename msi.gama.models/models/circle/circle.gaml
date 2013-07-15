model circle_model

global { 
	int number_of_agents min: 1 <- 50 ;
	int radius_of_circle min: 10 <- 1000 ;
	int repulsion_strength min: 1 <- 5 ;
	int width_and_height_of_environment min: 10 <- 3000 ; 
	int range_of_agents min: 1 <- 25 ;
	float speed_of_agents min: 0.1  <- 2.0 ; 
	int size_of_agents <- 100;
	const center type: point <- {width_and_height_of_environment/2,width_and_height_of_environment/2};
	geometry shape <- square(width_and_height_of_environment);
	init { 
		create cell number: number_of_agents;
	}  
}  
  
entities { 
	species cell skills: [moving] {  
		const color type: rgb <- [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
		const size type: float <- float(size_of_agents);
		const range type: float <- float(range_of_agents); 
		const speed type: float <- speed_of_agents;   
		int heading <- rnd(359);
		
		reflex go_to_center {
			heading <- (((self distance_to center) > radius_of_circle) ? self towards center : (self towards center) - 180);
			do move speed: speed; 
		}
		
		reflex flee_others {
			cell close <- one_of ( ( (self neighbours_at range) of_species cell) sort_by (self distance_to each) );
			if close != nil {
				heading <- (self towards close) - 180;
				float dist <- self distance_to close;
				do move speed: dist / repulsion_strength heading: heading;
			}
		}
		
		aspect default { 
			draw circle(size)  color: color;
		}
	}
}

experiment main type: gui {
	parameter 'Number of Agents' var: number_of_agents;
	parameter 'Radius of Circle' var: radius_of_circle; 
	parameter 'Strength of Repulsion' var: repulsion_strength; 
	parameter 'Dimensions' var: width_and_height_of_environment;
	parameter 'Range of Agents' var: range_of_agents;
	parameter 'Speed of Agents' var: speed_of_agents ; 
	
	output {
		display Circle refresh_every: 1 {
			species cell;
		}
	}
}

