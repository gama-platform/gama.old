/**
 *  Signal
 *  Author: Julien
 *  Description: This example shows a quick overview of how to use signals. You can change the parameters
 * during the simulation to see the effects on the signals. 
 */
model Signal

global {
	int gridsize <- 20;
	geometry shape <- rectangle(10#m,10#m);
	float decayValue<-0.1;
	float proportionValue <- 0.5;
	float variationValue <- 0.0;
	string propagationMode <- 'gradient';
	int timeVar <- 0;
	
	init {
		create greenAgent number:1 with: (location: {5.0,5.0});
		create blueAgent number:1 with: (location: {3.0,3.0});
	}
}


grid cells height: gridsize width: gridsize neighbours:8 use_regular_agents: false use_individual_shapes: false {
	const neighbours type: list of: cells <- self neighbors_at 1;
	
	reflex updateTime{
		if (timeVar=20){
			timeVar <- 0;
		}
		else {
			timeVar <- timeVar+1;
		}
	}

	aspect ShowOverlap{
		if ((greenSignal>10) and (blueSignal>10)){
			draw square(1) color:rgb(100,int(greenSignal),int(blueSignal));
		} 
		else {
			draw square(1) color:rgb(0,int(greenSignal),int(blueSignal));
		}
	}
	
	aspect SubstractOverlap{
		if (greenSignal>blueSignal){
			draw square(1) color:rgb(0,int(greenSignal)-int(blueSignal),0);
		} 
		else {
			draw square(1) color:rgb(0,0,int(blueSignal)-int(greenSignal));
		}
	}
	
	aspect AddOverlap{
		draw square(1) color:rgb(0,int(greenSignal),int(blueSignal));
	}
} 

species greenAgent skills:[moving] {
	signal greenSignal update:(timeVar=0) ? 5*240 : 0 decay:decayValue environment:cells range:5 propagation:gradient proportion:proportionValue variation:variationValue;
	reflex movement {
		do wander;
	}
	aspect default{
		draw circle(0.1) color:#green;
	}
}

species blueAgent skills:[moving] {
	signal blueSignal update:(timeVar=15) ? 5*240 : 0 decay:decayValue environment:cells range:5 propagation:gradient proportion:proportionValue variation:variationValue;
	reflex movement {
		do wander;
	}
	aspect default{
		draw circle(0.1) color:#blue;
	}
}

experiment ShowOverlap type: gui {
	parameter "decayValue : " var:decayValue;
	parameter "variation (between 0 and 1) : " var:variationValue min:0.0 max:1.0;
	parameter "proportion (between 0 and 1) : " var:proportionValue min:0.0 max:1.0;
	output {
		display a type: opengl {
			species cells aspect:ShowOverlap;
			species blueAgent aspect:default;
			species greenAgent aspect:default;
		}
	}
}

experiment SubstractOverlap type: gui {
	parameter "decayValue : " var:decayValue;
	parameter "variation (between 0 and 1) : " var:variationValue min:0.0 max:1.0;
	parameter "proportion (between 0 and 1) : " var:proportionValue min:0.0 max:1.0;
	output {
		display a type: opengl {
			species cells aspect:SubstractOverlap;
			species blueAgent aspect:default;
			species greenAgent aspect:default;
		}
	}
}

experiment AddOverlap type: gui {
	parameter "decayValue : " var:decayValue;
	parameter "variation (between 0 and 1) : " var:variationValue min:0.0 max:1.0;
	parameter "proportion (between 0 and 1) : " var:proportionValue min:0.0 max:1.0;
	output {
		display a type: opengl {
			species cells aspect:AddOverlap;
			species blueAgent aspect:default;
			species greenAgent aspect:default;
		}
	}
}