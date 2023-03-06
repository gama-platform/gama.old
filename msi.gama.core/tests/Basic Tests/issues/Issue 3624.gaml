/**
* Name: Issue3624
* A simple model to verify that issue #3624 has been addressed  
* Author: A. Drogoul
* Tags: 
*/


model Issue3624

global {
	init {
		int i <- 100;
		// The custom label should be added to the error raised by assert
		assert i < 100 label: "Var i should be greater than 100 !";
	}
}

experiment "Run me"; 