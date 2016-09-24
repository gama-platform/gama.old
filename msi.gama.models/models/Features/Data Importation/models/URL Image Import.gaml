/**
* Name: URLImageImport
* Author: Drogoul
* Description: Shows how to load an image from an URL (from the Gama website) and save it locally
* Tags: image, load_file
*/
model URLImageImport


global
{
	string address <- "http://gama-platform.org/assets/gamaws/img/logo_xs_border_117.png";
	image_file image <- image_file(address);
	// We modify a bit the image
	matrix<int> transpose <- transpose(image.contents);
	image_file copy;
	init
	{
		// We create a file with the new contents
		copy <- image_file("../images/local_copy.png", transpose);
		// And save it
		save copy;
	}

}

experiment urlImage
{
	output
	{
		display 'display' background: # black
		{
			image "URL Image" file: image.path size: { 0.5, 0.5 };
			image "Copy" file: copy.path size: { 0.5, 0.5 } position: { 0.5, 0.5 };
		}

	}

}

