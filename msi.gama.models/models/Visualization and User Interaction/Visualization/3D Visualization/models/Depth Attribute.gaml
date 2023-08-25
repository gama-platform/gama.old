/**
* Name: Depth attribute
* Author: Patrick Taillandier
* Description: Model presenting the impact of the depth facet on visualization
* Tags: 3d, depth
*/


model DepthAttribute

global {
	
	geometry line2D <- line([{20,30}, {40,30}]);
	geometry line3D <- line([{20,60}, {40,60}]);
	

	geometry polygon2D <- square(10) at_location {70,30};
	geometry polygon3D <- square(10) at_location {70,60};
	
}

experiment DepthAttribute type: gui {
	output {
		display view type: opengl {
			camera 'default' location: {-21.6032,7.1358,17.3394} target: {47.9384,49.7931,0.0};
			graphics "Lines" {
				draw line2D color: #red;
				draw line3D depth: 10 color: #red;
			}
			graphics "Polygons" {
				draw polygon2D color: #blue;
				draw polygon3D depth: 10 color: #blue;
			}
		}
	}
}
