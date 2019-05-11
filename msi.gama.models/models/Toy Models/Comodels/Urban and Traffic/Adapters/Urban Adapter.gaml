/**
* Name: urban_adapter
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Urban model. It is used in the "Urban and Traffic" as an interface. 
* Tags: comodel
*/
model urban_adapter

import "../../../../Toy Models/Urban Growth/models/Raster Urban Growth.gaml"
experiment "Adapter" type: gui 
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