/**
* Name: Heatmap
* A model that demonstrates how to build a simple heatmap based on fields and how to display it WITHOUT CHANGING A LINE OF THE ORIGINAL MODEL IMPORTED
* Author: drogoul
* Tags: 
*/


model BuildingHeatmap

import "3D Visualization/models/Building Elevation.gaml"

global {

	field heatmap <- field(200,200);

	reflex update {
		ask people {
			
			loop i from: -2 to: 2 {
				heatmap[location + {i,i}] <- float(heatmap[location + {i,i}]) + 0.2;
			}
		}
	}

}
	


experiment "Show heatmap" type: gui {

	
	output {
		layout #split;
		
//		display Normal type: opengl draw_env: false background: #white {			
//			species building aspect: base refresh: true;
//			species road aspect: base ;
//			species people aspect: base;
//		}
//		
		display Heatmap2 type: opengl  draw_env: false background: #black {
			species building refresh: false  {draw shape border: #white wireframe: true width: 3;}
			species road refresh: false {draw shape color: #white width: 3;}
			species people {draw sphere(2) color: #white at: {location.x, location.y};}
			mesh heatmap scale: 0 color: palette([#black, #darkorange, #orange, #orangered, #red]) transparency: 0.5 position: {0,0,0.02} smooth: true;
		}
	}
}