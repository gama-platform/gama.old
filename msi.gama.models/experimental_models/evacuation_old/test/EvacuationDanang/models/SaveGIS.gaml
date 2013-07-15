/**
 *  SaveGIS
 *  Author: Admin
 *  Description: 
 */

model SaveGIS

global {
	file shape_file_roadlines  <- file('../includes/danang.eastside.roadlines.shp');
	init{
		let roads_geom type: list of: geometry <- split_lines(geometry(shape_file_roadlines).geometries);
		loop road_geom over:roads_geom {
			create roadline {
				set shape <- road_geom;
				set width <- 10;
			}
		}
		save roadline to: ("danang.eastside.separatedroadlines.shp") type: "shp";
	} 
}

environment {
	/** Insert the grids and the properties of the environment */
}

entities {
	species roadline{
		float width;
		rgb color <- rgb('gray');
		aspect base{
			draw geometry: shape color: color;
		}
	}
}

experiment SaveGIS type: gui {
	/** Insert here the definition of the input and output of the model */
}
