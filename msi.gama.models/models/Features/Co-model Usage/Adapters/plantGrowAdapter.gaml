/**
* Name: plantGrowAdapter
* Author: ben
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model plantGrowAdapter

import "plantGrow.gaml"

experiment Adapter type: gui
{
	point centroid <- {100,100};

	action transform_environment {		
		loop p over: plotGrow {
		
			ask p {
				shape <- shape translated_by myself.centroid;
				// location <- location + myself.centroid;
			}	
		}
	}
	
	output {}
}