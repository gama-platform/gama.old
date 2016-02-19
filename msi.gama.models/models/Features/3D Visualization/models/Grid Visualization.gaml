/**
* Name: 3D Display model of differents shapes and a special Object
* Author: Arnaud Grignard
* Description: Model presenting a 3D display of different aspects of the same grid cells in the same experiment but different displays
* Tags: 3D Display, Grid
*/

model GridVisualization

global {
	//size of the grid
	int width parameter: "width" min: 1 <- 6 category: 'Initialization';
	int height parameter: "height" min: 1 <- 6 category: 'Initialization';
	
	//hue parameter for the hsb colors
	float hue parameter: 'Hue (between 0.0 and 1.0)' min: 0.0 max: 1.0 <- 0.66;
	
	//definition of the size of the world
	geometry shape <- rectangle(width, height);
}

grid cell width: width height: height  {
	
	//definition of a random value for the cell between 0 and 255
	int cellValue <- rnd(255);
	
	//definition of the color of the cell from cellvalue
	rgb color <- hsb(hue, (cellValue / 255), 1.0);
	
	//definition of the evelation from cellvalue
	float elevation <- ((cellValue / 100) ^ 2);
	
	aspect base {
		draw shape color: #white;
		draw string(cellValue) size: 0.5 color: #black;
	}

	aspect colored {
		draw shape color: color;
	}

	aspect square {
		draw shape color: color border: color;
	}

	aspect box {
		draw shape color: color depth: elevation border: color;
	}

	aspect hsbElevation {
		draw shape color:  hsb ((cellValue / 255), 1.0, 1.0) depth: elevation border:  hsb ((cellValue / 255), 1.0, 1.0);
	}

	aspect circle {
		draw circle(cellValue / (255 * 2)) color: color border: color;
	}

	aspect sphere {
		draw sphere(cellValue / (255 * 2)) color: color border: color;
	}

	aspect cylinder {
		draw circle(cellValue / (255 * 2)) color: color border: color depth: elevation;
	}

}

experiment visualization type: gui {
	output {
		display Circle type: opengl{
			species cell aspect: circle;
		}

		display Cylinder type: opengl{
			species cell aspect: cylinder;
		}

		display Sphere type: opengl{
			species cell aspect: sphere;
		}

		display Square type: opengl{
			species cell aspect: square;
		}

		display Box type: opengl{
			species cell aspect: box;
		}

		display hsb type: opengl{
			species cell aspect: hsbElevation;
		}
	}
}
