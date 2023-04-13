/**
* Name: Issue3685
* Demonstrates that the issue reported in #3685 does not exist anymore (shape files could not be saved twice in a row). 
* Author: Alexis Drogoul
* Tags: 
*/


model Issue3685

/* Insert your model definition here */


global {
	
	init {
		save self to:"test.shp" format:"shp" rewrite:true;
	}
}

experiment a;