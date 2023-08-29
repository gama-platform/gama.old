/**
* Name: DeclaringImages
* Shows how to declare images 
* Author: A. Drogoul
* Tags: image
*/
model DeclaringImages

global {

	// a blank image
	image blank <- image(100, 100);
	// or with a point...
	point size <- {100,100};
	image blank2 <- image(size);

	// a blank image with transparency
	image blank_transparent <- image(100, 100, true);

	// a blank image with transparency, filled with a color
	image colored <- image(100, 100, #red);

	// an image read from the clipboard (nil if none is present)
	image clip <- copy_from_clipboard(image);


	// an image read from a file on disk
	image on_disk <- image("../includes/Kandinsky.jpeg");
	// a shortcut equivalent to:
	image on_disk2 <- image(file("../includes/Kandinsky.jpeg"));
	// and of course, any image can be sent to the clipboard as well
	bool copied <- copy_to_clipboard(on_disk);
	
	// an image built from a field
	field terrain <- generate_terrain(10,100,100,0.5,0.5,0.5);
	image from_terrain <- image(terrain);
	
	//an image built from a matrix<int>
	matrix<int> mat <- {100,100} matrix_with int(rnd_color(255));
	image from_matrix <- image(mat);
	
	//an image built out of a grid
	grid cells width: 100 height: 100 {
		rgb color <- rnd_color(255);
	}
	image from_grid <- image(cells);
}




experiment name type: gui {

	output {
		display Images type:2d{
			image from_terrain;
		}
	}
}