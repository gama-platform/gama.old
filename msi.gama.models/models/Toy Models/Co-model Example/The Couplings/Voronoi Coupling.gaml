/**
* Name: boids_coupling
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Voronoi  model. It is used in the "Boid and Voronoi" as an interface. 
* Tags: comodel
*/
model voronoi_coupling

import "../../../Toy Models/Voronoi/Voronoi.gaml"


experiment voronoi type:gui  {
	
	list<center> get_center{
		return list(center);
	}
	list<cell> get_cell{
		return list(cell);
	}
	
	
	output{
	}
}