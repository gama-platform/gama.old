model StupidModel11

global {
    int numberBugs <- 100;
    float globalMaxConsumption <- 1.0;
    float globalMaxFoodProdRate <- 0.01;
    
    init {
        create bug number: numberBugs;
    }
    reflex shouldHalt when: !(empty (list(bug) where (each.size > 10))) {
        do halt;
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
    species bug schedules: (list (bug)) sort_by each.size {
        float size <- 1.0;
        rgb color <- rgb ([255, 255, 255]) update: rgb ([255, 255/size, 255/size]);
        float maxConsumption <- 1.0;
        stupid_cell myPlace <- (location as stupid_cell); 

        reflex basic_move {
            let destination type: stupid_cell <- last (((myPlace neighbours_at 4) where empty(each.agents)) sort_by (each.food));
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
        //reflex askToHalt when: (size>=100) {
        //    ask world_species {
        //        do halt;
        //    }
        //}
        aspect basic {
            draw circle(size) color: color;
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
	    inspect Species type: species refresh_every: 5;
	    
	    display histogram_display {
	        chart 'Size distribution' type: histogram background: rgb('lightGray') {
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
}