model circle


import "circle.gaml"
global {
	int number_of_agents parameter: 'Number of Agents' <- 800 min: 1;
	int radius_of_circle parameter: 'Radius of Circle' <- 25000 min: 10;
	int repulsion_strength parameter: 'Strength of Repulsion' <- 50 min: 1;
	int width_and_height_of_environment parameter: 'Dimensions' <- 60000 min: 10; 
	int range_of_agents parameter: 'Range of Agents' <- 250 min: 1;
	float speed_of_agents parameter: 'Speed of Agents' <- 100 min: 0.1; 
	int size_of_agents <- 100;
	
}
output {
	// we have to copy the output otherwise nothing is displayed
	display Circle refresh_every: 1 { 
		species cells;
	}
}
 