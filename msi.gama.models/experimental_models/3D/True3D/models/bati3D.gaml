/**
 *  bati3D
 *  Author: patricktaillandier
 *  Description: 
 */

model bati3D

global {
	//file shape_file_cn <- file('../includes/cn.shp') parameter: 'Courbe de niveau Shapefile :' category: 'GIS' ;
	file shape_file_bati <- file('../includes/CityGMLSHP/Batiments.shp') parameter: 'Batiments Shapefile :' category: 'GIS' ;
	init {
		/*create cn from: shape_file_cn with: [altitudeStr:: read("ALTITUDE")]; 
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
		}*/
		create bati from: shape_file_bati;
		
	}
}

environment bounds: shape_file_bati{
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
	species bati {
		aspect base {
			draw geometry: shape color: rgb("gray") ; 
		}
	} 
	
}

experiment bati3D type: gui {
	output {
		display bati type: opengl refresh_every: 1 {
			species bati aspect: base; 
			//species triangle aspect: base;
			//species cn aspect: base;
			
		}
	}
}
