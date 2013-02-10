/**
 *  comodel
 *  Author: Administrator
 *  Description: 
 */

model comodel

global {

	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 250;
//	comodel yourmodel<-comodel("predator.gaml");
	init{
		create MathsSolver number:2{
			do helloWorld;
		}
		create m number:2;
	} 
}

environment  width: width_and_height_of_environment height: width_and_height_of_environment;

entities {
species m {
	list n;
	float t;
	float K;
	
}
}

experiment comodel type: gui {
	output {
		display d refresh_every:1{
			
		}		
	}
}
