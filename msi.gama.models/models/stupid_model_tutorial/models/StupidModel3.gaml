model StupidModel3

global {
    init {
        create bug number: 100;
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
            let destination type: stupid_cell <- one_of ((myPlace neighbours_at 4) where empty(agents overlapping each));
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
            draw circle(size) color: color;
        }
    }
}

experiment stupidModel type: gui {
	output {
	    display stupid_display {
	        grid stupid_cell;
	        species bug aspect: basic;
	    }
	}
}