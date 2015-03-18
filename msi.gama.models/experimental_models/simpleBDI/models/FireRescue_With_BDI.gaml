/**
 *  new
 *  Author: Arno
 *  Description: 
 */

model FireRescueWithBDI

global {
	init {
		create helicopter number: 1 {
			waterValue <-0.0;
			needToTakeWAter <-true;
		}	
		create fireArea number:3;
		create waterArea number:2;
	}
}


species helicopter skills: [moving] control: simple_bdi{
	rgb color;
	float waterValue;
	bool needToTakeWAter;
	
	
	plan findFire priority:3{
		write "findFire";
	}
	
	plan stopFire priority:2{
		write "stopFire";
	} 

    plan gotoTakeWater priority:1{
    	write "gotoTakeWater";
    }


	aspect base {
		draw circle(1) color: rgb(0,0,waterValue);	
	}
}

species fireArea{
	float size <-5.0;

	aspect base {
	  draw circle(size) color: #red;
	}
}

species waterArea{
	float size <-5.0;
	aspect base {
	  draw circle(size) color: #blue;		
	}
}

experiment RESCUE type: gui {

	output {		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display gridTextured { 
			species fireArea aspect:base;
			species waterArea aspect:base;
			species helicopter aspect: base;
		}
	}

}