model Urban_coupling

import "../../../Toy Models/Urban Growth/models/raster model.gaml"
experiment Urban_coupling_exp type: gui parent: raster
{
	list<plot> getPlot
	{
		return list(plot);
	}

}