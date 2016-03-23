/**
* Name: ComodelAnts
* Author: hqnghi
* Description: Co-model example : coupling urban growth model with the traffic model.
* Tags: comodel
 */
model comodel_Urban_Traffic
import "Traffic_coupling.gaml" as myTraffic
import "Urban_coupling.gaml" as myUrban


global
{
	geometry shape <- envelope(shape_file("../includes/roads.shp"));
	init
	{
		create myTraffic.Traffic_coupling_exp;
		create myUrban.Urban_coupling_exp;
	}

	reflex dododo
	{
		ask myTraffic.Traffic_coupling_exp collect each.simulation
		{
			do _step_;
		}

		ask myUrban.Urban_coupling_exp collect each.simulation
		{
			if (cycle mod 200 = 0)
			{
				do _step_;
			}

		}

	}

}

experiment comodel_Urban_Traffic_exp type: gui
{
	output
	{
		display "comodel_disp"
		{
			agents "Plotgrid" value: first(myUrban.Urban_coupling_exp).getPlot() size:{0.037,0.052};
			
			agents "building" value: first(myTraffic.Traffic_coupling_exp).getBuilding();
			agents "people" value: first(myTraffic.Traffic_coupling_exp).getPeople();
			agents "road" value: first(myTraffic.Traffic_coupling_exp).getRoad();

		}

	}

}
