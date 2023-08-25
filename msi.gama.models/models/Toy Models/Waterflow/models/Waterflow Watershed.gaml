/***
* Name: Water flow with watershed
* Author: Benoit Gaudou
* Description: A model inspired by the SWAT model to make flow the water in a water basin using sub watershed.
* 	The water flows from the upstream watersheds to the downstream watershed. The water basin gets water only from the rain.
* 	To better vizualise the water flow, rain is uniform on the basin and occurs only every 20 steps.
* 	During the other steps, there is no water input in the basin.
* Tags: shapefile, gis, gui, hydrology, water flow
***/


model Waterflowwatershed

global {	
	file watershed_shape_file <- shape_file("../includes/ZH2.shp");
	geometry shape <- envelope(watershed_shape_file);

	float rain <- rnd(10.0) update: every(20#cycle) ? rnd(10.0) : 0.0;
	
	init {
		create watershed from: watershed_shape_file with: [id_watershed::int(read("ID_ZH")), id_watershed_outlet::int(read("ID_ND_EXUT")),order::int(read("order"))];
		
		ask watershed {
			do init_watershed;
			write "" + int(self) + " " + length(shape.points) + " points";
		}
	}
	
	reflex water_floaw {
		ask reverse(watershed sort_by(each.order)) {
			do model_hydro;
		}
	}
}

species watershed schedules: [] {
	int id_watershed;
	int id_watershed_outlet;
	int order;
	
	list<watershed> watershed_upstream;

	float volume_watershed ;

	action init_watershed {
		// Find ZH in the upstream 
		watershed_upstream <- watershed where(each.id_watershed_outlet = id_watershed);
	}
		
	action model_hydro {	
		volume_watershed <- 0.7 * rain * self.shape.area  + (watershed_upstream sum_of(each.volume_watershed));	
	}	

	aspect blueFlow {
		draw shape border: #white color:rgb(0,0,255*volume_watershed/100000000);
	}
}

experiment waterFlow type: gui {
	output {
	 	display "My display Abs" type:2d{ 
			species watershed aspect: blueFlow;
		}
	}
}