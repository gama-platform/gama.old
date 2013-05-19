/**
 *  Author: Arnaud Grignard
 */
model Graphic_primitive

global{
	file dem parameter: 'DEM' <- file(project_path + 'DEM/includes/DEM-Vulcano/DEM.png');
    file texture parameter: 'Texture' <- file(project_path + 'DEM/includes/DEM-Vulcano/Texture.png');
}

environment width:500 height:395;

experiment display type: gui {
	output {
		display Poincare  type: opengl ambient_light:255 {
			graphics GraphicPrimitive {
				draw dem(dem, texture);
			}
		}
	}
}
