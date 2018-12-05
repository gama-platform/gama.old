/**
* Name: Comodel0 
* Author: Benoit Gaudou & Damien Philippon
* Description: Couple two models: weather and plant growth 
* - create 4 instances of the plantGrow model and populate a landscape.
* Tags: comodel
*/

model coModel

import "../Adapters/weather.gaml" as weather
import "../Adapters/plantGrowAdapter.gaml" as plantGrowAdapter

global {
	geometry shape <- square(200#m);
	
	weather weather_simu ;
	list<plantGrowAdapter> plantGrowAdapter_simu;
		
	init {	
		create weather.weather_coModeling with:[grid_size::40, env_size::200#m];
		weather_simu <- first(weather.weather_coModeling).simulation;	

		create plantGrowAdapter.Adapter number: 4 with:[grid_size::40, env_size::100#m] {
			write "" + int(self) + " " + self;
			centroid <- {(int(self) mod 2)*100,int(int(self) / 2)*100};
			do transform_environment;	
		}
		plantGrowAdapter_simu <- plantGrowAdapter.Adapter collect each.simulation; 		
	}

	reflex simulate_micro_models_weather {
		ask weather_simu {
			do _step_;
		}	
	}

	reflex coupling {
		
		ask weather_simu.plotWeather {
			list<plotGrow> overlapped_plots <- (plantGrowAdapter_simu accumulate(each.plotGrow)) where (each.shape.location overlaps self);
						
			ask overlapped_plots {
				available_water <- available_water + myself.rain;
			}
		}
	}
	
	reflex simulate_micro_models_plantGrow {
		ask plantGrowAdapter_simu {
			do _step_;
		}		
	}
}

experiment coModel type: gui {
	output {
		display w {
			agents "weather" value: weather_simu.plotWeather ;
		}
		display pG {			
//			agents "ppG" value: plantGrowAdapter.Adapter accumulate (each.simulation.plotGrow);
			agents "ppG0" value: plantGrowAdapter_simu[0].plotGrow;
			agents "ppG1" value: plantGrowAdapter_simu[1].plotGrow;
			agents "ppG2" value: plantGrowAdapter_simu[2].plotGrow;
			agents "ppG3" value: plantGrowAdapter_simu[3].plotGrow;
			
		}		
	}
}
