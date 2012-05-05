model circle


import "circle.gaml"  
global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 800;
	int radius_of_circle parameter: 'Radius of Circle' min: 10 <- 25000;  
	int repulsion_strength parameter: 'Strength of Repulsion' min: 1 <- 50;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 60000 ; 
	int range_of_agents parameter: 'Range of Agents' min: 1 <- 250;
	float speed_of_agents parameter: 'Speed of Agents' min: 0.1 <- 100.0;    
	int size_of_agents <- 100;

	
} 
output {
	// we have to copy the output otherwise nothing is displayed 
	display Circle refresh_every: 1 { 
		species cells;
	}
}
  