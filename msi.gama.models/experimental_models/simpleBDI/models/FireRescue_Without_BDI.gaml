/**
 *  new
 *  Author: Arno
 *  Description: 
 */

model FireRescueWithoutBDI

global {
	init {
		create helicopter number: 10 {
			waterValue <-0.0;
			needToTakeWAter <-true;
		}	
		create fireArea number:3;
		create waterArea number:2;
	}
}


species helicopter skills: [moving] {
	rgb color;
	float waterValue;
	bool needToTakeWAter;

	reflex goToTakeWater when: (needToTakeWAter){
		do goto target:waterArea closest_to(self);
		if(location = (waterArea closest_to(self)).location){
			waterValue <- waterValue+1;
			if(waterValue = 255){
			  needToTakeWAter <-false;
		    }
		}
		
	}
	
	reflex goToStopFire when: (!needToTakeWAter){
		do goto target:fireArea closest_to(self);	
		if(location = (fireArea closest_to(self)).location){
			waterValue <- waterValue-1;
			if(waterValue = 0){
			  needToTakeWAter <-true;
		    }
		}
		
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