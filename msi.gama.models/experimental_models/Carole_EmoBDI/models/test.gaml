/**
 *  test
 *  Author: carole
 *  Description: 
 */

model test

import "bdimodel.gaml"

/* Insert your model definition here */


global {
	int displatTextSize <-4;
	
	int dimensions <- 100;
	
	bool stop <- false;
	
	init {
		create base number: 1;
		create fireArea number:3;
		create waterArea number:1;
		create helicopter number: 1;
		//do pause;
	}
	
	reflex pausing {
		if stop {
			do pause;
		}
	}
}

grid space width: dimensions height: dimensions neighbours: 8 use_regular_agents: false frequency: 0{
		const color type: rgb <- rgb("black");
	}

species helicopter skills: [moving]  parent: bdiagent {

	predicate a <- new_predicate('a');
	predicate b <- new_predicate('b');
	predicate c <- new_predicate('c');

	predicate nofire <- new_predicate('no fire');
	predicate nowater <- new_predicate('no water');
	float waterLevel <- 1.0;

	init{
		
	}
	
	plan fightfire when:is_current_intention(nofire) {
		waterLevel <- waterLevel - 0.1;
		write 'fight fire, water level = '+waterLevel;
	}
	
	reflex debug {
		/*do add_rule('r',a,b);
		do add_belief(a);
		write 'avant';
		do display_bdie;
		do update_kb;
		write 'apres';
		do display_bdie;
		
		do add_desire(b);
		do joy_pred;
		write 'after emotions';
		do display_bdie;
		
		predicate happy <- new_predicate('happy') with_priority 3;
		do add_after('run effect','running',happy,0.8);
		do add_desire(happy);
		do hope_trigger;
		write 'test hope';
		do display_bdie;*/
		
		do add_after('rule fire uses water','fightfire',nowater,1-waterLevel);
		do add_desire(nofire);
		do add_desire(new_predicate('no water',false));
		do display_bdie;
		do plans_emotion;
		do display_bdie;
		
		write 'fini';
		
		//stop <- true;
	}
	
	aspect base {
		draw circle(5) color: #green;
		draw 'new emo '+new_emotion at: {10,10} size:4 color: #black;
	}	
}



species fireArea{
	float size <-5.0;
	aspect base {
	  draw circle(size) color: #red;
	}
}

species waterArea{
	float size <-10.0;
	aspect base {
	  draw circle(size) color: #blue;		
	}
}

species base {
	float size <- 5.0;
	aspect base {
		draw square(size) color: #orange;
	}
}

experiment K_RESCUE type: gui {

	output {		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display view1 { //type: opengl { 
			species fireArea aspect:base;
			species waterArea aspect:base;
			species base aspect: base;
			species helicopter aspect: base;
		}
	}
}
