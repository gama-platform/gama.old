/**
* Name: Ants_coupling
* Author: Lô
* Description: This is the coupling that manipulate the elements inside Voronoi  model and propose the function would be used outside.
* Tags: Tag1, Tag2, TagN
*/

model Voronoi_coupling

import "../../../Toy Models/Voronoi/Voronoi.gaml"


experiment Voronoi_coupling_exp type:gui  parent:voronoi{
	
	list<center> getCenter{
		return list(center);
	}
	list<cell> getCell{
		return list(cell);
	}
	
	
	output{
	}
}