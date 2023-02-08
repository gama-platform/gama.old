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
	int num_points <- 15 min: 1 max: 1000;
	//Size of the environment
	int env_width <- 150 min: 10 max: 400;
	int env_height <- 150 min: 10 max: 400;
	int num_neighbours <- 4 among: [4, 8];
	string palette <- 'PRGn' among: brewer_palettes(15);
	list<rgb> colors <- brewer_colors(palette);
	bool blur1 <- false;
	bool blur2 <- false;
	topology w;

	// Environment
	geometry shape <- rectangle(env_width, env_height);

	init {
		write
		'This model shows how Voronoi-like shapes can be drawn on a regular surface. A set of mobile agents is placed on a grid. Each agent possesses an attribute called *inside_color*. Each step, the agents move randomly and the grid cells paint themselves using the *inside_color* of the nearest agent. Dynamical boundaries then appear on the screen without any further calculations.';
		//Creation of all the points
		create center number: num_points;
		w <- topology(self);
	}

}
//Grid for the voronoi clustering
grid cell width: env_width height: env_height neighbors: num_neighbours use_neighbors_cache: true use_individual_shapes: false use_regular_agents: false parallel: true {
// Note: since GAMA 1.7, the topology needs to be specified for this computation to use continuous distances
	rgb color <- #white update: ((center closest_to location) using w).color;

	reflex when: blur1 {
		color <- blend(color, one_of(neighbors).color, 0.7);
	}

	reflex when: blur2 {
		loop n over: neighbors {
			color <- blend(color, n.color, 0.8);
		}

	}

}
//Species representing the center of a Voronoi polygon
species center skills: [moving] {
	rgb color <- colors[int(self) mod length(colors)]; //rnd_color(255);
	//Make the center of the cluster wander in the environment       
	reflex wander {
		do wander amplitude: 90.0;
	}

	aspect default {
		draw circle(1.0) color: color border: #black;
	}

}

experiment voronoi type: gui autorun: true {
	parameter 'Number of points:' var: num_points {
		if (num_points > length(center)) {
			create center number: num_points - length(center);
		} else {
			ask (length(center) - num_points) among center {
				do die;
			}

		}

	}
	parameter 'Number of neighbours in the grid:' var: num_neighbours;
	parameter 'Color palette' var: palette  {
		colors <- brewer_colors(palette);
		ask center {
			color <- colors[int(self) mod length(colors)];
		}

	}
	parameter 'Width of the environment:' var: env_width;
	parameter 'Height of the environment:' var: env_height;
	parameter "Simple blur" var: blur1;
	parameter "Complex blur" var: blur2;
	output {
		layout #split navigator: false tray: false toolbars: false consoles: false editors: false;
		display "Voronoi 2D" background: #black type: 3d axes:false{
			light #ambient intensity: 100;
			grid cell;
		}

	}

}
