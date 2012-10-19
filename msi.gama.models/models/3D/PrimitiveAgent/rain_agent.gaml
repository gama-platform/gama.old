model Rain_Agent

import "global_Param.gaml"
	
global{
geometry envRain;
float rain_Rate <-100.0;
float speedRain <-1.0;	
}	
	
	
entities {
	species Rain skills : [ moving ] {
				
		float z;	
		geometry shape <- circle (envRain.width/1000) ;
		
		reflex augmente {
			set z value : z - (envRain.width/20)*speedRain ;
			set shape <- shape add_z z ;
			if ( z <= 0 ) {
				if ( rnd ( 100 ) < rain_Rate ) {
					create Rain number : 1 {
						set location <- { rnd ( envRain.width ) , rnd (envRain.height ) } ;
						set shape <- shape add_z z ;
						set z <-envRain.width/2;			
					}
				}
				do die ;
			} else {
				do wander speed : 10 ;
			}
		}
		
		aspect circle {
			draw geometry: shape color: rgb('blue');
		}
			
		aspect sphere {
			draw geometry: shape color: rgb('blue') z:z ;
		}
	}
	}
	
	output ;
	