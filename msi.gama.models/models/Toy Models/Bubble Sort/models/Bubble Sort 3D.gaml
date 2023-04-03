/**
* Name: Creating color and sort cubes by color
* Author:  Arnaud Grignard
* Description: A model to show how to create color by using the rgb operator, the color depending on the position of cube in the xyz space. 
* 	The cubes are randomly mixed to finally be sorted according to the color of each vertix of the whole big cube, using the bubble sort 
*        algorithm (https://en.wikipedia.org/wiki/Bubble_sort). 
* Tags: color, 3d
*/

model bubblesort3D


global {

//Number of cubes by faces of the whole big cube
int nb_cells<-15;

geometry shape <- cube(nb_cells) ;


bool change <- true;

init {
	//We create nb_cells^3 cubes and we define their color depending on their position in XYZ
	loop i from:0 to:nb_cells-1{
		loop j from:0 to: nb_cells-1{
			loop k from:0 to:nb_cells-1{
			  create cells{
				location <-{i mod nb_cells,j mod nb_cells, k mod nb_cells};
				
				//The canal RGB limit color to 255 by canal
				red <- float((i mod nb_cells)* int(255 / nb_cells));
				green <- float((j mod nb_cells) * int(255 / nb_cells));
				blue <- float((k mod nb_cells) * int(255 / nb_cells));
				
				//We create the color according to the value of the red, green and blue canals
				color <- rgb(red,green,blue);
			  }	
			}	
	    }
	}
	
	//We mix all the cubes randomly by permuting two randomly chosen cubes
	loop times: 10000 {
			ask one_of(cells) {
				cells one_cells <- one_of(cells);
				if (self != one_cells) {
					point loc <- copy(location);
					location <- one_cells.location;
					one_cells.location <- loc;
				}
			}
		}
	
		

}

//Reflex to finish the execution of the model when nothing has changed during the cycle
reflex end {
	if (not change) {
		do pause;	
	} 
	change <- false;
	}
}


species cells{

	rgb color;
	float red;
	float green;
	float blue;
	list<cells> neigbhours update: cells at_distance (1.1);
	
	//Update of the neighbours cubes at each cycle of the simulation according to their location
	cells upper_cell_y update: neigbhours first_with (shape.location.y > each.shape.location.y);
	cells upper_cell_x update: neigbhours first_with (shape.location.x > each.shape.location.x);
	cells upper_cell_z update: neigbhours first_with (shape.location.z > each.shape.location.z);
	
	//We permute the cube agent with its neighbour if its intensity is greater according to the canal related to its axis (z for canal blue, y for green and x for red)
	reflex swap_z when: upper_cell_z != nil and blue < upper_cell_z.blue{ 
		point tmp1Loc <-location;
		location <- upper_cell_z.location;  
    	upper_cell_z.location<-tmp1Loc; 	
	    change <- true;	
    }
	
	reflex swap_y when: upper_cell_y != nil and green < upper_cell_y.green{ 
		point tmp1Loc <-location;
		location <- upper_cell_y.location;  
    	upper_cell_y.location<-tmp1Loc; 	
	    change <- true;	
    }
    
    reflex swap_x when: upper_cell_x != nil and red < upper_cell_x.red{ 
		point tmp1Loc <-location;
		location <- upper_cell_x.location;  
    	upper_cell_x.location<-tmp1Loc; 	
	    change <- true;	
    }

	aspect default {
		draw cube(1) color:color border:color at:location;
	}	
}


experiment Display type: gui autorun:true{
	output {
		display View1 type:3d axes:false background:#black {
			camera 'default' location: {26.889,23.7693,37.0687} target: {2.2036,3.0558,0.0};
			species cells transparency:0.1;
			graphics "di"{
			 draw "black(0,0,0)" at:{0,0,0} color:#black perspective:false;
			 draw "red(1,0,0)" at:{world.shape.width,0,0} color:#red perspective:false;
			 draw "green(0,1,0)" at:{0,world.shape.height,0} color:#green perspective:false;
			 draw "blue(0,0,1)" at:{0,0,world.shape.width} color:#blue perspective:false;
			 draw "yellow(1,1,0)" at:{world.shape.width,world.shape.height,0} color:#yellow perspective:false;
			 draw "magenta(1,0,1)" at:{world.shape.width,0,world.shape.depth} color:#magenta perspective:false;
			 draw "cyan(0,1,1)" at:{0,world.shape.height,world.shape.depth} color:#cyan perspective:false;
			 draw "white(1,1,1)" at:{world.shape.width,world.shape.height,world.shape.depth} color:#white perspective:false;	
			}
		}
	}
}