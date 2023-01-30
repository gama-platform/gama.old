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
	file dem_file parameter: 'DEM' <- file('../includes/DEM-Vulcano/DEM.png');
	image_file europe <- image_file("../images/mnt/europe.jpg");
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

grid cell file: grid_data;

experiment Comparison type: gui {
	output {
		layout #split;

		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color cells (if defined otherwise in black)
 		display "Grid with triangles" type: 3d {
			grid cell elevation: true triangulation: true;
		}

		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color of cells as a gray value corresponding to grid_value / maxZ *255
		display "Grid with triangles and grayscale" type: 3d {
			grid cell elevation: true grayscale: true triangulation: true;
		}

		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display "Grid with triangles and texture" type: 3d {
			grid cell texture: texture triangulation: true elevation: true;
		}

		//Display the field triangulated in 3D with the cell altitude corresponding to its value and the color defined (otherwise in default color)
 		display "Field with triangles, green color" type: 3d {
			mesh grid_data triangulation: true color: #green;
		}

		//Display the field triangulated in 3D with the cell altitude corresponding to its value and the color of cells as a gray value corresponding to grid_value / maxZ *255
 		display "Field with triangles and grayscale" type: 3d {
			mesh grid_data grayscale: true triangulation: true;
		}

		//Display the textured field in 3D with the cell altitude corresponding to its value.				
 		display "Field scaled by 2, with triangles and texture" type: 3d {
			mesh grid_data texture: texture triangulation: true scale: 2.0;
		}

	}

}

experiment "Grids" type: gui {
	output {
		layout #split toolbars: false;

		//Display the grid on a plan with cell color (if defined otherwise in black)
 		display grid type: 3d { //Same as in 2d
 			grid cell border: #black;
		}

		//Display the grid in 3D with the cell altitude corresponding to its grid_value and the color cells (if defined otherwise in black)
 		display gridWithElevation type: 3d {
			grid cell elevation: true;
		}

		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color cells (if defined otherwise in black)
 		display gridWithElevationTriangulated type: 3d {
			grid cell elevation: true triangulation: true;
		}

		//Display the grid in 3D with the cell altitude corresponding to its grid_value and the color of cells as a gray value corresponding to grid_value / maxZ *255
 		display gridGrayScaled type: 3d {
			grid cell elevation: true grayscale: true;
		}
		//Display the grid triangulated in 3D with the cell altitude corresponding to its grid_value and the color of cells as a gray value corresponding to grid_value / maxZ *255
		display gridGrayScaledTriangulated type: 3d {
			grid cell elevation: true grayscale: true triangulation: true;
		}

		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display gridTextured type: 3d {
			grid cell texture: texture text: false triangulation: false elevation: true;
		}

		//Display the textured triangulated grid in 3D with the cell altitude corresponding to its grid_value.
 		display gridTexturedTriangulated type: 3d {
			grid cell texture: texture text: false triangulation: true elevation: true;
		}

		display gridWithText type: 3d {
			grid cell text: true elevation: true wireframe: true refresh: false;
		}

	}

}

experiment "Meshes" type: gui {
	output {
		layout #split toolbars: false;
		display "Large file, rectangles, wireframe and scaled" type: 3d axes: false {
			camera "default" location: {100.0, 269.7056, 169.7056} target: {100.0, 100.0, 0.0};
			mesh europe wireframe: true border: #green refresh: false size: {1, 1, 0.2};
		}

		display "Large file, triangles, wireframe and scaled" type: 3d axes: false {
			camera "default" location: {100.0, 269.7056, 169.7056} target: {100.0, 100.0, 0.0};
			mesh europe grayscale: true triangulation: true smooth: true refresh: false size: {1, 1, 0.2};
		}

		display "Large file, triangles, smooth, wireframe and scaled" type: 3d axes: false {
			camera "default" location: {100.0, 269.7056, 169.7056} target: {100.0, 100.0, 0.0};
			mesh europe wireframe: true triangulation: true border: #green refresh: false size: {1, 1, 0.2} smooth: true;
		}

		display "Triangles, grayscale, lines, colored and scaled" type: 3d axes: false {
			camera "default" location: {100.0, 269.7056, 169.7056} target: {100.0, 100.0, 0.0};
			mesh grid_data size: {1, 1, 0.75} triangulation: true border: #yellow color: #violet;
		}

		display "Triangles, textured, no scale" type: 3d axes: false {
			camera "default" location: {100.0, 269.7056, 169.7056} target: {100.0, 100.0, 0.0};
			mesh grid_data texture: texture triangulation: true;
		}

		display "Triangles, textured and scaled" type: 3d axes: false {
			camera "default" location: {100.0, 269.7056, 169.7056} target: {100.0, 100.0, 0.0};
			mesh grid_data texture: texture triangulation: true scale: 0.75;
		}

		display "Triangles, textured, smooth and scaled" type: 3d axes: false {
			camera "default" location: {100.0, 269.7056, 169.7056} target: {100.0, 100.0, 0.0};
			mesh grid_data texture: texture smooth: true triangulation: true size: {1, 1, 0.75};
		}

		display "Triangles, textured, scaled, with labels" type: 3d axes: false {
			camera "default" location: {100.0, 269.7056, 169.7056} target: {100.0, 100.0, 0.0};
			mesh grid_data texture: texture triangulation: true size: {1, 1, 0.5} text: true;
		}

		display "Large file, trianges, grayscale, scaled" type: 3d axes: false {
			camera 'default' location: {104.7273,233.3361,685.4581} target: {100.135,-29.7603,0.0};
			mesh dem_file grayscale: true triangulation: true scale: 2.0;
		}

	}

}
