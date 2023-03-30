/**
* Name: CastingImages
* Shows how to cast Images from and to popular formats 
* Author: Alexis Drogoul
* Tags: 
*/


model CastingImages

/* Insert your model definition here */


global {
	image im <- image("../includes/Kandinsky.jpeg");
	matrix<int> mat <- matrix(im);
	field f <- field(im);
}

experiment Show {
	
	output {
		layout #split;
		display im {
			image im;
		}
		
		display mat {
			image mat;
		}
		
		display field {
			mesh f scale: 0.05 triangulation: true;
		}
	}
	
}

