/***
* Name: UserPauseandResume
* Author: A. Drogoul
* Description: Shows how to use the simulation actions 'pause' and 'resume' within a user interaction
* Tags: simulation, pause, resume
***/

model UserPauseandResume

global {
	geometry shape <- square(400);
	image_file play <- image_file("../images/play.png");
	image_file stop <- image_file("../images/stop.png");
	
	
	action toggle {
		if paused {
			do resume;
		} else {
			do pause;
		}
	}
	
	init {
		create sign;
	}
}



species sign skills: [moving] {
	image_file icon <- stop;
	point location <- centroid(world);
	
	aspect default {
		draw (world.paused ? play : stop) size: {100,100};
	}
	 
	reflex wander {
		do wander (speed: 0.3);
	}
}

experiment 'Try Me !' {
	
	output {
		display Interaction {
			species sign;
			event mouse_down action:{if ((#user_location distance_to sign[0]) < 50) {ask world {do toggle;}}};
		}
	}
	
}
