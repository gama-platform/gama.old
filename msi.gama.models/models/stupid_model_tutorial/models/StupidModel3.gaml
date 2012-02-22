model StupidModel3

global {
    init {
        create species: bug number: 100;
    }
}

environment {
    grid stupid_grid width: 100 height: 100 torus: true {
        var color type: rgb init: rgb('black');
        var maxFoodProdRate type: float value: 0.01;
        var foodProd type: float value: (rnd(1000) / 1000) * 0.01;
        var food type: float init: 0.0 value: food + foodProd;
    }
}

entities {
    species bug {
        var size type: float init: 1;
        var color type: rgb value: rgb ([255, 255/size, 255/size]);
        var maxConsumption type: float value: 1;
        var myPlace type: stupid_grid value: location as stupid_grid;

        reflex basic_move {
            let destination type: stupid_grid value: one_of ((myPlace neighbours_at 4) where empty(each.agents));
            if condition: destination != nil {
                 set myPlace value: (location as stupid_grid);                                                
            }
        }
        reflex grow {
            let transfer value: min ([maxConsumption, myPlace.food]);
            set size value: size + transfer;
            set myPlace.food value: myPlace.food - transfer;
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