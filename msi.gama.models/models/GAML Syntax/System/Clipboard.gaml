/**
* Name: Clipboard
* Shows how the clipboard can be used to retrieve and save text or other objects 
* Author: A. Drogoul
* Tags: system, clipboard, casting
*/


model Clipboard

global {
	
	init {
		// We transform the geometry into a string
		string my_shape <- string(shape);
		write "Original shape: " + my_shape;
		// We transform it a bit
		my_shape <- my_shape replace("100","150");
		// We copy the string representation of the shape into the clipboard
		bool copied <- copy_to_clipboard(my_shape);
		// If it has been correctly copied, we retrieve it as a geometry
		if (copied) {
			geometry received <- copy_from_clipboard(geometry);
			write "Transformed shape: " + received;
		}
	}
	
}

experiment run;