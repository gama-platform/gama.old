/**
 *  round_rectangle
 *  Author: Arnaud Grignard
 *  Description: Display random rectangle with roundCorner
 */
model Graphic_primitive

global{
	file dem parameter: 'DEM' <- file('includes/DEM/MNT.png');
    file texture parameter: 'DEM' <- file('includes/DEM/Texture.png');
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
