model circle3D   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 1000 ;
	int radius_of_circle parameter: 'Radius of Circle' min: 10 <- 60 ;
	int repulsion_strength parameter: 'Strength of Repulsion' min: 1 <- 5 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 1000 ; 
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 25 ;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1  <- 2.0 ; 
	int size_of_agents <- 20;
	const center type: point <- {width_and_height_of_environment/2,width_and_height_of_environment/2};

	init { 
		create cells number: number_of_agents { 
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};
			let valZ type: float <- rnd(2000);
			set shape <- shape add_z valZ;
		} 
	}  
} 
 
 
environment width: width_and_height_of_environment height: width_and_height_of_environment torus: true;  
 
  
entities { 
	species cells skills: [moving] {  
		const color type: rgb <- [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
		const size type: float <- float(size_of_agents);
		const range type: float <- float(range_of_agents); 
		const speed type: float <- speed_of_agents;   
		int heading <- rnd(359);
		geometry shape <- circle (size) ;
		
		reflex move {
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};

		}

		
		aspect default {
			draw geometry: shape color: color;
		}
	}
}
experiment display  type: gui {
	output {
		display Circle refresh_every: 1  type:opengl {
			species cells;
		}
	}
}
