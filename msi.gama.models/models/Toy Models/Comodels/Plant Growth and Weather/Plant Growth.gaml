/**
* Name: plantGrow
* Author: Benoit Gaudou
* Description: A simple model of plant growth
* Tags: ecology, 
*/

model plantGrow

global {
	int grid_size <- 20;
	float env_size <- 100 #m;
	geometry shape <- square(env_size);
		
	float CARRYING_CAPACITY <- 50.0;
	float RESERVE_CAPACITY <- 50.0;
	
	init {
		write "[PLANT GROWTH] Initialization";		
	}
}

grid plotGrow height: grid_size width: grid_size neighbors: 8 {
	float biomass <- rnd(CARRYING_CAPACITY) max: CARRYING_CAPACITY ;
	float available_water <- rnd(RESERVE_CAPACITY) max: RESERVE_CAPACITY ;
	
	rgb color <- rgb(0,255*biomass/CARRYING_CAPACITY,0)
				update: rgb(0,255*biomass/CARRYING_CAPACITY,0);		
	
	reflex grow {
		if( available_water >0 ) {
			biomass <- min([CARRYING_CAPACITY, biomass + 1]);
			available_water <- max([0, available_water - 1]);
		} else {
			biomass <- max([0, biomass - 1]);
		}
	}

}

experiment "Plant Growth" type: gui {
	output {
		display d type:2d antialias:false{
			grid plotGrow border: #black;
		}
		display biomass  type: 2d {
			chart "levels" type: series {
				data "water" value: sum(plotGrow collect(each.available_water)) color: #blue;
				data "biomass" value: sum(plotGrow collect(each.biomass)) color: #green;				
			}
		}
	}
}

experiment "Plant Growth Co-Modeling" type: gui {
}

