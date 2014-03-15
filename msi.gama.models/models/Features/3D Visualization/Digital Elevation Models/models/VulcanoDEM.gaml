/**
 *  Author: Arnaud Grignard
 *  Digital Model Elevation representation of Vulcano.
 */
model Graphic_primitive

global{
	file dem parameter: 'DEM' <- file('../includes/DEM-Vulcano/DEM.png');
	file texture parameter: 'Texture' <- file('../includes/DEM-Vulcano/Texture.png');
}

experiment DEM type: gui {
	output {
		display VulcanoTexturedScaled  type: opengl ambient_light:255 draw_env:false{
			graphics 'GraphicPrimitive' {
				draw dem(dem, texture,0.1);
			}
		}
		
		display VulcanoDEMSacled  type: opengl ambient_light:255 draw_env:false{
			graphics 'GraphicPrimitive' {
				draw dem(dem,0.1);
			}
		}
		
		display VulcanoTextured  type: opengl ambient_light:255 draw_env:false{
			graphics 'GraphicPrimitive' {
				draw dem(dem, texture);
			}
		}
		
		display VulcanoDEM  type: opengl ambient_light:255 draw_env:false{
			graphics 'GraphicPrimitive' {
				draw dem(dem);
			}
		}
	}
}
