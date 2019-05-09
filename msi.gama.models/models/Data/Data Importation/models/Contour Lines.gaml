/**
* Name: Contour Lines Import
* Author: Patrick Taillandier
* Description: Model which imports a shapefile of contour lines, build triangles from these contour lines, compute their elevation by using 
* 	the elevation attribute of the contour lines which had been linked to the elevation column of the shapefile. 
* Tags:  load_file, gis, shapefile
*/


model contour_lines_import

global {
	//the contour lines shapefile
	file shape_file_cl <- file('../includes/contourLines.shp') ;
	
	//define the size of the world from the countour line shapefile
	geometry shape <- envelope(shape_file_cl);
	
	init {
		//create the contour line agents from the shapefile, and init the elevation for each agent
		create contour_line from: shape_file_cl with: [elevation:: float(read("ELEVATION"))];
		
		//triangulate the contour lines
		list<geometry> triangles  <- triangulate (list(contour_line));
		
		//for each triangle geometry, create a triangle_ag agent and compute the elevation of each of its points (and modified their z value)
		loop tr over: triangles {
			create triangle_ag {
				shape <- tr;
				loop i from: 0 to: length(shape.points) - 1{ 
					float val <- (contour_line closest_to (shape.points at i)).elevation;
					shape <- shape set_z (i,val);
				}
			}
		}	
	}
}

species contour_line {
	float elevation;
	aspect default {
		draw shape + 5.0 color: #red depth: 10 at: {location.x,location.y, elevation}; 
	}
}
species triangle_ag {
	aspect default {
		draw shape color: #grey ; 
	}
}


experiment contour_lines_import type: gui {
	output {
		display map type: opengl {
			species triangle_ag refresh: false;
			species contour_line refresh: false;
		}
	}
}
