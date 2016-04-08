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