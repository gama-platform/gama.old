

model circle

global {
	int number_of_agents min: 1 <- 100 ;
	int radius_of_circle  min: 10 <- 690 ;
	int repulsion_strength min: 1 <- 5 ;
	int width_and_height_of_environment min: 10 <- 1600 ; 
	int range_of_agents min: 1 <- 25 ;
	float speed_of_agents min: 0.1  <- 4.0;

 
	int size_of_agents <- 50;
	const center type: point <- {width_and_height_of_environment/2,width_and_height_of_environment/2};

	init {
		create cells number: number_of_agents { 
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};
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
		geometry shape <- circle (size) update: circle (size);
		
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


	
	


experiment default type: gui{
	parameter 'Number of Agents' var: number_of_agents;
	parameter "Range of agents" var: range_of_agents;
	parameter Dimensions var: width_and_height_of_environment;
	parameter 'Speed of Agents' var: speed_of_agents;
	parameter 'Strength of Repulsion' var: repulsion_strength;
	parameter 'Radius of Circle' var: radius_of_circle;
	
	output {

	
	display Circle type:opengl  refresh_every: 1 {
		species cells;
	}
	
}}
