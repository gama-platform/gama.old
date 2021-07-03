/**
* Name: Heatmap
* A model that demonstrates how to build a simple heatmap based on fields and how to display it WITHOUT CHANGING A LINE OF THE ORIGINAL MODEL IMPORTED. 
* This heatmap records the number of people passed in each area of the city.
* Author: Alexis Drogoul
* Tags: 
*/
model BuildingHeatmap

import "3D Visualization/models/Building Elevation.gaml"

global {
	field heatmap <- field(100, 100);

	reflex update {
		ask people {
			loop i from: -5 to: 5 {
				heatmap[location + {i, i}] <- heatmap[location + {i, i}] + 5 / (abs(i) + 1);
			}
		}
	}
}

experiment "Show heatmap" type: gui {
	output {
		layout #split;
		display Heatmap2 type: opengl draw_env: false background: #black {
			species building refresh: false {
				draw shape border: #white wireframe: true width: 3;
			}

			species road refresh: false {
				draw shape color: #white width: 3;
			}

			species people {
				draw circle(1) color: #white at: {location.x, location.y};
			}

			mesh heatmap scale: 0 color: palette([#black, #darkorange, #orange, #orangered, #red]) transparency: 0.2 position: {0, 0, 0.001} smooth: true ;
		}

	}

}