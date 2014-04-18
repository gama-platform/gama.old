/**
 *  rgb_to_xyz
 *  Author: Arnaud Grignard
 *  Description: Display a RGB value of a picture in 3D. 
 */
model rgbCube

global {
	file imageRaster <- file('images/RGB.jpg');
	list<point> p;
	geometry shape <- square(255);
	init {
		create myRGBCube {
			p <- list<point> (rgb_to_xyz(imageRaster));
			shape <- square(255);
			location <- { 0, 0, 0 };
		}

	}

}

species myRGBCube {
	aspect rgb_to_xyz {
		loop pp over: p {
			draw circle(1) at: pp color: rgb(pp.x, pp.y, pp.z);
		}

	}

}

experiment Display type: gui {
	output {
		display RGB_to_XYZ type: opengl { species myRGBCube aspect: rgb_to_xyz;
		image imageRaster.path;
		}
	}

}
