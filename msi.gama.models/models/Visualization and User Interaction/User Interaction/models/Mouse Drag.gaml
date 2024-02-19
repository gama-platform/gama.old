/**
* Name: MouseDrag
* 
* Demonstrates the usage of the mouse_drag events to move an agent using user
* interactions. Drag events are triggered when the mouse is moving with the
* mouse button down. Notice that move events are not triggered when the mouse
* button is down.
* 
* Author: breugnot
* Tags: gui, event, mouse_move, mouse_drag, mouse_down, mouse_up
*/

model mouse_event

global {
	geometry shape <- square(20);
	DraggedAgent selected_agent <- nil;
	init {
		create DraggedAgent with: (location: {10, 10});
	}
	
	/** Insert the global definitions, variables and actions here */
	action mouse_down {
		ask DraggedAgent {
			if( self covers #user_location) {
				// Selects the agent
				selected_agent <- self;
			}
		}
	}
	
	action mouse_up {
		if(selected_agent != nil) {
			selected_agent <- nil;
		}
	}
	
	action mouse_drag {
		// Makes the agent follow the mouse while the mouse button is down
		if(selected_agent != nil) {
			ask selected_agent {
				location <- #user_location;
			}
		}
	}
}

species DraggedAgent {
	init {
		shape <- circle(1);
	}
	aspect default {
		draw shape at: location;
	}
}

experiment "Mouse Drag" type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
		display "Mouse Drag [in OpenGL]" type:opengl {
			camera #from_above locked:true;
			event #mouse_down {ask simulation {do mouse_down;}}
			event #mouse_up {ask simulation {do mouse_up;}}
			event #mouse_drag {ask simulation {do mouse_drag;}}
			
			graphics "world" {
				draw world color: #white border:#black;
			}

			species DraggedAgent aspect:default;
		}

		display "In Java2D, one needs to lock the surface first " type:java2D {
			event #mouse_down {ask simulation {do mouse_down;}}
			event #mouse_up {ask simulation {do mouse_up;}}
			event #mouse_drag {ask simulation {do mouse_drag;}}
			
			graphics "world" {
				draw world color: #white border:#black;
			}
			
			species DraggedAgent aspect:default;
		}
	}
}
