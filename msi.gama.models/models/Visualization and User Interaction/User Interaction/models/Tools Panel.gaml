/***
* Name: ToolsPanel
* Author: Patrick Taillandier
* Description: Model which shows how to use the event layer to define a tools panel. In this model, the modelers can select one of 
* the 4 tools (icon - building1, building2, building3 and eraser) to carry out action on the map display. More precisely, when one tool 
* is selected (red rectangle), the tool change the color of the selected cells and display the corresponding icon (in map display).
* Tags: gui, user event, tool panel
***/

model ToolsPanel

global {

	//current action type
	int action_type <- -1;	
	
	//images used for the buttons
	list<file> images <- [
		file("../images/building1.png"),
		file("../images/building2.png"),
		file("../images/building3.png"),
		file("../images/eraser.png")
		
	]; 
	
	
	action activate_act {
		button selected_but <- first(button overlapping (circle(1) at_location #user_location));
		if(selected_but != nil) {
			ask selected_but {
				ask button {bord_col<-#black;}
				if (action_type != id) {
					action_type<-id;
					bord_col<-#red;
				} else {
					action_type<- -1;
				}
				
			}
		}
	}
	
	action cell_management {
		cell selected_cell <- first(cell overlapping (circle(1.0) at_location #user_location));
		if(selected_cell != nil) {
			ask selected_cell {
				building <- action_type;
				switch action_type {
					match 0 {color <- #red;}
					match 1 {color <- #white;}
					match 2 {color <- #yellow;}
					match 3 {color <- #black; building <- -1;}
				}
			}
		}
	}

}

grid cell width: 10 height: 10 {
	rgb color <- #black ;
	int building <- -1;
	aspect default {
		if (building >= 0) {
			draw image_file(images[building]) size:{shape.width * 0.5,shape.height * 0.5} ;
		}
		 
	}
}

grid button width:2 height:2 
{
	int id <- int(self);
	rgb bord_col<-#black;
	aspect normal {
		draw rectangle(shape.width * 0.8,shape.height * 0.8).contour + (shape.height * 0.01) color: bord_col;
		draw image_file(images[id]) size:{shape.width * 0.5,shape.height * 0.5} ;
	}
}


experiment ToolsPanel type: gui {
	output {
			layout horizontal([0.0::7285,1::2715]) tabs:true;
		display map {
			grid cell border: #white;
			species cell;
			event mouse_down action:cell_management;
			
		}
		//display the action buttons
		display action_buton background:#black name:"Tools panel"  	{
			species button aspect:normal ;
			event mouse_down action:activate_act;    
		}
	}
}
