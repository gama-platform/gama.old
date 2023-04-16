/**
* Name: Issue3776
* Demonstrates how very small fields can be displayed 
* Author: drogoul
* Tags: 
*/


model Issue3776

global {
	field f <- field(matrix([1],[5]));
}

experiment Issue3776 type: gui {
	output {
		display map type: opengl {
			mesh f grayscale: true ;
		}
		display map_image  {
			graphics "image" {
				draw image(f);
			}
		}
	}
}