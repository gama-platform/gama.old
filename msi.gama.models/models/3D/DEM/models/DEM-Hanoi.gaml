/**
 *  Author: Arnaud Grignard
 */
model Graphic_primitive

global{
	file dem parameter: 'DEM' <- file('../includes/MNT-Hanoi/MNT.png');
    file texture parameter: 'Texture' <- file('../includes/MNT-Hanoi/photo-hanoi-bing.png');
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
