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