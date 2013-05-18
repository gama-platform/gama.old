model StupidModel2

global {
    init {
        create bug number: 100;
    }
}

environment width: 100 height: 100 {
    grid stupid_cell width: 100 height: 100 torus: false neighbours: 4 { 
        rgb color <- rgb('black');
    }
}

entities {
    species bug {
        float size <- 1.0;
        rgb color <- rgb ([255, 255, 255]) update: rgb ([255, 255/size, 255/size]);
        
        reflex basic_move {
            let place type: stupid_cell <- (location as stupid_cell);
            let destination type: stupid_cell <- one_of ((place neighbours_at 4) where empty(agents overlapping each));
            if (destination != nil) {
                set location <- destination.location;
            }
        }
                
        reflex grow {
            set size <- (size + 0.1);
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