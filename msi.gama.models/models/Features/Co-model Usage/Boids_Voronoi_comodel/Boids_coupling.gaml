/**
* Name: Ants_coupling
* Author: Lô
* Description: This is the coupling that manipulate the elements inside Boids  model and propose the function would be used outside.
* Tags: Tag1, Tag2, TagN
*/

model Boids_coupling

import "../../../Toy Models/Boids/models/Boids.gaml"


experiment Boids_coupling_exp type:gui  parent:boids_gui{
	
	list<boids_goal> getBoids_goal{
		return list(boids_goal);
	}
	
	list<boids> getBoids{
		return list(boids);
	}
	
	
	output{
	}
}