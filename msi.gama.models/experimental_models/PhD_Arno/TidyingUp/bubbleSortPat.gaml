/**
 *  bubbleSortPat
 *  Author: Arno
 *  Description: 
 */

model bubbleSortPat


global {

int nb_patates<-20;

geometry shape <- rectangle(nb_patates,nb_patates);

bool change <- true;

init {

	int j<-0;
	int i<-0;
	
	loop times: nb_patates{
		create cell number: nb_patates {
			location <-{i mod nb_patates,j mod nb_patates};
			//shape <- square(1);
			create patate {
				saturation <- rnd(100)/100.0;
				brightness <- rnd(100)/100.0;
				color <- hsb(0.66,saturation,brightness);
				my_cell <- myself;
				location <- myself.location;
				myself.my_patate <- self;
			}
			
			j <- j+1;	
		}
		i<-i+1;
	}
	ask cell {
		list<cell> neigbhours <- cell at_distance (1.1);
		write name + " -> " + neigbhours;
		cell_up <-neigbhours first_with (shape.location.y > each.shape.location.y);
		cell_east <-neigbhours first_with (shape.location.x < each.shape.location.x);
	}
		

}

reflex end {
	if (not change) {
		do pause;	
	} 
	change <- false;
	}
}

species patate{
	rgb color;
	float saturation;
	float brightness;
	cell my_cell;
	patate patate_cell_y update: patate(my_cell.cell_up = nil ? nil : my_cell.cell_up.my_patate);
	patate patate_cell_x update:  patate(my_cell.cell_east = nil ? nil :my_cell.cell_east.my_patate);
	

	/*reflex swap_y when: patate_cell_y != nil and saturation < patate_cell_y.saturation and (time mod 2 = 0){ 
		point tmp1Loc <-location;
		location <- my_cell.cell_up.location; 
		my_cell.cell_up.my_patate <- self;
    	patate_cell_y.location<-tmp1Loc; 
    	my_cell.my_patate <- patate_cell_y;
	    change <- true;	
    }
    */
    reflex swap_x when: patate_cell_x != nil and brightness < patate_cell_x.brightness and (time mod 2 = 1){ 
		point tmp1Loc <-location;
		location <- my_cell.cell_east.location; 
		my_cell.cell_east.my_patate <- self;
    	patate_cell_x.location<-tmp1Loc; 
    	my_cell.my_patate <- patate_cell_x;	
    	my_cell <- my_cell.cell_east;
	    change <- true;	
    }

	aspect default {
		draw circle(0.5) color:color border:color at:location;
	}
}

species cell{
	patate my_patate;
	cell cell_up;
	cell cell_east;
	aspect default {
		draw square(1) color:°yellow;
	}
}


experiment Display type: gui {
	output {
		display View1 type:opengl {
			species cell;
			species patate;
		}
	}
}