model large_circle_model


import "Circle.gaml"     
global {
	int size_of_agents <- 100;
} 

experiment main2 type: gui  {
	parameter 'Number of Agents' var: number_of_agents <- 300;
	parameter 'Radius of Circle' var: radius_of_circle min: 10 <- 15000; 
	parameter 'Strength of Repulsion' var: repulsion_strength min: 1 <- 50;
	parameter 'Dimensions' var: width_and_height_of_environment  min: 10 <- 40000;
	parameter 'Range of Agents' var: range_of_agents min: 1 <- 250;
	parameter 'Speed of Agents' var: speed_of_agents min: 0.1 <- 100.0 ; 
	
	init {
		map values <- user_input("Enter initial values", ["Number" :: 100, "Radius"::10000, "Repulsion"::50, "Other"::file("toto.txt")]);
		number_of_agents <- int(values["Number"]);
	}
	
	
	output {
		display Circle refresh_every: 1 {
			species cell; 
		}
	}
}
  