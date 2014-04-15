model AugmentedGrid

/**
 *  AugmentedGrid
 * 
 *  Author: Arnaud Grignard
 * 
 *  Description: Initialize a grid with a random value between 0 and 255 and display using different aspects.

 */
global {
	int width parameter: "width" min 1 <- 6 category: 'Initialization';
	int height parameter: "height" min 1 <- 6 category: 'Initialization';
	float hue parameter: 'Hue (between 0.0 and 1.0)' min: 0.0 max: 1.0 <- 0.66;
	geometry shape <- rectangle(width, height);
	init {
		ask cell as list {
			color <- hsb(hue, (cellValue / 255), 1.0);
			elevation <- ((cellValue / 100) ^ 2);
		}

	}

}

grid cell width: width height: height neighbours: 4 {
	int cellValue <- rnd(255);
	float elevation;
	reflex changeCellValue {
		cellValue <- rnd(255);
		color <- hsb(hue, (cellValue / 255), 1.0);
		elevation <- ((cellValue / 100) ^ 2);
	}

	aspect base {
		draw shape color: rgb('white');
		draw string(cellValue) size: 0.5 color: rgb('black');
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

experiment AugmentedGrid type: gui {
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
