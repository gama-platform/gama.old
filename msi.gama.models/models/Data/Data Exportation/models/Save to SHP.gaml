/**
* Name: Save to Shapefile
* Author: Patrick Taillandier
* Description: This is a model that shows how to save agents inside a Shapefile to reuse it later or to keep it.
* Tags: save_file, shapefile
*/

model Savetoshapefile

global {
	init {
		geometry free_space <- copy(shape);
		
		//creation of the building agents that will be saved
		create building number: 50 {
			shape <- square(5.0);
			location <- any_location_in (free_space - 5.0);
			free_space <- free_space - shape;
		}
		//save building geometry into the shapefile: add the attribute TYPE which value is set by the type variable of the building agent and the attribute ID 
		save building to:"../results/buildings.shp" format:"shp" attributes: ["ID":: int(self), "TYPE"::type]; 
	}
}
  
//species that represent the building agents that will be saved
species building {
	string type <- flip(0.8) ? "residential" : "industrial";
	aspect default {
		draw shape color: type = "residential" ? #gray : #pink;
	}
}
experiment main type: gui {
	output {
		display map {
			species building;
		}
	}
}
