model voronoi
 
global {
	int number_of_points parameter: 'Number of points:' min: 1 max: 1000 <- 15;
	int environment_width parameter: 'Width of the environment:' min: 10 max: 400 <- 120;
	int environment_height parameter: 'Height of the environment:' min: 10 max: 400 <- 120;
	list centers of: center <- [];
	list scheduling <- [cell, center];
	
	init { 
		do write message: 'This model shows how Voronoi-like shapes can be drawn on a regular surface. A set of mobile agents is placed on a grid. Each agent possesses an attribute called *inside_color*. Each step, the agents move randomly and the grid cells paint themselves using the *inside_color* of the nearest agent. Dynamical boundaries then appear on the screen without any further calculations.';
		create center number: number_of_points {
			set color <- [rnd(255),rnd(255),rnd(255)] as rgb;
		}		
		set centers <- center as list;   
	}
} 

environment width: environment_width height: environment_height {
	grid cell width: environment_width height: environment_height neighbours: 8  {
		rgb color <- rgb('white') update: (center closest_to (self)).color;
	}
} 
	
entities { 
	species center skills: [moving] {
		rgb color <- [rnd (255),rnd (255),rnd (255)];
		reflex {
			do wander amplitude: 90;
		}
		aspect default {
			draw shape: circle size: 3 color: color;
		}
	}
}
output {
	display Voronoi {
		grid cell;
		species center;
	}
}
