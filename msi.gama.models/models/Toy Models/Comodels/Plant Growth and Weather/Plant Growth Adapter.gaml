/**
* Name: plantGrowAdapter
* Author: ben
* Description: 
* Tags: comodel
*/

model plantGrowAdapter

import "Plant Growth.gaml"

experiment Adapter type: gui
{
	point centroid <- {100,100};

	action transform_environment {		
		loop p over: plotGrow {
			//p.shape <- p.shape translated_by centroid;
			 
			ask p {
				shape <- shape translated_by myself.centroid;
				//location <- location + myself.centroid;
			}	
			
		}
	}
	
	output {}
}