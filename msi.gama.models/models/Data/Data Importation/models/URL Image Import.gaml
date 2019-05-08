/**
* Name: URLImageImport
* Author: Drogoul
* Description: Shows how to load an image from an URL (from the Gama website) and save it locally
* Tags: image, load_file
*/
model URLImageImport

global {
	image_file image <- image_file("https://raw.githubusercontent.com/wiki/gama-platform/gama/resources/images/general/GamaPlatform.png");
	geometry shape <- envelope(image);
	// We modify a bit the image 
	matrix<int> shuffle <- shuffle(image.contents);
	// We create a file with the new contents
	image_file copy <- image_file("../images/local_copy.jpg", shuffle);
	init {
		// And save it
		save copy;
	}

}

experiment urlImage {
	output {
		display 'display' background: #white {
			image "Copy" file: copy;
			image "URL Image" file: image ;
		}

	}

}

