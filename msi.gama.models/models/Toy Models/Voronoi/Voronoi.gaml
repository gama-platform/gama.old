model voronoi
 
global {
	// Parameters 
	int num_points <- 15 min: 1 max: 1000;
	int env_width <- 100 min: 10 max: 400;
	int env_height <- 100 min: 10 max: 400;
	
	// Environment
	geometry shape <- rectangle(env_width, env_height);
	
	init { 
		write 'This model shows how Voronoi-like shapes can be drawn on a regular surface. A set of mobile agents is placed on a grid. Each agent possesses an attribute called *inside_color*. Each step, the agents move randomly and the grid cells paint themselves using the *inside_color* of the nearest agent. Dynamical boundaries then appear on the screen without any further calculations.';
		create center number: num_points ;  
	}   
} 

entities {
	grid cell width: env_width height: env_height neighbours: 8 use_regular_agents: false {
		center closest_center <- nil update: center closest_to(self);
		rgb color <- rgb('white') update: (closest_center).color;
	}
	
	species center skills: [moving] { 
		rgb color <- rgb([rnd (255),rnd (255),rnd (255)]);        
		reflex wander {
			do wander amplitude: 90;
		}  
		aspect base {
			draw circle(1.0) color: color;
		}
	}
}


experiment voronoi type: gui{ 
	parameter 'Number of points:' var: num_points;
	parameter 'Width of the environment:' var: env_width;
	parameter 'Height of the environment:' var: env_height;
	
	output {
		display Voronoi type: opengl {
			grid cell triangulation: true elevation: closest_center = nil ? 0 : env_width/3 - self distance_to (closest_center);
			species center aspect: base ;
		}
	}	
}
