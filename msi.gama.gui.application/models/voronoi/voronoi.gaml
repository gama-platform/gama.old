model voronoi
// gen by Xml2Gaml


global {
	var number_of_points type: int parameter: 'Number of points:' init: 15 min: 1 max: 1000;
	var environment_width type: int parameter: 'Width of the environment:' init: 120 min: 10 max: 400;
	var environment_height type: int parameter: 'Height of the environment:' init: 120 min: 10 max: 400;
	var centers type: list init: [] of: center;
	init {
		do action: write {
			arg message value: '\\n\\u25B6 This model shows how Voronoi-like shapes can be drawn on a regular surface.\\n\\u25B6 A set of mobile agents is placed on a grid. Each agent possesses an attribute called *inside_color*. Each step, the agents move randomly and the grid cells paint themselves using the *inside_color* of the nearest agent.\\n\\u25B6 Dynamical boundaries then appear on the screen without any further calculations.';
		}
		create species: center number: number_of_points {
			set color value: [rnd(255),rnd(255),rnd(255)] as rgb;
		}
		set centers value: center as list;
	}
	 
	var scheduling type: list value: (list (grid)) + (list (center));
} 
environment width: environment_width height: environment_height {

}

	grid grid width: environment_width height: environment_height neighbours: 8  {
		var color type: rgb init: 'black';
		var color type: rgb init: rgb('white') value: (centers with_min_of (world.topology distance_between [location,each.location])).color;
	}
entities { 
	species center skills: [situated, visible, moving] {
		rgb color;
		reflex {
			do action: wander {
				arg amplitude value: 90;
			} 
		}
		aspect default {
			draw shape: circle size: 3 color: 'white';
	//		let other value: ((centers - self) with_min_of (self distance_to each));
	//		draw shape: line to: other color: 'white';
		}
	}
}
output {
	display Voronoi {
		grid grid;
		species center;
	}
}
