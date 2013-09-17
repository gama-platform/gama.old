/**
 *  Author: Arnaud Grignard
 *  Digital Model Elevation representation of Vulcano.
 */
model Graphic_primitive

global{
	file dem parameter: 'DEM' <- file('../includes/DEM-Vulcano/DEM.png');
	file texture parameter: 'Texture' <- file('../includes/DEM-Vulcano/Texture.png');
	geometry shape <- rectangle(100,100);
}

experiment DEM type: gui {
	output {
		display VulcanoTextured  type: opengl ambient_light:255 draw_env:false{
			graphics 'GraphicPrimitive' {
				draw dem(dem, texture,0.1);
			}
		}
		
		display VulcanoDEM  type: opengl ambient_light:255 draw_env:false{
			graphics 'GraphicPrimitive' {
				draw dem(dem, dem,0.1);
			}
		}
	}
}
