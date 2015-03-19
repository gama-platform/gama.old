/**
 *  new
 *  Author: Arno
 *  Description: 
 */

model FireRescueWithBDI

global {
	int displatTextSize <-4;
	init {
		create fireArea number:3;
		create waterArea number:2;
		create helicopter number: 1 {
			waterValue <-0.0;
			needToTakeWAter <-true;
			do add_belief(new_predicate("has water", false));
			do add_belief(new_predicate("fire", first(fireArea).location,first(fireArea).extinguish::false,1));		
			do add_desire(new_predicate("patrol") with_priority 1);
			do add_desire(new_predicate("has water", true) with_priority 3);
			do add_desire(new_predicate("fire", 255) with_priority 2);
		}		
	}
}


species helicopter skills: [moving] control: simple_bdi{
	rgb color;
	float waterValue;
	bool needToTakeWAter;
	
	plan findFire {
		write " plan findFire";
	}
	
	plan stopFire  {
		write "stopFire";
	} 

    plan gotoTakeWater{
    	write "gotoTakeWater";
    }

	aspect base {
		draw circle(1) color: rgb(0,0,waterValue);	
	}
	
	aspect bdi {
		draw circle(1) color: rgb(0,0,waterValue);
		draw ("B:" + length(belief_base) + ":" + belief_base) color:#black size:displatTextSize; 
		draw ("D:" + length(desire_base) + ":" + desire_base) color:#black size:displatTextSize at:{location.x,location.y+displatTextSize}; 
		draw ("I:" + length(intention_base) + ":" + intention_base) color:#black size:displatTextSize at:{location.x,location.y+2*displatTextSize}; 
		draw ("curIntention:" + get_current_intention()) color:#black size:displatTextSize at:{location.x,location.y+3*displatTextSize}; 	
	}
}

species fireArea{
	float size <-5.0;
	bool extinguish <- false;

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
		display view1 type:opengl{ 
			species fireArea aspect:base;
			species waterArea aspect:base;
			species helicopter aspect: bdi;
		}
	}

}