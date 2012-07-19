/**
 *  courbeniveau
 *  Author: patricktaillandier
 *  Description: 
 */

model courbeniveau

global {
	file shape_file_cn <- file('../includes/cn.shp') parameter: 'Courbe de niveau Shapefile :' category: 'GIS' ;
	init {
		create cn from: shape_file_cn with: [altitudeStr:: read("ALTITUDE")];
		ask cn as list {
			set shape <- shape simplification 10.0; 
			set altitude <- float(altitudeStr);
		}
		ask cn as list {
			if (flip(0.0)) {do die;} 
			
		}
		let triangles type: list of: geometry <- list(triangulate (cn as list));
		loop tr over: triangles {
			create triangle {
				set shape <- tr;
				loop i from: 0 to: length(shape.points) - 1{ 
					let val type: float <- ((cn as list) closest_to (shape.points at i)).altitude;
					set shape <- shape add_z_pt {i,val};
				}
			}
		}
		
	}
}

environment bounds: shape_file_cn{
	/** Insert the grids and the properties of the environment */
}

entities {
	species cn {
		string altitudeStr;
		float altitude;
		aspect base {
			draw geometry: shape color: rgb("gray") ; 
		}
	}
	species triangle {
		aspect base {
			draw geometry: shape color: rgb("green") ; 
		}
	}
}

experiment courbeniveau type: gui {
	output {
		display cn type: opengl refresh_every: 1 {
			species triangle aspect: base;
			//species cn aspect: base;
			
		}
	}
}
