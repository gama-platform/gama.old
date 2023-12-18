/**
* Name: Conditional_aspect_selection
* Author: Baptiste Lesquoy
* Tags: 
*/


model Conditional_aspect_selection

global {
	
	bool dark_mode <- false;
	font my_font <- font("Helvetica", 16, #bold);
	init {
		create dummy number:10;
	}
	
}

species dummy {
	
	aspect light {
		draw circle(2) color:#red;
	}
	aspect dark {
		draw circle(2) color:#darkred;
	}
}


experiment test {
	parameter "Toggle dark mode" var:dark_mode;
	output{
		display main background:dark_mode?#black:#white{
			species dummy {
				if dark_mode {
					draw dark;
				}
				else{
					draw light;
				}	
			}
			
			graphics "Instructions"{
				draw "Toggle the dark mode parameter and run a simulation step" at:{5,5} color:dark_mode?#white:#black font:my_font;				
			}
		}
	}
}

