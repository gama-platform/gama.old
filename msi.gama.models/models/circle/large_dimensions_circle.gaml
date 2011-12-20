model circle


import "circle.gaml"
global {
	var number_of_agents type: int init: 800 parameter: 'number_of_agents' min: 1 ;
	var radius_of_circle type: int init: 25000 parameter: 'radius_of_circle' min: 10 ;
	var repulsion_strength type: int init: 50 parameter: 'repulsion_strength' min: 1 ;
	var width_and_height_of_environment type: int init: 60000 parameter: 'width_and_height_of_environment' min: 10 ;
	var speed_of_agents type: float init: 100 parameter: 'speed_of_agents' min: 0.1 ;
	var size_of_agents type: int init: 100;
	var range_of_agents type: int parameter: 'Range of Agents' init: 250 min: 1;
}
output {
	// we have to copy the output otherwise nothing is displayed
	display Circle refresh_every: 1 {
		species cells;
	}
}
