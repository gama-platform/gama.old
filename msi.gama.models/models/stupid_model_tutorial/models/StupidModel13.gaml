model StupidModel13

global {
    var numberBugs type: int init: 100 parameter: 'numberBugs';
    var globalMaxConsumption type: float init: 1 parameter: 'globalMaxConsumption';
    var globalMaxFoodProdRate type: float init: 0.01 parameter: 'globalMaxFoodProdRate';
    var survivalProbability type: float init: 0.95 parameter: 'survivalProbability';
    init {
        create species: bug number: numberBugs;
    }
    reflex shouldHalt when: (time > 1000) or (empty (bug as list)) {
        do action: halt;
    }
}

environment {
    grid stupid_grid width: 100 height: 100 torus: true {
        var color type: rgb init: rgb('black');
        var maxFoodProdRate type: float value: globalMaxFoodProdRate;
        var foodProd type: float value: (rnd(1000) / 1000) * maxFoodProdRate;
        var food type: float init: 0.0 value: food + foodProd;
    }
}

entities {
    species bug schedules: (list (bug)) sort_by each.size {
        var size type: float init: 1;
        var color type: rgb value: rgb ([255, 255/size, 255/size]);
        var maxConsumption type: float value: globalMaxConsumption;
        var myPlace type: stupid_grid value: location as stupid_grid;
        
        reflex basic_move {
            let destination type: stupid_grid value: last (((myPlace neighbours_at 4) where empty(each.agents)) sort_by (each.food));
            if condition: destination != nil {
                set location value: destination;
                set myPlace value: (location as stupid_grid);                
            }
        }
        reflex grow { 
            let transfer value: min ([maxConsumption, myPlace.food]);
            set size value: size + transfer;
            set myPlace.food value: myPlace.food - transfer;
        }
        reflex shallDie when: ((rnd(100)) / 100.0) > survivalProbability {
            do action: die;
        }
        reflex multiply {
            if condition: size > 10 {
                let possible_nests value: (myPlace neighbours_at 3) where empty(each.agents);
                loop times: 5 {
                    let nest value: one_of(possible_nests);
                    if condition: nest != nil {
                        set possible_nests value: possible_nests - nest;
                        create species: bug number: 1 returns: child;
                        ask target: child {
                            set location value: nest.location;
                        }
                    }
                }
                do action: die;
            }
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
    inspect name: 'Species' type: species refresh_every: 5;
    display histogram_display {
        chart name: 'Size distribution' type: histogram background: rgb('lightGray') {
            data name: "[0;1]" value: (bug as list) count (each.size < 1);
            data name: "[1;2]" value: (bug as list) count ((each.size > 1) and (each.size < 2));
            data name: "[2;3]" value: (bug as list) count ((each.size > 2) and (each.size < 3));
            data name: "[3;4]" value: (bug as list) count ((each.size > 3) and (each.size < 4));
            data name: "[4;5]" value: (bug as list) count ((each.size > 4) and (each.size < 5));
            data name: "[5;6]" value: (bug as list) count ((each.size > 5) and (each.size < 6));
            data name: "[6;7]" value: (bug as list) count ((each.size > 6) and (each.size < 7));
            data name: "[7;8]" value: (bug as list) count ((each.size > 7) and (each.size < 8));
            data name: "[8;9]" value: (bug as list) count ((each.size > 8) and (each.size < 9));
            data name: "[9;10]" value: (bug as list) count ((each.size > 9) and (each.size < 10));
        }
    }
    file stupid_results type: text data: 'cycle: '+ (time as string) + '; minSize: '
        + (((bug as list) min_of each.size) as string) + '; maxSize: '
        + (((bug as list) max_of each.size) as string) + '; meanSize: '
        + (((sum ((bug as list) collect ((each as bug).size))) / (length((bug as list)))) as string);
    display series_display {
        chart name: 'Population history' type: series background: rgb('lightGray') {
            data name: 'Bugs' value: length((bug as list)) color: rgb( 'blue');            
        }
    }
}