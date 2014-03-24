/**
 *  model1
 *  Author: Arno
 *  Description: 
 */
model model1

global {

	int nbAgent <- 500;

	init {
		create cells number: nbAgent {
		color <-°yellow;
		}
	}
	
	action myAction (point location, list selected_agents)
    {
      int i <- 0;	
      loop times:length(selected_agents){
      	cells(selected_agents[i]).color <-°red;
      	i<-i+1;
      }
    } 
}

species cells {
	rgb color;	
	aspect default {
		draw circle(1) color: color;
	}

}

experiment Display type: gui {
	output {
		display View1 { 
			species cells;
			event [mouse_down] action: myAction;
		}
	}

}

