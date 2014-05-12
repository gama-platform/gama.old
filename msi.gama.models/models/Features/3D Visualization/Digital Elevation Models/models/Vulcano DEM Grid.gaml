/**
 *  gridloading
 * 
 *  Author: Arnaud Grignard
 * 
 *  Description: This create a DEM representation from an .asc file
 *  Cells are created with the parameter file:grid_file that will initialize the z value of each cell
 *  
 */
model gridloading

global {
	file grid_file <- file("../includes/DEM-Vulcano/vulcano_50.asc");
	file dem parameter: 'DEM' <- file('../includes/DEM-Vulcano/DEM.png');
	file texture parameter: 'Texture' <- file('../includes/DEM-Vulcano/Texture.png');
	map colors <- map([1::rgb([178, 180, 176]), 2::rgb([246, 111, 0]), 3::rgb([107, 0, 0]), 4::rgb([249, 0, 255]), 5::rgb([144, 96, 22]), 6::rgb([255, 255, 86]), 7::rgb([19, 114, 38]), 8::rgb("black"), 9::rgb([107, 94, 255]), 10::rgb([43, 255, 255])]);
	geometry shape <- envelope(grid_file);
	
	init {
		create people number: 100 {
			float z <- (cell(location)).grid_value;
			location <- { location.x, location.y, z };
		}
	}
}

grid cell file: grid_file {
	init {
		color <- colors at int(grid_value);
	}

	reflex decreaseValue {
		grid_value <- grid_value - 0.01;
	}
}

species people skills: [moving] {
	rgb color;
	reflex move {
		do wander;
		float z <- (cell(location)).grid_value;
		location <- { location.x, location.y, z };
	}

	aspect base {
		int heading1 <- rnd(360);
		float hue <- heading1 / 360;
		color <- hsb(hue, 1.0, 1.0);
		draw triangle(1) size: 1 rotate: heading1 color: color border: color depth: 0.5;
	}

}

experiment DEM type: gui {
	geometry shape <- envelope(grid_file);
	output {

		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color cells (if defined otherwise in black)
		display gridWithElevationTriangulated type: opengl ambient_light: 100 { 
			grid cell elevation: true triangulation: true;
			species people aspect: base;
		}

		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color of cells as a gray value corresponding to grid_value / maxZ *255
		display gridGrayScaledTriangulated type: opengl ambient_light: 100 { 
			grid cell elevation: true grayscale: true triangulation: true;
			species people aspect: base;
		}

		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display gridTextured type: opengl ambient_light: 100 { 
			grid cell texture: texture text: false triangulation: true elevation: true;
			species people aspect: base;
		}
	}

}

experiment GridDEMComplete type: gui {
	geometry shape <- envelope(grid_file);
	output {

	//Display the grid on a plan with cell color (if defined otherwise in black)
		display grid type: opengl ambient_light: 100 { //Same as in java2D
			grid cell;
			species people aspect: base;
		}

		//Display the grid in 3D with the cell altitude corresponding to its grid_value and the color cells (if defined otherwise in black)
		display gridWithElevation type: opengl ambient_light: 100 { 
			grid cell elevation: true;
			species people aspect: base;
		}

		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color cells (if defined otherwise in black)
		display gridWithElevationTriangulated type: opengl ambient_light: 100 { 
			grid cell elevation: true triangulation: true;
			species people aspect: base;
		}

		//Display the grid in 3D with the cell altitude corresponding to its grid_value and the color of cells as a gray value corresponding to grid_value / maxZ *255
		display gridGrayScaled type: opengl ambient_light: 100 { 
			grid cell elevation: true grayscale: true;
			species people aspect: base;
		}
		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color of cells as a gray value corresponding to grid_value / maxZ *255
		display gridGrayScaledTriangulated type: opengl ambient_light: 100 { 
			grid cell elevation: true grayscale: true triangulation: true;
			species people aspect: base;
		}

		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display gridTextured type: opengl ambient_light: 100 { 
			grid cell texture: texture text: false triangulation: false elevation: true;
			species people aspect: base;
		}

		//Display the textured triangulated grid in 3D with the cell altitude corresponding to its grid_value.
		display gridTexturedTriangulated type: opengl ambient_light: 100 { 
			grid cell texture: texture text: false triangulation: true elevation: true;
			species people aspect: base;
		}
		display gridWithText type: opengl { 
			grid cell text: true elevation: true grayscale: true;
		}
	}

}

experiment GraphicDEMComplete type: gui {
	output {
		display VulcanoTexturedScaled type: opengl ambient_light: 255 draw_env: false { 
			graphics 'GraphicPrimitive' {
				draw dem(dem, texture, 0.1);
			}
		} 
		display VulcanoDEMSacled type: opengl ambient_light: 255 draw_env: false { 
			graphics 'GraphicPrimitive' {
				draw dem(dem, 0.1);
			}
		} 
		display VulcanoTextured type: opengl ambient_light: 255 draw_env: false { 
			graphics 'GraphicPrimitive' {
				draw dem(dem, texture);
			}
		} 
		display VulcanoDEM type: opengl ambient_light: 255 draw_env: false { 
			graphics 'GraphicPrimitive' {
				draw dem(dem);
			}
		}
	}
}
