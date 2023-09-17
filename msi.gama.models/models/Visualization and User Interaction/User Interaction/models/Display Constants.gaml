/**
* Name: DisplayConstants
* This model shows the use of some "constants" defined in GAML that allow to gather information about the user manipulation / actions in displays 
* Author: drogoul
* Tags: Display, 
*/


model DisplayConstants

experiment "Run" {

	output {
		display "Display2d" background: #black type: 2d {
			graphics back {
				draw rectangle(shape.width, shape.height) color: #blue;
			}
			graphics g {
				draw "In world: " + #user_location at: #user_location;
				draw "In display: " + #user_location_in_display at: #user_location + {0,5};
			}
			event #mouse_move {do update_outputs;}
			
		}
		display "Display3d" background: #black type: 3d {
			graphics back {
				draw rectangle(shape.width, shape.height) color: #blue;
			}
			graphics g {
				draw "In world: " + #user_location at: #user_location;
				draw "In display" + #user_location_in_display at: #user_location + {0,5};
			}
			event #mouse_move {do update_outputs;}
			
		}



	}
	
}

