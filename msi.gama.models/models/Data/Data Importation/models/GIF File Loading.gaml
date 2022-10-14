/**
* Name: AnimatedGIFLoading
* Author: A. Drogoul
* Description:  Shows how to load animated GIF files and use them as textures or display them directly. 
* Tags: Image, Display, File
*/

model AnimatedGIFLoading

global {
	init {
		create fish number: 100;
	}
}

	species fish skills:[moving] {
		reflex r {
			do wander amplitude:2.0 speed: 0.1;
		}
		
		aspect default {
			draw gif_file("../images/fish3.gif") size: {10,10} rotate: heading-45 ;
		}
	}


experiment "Ripples and Fish" type: gui {
	
	output synchronized: true{
		display Ripples  type: 3d camera: #from_up_front
		{
			species fish position: {0,0,0.05};
			graphics world transparency: 0.4{ 
				draw cube(100) scaled_by {1,1,0.08} texture:("../images/water2.gif") ;
			}
		}
	}
}

