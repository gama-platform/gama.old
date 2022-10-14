/**
* Name: 3D shapefile loading
* Author:  
* Description: Model which shows how to create a shape using a 3D Shapefile after this one has been loaded. 
* Tags: 3d, shapefile, load_file
*/
model shapefile_loading

global {
	 
	//file variable that will store the shape file : the "true" argument allows to specify that we want to take into account the 3D dimension of the data
	file shape_file_gis_3d_objects <- shape_file('../includes/Mobilier.shp', true);
	geometry shape <- envelope(shape_file_gis_3d_objects);
	init {
		create gis_3d_object from: shape_file_gis_3d_objects with:[location::location];
	}
}

species gis_3d_object {
	aspect base {
		draw shape  color: #gray border: #darkgray width: 4;
	}
}

experiment display_shape type: gui {

	output {
		display city_display type: 3d axes:false background: #black{
			species gis_3d_object aspect: base;
		}

	}
}

