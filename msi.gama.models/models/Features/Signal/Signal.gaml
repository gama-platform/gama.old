/**
* Name: Signal
* Author: Julien Mazars
* Description: A model which shows how to use the signal variable, displaying it on a grid according to a frequence. Three
* 	experiments are proposed : one which highlight the overlapping in red named showOverlap, one removing the 
* 	overlapping signals named SubstractOverlap and the last one allowing overlapping but not doing highlight
* Tags : Signal, Grid
*/
model Signal

global {
	//Parameter for the size of the grid
	int gridsize <- 20;
	
	//Shape of the environment
	geometry shape <- rectangle(10#m,10#m);
	
	//Parameters for the signals
	float decayValue<-0.1;//Amount to remove from intensity of a place
	float proportionValue <- 0.5; //Intensity shared by the neighbours
	float variationValue <- 0.0; //variation, ie the decrease of intensity that occurs between each place
	string propagationMode <- 'gradient'; //propagation mode among gradient and diffusion
	int timeVar <- 0; //Initialise the time to used it for the Frequency of the signal
	
	init {
		//greenAgent that will display a green signal
		create greenAgent number:1 with: (location: {5.0,5.0});
		//blueAgent that will display a blue signal
		create blueAgent number:1 with: (location: {3.0,3.0});
	}
}

//Grid of cells on which the signal will be spread by the greenAgent and blueAgent
grid cells height: gridsize width: gridsize neighbors:8 use_regular_agents: false use_individual_shapes: false {
	const neighbours type: list of: cells <- self neighbors_at 1;
	
	//Update the time to compute the frequency of launching a signal
	reflex updateTime{
		if (timeVar=20){
			timeVar <- 0;
		}
		else {
			timeVar <- timeVar+1;
		}
	}
	
	//Aspect for the ShowOverlap experiment
	aspect ShowOverlap{
		if ((greenSignal>10) and (blueSignal>10)){
			draw square(1) color:rgb(100,int(greenSignal),int(blueSignal));
		} 
		else {
			draw square(1) color:rgb(0,int(greenSignal),int(blueSignal));
		}
	}
	
	//Aspect for the SubstractOverlap experiment
	aspect SubstractOverlap{
		if (greenSignal>blueSignal){
			draw square(1) color:rgb(0,int(greenSignal)-int(blueSignal),0);
		} 
		else {
			draw square(1) color:rgb(0,0,int(blueSignal)-int(greenSignal));
		}
	}
	
	//Aspect for the AddOverlap experiment
	aspect AddOverlap{
		draw square(1) color:rgb(0,int(greenSignal),int(blueSignal));
	}
} 
//Species greenAgent that creates a green signal according to the time and moves randomly
species greenAgent skills:[moving] {
	signal greenSignal update:(timeVar=0) ? 5*240 : 0 decay:decayValue environment:cells range:5 propagation:gradient proportion:proportionValue variation:variationValue;
	reflex movement {
		do wander;
	}
	aspect default{
		draw circle(0.1) color:#green;
	}
}

//Species blueAgent that creates a blue signal according to the time and moves randomly
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