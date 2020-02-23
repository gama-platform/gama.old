/***
* Name: MouseExitandEnter
* Author: A. Drogoul
* Description:  Shows the use of the two mouse events : mouse_exit and mouse_enter. The display shows two eyes. When the mouse enters it, the eyes open and follow its movement. When the mouse exits the screen, they close.
* Tags: user, mouse, interaction
***/

model MouseExitandEnter 


global {
	bool closed <- false;
	init {
		create eyes with: [location::{25,30}];
		create eyes with: [location::{65,30}];
	}
}

species eyes {
	point look_at <- location;
	
	action follow {
		float heading <- location towards #user_location;
		look_at <- location + {5*cos(heading), 5*sin(heading)};
	}
	
	aspect outside {
			draw sphere(15) color: closed ? #black : #white ;
	}
	
	aspect inside {
		draw sphere(5) color: #blue empty: false at: look_at;
	}
}

experiment Run {
	output {
		display Eyes type: opengl draw_env: false {
			graphics face {
				draw circle(60) color: #gamaorange;
			}
			graphics mouth {
				draw curve({20,80},{70, 80},  (cos(location towards #user_location))) + 5  depth: 5;
			}
			species eyes aspect: inside;
			species eyes aspect: outside transparency: closed ? 0.1 : 0.5;
			event mouse_exit action:{closed <- true;};
			event mouse_enter action: {closed <- false;};
			event mouse_move action: {ask eyes {do follow;}};
			event mouse_menu action: {closed <- !closed;};
			
			}
	}
}