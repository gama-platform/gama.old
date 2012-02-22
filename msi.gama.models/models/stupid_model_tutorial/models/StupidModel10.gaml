model StupidModel10

global {
    var numberBugs type: int init: 100 parameter: 'numberBugs';
    var globalMaxConsumption type: float init: 1 parameter: 'globalMaxConsumption';
    var globalMaxFoodProdRate type: float init: 0.01 parameter: 'globalMaxFoodProdRate';
    init {
        create species: bug number: numberBugs;
    }
    reflex shouldHalt when: !(empty ((bug as list) where (each.size > 100))) {
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
            let destination type: stupid_grid value: one_of ((myPlace neighbours_at 4) where empty(each.agents));
            if condition: destination != nil {
                set location value: destination.location;
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
    inspect name: 'Species' type: species refresh_every: 5;
    display histogram_display {
        chart name: 'Size distribution' type: histogram background: rgb('lightGray') {
            data name: "[0;10]" value: (bug as list) count (each.size < 10);
            data name: "[10;20]" value: (bug as list) count ((each.size > 10) and (each.size < 20));
            data name: "[20;30]" value: (bug as list) count ((each.size > 20) and (each.size < 30));
            data name: "[30;40]" value: (bug as list) count ((each.size > 30) and (each.size < 40));
            data name: "[40;50]" value: (bug as list) count ((each.size > 40) and (each.size < 50));
            data name: "[50;60]" value: (bug as list) count ((each.size > 50) and (each.size < 60));
            data name: "[60;70]" value: (bug as list) count ((each.size > 60) and (each.size < 70));
            data name: "[70;80]" value: (bug as list) count ((each.size > 70) and (each.size < 80));
            data name: "[80;90]" value: (bug as list) count ((each.size > 80) and (each.size < 90));
            data name: "[90;100]" value: (bug as list) count ((each.size > 90) and (each.size < 100));
        }
    }
    file stupid_results type: text data: 'cycle: ' + (time as string) 
         + '; minSize: ' + (((bug as list) min_of each.size) as string)
         + '; maxSize: ' + (((bug as list) max_of each.size) as string)
         + '; mean: ' + (((sum ((bug as list) collect ((each as bug).size))) / (length((bug as list)))) as string);
}

