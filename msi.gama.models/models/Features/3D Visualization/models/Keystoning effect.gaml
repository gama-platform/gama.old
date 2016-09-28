/**
* Name: Advanced 3D properties : keystoning effect
* Author: Julien Mazars
* Description: Model presenting how to use keystoning effect to your display, tunning the 4 corner positions. 
* Notice that in order to run this model, you need to have a graphical card 
* and the facet "use_shader" activated in your display definition.
* Tags: keystone
*/
model keystone


global
{
	float tl_x <- -1.0; // top-left x default position
	float tl_y <- -1.0; // top-left y default position
	float tr_x <- 1.0; // top-right x default position
	float tr_y <- -1.0; // top-right y default position
	float bl_x <- -1.0; // bottom-left x default position
	float bl_y <- 1.0; // bottom-left y default position
	float br_x <- 1.0; // bottom-right x default position
	float br_y <- 1.0; // bottom-right y default position
}

grid my_grid width:10 height:10 {
	init {
		color <- (grid_x mod 2 = grid_y mod 2) ? #white : #black;
	}
}

experiment keystone type: gui
{
	parameter "x position (tl)" var:tl_x min:-1.0 max:0.0 category:"top-left corner";
	parameter "y position (tl)" var:tl_y min:-1.0 max:0.0 category:"top-left corner";
	parameter "x position (tr)" var:tr_x min:0.0 max:1.0 category:"top-right corner";
	parameter "y position (tr)" var:tr_y min:-1.0 max:0.0 category:"top-right corner";
	parameter "x position (bl)" var:bl_x min:-1.0 max:0.0 category:"bottom-left corner";
	parameter "y position (bl)" var:bl_y min:0.0 max:1.0 category:"bottom-left corner";
	parameter "x position (br)" var:br_x min:0.0 max:1.0 category:"bottom-right corner";
	parameter "y position (br)" var:br_y min:0.0 max:1.0 category:"bottom-right corner";
	
	output {
		// we set the "use_shader" facet to true, in order to activate "advanced" 3D display properties.
		display grid use_shader:true keystone:[{tl_x,tl_y},{tr_x,tr_y},{bl_x,bl_y},{br_x,br_y}] type:opengl background:#darkblue {
			grid my_grid;
			graphics "text" {
				draw "Run the model, then change the position" font:font("Helvetica", 30, #plain) color:#red at:{3,45};
				draw "of the corners of the screen changing the parameters value" font:font("Helvetica", 30, #plain) color:#red at:{-15,55};
			}
		}
	}
}