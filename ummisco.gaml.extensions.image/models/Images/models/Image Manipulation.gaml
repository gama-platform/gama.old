/**
* Name: ImageManipulation
* A model to demonstrate some tools to produce, use and save images 
* Author: A. Drogoul
* Tags: image
*/
model ImageManipulation

global {
	image im1;
	int x <- 0;
	int y <- 0;
	int row <- 5 * im1.width;
	int cols <- 5 * (im1.height + 50);
	geometry shape <- rectangle(row, cols);

	init {
		do create_agent(im1, "Original");
		do create_agent(horizontal_flip(im1), "Horizontal flip");
		do create_agent(vertical_flip(im1), "Vertical flip");
		do create_agent((im1 rotated_by 180), "Rotation 180°");
		do create_agent(blend(im1, horizontal_flip(im1), 0.5), "Blend with flip");
		do create_agent((im1 * 0.5), "Size / 2");
		do create_agent((im1 * 0.2), "Size / 5");
		do create_agent(((im1 * 0.1) * 10), "(Size / 10) * 10");
		do create_agent((im1 rotated_by 90) * 0.5, "Rotation 90° / 2");
		do create_agent((im1 rotated_by -33) * 0.7, "Rotation -33° * 0.7");
		do create_agent(darker(darker(im1)), "2 x darker");
		do create_agent(brighter(brighter(im1)), "2 x brighter");
		do create_agent((im1 * #lightgreen), "Light green tint");
		do create_agent((im1 * #lightskyblue), "Light blue tint");
		do create_agent(im1 tinted_with (#red, 0.15), "15% red tint");
		do create_agent(grayscale(im1), "Grayscale");
		do create_agent(grayscale(im1 * rgb(100, 100, 100)), "Dark Grayscale");
		do create_agent(brighter(brighter(brighter(grayscale(im1)))), "Bright Grayscale");
		do create_agent((sharpened(im1, 5)), "5 x Sharpened");
		do create_agent(blurred(im1,2), "2 x Blurred");
		do create_agent(antialiased(im1, 10), "10 x Antialiased");
		do create_agent(im1 with_width 200, "With width 200"); 
		do create_agent(im1 with_height 400, "With height 400");
		do create_agent(im1 with_size (im1.width, im1.height / 2), "Distorted height / 2");
		do create_agent(im1 with_size (im1.width / 3, im1.height), "Distorted width / 3");
		do create_agent(im1 cropped_to (200, 200, im1.width - 200, im1.height - 200), "Cropped ");
	}

	font title <- font("Arial", 20 #px, #bold);

	action create_agent (image im, string n) {
		create support {
			self.img <- im;
			self.name <- n;
			self.shape <- envelope(img) at_location {x + im1.width / 2, y + im1.height / 2};
			if (x < row - im1.width) {
				x <- x + im1.width;
			} else {
				x <- 0;
				y <- y + im1.height;
			}

		}

	}

}

species support {
	image img;

	
	aspect default {
		draw img at: location size: {shape.width, shape.height};
		draw name font: title at: location + {0, 0, 10} anchor: #center color: #white border: #black;
	}

}

experiment ImageManipulation type: gui {

	action _init_ {
		image im <- copy_from_clipboard(image);
		if im = nil {
			im <- image("../includes/Kandinsky.jpeg");
		}
		create simulation with: [im1::im];
		
	}
 
	output {
		display Agents type: 3d axes: false fullscreen: true background: #black {
			camera 'default' location: {shape.width/2, shape.height/2, max(shape.width, shape.height)*1.5} target: {shape.width/2, shape.height/2, 0};
			species support aspect: default;
		}

	}

}
