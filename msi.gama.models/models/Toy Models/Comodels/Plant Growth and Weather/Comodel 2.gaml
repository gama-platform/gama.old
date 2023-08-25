/**
* Name: Comodel0 
* Author: Benoit Gaudou & Damien Philippon
* Description: Couple two models: weather and plant growth
* - create interaction between models entities: water on rain will full the water reserve
* Tags: comodel
*/

model coModel

import "Weather.gaml" as weather
import "Plant Growth.gaml" as plantGrow


global {
	
	weather weather_simu ;
	plantGrow plantGrow_simu;
		
	init {
		create weather."Weather Co-Modeling" with: [grid_size::20,write_in_console_step::false];
		weather_simu <- first(weather."Weather Co-Modeling").simulation; 
		
		create plantGrow."Plant Growth Co-Modeling" with: [grid_size::40];
		plantGrow_simu <- first(plantGrow."Plant Growth Co-Modeling").simulation; 		
	}

	reflex simulate_micro_models_weather {
		ask weather_simu
		{
			do _step_;
		}	
	}
	
	reflex coupling {
		
		ask weather_simu.plotWeather {
			list<plotGrow> overlapped_plots <- plantGrow_simu.plotGrow where (each.shape.location overlaps self);
						
			ask overlapped_plots {
				available_water <- available_water + myself.rain;
			}
		}
	}
	
	reflex simulate_micro_models_plantGrow {
		ask plantGrow_simu
		{
			do _step_;
		}		
	}
}

experiment "CoModel" type: gui {
	output {
		display w type:2d antialias:false{
			agents "weather" value: weather_simu.plotWeather ;
		}
		display pG type:2d antialias:false{
			agents "plantGrowth" value: plantGrow_simu.plotGrow ;
		}		
		
		display data  type: 2d  {
			chart "rain" type: series {
				data "rainfall" value: sum(weather_simu.plotWeather accumulate (each.rain));
			}
		}
		
	}
}
