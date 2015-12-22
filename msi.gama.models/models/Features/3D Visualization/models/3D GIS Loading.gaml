model tutorial_gis_city_traffic

global {
	file shape_file_gis_3d_objects <- shape_file('../includes/Mobilier.shp', 0);
	geometry shape <- envelope(shape_file_gis_3d_objects);
	init {
		create gis_3d_object from: shape_file_gis_3d_objects;
	}
}

species gis_3d_object {
	aspect base {
		draw shape at:{world.shape.width/2,world.shape.height/2,0};
	}
}

experiment road_traffic type: gui {

	output {
		display city_display type: opengl ambient_light: 100 draw_env:false{
			species gis_3d_object aspect: base;
		}

	}
}

