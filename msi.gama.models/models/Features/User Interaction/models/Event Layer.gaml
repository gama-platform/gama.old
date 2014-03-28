/**
 *  event_layer_model
 *  Author: Arno & Patrick
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
	
	action change_color (point loc, list selected_agents)
    {
     ask selected_agents as: cell {
      	color <- color = °green ? °pink : °green;
      }
    } 
    action change_shape (point loc, list selected_agents)
    {
       ask selected_agents as: cell{
      	is_square <- not (is_square);
      }
    }
}

species cell {
	rgb color;	
	bool is_square <- false;
	aspect default {
		draw is_square ? square(2): circle(1) color: color;
	}
}

experiment Displays type: gui {
	output {
		display View_change_color { 
			species cell;
			event [mouse_down] action: change_color;
		}
		display View_change_shape type:opengl{ 
			species cell;
			event [mouse_down] action: change_shape;
		}
	}
}

