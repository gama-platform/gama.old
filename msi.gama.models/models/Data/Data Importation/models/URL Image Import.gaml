/**
* Name: URLImageImport
* Author: Drogoul
* Description: Shows how to load an image from an URL (from the Gama website) and save it locally (the saved version is a shuffled version of the original one)
* Tags: image, load_file
*/
model URLImageImport

global {
	image_file im <- image_file("https://raw.githubusercontent.com/wiki/gama-platform/gama/resources/images/general/GamaPlatform.png");
	geometry shape <- envelope(im);
	// We modify a bit the image 
	matrix<int> shuffle <- shuffle(im.contents); 
	// We create a file with the new contents
	image_file shuffled_copy <- image_file("../images/local_copy.png", shuffle);
	init {
		// And save it
		save shuffled_copy;
	}
}

experiment urlImage {
	output {
		display 'Original' background: #white {
			image im ;
		}
		display 'Shuffled_copy' background: #white {
			image  shuffled_copy;
		}

	}
}

