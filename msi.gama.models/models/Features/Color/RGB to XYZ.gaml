/**
 *  rgb_to_xyz
 *  Author: Arnaud Grignard
 *  Description: Display a RGB value of a picture in 3D. 
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
