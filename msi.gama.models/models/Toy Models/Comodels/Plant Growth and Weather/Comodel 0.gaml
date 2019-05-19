/**
* Name: Comodel0 
* Author: Benoit Gaudou & Damien Philippon
* Description: First co-Model
*   - instanciate a co-model in a model
*   - step it  
* Tags: comodel
*/

model coModel

import "Weather.gaml" as weather


global {
	
	weather weather_simu ;
		
	init {
		create weather."Weather Co-Modeling" ;
		weather_simu <- first(weather."Weather Co-Modeling").simulation; 
	}

	reflex simulate_micro_models
	{
		ask weather_simu
		{
			do _step_;
		}
	}
}

experiment coModel type: gui {
	output {
		
	}
}
