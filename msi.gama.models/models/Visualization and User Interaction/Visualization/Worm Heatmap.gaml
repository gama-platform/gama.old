/**
* Name: Heatmap
* A model that demonstrates how to build a simple heatmap based on fields and how to display it. 
* Author: drogoul
* Tags: 
*/


model WormHeatmap

global {

	int size <- 1000;
	field heatmap <- field(500,500);
	
	init {
		create worm number: size;
	}
}

species worm skills: [moving] {
	
	reflex wander {
		do wander amplitude: 5.0 speed: 0.01;
	}
	
	reflex mark {
			heatmap[location] <- (heatmap[location]) + 0.1;
	}
}
	


experiment "Show heatmap" type: gui {

	
	output {
		layout #split;
		
		display Heatmap type: 3d background: #black {
			// The display is 2D only, and defining a fixed palette makes the color slowly slide as the values increase
			mesh heatmap scale: 0 color: palette([#white, #orange, #red, #red]);
		}
		
		display Other type: 3d background: #black camera: #from_up_front{
			// Bumps appear due to the values increasing
			mesh heatmap scale: 0.1 color: brewer_colors("Reds") triangulation: true;
		}
	}
}