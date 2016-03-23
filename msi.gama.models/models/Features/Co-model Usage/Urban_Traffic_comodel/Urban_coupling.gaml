/**
* Name: AntsIF
* Author: Lô
* Description: This is the coupling that manipulate the elements inside Urban model and propose the function would be used outside.
* Tags: Tag1, Tag2, TagN
*/
model Urban_coupling

import "../../../Toy Models/Urban Growth/models/raster model.gaml"
experiment Urban_coupling_exp type: gui parent: raster
{
	list<plot> getPlot
	{
		return list(plot);
	}

}