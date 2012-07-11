model StupidModel1

global {
    init {
        create bug number: 100;
    }
} 

environment width: 100 height: 100 {
    grid stupid_cell width: 100 height: 100 torus: false neighbours: 4;
}

entities {
    species bug {
        reflex basic_move {
            let place type: stupid_cell <- (location as stupid_cell);
            let destination type: stupid_cell <- one_of ((place neighbours_at 4) where empty(each.agents));
            if (destination != nil) {
                set location <- destination.location;
            }
        }
        aspect basic {
            draw shape: circle color: rgb('red') size: 1;
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
