model StupidModel1

global {
    init {
        create species: bug number: 100;
    }
} 

environment {
    grid stupid_grid width: 100 height: 100 torus: true;
}

entities {
    species bug {
        reflex basic_move {
            let place type: stupid_grid value: location as stupid_grid;
            let destination type: stupid_grid value: one_of ((place neighbours_at 4) where empty(each.agents));
            if condition: destination != nil {
                set location value: destination.location;
            }
        }
        aspect basic {
            draw shape: circle color: rgb('red') size: 1;
        }
    }
}

output {
    display stupid_display {
        grid stupid_grid;
        species bug aspect: basic;
    }
} 