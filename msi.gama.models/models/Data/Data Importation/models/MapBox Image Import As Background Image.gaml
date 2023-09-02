/***
* Name: mainroadcells
* Author: minhduc0711
* Description: 
* Tags: Tag1, Tag2, TagN
***/
model main

global {
	string appkey <- "pk.eyJ1IjoiaHFuZ2hpODgiLCJhIjoiY2t0N2w0cGZ6MHRjNTJ2bnJtYm5vcDB0YyJ9.oTjisOggN28UFY8q1hiAug";
	image_file static_map_request;
	string map_center;
	point map_size;

	action load_map {
		float s <- world.shape.height / world.shape.width;
		map_size <- {500, 500 * s};
		string
		request <- "https://api.mapbox.com/styles/v1/mapbox/satellite-v9/static/" + "[" + map_center + "]/" + int(map_size.x) + "x" + int(map_size.y) + "@2x?" + "access_token=" + appkey;
		write "Request : " + request;
		static_map_request <- image_file(request, "JPEG");
	}

	shape_file buildings_shape_file <- shape_file("../includes/buildings.shp");
	geometry shape <- envelope(buildings_shape_file);

	init {
		geometry loc <- (world.shape CRS_transform ("EPSG:4326"));
		map_center <- "" + loc.points[0].x + "," + loc.points[0].y + "," + loc.points[2].x + "," + loc.points[2].y;
		write loc;
		write map_center;
		do load_map;
		create building from: buildings_shape_file;
	}

}

species building {
}

experiment exp {
	output {
		display main type: 3d {
			image static_map_request;
			species building;
		}

	}

}