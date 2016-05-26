model urban_coupling

import "../../../../Toy Models/Urban Growth/models/raster model.gaml"
experiment raster type: gui 
{
	action transform{
		ask plot{
			location<-{location.x*0.037 , location.y*0.053};
		}
	}
	list<plot> get_plot
	{
		return list(plot);
	}

}