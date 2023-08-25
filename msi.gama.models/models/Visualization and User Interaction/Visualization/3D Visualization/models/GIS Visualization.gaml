/**
* Name: Visualization of GIS data
* Author:  Patrick Taillandier
* Description:  this model shows how to visualize GIS data without having to create agents  
* Tags: 3d, shapefile, texture
*/

model GIS_visualization

global {
	file shape_file_buildings <- shape_file("../includes/building.shp");
	geometry shape <- envelope(shape_file_buildings);
	string texture <- "../images/building_texture/texture1.jpg";
	string roof_texture <- "../images/building_texture/roof_top.jpg";	
}

experiment GIS_visualization type: gui {
	float minimum_cycle_duration <- 1#s;

	
	output {
		layout #split parameters: false navigator: false editors: false consoles: false;
		// display of buildings in 3D with texture and with reading their HEIGHT attribute from the shapefile
		display gis_displays_graphics type: 3d  {
			camera 'default' location: {809.5553,1386.2038,705.1688} target: {301.1933,449.9174,0.0};
			graphics "Buildings as shapes" refresh: false {
				loop bd over: shape_file_buildings {
					draw bd depth: rnd(50) + 50 texture:[roof_texture,texture] border:false;
				}
			}
		}
		
		//display of the building as an image
		display gis_displays_image type: 3d {
			image "Buildings as images" gis: shape_file_buildings.path color: rgb("gray") refresh: false;
		}
	}
}
