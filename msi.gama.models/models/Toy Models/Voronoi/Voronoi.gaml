/**
* Name: Voronoi
* Author: 
* Description: A model showing how to clusterize space using the closest center as the kernel of our cluster. The space 
* 	 is discretized using a grid, each cell computing its distance from a center to know in which cluster it is. 
* Tags: skill, agent_movement, grid
*/

model voronoi
 
global {
	// Parameters 
	//Number of points
	int num_points <- 4 min: 1 max: 1000;
	//Size of the environment
	int env_width <- 100 min: 10 max: 400;
	int env_height <- 100 min: 10 max: 400;
	
	// Environment
	geometry shape <- rectangle(env_width, env_height);
	
	init { 
		write 'This model shows how Voronoi-like shapes can be drawn on a regular surface. A set of mobile agents is placed on a grid. Each agent possesses an attribute called *inside_color*. Each step, the agents move randomly and the grid cells paint themselves using the *inside_color* of the nearest agent. Dynamical boundaries then appear on the screen without any further calculations.';
		//Creation of all the points
		create center number: num_points ;  
	}   
} 
//Grid for the voronoi clustering
grid cell width: env_width height: env_height neighbors: 8 use_regular_agents: false {
	// Note: since GAMA 1.7, the topology needs to be specified for this computation to use continuous distances
	center closest_center <- nil update: (center closest_to self.location) using topology(world);
	rgb color <- #white update: (closest_center).color;
}
//Species representing the center of a Voronoi point
species center skills: [moving] { 
	rgb color <- rgb([rnd (255),rnd (255),rnd (255)]); 
	//Make the center of the cluster wander in the environment       
	reflex wander {
		do wander amplitude: 90;
	}  
	aspect base {
		draw square(1.0) color: color;
	}
}


experiment voronoi type: gui{ 
	parameter 'Number of points:' var: num_points;
	parameter 'Width of the environment:' var: env_width;
	parameter 'Height of the environment:' var: env_height;
	
	output {
		display Voronoi type: opengl {
			grid cell  ;
			species center aspect: base ;
		}
	}	
}
