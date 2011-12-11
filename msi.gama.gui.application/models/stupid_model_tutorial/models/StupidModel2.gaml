model StupidModel2

global {
    init {
        create species: bug number: 100;
    }
}

environment {
    grid stupid_grid width: 100 height: 100 torus: true {
        var color type: rgb init: rgb('black');
    }
}

entities {
    species bug {
        var size type: float init: 1;
        var color type: rgb value: rgb [255, 255/size, 255/size];
        
        reflex basic_move {
            let place type: stupid_grid value: location as stupid_grid;
            let destination type: stupid_grid value: one_of ((place neighbours_at 4) where empty(each.agents));
            if condition: destination != nil {
                set location value: destination.location;
            }
        }
                
        reflex grow {
            set size value: size + 0.1;
        }
        
        aspect basic {
            draw shape: circle color: color size: size;
        }
    }
}

output {
    display stupid_display {
        grid stupid_grid;
        species bug aspect: basic;
    }
}