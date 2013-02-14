
model voronoi
 
global {
	int number_of_points <- 15 min: 1 max: 500;
	int environment_width <- 120 min: 10 max: 400;
	int environment_height <- 120 min: 10 max: 400;
	list centers <- [] of: center;
	init { 
		do write message: 'This model shows how Voronoi-like shapes can be drawn on a regular surface. A set of mobile agents is placed on a grid. Each agent possesses an attribute called *inside_color*. Each step, the agents move randomly and the grid cells paint themselves using the *inside_color* of the nearest agent. Dynamical boundaries then appear on the screen without any further calculations.';
		create center number: number_of_points {
			set color <- [rnd(255),rnd(255),rnd(255)] as rgb;
		}  
		set centers value: center as list;    
	} 
} 

environment width: environment_width height: environment_height {
	grid cell width: environment_width height: environment_height neighbours: 8  {
		rgb color <- rgb('white') update: (center closest_to (self)).color;
	}
}

entities {	
	species center skills: [moving] {
		rgb color <- rgb([rnd (255),rnd (255),rnd (255)]);
		reflex wander {
			do wander amplitude: 90;
		}  
		aspect default {
			draw circle(3) color: color;
		}
	}
}
	
experiment voronoi type: gui{
	parameter 'Number of points:' var: number_of_points;
	parameter 'Width of the environment:' var: environment_width;
	parameter 'Height of the environment:' var: environment_height;
	output {
		display Voronoi type: opengl{
			grid cell;
			species center;
		}
	}	
}
