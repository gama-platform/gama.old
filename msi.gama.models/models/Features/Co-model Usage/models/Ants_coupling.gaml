/**
* Name: Ants_coupling
* Author: Lô
* Description: This is the coupling that manipulate the elements inside Ants  model and propose the function would be used outside.
* Tags: Tag1, Tag2, TagN
*/

model Ants_coupling

import "../../../Toy Models/Ants (Foraging and Sorting)/models/Ant Foraging (Classic).gaml" 



experiment Ants_coupling_exp type:gui  parent:Ant{
	
	list<ant> getAnts{
		return list(ant);
	}
	
	list<ant_grid> getAnt_grid{
		return list(ant_grid);
	}
	
//	output{
//	
//	}
}