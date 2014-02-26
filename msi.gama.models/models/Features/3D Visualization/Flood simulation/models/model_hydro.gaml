/**
 *  hydro
 *  Author: patricktaillandier
 *  Description: 
 */

model hydro

global {
   
   file river_shapefile <- file("../includes/RedRiver.shp");
   file dykes_shapefile <- file("../includes/Dykes.shp");
   file buildings_shapefile <- file("../includes/Building.shp");
   
   file dem_file <- file("../includes/mnt50.asc");  
   float diffusion_rate <- 0.6;
   float dyke_height <- 15.0;
    
   geometry shape <- envelope(dem_file);
   list<cell> drain_cells;
   list<cell> river_cells;
    
   float dyke_width <- 15.0;
 
   float step <- 1°h;
   
   init {
      do init_cells;
      do init_water;
     river_cells <- cell where (each.is_river);
      drain_cells <- cell where (each.is_drain);
      do init_obstacles;
      ask cell {
         obstacle_height <- compute_highest_obstacle();
         do update_color;
      }
   }
   
   action init_cells {
      ask cell {
         altitude <- grid_value;
         neighbour_cells <- (self neighbours_at 1) ;
      }
   }
   action init_water {
      geometry river <- geometry(river_shapefile);
      ask cell overlapping river {
         water_height <- 10.0;
         is_river <- true;
         is_drain <- grid_y = matrix(cell).rows - 1;
      }
   }
   
   action init_obstacles{
      create building from: buildings_shapefile  {
         do update_cells;
      }
      create dyke from: dykes_shapefile;
      ask dyke {
          shape <-  shape + dyke_width;
            do update_cells;
      }
   }
   reflex adding_input_water {
   	  float water_input <- rnd(100)/100;
      ask river_cells {
         water_height <- water_height + water_input;
      }
   }
   reflex flowing {
      ask cell {already <- false;}
      ask (cell sort_by ((each.altitude + each.water_height + each.obstacle_height))) {
         do flow;
      }
   }
   reflex update_cell_color {
      ask cell {
         do update_color;
      }
   }
   reflex draining {
      ask drain_cells {
         water_height <- 0.0;
      }
   }
   
}

   species obstacle {
      float height min: 0.0;
      rgb color;
      float water_pressure update: compute_water_pressure();
      
      list<cell> cells_concerned ;
      list<cell> cells_neighbours;
      float compute_water_pressure {
         if (height = 0.0) {
            return 0.0;
         } else {
         	
            float water_level <- cells_neighbours max_of (each.water_height);
            return min([1.0,water_level / height]);
         } 
      }
      action update_cells {
         cells_concerned <- (cell overlapping self);
        	ask cells_concerned {
            add myself to: obstacles;
            water_height <- 0.0;
         }
         cells_neighbours <- cells_concerned + cells_concerned accumulate (each.neighbour_cells);
      	 do compute_height();
         if (height > 0.0) {   
            water_pressure <- compute_water_pressure();
         } else {water_pressure <- 0.0;}
      }
      action compute_height;
      aspect geometry {
         int val <- int( 255 * water_pressure);
         color <- rgb(val,255-val,0);
         draw shape color: color depth: height border: color;
      }
   }
   species building parent: obstacle {
      float height <- 2.0 + rnd(8);
   }
   
   species dyke parent: obstacle{
       int counter_wp <- 0;
       int breaking_threshold <- 24;
      
       action break{
         ask cells_concerned {
            do update_after_destruction(myself);
         }
         do die;
      }
      
      action compute_height
       {
      	   height <- dyke_height - mean(cells_concerned collect (each.altitude));
      
      }
      
      reflex breaking_dynamic {
      	if (water_pressure = 1.0) {
      		counter_wp <- counter_wp + 1;
      		if (counter_wp > breaking_threshold) {
      			do break;
      		}
      	} else {
      		counter_wp <- 0;
      	}
      }
      user_command "Destroy dyke" action: break; 
   }
   
   grid cell file: dem_file neighbours: 8 frequency: 0  use_regular_agents: false use_individual_shapes: false use_neighbours_cache: false {
      float altitude;
      float water_height <- 0.0 min: 0.0;
      float height;
      list<cell> neighbour_cells ;
      bool is_drain <- false;
      bool is_river <- false;
      list<obstacle> obstacles;
      float obstacle_height <- 0.0;
      bool already <- false;
      
      float compute_highest_obstacle {
         if (empty(obstacles))
         {
            return 0.0; 
         } else {
            return obstacles max_of(each.height);
         }
      }
      action flow {
         if (water_height > 0) {
            list<cell> neighbour_cells_al <- neighbour_cells where (each.already);
            if (!empty(neighbour_cells_al)) {
               ask neighbour_cells_al {height <- altitude + water_height + obstacle_height;}
               height <-  altitude +  water_height;
               list<cell> flow_cells <- (neighbour_cells_al where (height > each.height)) ;
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
      action update_color { 
         int val_water <- 0;
         val_water <- max([0, min([255, int(255 * (1 - (water_height / 12.0)))])]) ;  
         color <- rgb([val_water, val_water, 255]);
         grid_value <- water_height + altitude;
      }
      
      action update_after_destruction(obstacle the_obstacle){
         remove the_obstacle from: obstacles;
         obstacle_height <- compute_highest_obstacle();
      }
       
   }


experiment main_gui type: gui {
   parameter "Shapefile for the river" var:river_shapefile category:"Water data";
   parameter "Shapefile for the dykes" var:dykes_shapefile category:"Obstacles";
   parameter "Shapefile for the buildings" var:buildings_shapefile category:"Obstacles";
   parameter "Height of the dykes" var:dyke_height category:"Obstacles";
   parameter "Diffusion rate" var:diffusion_rate category:"Water dynamic";
   output { 
      display map type: opengl ambient_light: 100{
         grid cell triangulation: true;
         species building aspect: geometry;
         species dyke aspect: geometry;
      }
      display chart_display refresh_every: 24 { 
         chart "Pressure on Dykes" type: series {
            data "Mean pressure on dykes " value: mean(dyke collect (each.water_pressure)) style: line color: rgb("magenta") ;
            data "Rate of dykes with max pressure" value: (dyke count (each.water_pressure = 1.0))/ length(dyke) style: line color: rgb("red") ;
            data "Rate of dykes with high pressure" value: (dyke count (each.water_pressure > 0.5))/ length(dyke) style: line color: rgb("orange") ;
            data "Rate of dykes with low pressure" value: (dyke count (each.water_pressure < 0.25))/ length(dyke) style: line color: rgb("green") ;
         }
      }
   }
}