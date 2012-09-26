model cube   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 1 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 1600 ; 
	int size_of_agents parameter: 'size' min: 1 <- 50;
	const center type: point <- {width_and_height_of_environment/2,width_and_height_of_environment/2};

	init { 
		create cube number: number_of_agents; 
	}  
} 
 
 
environment width: width_and_height_of_environment height: width_and_height_of_environment torus: true;  
 
  
entities { 
	species cube skills: [moving] {  
		const color type: rgb <- [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
		const size type: float <- float(size_of_agents);
		int heading <- rnd(359);
		geometry shape <- square (size) ;
		
		reflex move {
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};

		}

		aspect default {
			draw geometry: shape color: color z:size_of_agents ;
		}
	}
}
experiment display  type: gui {
	output {
		display Circle refresh_every: 1  type:opengl {
			species cube;
		}
	}
}
