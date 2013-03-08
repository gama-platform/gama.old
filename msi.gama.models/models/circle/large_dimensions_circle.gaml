model circle


import "circle.gaml"     
global {
	int number_of_agents min: 1 <- 30;
	int radius_of_circle min: 10 <- 15000;  
	int repulsion_strength min: 1 <- 50;
	int width_and_height_of_environment min: 10 <- 40000 ; 
	int range_of_agents min: 1 <- 250;
	float speed_of_agents min: 0.1 <- 100.0;    
	int size_of_agents <- 100;

	
} 

experiment circle type: gui {
	parameter 'Number of Agents' var: number_of_agents;
	parameter 'Radius of Circle' var: radius_of_circle; 
	parameter 'Strength of Repulsion' var: repulsion_strength;
	parameter 'Dimensions' var: width_and_height_of_environment;
	parameter 'Range of Agents' var: range_of_agents;
	parameter 'Speed of Agents' var: speed_of_agents ; 
	
	output {
		display Circle refresh_every: 1 {
			species cells; 
		}
	}
}
  