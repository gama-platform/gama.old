/**
 *  bubblesort3D
 *  Author: Arnaud Grignard
 */

model bubblesort3D


global {

int nb_cells<-15;

geometry shape <- cube(nb_cells) ;


bool change <- true;

init {

	loop i from:0 to:nb_cells{
		loop j from:0 to: nb_cells{
			loop k from:0 to:nb_cells{
			  create cells{
				location <-{i mod nb_cells,j mod nb_cells, k mod nb_cells};
				red <- float((i mod nb_cells)* int(255 / nb_cells));
				green <- float((j mod nb_cells) * int(255 / nb_cells));
				blue <- float((k mod nb_cells) * int(255 / nb_cells));
				color <- rgb(red,green,blue);
			  }	
			}	
	    }
	}
	
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
	cells upper_cell_y update: neigbhours first_with (shape.location.y > each.shape.location.y);
	cells upper_cell_x update: neigbhours first_with (shape.location.x > each.shape.location.x);
	cells upper_cell_z update: neigbhours first_with (shape.location.z > each.shape.location.z);
	

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


experiment Display type: gui {
	output {
		display View1 type:opengl draw_env:false{
			species cells transparency:0.1;
			graphics "di"{
			 draw "black(0,0,0)" at:{0,0,0} color:°black bitmap:false;
			 draw "red(1,0,0)" at:{world.shape.width,0,0} color:°red bitmap:false;
			 draw "green(0,1,0)" at:{0,world.shape.height,0} color:°green bitmap:false;
			 draw "blue(0,0,1)" at:{0,0,world.shape.width} color:°blue bitmap:false;
			 draw "yellow(1,1,0)" at:{world.shape.width,world.shape.height,0} color:°yellow bitmap:false;
			 draw "magenta(1,0,1)" at:{world.shape.width,0,world.shape.depth} color:°magenta bitmap:false;
			 draw "cyan(0,1,1)" at:{0,world.shape.height,world.shape.depth} color:°cyan bitmap:false;
			 draw "white(1,1,1)" at:{world.shape.width,world.shape.height,world.shape.depth} color:°white bitmap:false;	
			}
		}
	}
}