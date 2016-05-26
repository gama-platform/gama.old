model urban_coupling

import "../../../../Toy Models/Urban Growth/models/raster model.gaml"
experiment raster type: gui 
{
	list<plot> get_plot
	{
		return list(plot);
	}

}