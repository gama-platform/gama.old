/**
 *  Name: Event Layer Feature
 *  Author: Arnaud Grignard & Patrick Taillandier
 *  Description: shows how to use event layer
 */
model event_layer_model 

global {

	int nbAgent <- 500;
	
	init {
		create cell number: nbAgent {
			color <-°green;
		}
	}
	
	action change_color (point loc, list<cell> selected_agents)
    {
     ask selected_agents  {
      	color <- color = °green ? °pink : °green;
      }
    } 
    action change_shape (point loc, list<cell> selected_agents)
    {
       ask selected_agents {
      	is_square <- not (is_square);
      }
    }
}

species cell skills: [moving] {
	rgb color;	
	bool is_square <- false;
	reflex mm {
		do wander amplitude: 30;
	}
	aspect default {
		draw is_square ? square(2): circle(1) color: color;
	}
}

experiment Displays type: gui {
	output {
		display View_change_color  { 
			species cell aspect: default;
			event mouse_down action: change_color;
		}
		display View_change_shape type: opengl{ 
			species cell;
			event mouse_down action: change_shape;
		}
	}
}

