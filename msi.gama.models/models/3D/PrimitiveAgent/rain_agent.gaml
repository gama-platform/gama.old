model Rain_Agent



/*Need to define in a global file
geometry env;
float rain_Rate <-100.0;
 */
 

import "global_Param.gaml"
	
global{
	
	
}	
	
	
entities {
	species Rain skills : [ moving ] {
				
		float z;	
		geometry shape <- circle (10) ;
		
		reflex augmente {
			set z value : z - 10 ;
			set shape <- shape add_z z ;
			if ( z <= 0 ) {
				if ( rnd ( 100 ) < rain_Rate ) {
					create Pluie number : 1 {
						set location <- { rnd ( env.width ) , rnd (env.height ) } ;
						set shape <- shape add_z z ;
						set z <-1000.0;			
					}
				}
				do die ;
			} else {
				do wander speed : 0.1 ;
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
	