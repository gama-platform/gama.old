/**
 *  Author: Arnaud Grignard
 */
model Graphic_primitive

global{
	file dem parameter: 'DEM' <- file('includes/DEM/DEM.png');
    file texture parameter: 'Texture' <- file('includes/DEM/Texture.png');
    geometry shape <- square(500);
}

experiment display type: gui {
	output {
		display DEM  type: opengl ambient_light:255 {
			graphics GraphicPrimitive {
				draw dem(dem, texture);
			}
		}
	}
}
