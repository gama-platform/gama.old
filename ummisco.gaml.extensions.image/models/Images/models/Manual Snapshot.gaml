/**
* Name: ManualSnapshot
* Shows how to make (and use) snapshots of displays using images 
* Author: A. Drogoul, 2023
* Tags: image, clipboard, snapshot
*/
model ManualSnapshot

global {
	
	image background <- image(100,100, #white) ;

	init {
		create bug number: 100;
	}

}

species bug skills:[moving]{
	
	rgb color <- rnd_color(255);
	
	reflex {
		do wander amplitude: 20.0 speed: 0.1;
	}
	
	aspect default {
		draw circle(1) color: color;
	}
}

experiment "Snapshot Depth" type: gui {
	
	reflex when: (cycle > 1) and every(100 #cycle) {
		ask simulation {
			// We choose a neutral background
			background <- grayscale(brighter(snapshot("My Display")));
		}
	}
	
	
	output synchronized: true {
		display "My Display" type:3d axes: false{
			image background refresh: true;
			species bug;
		}
	}

}

experiment "Save Snapshots" type: gui {
	
	int i <- 0;
	
	reflex when: (cycle > 1) and every(100 #cycle) {
		ask simulation {
			// We choose a neutral background
			save (snapshot("My Display")) to: "snapshots/snapshot" + myself.i + ".png";
		}
		i <- i + 1;
	}
	
	
	output synchronized: true {
		display "My Display" type:3d axes: false{
			species bug;
		}
	}

}

experiment "Copy Snapshot to Clipboard" type: gui {
	
	int i <- 0;
	
	
	output synchronized: true {
		display "My Display" type:3d axes: false{
			species bug;
			event "c" {if (copy_to_clipboard(snapshot(simulation, "My Display"))) {write "Snapshot copied to the clipboard !" ;}}
		}
	}

}
