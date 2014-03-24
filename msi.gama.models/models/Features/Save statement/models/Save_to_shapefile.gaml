/**
 *  Savetoshapefile
 *  Author: Patrick Taillandier
 *  Description: 
 */

model Savetoshapefile

global {
	init {
		geometry free_space <- copy(shape);
		create building number: 50 {
			shape <- square(5.0);
			location <- any_location_in (free_space - 5.0);
			free_space <- free_space - shape;
		}
		//save building geometry into the shapefile: add the attribute TYPE which value is set by the type variable of the building agent
		save building to:"../results/buildings.shp" type:"shp" with:[type::"TYPE"];
	}
}

species building {
	string type <- flip(0.8) ? "residential" : "industrial";
	aspect default {
		draw shape color: type = "residential" ? °gray : °pink;
	}
}
experiment Savetoshapefile type: gui {
	output {
		display map {
			species building;
		}
	}
}
