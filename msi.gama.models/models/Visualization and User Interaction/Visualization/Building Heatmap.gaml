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
	int size <- 400;
	field heatmap <- field(size, size);
	reflex update {
		ask people {
			heatmap[location] <- heatmap[location] + 10;
		}
	}
}

experiment "Show heatmap" type: gui {
	output {
		layout #split;
		display HeatmapPalette type: opengl axes: false background: #black {
			// The field is displayed  without 3D rendering, a palettre of warm colors and a smoothness of 3 (meaning three passes of box blur are being done to "spread" the values)
			mesh heatmap scale: 0 color: palette([ #black, #yellow, #yellow, #orange, #orange, #red, #red]) smooth: 3 ;
		}
		display HeatmapGradient type: opengl axes: false background: #black camera: #from_up_front {
			species building refresh: false {
				draw shape border: #white wireframe: true width: 3;
			}

			species road refresh: false {
				draw shape color: #white width: 3;
			}

			species people {
				draw circle(1) color: #white at: {location.x, location.y};
			}
			// The field is displayed a little bit above the other layers, with a slight 3D rendering, and a smoothness of 1 (meaning one passe of box blur are being done to "spread" the values). The colors are provided by a gradient with three stops
			mesh heatmap scale: 0.01 color: gradient([#black::0, #cyan::0.1, #red::1]) transparency: 0.2 position: {0, 0, 0.001} smooth: 1 ;
		 }
		

	}

}