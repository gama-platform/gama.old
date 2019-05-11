/**
* Name: boids_adapter
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Voronoi model. It is used in the "Boid and Voronoi" as an interface. 
* Tags: comodel
*/
model voronoi_adapter

import "../../../../Toy Models/Art/Voronoi/Voronoi.gaml"


experiment "Adapter 2" type:gui  {
	
	list<center> get_center{
		return list(center);
	}
	
	list<cell> get_cell{
		return list(cell);
	}	
	
	output{
	}
}