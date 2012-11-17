/**
 *  watertest
 *  Author: Philippe
 *  Description: 
 */

model watertest

global {
	geometry testline;
	float h0;
	float h1;
	float h2;
	float h3;
	float h4;
	init {
 	set testline<-polyline([{0,0},{0,-20},{20,-20},{20,0},{30,-10},{40,0}]);
	set h0<-testline water_level_for 400;
 	write("h0 "+h0);
 	set h1<-testline water_level_for 200;
	write("h1 "+h1);
	set h2<-testline water_level_for 410;
	write("h2 "+h2);
 	set h3<-testline water_level_for 100;
	write("h3 "+h3);
	set h4<-testline water_level_for 25;
	write("h4 "+h4);
		
	}
	/** Insert the global definitions, variables and actions here */
}

environment {
	/** Insert the grids and the properties of the environment */
}

entities {
	/** Insert here the definition of the species of agents */
}

experiment watertest type: gui {
	/** Insert here the definition of the input and output of the model */
}
