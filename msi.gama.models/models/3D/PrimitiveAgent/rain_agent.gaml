model Rain_Agent

	
global{
geometry envRain;
int rain_Rate parameter: 'Amount of rain' <- 100 min: 1 max: 200 category: 'Rain' ;
int speedRain parameter: 'Rain speed' <- 1 min: 1 max: 100 category: 'Rain' ;
}	
	
	
entities {
	species Rain skills : [ moving ] {
				
		float z;	
		geometry shape <- circle (envRain.width/1000) ;
		geometry shape <- geometry (point([1,1]));
		
		reflex augmente {
			set z value : z - speedRain * envRain.width/100 ;
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
				//do wander speed : envRain.width/100 ;
			}
		}
		
		aspect circle {
			draw geometry: shape color: rgb('blue');
		}
			
		aspect sphere {
			draw geometry: shape color: rgb('blue') depth:envRain.width/1000 ;
		}
	}
	}
	
	output ;
	