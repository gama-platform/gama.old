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
	int size <- 300;
	field heatmap <- field(size, size);

	reflex update {
		ask people {
			loop i from: -(size/100) to: size/100 step: 2 {
				loop j from:  -(size/100) to: size/100 step: 2 {
					heatmap[location + {i, j}] <- heatmap[location + {i, j}] + 5 / (abs(i) + 1);
				}
			}
		}
	}
}

experiment "Show heatmap" type: gui {
	output {
		layout #split;
		display HeatmapPalette type: opengl draw_env: false background: #black {
//			species building refresh: false {
//				draw shape border: #white wireframe: true width: 3;
//			}
//
//			species road refresh: false {
//				draw shape color: #white width: 3;
//			}
//
//			species people {
//				draw circle(1) color: #white at: {location.x, location.y};
//			}
			//palette([#black, #darkorange, #orange, #orangered, #red])
			// The resulting field is displayed a little bit above the other layers, with no 3D rendering, and a smoothness of 3 (meaning three passes of box blur are being done to "spread" the values)
			mesh heatmap scale: 0 color: palette([ #black, #cyan, #yellow, #yellow, #red, #red, #red]) transparency: 0.2 position: {0, 0, 0.001} smooth: 4 ;
		}
		display HeatmapGradient type: opengl draw_env: false background: #black {
			species building refresh: false {
				draw shape border: #white wireframe: true width: 3;
			}

			species road refresh: false {
				draw shape color: #white width: 3;
			}

			species people {
				draw circle(1) color: #white at: {location.x, location.y};
			}
			//palette([#black, #darkorange, #orange, #orangered, #red])
			// The resulting field is displayed a little bit above the other layers, with no 3D rendering, and a smoothness of 3 (meaning three passes of box blur are being done to "spread" the values)
			mesh heatmap scale: 0 color: gradient([#black::0.1, #orange:: 0.5, #red::1]) transparency: 0.2 position: {0, 0, 0.001} smooth:4 ;
		}
		

	}

}