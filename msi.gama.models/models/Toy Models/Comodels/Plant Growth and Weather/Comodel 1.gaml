/**
* Name: Comodel0 
* Author: Benoit Gaudou & Damien Philippon
* Description: Second co-Model
*  - instanciate a sub-model in a model
*  - step it
*  - display its agents
*  - compute indicators on it 
* Tags: comodel
*/

model coModel

import "Weather.gaml" as weather


global {
	
	weather weather_simu ;
		
	init {
		create weather."Weather Co-Modeling" with: [grid_size::30,write_in_console_step::false];
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

experiment "CoModel" type: gui {
	output {
		display d type:2d antialias:false{
			agents "weather" value: weather_simu.plotWeather ;
		}
		
		display data  type: 2d {
			chart "rain" type: series {
				data "rainfall" value: sum(weather_simu.plotWeather accumulate (each.rain));
			}
		}
		
	}
}
