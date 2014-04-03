model bubblesort3D


global {

int nb_cells<-20;
int cell_size<-10;

geometry shape <- square(nb_cells*cell_size);

bool change <- true;

init {

	int j<-0;
	int i<-0;
	int k<-0;
	
	loop times: nb_cells{
		loop times: nb_cells{
			create cells number: nb_cells {
				location <-{cell_size/2 + i mod nb_cells * cell_size,cell_size/2 + j mod nb_cells*cell_size, cell_size/2 +k mod nb_cells*cell_size};
				red <- rnd(255);
				green <- rnd(255);
				blue <- rnd(255);
				color <- rgb(red,green,blue);
				j <- j+1;	
			}
			i<-i+1;
	    }
		k<-k+1;
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
	int red;
	int green;
	int blue;
	list<cells> neigbhours update: cells at_distance (cell_size*1.1);
	cells upper_cell_y update: neigbhours first_with (shape.location.y > each.shape.location.y);
	cells upper_cell_x update: neigbhours first_with (shape.location.x > each.shape.location.x);
	cells upper_cell_z update: neigbhours first_with (shape.location.z > each.shape.location.z);
	

	reflex swap_z when: upper_cell_z != nil and blue > upper_cell_z.blue{ 
		point tmp1Loc <-location;
		location <- upper_cell_z.location;  
    	upper_cell_z.location<-tmp1Loc; 	
	    change <- true;	
    }
	
	reflex swap_y when: upper_cell_y != nil and red > upper_cell_y.red{ 
		point tmp1Loc <-location;
		location <- upper_cell_y.location;  
    	upper_cell_y.location<-tmp1Loc; 	
	    change <- true;	
    }
    
    reflex swap_x when: upper_cell_x != nil and green > upper_cell_x.green{ 
		point tmp1Loc <-location;
		location <- upper_cell_x.location;  
    	upper_cell_x.location<-tmp1Loc; 	
	    change <- true;	
    }

	aspect default {
		draw cube(cell_size) color:color border:color at:location;
	}	
}


experiment Display type: gui {
	output {
		display View1 type:opengl {
			species cells;
			
			
		}
	}
}