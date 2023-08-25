/***
* Name: Waterflowgridelevation
* Author: ben
* Description: 
* Tags: water, dem, grid
***/

/***
* Name: Water flow in a river represented by a set of cells, depending on the elevation
* Author: Benoit Gaudou
* Description: In this model, the space is discretised using a grid. Thus the river is a set of cells, each of them with an elevation.
* 	The data comes from a DEM (Digital Elevation Model) file.
* 	The upstream cells (i.e. the source cells) and the downstrem cells (i.e. the drain cells) are chosen by the modeler.
* 	At each step, the cells transmits a part of their water to their neighbor cells that are lower (their height is computed taken into account their elevation and height of water. 
* Tags: grid, gui, hydrology, water flow, DEM
***/

model Waterflowgridelevation

global {
	file dem_file <- file("../includes/DEM_100m_PP.asc");
	file river_file <- file("../includes/river.shp");
 
  	 //Shape of the environment using the dem file
  	 geometry shape <- envelope(dem_file);
	
	//Diffusion rate
	float diffusion_rate <- 0.8;
	
	list<cell> drain_cells;
	list<cell> source_cells;
	
	float input_water;
	
	init {
		create river from: river_file;
		do init_cells;
		do init_water;
   	  	//Initialization of the drain cells
		drain_cells <- cell where (each.is_drain);
		source_cells <- cell where(each.is_source);
		ask cell {do update_color;}
	}	
	
   //Action to initialize the altitude value of the cell according to the dem file
   action init_cells {
      ask cell {
         altitude <- grid_value;
         neighbour_cells <- (self neighbors_at 1) ;
      }
   }	
   
   action init_water {
      ask cell overlapping first(river) {
         //water_height <- 3.0;
         is_source <- grid_y = 0;
         is_drain <- grid_y = matrix(cell).rows - 1;
      }
   }   

   //Reflex to add water among the water cells
   reflex adding_input_water {
   	  float water_input <- input_water;
      ask source_cells {
         water_height <- water_height + water_input;
      }
   }
   
   //Reflex to flow the water according to the altitute and the obstacle
   reflex flowing {
      ask cell {already <- false;}
      ask (cell sort_by ((each.altitude + each.water_height))) {
         do flow;
      }
   }
   
   //Reflex to update the color of the cell
   reflex update_cell_color {
      ask cell {
         do update_color;
      }
   }
   
   //Reflex for the drain cells to drain water
   reflex draining when: false{
      ask drain_cells {
         water_height <- 0.0;
      }
   }   
	
//	reflex d {
//		write "min  " + cell min_of(each.grid_value);
//		write "max  " + cell max_of(each.grid_value);		
//	}
}

   grid cell file: dem_file neighbors: 8 frequency: 0  use_regular_agents: false use_individual_shapes: false use_neighbors_cache: false {
	float altitude;
	float water_height;
	float height;
	list<cell> neighbour_cells;
	bool is_drain;
	bool is_source;
	bool already;

     //Action to flow the water 
      action flow {
      	//if the height of the water is higher than 0 then, it can flow among the neighbour cells
         if (water_height > 0) {
         	//We get all the cells already done
            list<cell> neighbour_cells_al <- neighbour_cells where (each.already);
            //If there are cells already done then we continue
            if (!empty(neighbour_cells_al)) {
               //We compute the height of the neighbours cells according to their altitude, water_height and obstacle_height
               ask neighbour_cells_al {height <- altitude + water_height ;}
               //The height of the cell is equals to its altitude and water height
               height <-  altitude +  water_height;
               //The water of the cells will flow to the neighbour cells which have a height less than the height of the actual cell
               list<cell> flow_cells <- (neighbour_cells_al where (height > each.height)) ;
               //If there are cells, we compute the water flowing
               if (!empty(flow_cells)) {
                  loop flow_cell over: shuffle(flow_cells) sort_by (each.height){
                     float water_flowing <- max([0.0, min([(height - flow_cell.height), water_height * diffusion_rate])]); 
                     water_height <- water_height - water_flowing;
                     flow_cell.water_height <-flow_cell.water_height +  water_flowing;
                     height <- altitude + water_height;
                  }   
               }
            }
         }
         already <- true;
      }  
      //Update the color of the cell
      action update_color { 
         int val_water <- 0;
         val_water <- max([0, min([255, int(255 * (1 - (water_height / 12.0)))])]) ;  
         color <- rgb([val_water, val_water, 255]);
         grid_value <- water_height + altitude;
      }
}

species river {
	aspect default {
		draw shape color: #red;
	}
}

experiment hydro type: gui {
	parameter "input water" var: input_water <- 1.0;
	output {
		display d type:2d antialias:false{
			grid cell border: #black;
			//species river;
		}
	}
}
