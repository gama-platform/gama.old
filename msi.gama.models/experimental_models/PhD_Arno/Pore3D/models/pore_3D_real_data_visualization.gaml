model pore3D   

/**
 *  AugmentedGrid
 *  Author: Arnaud Grignard
 *  Description: Initialize a grid with a random value between 0 and 255
 *  In TextDisplay only the value of the cell is displayed as a text
 *  In AugmentedDisplay the value of the cell is displayed:
 * 		1: Circle with a radius equal to the cellValue
 * 		2: Blue colored square
 *      3: Elevation + blue color
 * 		4: Elevation + hsb color
 */

global {
	//graph myGraph;
	
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 600000 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 100 ;  
	//matrix<int> init_data <- matrix<int>(matrix(file('./../includes/SL2_2.9.txt')));
	matrix<int> init_data <- matrix<int>(matrix(csv_file('./../includes/SL2_2.9.txt', " ")));
	float distance <-100.0;


	init { 
		create pore number: number_of_agents { 
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment),rnd(width_and_height_of_environment)};
		} 
	} 
	

	
	reflex update{
		int i<-1;
		ask pore{
			float x <- float(init_data at {1,i});
			float y <- float(init_data at {2,i});
			float z <- float(init_data at {3,i});
			float r <- float(init_data at {4,i});
			//set location <- {init_data at {1,i}, init_data at {2,i}, init_data at {3,i}};
			set location <- {x, y, z};
			set radius <- r;
			set color <- hsb (0.66, 1.0, (2.9-r)/(2.9-2.25));
			//write "x:" + init_data at {1,i} + "y:" + init_data at {2,i} + "z:" + init_data at {3,i} + "radius:" + init_data at {4,i};
			i <- i+1;
		}
	}
	

} 
  

 

species pore{ 
	rgb color; 
    float radius <-1.0;		
	aspect default {
	  draw square(radius) color: color;	
    }
}
	


experiment Pore3D  type:gui {
	output {
		display Pore3D type:opengl{
			species pore refresh:true;
		}
	}
}
