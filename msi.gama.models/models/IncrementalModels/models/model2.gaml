/**
 *  model2
 *  Author: hqnghi
 *  Description: 
 */

model model2

/* Insert your model definition here */
global{
	file shape_file_buildings <- file('../includes/building.shp');
	geometry shape<-envelope(shape_file_buildings);
	init{
		create Buildings from: shape_file_buildings with: [type::string(read ('NATURE'))] {       
			if type='Industrial' {
				mycolor <- rgb('blue') ;
			}  
	  	}
	}
}

entities{
	species Buildings{
		string type;
		rgb mycolor;
		aspect asp1{
			draw shape color:mycolor;
		}
	}
}
experiment exp1 type:gui{
	output{
		display disp1 refresh_every:1{
			species Buildings aspect:asp1;			
		}
	}
}