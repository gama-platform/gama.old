/**
* Name: ASCII File to DEM Representation
* Author: Arnaud Grignard
* Description: Model to show how to import a ASCII File to make a DEM Representation and apply a Texture on it. In this 
* 	model, three experiments are presented : DEM to show the grid elevation using the ASCII File as data for the height of the 
* 	cells, and showing different 3D displays. GridDEMComplete shows more displays with the three of the previous experiment, the grid 
* 	of the cells in a 2D Display, with the Elevation but without triangulation, and the grid with text values to show the content of the 
* 	ASCII used by the cells. GraphicDEMComplete shows the use of the z_factor to amplify or reduces the difference between the z values 
* 	of a Dem geometry.
* Tags:  load_file, gis, 3d, dem
*/
model gridloading

global {
	file grid_data <- file("../includes/DEM-Vulcano/vulcano_50.asc");
	file dem parameter: 'DEM' <- file('../includes/DEM-Vulcano/DEM.png');
	file texture parameter: 'Texture' <- file('../includes/DEM-Vulcano/Texture.jpg');
	geometry shape <- envelope(200);

	init {
		ask cell {
			float r;
			float g;
			float b;
			if (grid_value < 20) {
				r <- 76 + (26 * (grid_value - 7) / 13);
				g <- 153 - (51 * (grid_value - 7) / 13);
				b <- 0.0;
			} else {
				r <- 102 + (122 * (grid_value - 20) / 19);
				g <- 51 + (173 * (grid_value - 20) / 19);
				b <- 224 * (grid_value - 20) / 19;
			}

			self.color <- rgb(r, g, b);
		}

	}

}

grid cell file: grid_data {
	rgb color;

	reflex decreaseValue {
		grid_value <- grid_value + rnd(0.2) - 0.1;
	}

}

experiment DEM type: gui {
	output {
		layout #split;

		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color cells (if defined otherwise in black)
		display gridWithElevationTriangulated type: opengl {
			grid cell elevation: true triangulation: true;
		}

		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color of cells as a gray value corresponding to grid_value / maxZ *255
		display gridGrayScaledTriangulated type: opengl {
			grid cell elevation: true grayscale: true triangulation: true;
		}

		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display gridTextured type: opengl {
			grid cell texture: texture text: false triangulation: true elevation: true;
		}

	}

}

experiment GridDEMComplete type: gui {
	output {
		layout #split toolbars: false;

		//Display the grid on a plan with cell color (if defined otherwise in black)
		display grid type: opengl { //Same as in java2D
			grid cell lines:#black ;
		}

		//Display the grid in 3D with the cell altitude corresponding to its grid_value and the color cells (if defined otherwise in black)
		display gridWithElevation type: opengl {
			grid cell elevation: true;
		}

		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color cells (if defined otherwise in black)
		display gridWithElevationTriangulated type: opengl {
			grid cell elevation: true triangulation: true;
		}

		//Display the grid in 3D with the cell altitude corresponding to its grid_value and the color of cells as a gray value corresponding to grid_value / maxZ *255
		display gridGrayScaled type: opengl {
			grid cell elevation: true grayscale: true;
		}
		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color of cells as a gray value corresponding to grid_value / maxZ *255
		display gridGrayScaledTriangulated type: opengl {
			grid cell elevation: true grayscale: true triangulation: true;
		}

		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display gridTextured type: opengl {
			grid cell texture: texture text: false triangulation: false elevation: true;
		}

		//Display the textured triangulated grid in 3D with the cell altitude corresponding to its grid_value.
		display gridTexturedTriangulated type: opengl {
			grid cell texture: texture text: false triangulation: true elevation: true;
		}

		display gridWithText type: opengl {
			grid cell text: true elevation: true grayscale: true;
		}

	}

}

experiment GraphicDEMComplete type: gui {

	output {
		layout #split toolbars: false;
		display VulcanoTexturedScaled type: opengl draw_env: false {
			graphics 'GraphicPrimitive' {
				draw dem(dem, texture, 0.1);
			}

		}

		display VulcanoDEMScaled type: opengl draw_env: false {
			graphics 'GraphicPrimitive' {
				draw dem(dem, 0.1);
			}

		}

		display VulcanoTextured type: opengl draw_env: false {
			graphics 'GraphicPrimitive' {
				draw dem(dem, texture);
			}

		}

		display VulcanoDEM type: opengl draw_env: false {
			graphics 'GraphicPrimitive' {
				draw dem(dem);
			}

		}

	}

}
