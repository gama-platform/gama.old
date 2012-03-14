model circle

global {
	int number_of_agents parameter: 'Number of Agents' <- 100 min: 1;
	int radius_of_circle parameter: 'Radius of Circle' <- 690 min: 10;
	int repulsion_strength parameter: 'Strength of Repulsion' <- 5 min: 1;
	int width_and_height_of_environment parameter: 'Dimensions' <- 1600 min: 10; 
	int range_of_agents parameter: 'Range of Agents' <- 25 min: 1;
	float speed_of_agents parameter: 'Speed of Agents' <- 2 min: 0.1; 
	int size_of_agents <- 10;
	const center type: point <- {width_and_height_of_environment/2,width_and_height_of_environment/2};

	init {
		create species: cells number: number_of_agents { 
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};
		}
	}  
} 

environment width: width_and_height_of_environment height: width_and_height_of_environment torus: true; 

entities {
	species cells skills: [moving] {  
		const color type: rgb <- [100 + rnd (155),100 + rnd (155), 100 + rnd (155)] as rgb;
		const size type: float <- size_of_agents;
		const range type: float <- range_of_agents; 
		const speed type: float <- speed_of_agents;  
		int heading <- rnd(359);
		geometry shape <- circle (12) update: circle (size);
		
		reflex go_to_center {
			set heading <- (((self distance_to center) > radius_of_circle) ? self towards center : (self towards center) - 180);
			do move speed: speed; 
		}
		
		reflex flee_others {
			let close type: cells <- one_of ( ( (self neighbours_at range) of_species cells) sort_by (self distance_to each) );
			if close != nil {
				set heading <- (self towards close) - 180;
				let dist <- self distance_to close;
				do move speed: dist / repulsion_strength heading: heading;
			}
		}
		
		aspect default {
			draw shape: geometry color: color;
		}
	}
}

output {
	display Circle refresh_every: 1 {
		species cells;
	}
}
