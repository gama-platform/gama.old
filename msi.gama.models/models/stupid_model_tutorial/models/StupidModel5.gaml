model StupidModel5

global {
    int numberBugs <- 100;
    float globalMaxConsumption <- 1.0;
    float globalMaxFoodProdRate <- 0.01;
    
    init {
        create bug number: numberBugs;
    }
}

environment width: 100 height: 100 {
    grid stupid_cell width: 100 height: 100 torus: false neighbours: 4 { 
        rgb color <- rgb('black');
        float maxFoodProdRate <- 0.01;
        float foodProd <- (rnd(1000) / 1000) * 0.01;
        float food <- 0.0 update: food + foodProd;
    }
}

entities {
    species bug {
        float size <- 1.0;
        rgb color <- rgb ([255, 255, 255]) update: rgb ([255, 255/size, 255/size]);
        float maxConsumption <- 1.0;
        stupid_cell myPlace <- (location as stupid_cell); 

        reflex basic_move {
            let destination type: stupid_cell <- one_of ((myPlace neighbours_at 4) where empty(each.agents));
            if (destination != nil) {
                 set myPlace <- destination;
                 set location <- myPlace.location;                                                
            }
        }
        reflex grow {
            let transfer <- min ([maxConsumption, myPlace.food]);
            set size <- size + transfer;
            set myPlace.food <- myPlace.food - transfer;
        }
        
        aspect basic {
            draw shape: circle color: color size: size;
        }
    }
}

experiment stupidModel type: gui {
    parameter 'numberBugs' var: numberBugs;
    parameter 'globalMaxConsumption' var: globalMaxConsumption;
    parameter 'globalMaxFoodProdRate' var: globalMaxFoodProdRate;	
    
	output {
	    display stupid_display {
	        grid stupid_cell;
	        species bug aspect: basic;
	    }
	    inspect name: 'Species' type: species refresh_every: 5;
	}
}