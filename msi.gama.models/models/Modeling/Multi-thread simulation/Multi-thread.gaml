/**
* Name: multithread
* Author: Patrick Taillandier
* Description: Shows how to use the multi-thread capabilities of GAMA
* Tags: multi-thread, parallel 
*/

model multithread

global {
	init {
		create simple_agent number: 10;
		
		//parallelize the computation ask by the agent
		ask simple_agent parallel: true {
			var1 <- exp(-size);
		}
	}
}

species simple_agent parallel: true{
	int size <- rnd(1,10) update: size + (flip(0.5) ? 1 : 0);
	float var1;
	reflex behavior {
		var1 <- exp(-size);
		write string(cycle) + ":" +name + "->" + var1;
	}
}

grid cell width: 5 height: 5 parallel: true {
	float val <- rnd(1.0) max: 1.0;
	
	reflex behavior {
		val <- val + rnd(0.002) - 0.0009;
		color <- rgb((1.0 - val)*255,(1.0 - val)*255,(1.0 - val)*255);
	}
}
experiment multithread type: gui {
	output {
		display map type:2d antialias:false{
			grid cell border: #black;
		}
	}
}