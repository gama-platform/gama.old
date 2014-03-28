/**
 *  Visualization of GIS data
 *  Author: Patrick Taillandier
 *  Description: this model shows how to visualize GIS data without having to create agents  
 */

model GIS_visualization

global {
	file shape_file_buildings <- shape_file("../includes/buildings_simple.shp");
	geometry shape <- envelope(shape_file_buildings);
	string texture <- "../images/building_texture/texture1.jpg";
	string roof_texture <- "../images/building_texture/roof_top.png";	
}

experiment GIS_visualization type: gui {
	output {
		// display of buildings in 3D with texture and with reading their HEIGHT attribute from the shapefile
		display gis_displays_graphics type: opengl light: true ambient_light: 50{
			graphics "buildings" refresh: false {
				loop bd over: shape_file_buildings {
					draw bd depth: float(geometry(bd) get "HEIGHT") texture:[roof_texture,texture] ;
				}
			}
		}
		
		//display of the building as an image
		display gis_displays_image type: opengl {
			image "shape buildings" gis: shape_file_buildings.path color: rgb("gray") refresh: false;
		}
	}
}
