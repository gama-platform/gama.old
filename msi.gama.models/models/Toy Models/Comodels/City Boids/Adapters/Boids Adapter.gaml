/**
* Name: boids_adapter
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Boids model. It is used in the "City Boids" as an interface. 
* Tags: comodel
*/
model boids_adapter

import "../../../../Toy Models/Boids/models/Boids 3D Motion.gaml"


experiment "Adapter of Boids" type:gui  {
	
	list<boids_goal> get_boids_goal{
		return list(boids_goal);
	}
	
	list<boids> get_boids{
		return list(boids);
	}
	
	output{
	}
}