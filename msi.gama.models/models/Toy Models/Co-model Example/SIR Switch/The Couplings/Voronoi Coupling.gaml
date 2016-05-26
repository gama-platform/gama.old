model voronoi_coupling

import "../../../../Toy Models/Voronoi/Voronoi.gaml"


experiment VoronoiCouplingExperiment type:gui  parent:voronoi{
	
	list<center> get_center{
		return list(center);
	}
	list<cell> get_cell{
		return list(cell);
	}
	
	
	output{
	}
}