/**
 *  Author: Arnaud Grignard
 */
model Graphic_primitive

global{
	file dem parameter: 'DEM' <- file('../includes/DEM-Vulcano/DEM.png');
	file texture parameter: 'Texture' <- file('../includes/DEM-Vulcano/Texture.png');
}

environment width:100 height:100;

experiment display type: gui {
	output {
display Vulcano  type: opengl ambient_light:255 {
	graphics GraphicPrimitive {
		draw dem(dem, texture);
	}
}

display VulcanoDEM  type: opengl ambient_light:255 {
	graphics GraphicPrimitive {
		draw dem(dem, dem);
	}
}
	}
}
