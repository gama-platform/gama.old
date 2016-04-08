model Ants_coupling

import "../../../Toy Models/Ants (Foraging and Sorting)/models/Ant Foraging (Complex).gaml" 



experiment Ants_coupling_exp type:gui  parent:Complete{
	
	list<ant> getAnts{
		return list(ant);
	}
	
	list<ant_grid> getAnt_grid{
		return list(ant_grid);
	}
	
	output{	
	}
}