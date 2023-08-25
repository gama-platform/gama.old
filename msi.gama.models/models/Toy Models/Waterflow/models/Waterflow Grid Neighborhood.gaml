/***
* Name: Water flow in a river represented by a set of cells
* Author: Benoit Gaudou and Patrick Taillandier
* Description: In this model, the space is discretised using a grid. Thus the river is a set of cells. 
* 	The data comes from a raster image.
* 	The upstream cells (i.e. the source cells) are chosen by the modeler.
* 	Initialy, an order is computed by neighborhood to make the water flows from the source cells, all over the river.
* 	At each step, the cells transmits a part of their water to their downstream cells (the neighbor cells with a greater order number).
* Tags: grid, gui, hydrology, water flow
***/

model Waterflowgridneighborhood

global {
	int image_size <- 20;
	file image_river_file <- image_file('../includes/river_image.png') ;
	
	list<cell> river;
	
	float entrance_water <- 255.0;
	
	
	init {
		ask cell {	
			color <- rgb( (image_river_file) at {grid_x,grid_y}) ;
			if(color = rgb(0,61,245)) {
				is_river_cell <- true;
				order <- image_size - 1 - grid_x;
				source <- order = 0;			
			}
		}
		river <- cell where(each.is_river_cell);
		
    }	
    
    
}

grid cell  width: image_size height: image_size schedules: reverse(river sort_by(each.order)){
	bool is_river_cell <- false;
	bool source <- false;
	int order <- -1;
	
	float water_volume;

	reflex water_flow {
		ask neighbors where(each.order > self.order) {
			water_volume <- water_volume + 0.9*myself.water_volume;
		}
		water_volume <- 0.1*water_volume;
	}
	
	reflex water_source when: source and every(20 #cycle) {
		water_volume <- water_volume + entrance_water;
	}
		
	
	aspect default {
		draw shape color: is_river_cell? rgb(0,0,2 * water_volume) : #lightgreen border: #grey;
	}
}

experiment Waterflowgridneighborhood type: gui {
	output synchronized: true{
		display flow type:2d{
			species cell;
		}		
	}
}

