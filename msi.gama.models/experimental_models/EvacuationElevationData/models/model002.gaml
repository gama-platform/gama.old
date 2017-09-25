/**
* Name: model001
* Author: gama
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model model002

global {
	file my_shapefile_file <- file("../includes/02_MienTrung_bounds.shp");
	file my_csv_file <- csv_file("../includes/02_MienTrung.xyz","\t");
	geometry shape <- envelope(my_shapefile_file);
	init{
		create bounderies from: my_shapefile_file ;
		matrix data <- matrix(my_csv_file);
		create mypoint from: my_csv_file with: [location::({float(get("0")),float(get("1")), float(get("2"))})]{
			set z <- location.z;
		}
		save mypoint to: "../includes/02_MienTrung_points.shp" type: "shp" with: [z::"z"];
	}
}

species bounderies{
	aspect base{
		draw shape color:color;
	}
}
species mypoint {
	float z;
	aspect base {
		int current_value <- int(location.z);
		if (location.z <= 0){
			current_value <- -current_value;
			current_value <- current_value mod 256;
			color <- rgb(66, current_value, 244);
			//draw shape color: color ;
		}
		else{
			current_value <- current_value mod 256;
			color <- rgb(244, current_value, 66);
			
		}
		draw shape color: color ;
	}
}

experiment model002 type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
		display city_display type:opengl {
			species mypoint aspect: base ;
			//species bounderies aspect: base ;
		}
	}
}
