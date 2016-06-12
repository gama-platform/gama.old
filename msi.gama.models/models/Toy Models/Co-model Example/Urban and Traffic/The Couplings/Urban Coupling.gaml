/**
* Name: urban_coupling
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Urban model. It is used in the "Urban and Traffic" as an interface. 
* Tags: comodel
*/
model urban_coupling

import "../../../../Toy Models/Urban Growth/models/raster model.gaml"
experiment "Coupling Experiment" type: gui 
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