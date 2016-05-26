model boids_coupling

import "../../../../Toy Models/Boids/models/Boids.gaml"


experiment boids_gui type:gui  {
	
	list<boids_goal> get_boids_goal{
		return list(boids_goal);
	}
	
	list<boids> get_boids{
		return list(boids);
	}
	
	output{
	}
}