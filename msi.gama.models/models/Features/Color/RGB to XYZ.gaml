
/**
* Name: RGB color to XYZ position
* Author:  Arnaud Grignard
* Description: A model to show how to convert rgb values in xyz position using the operator rgb_to_xyz. Each pixel of a given image is used to create a point with 
* 	its coordinates depending on its color : red value for x coordinate, green value for y coordinate and blue value for the z coordinate.
* Tags: color, 3d
*/

model rgbCube

global {
	//import an image
	file imageRaster <- file('images/RGB.jpg');
	
	//list of points  create from the image 
	list<point> p;
	
	//geometry of the world (environment)
	geometry shape <- square(255);
	
	//create the list of points from the image: a point is defined per pixel, its coordinate correspond to the value of the red,green,blue color
	init {
		p <- list<point> (rgb_to_xyz(imageRaster));
	}
}


experiment Display type: gui {
	output {
		display RGB_to_XYZ type: opengl { 
			image imageRaster.path refresh: false;
			graphics "pts" refresh: false{
				loop pt over: p {
					draw cube(1) at: pt color: rgb(pt.x, pt.y, pt.z);
				}
			}
		}
	}

}
