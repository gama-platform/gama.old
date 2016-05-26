model voronoi_coupling

import "../../../../Toy Models/Voronoi/Voronoi.gaml"


experiment voronoi type:gui  {
	
	list<center> get_center{
		return list(center);
	}
	list<cell> get_cell{
		return list(cell);
	}
	
	
	output{
	}
}