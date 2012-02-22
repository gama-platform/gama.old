model StupidModel16

global {
    var numberBugs type: int init: 100 parameter: 'numberBugs';
    var globalMaxConsumption type: float init: 1 parameter: 'globalMaxConsumption';
    var globalMaxFoodProdRate type: float init: 0.01 parameter: 'globalMaxFoodProdRate';
    var survivalProbability type: float init: 0.95 parameter: 'survivalProbability';
    var initialBugSizeMean type: float init: 0.1 parameter: 'initialBugSizeMean';
    var initialBugSizeSD type: float init: 0.03 parameter: 'initialBugSizeSD';
    var init_data type: matrix init: matrix(file('../data/Stupid_Cell.Data')) const: true;
    var width type: int init: ((init_data column_at 0) copy_between {3, ((init_data.rows)) - 1}) max_of each const: true;
    var height type: int init: ((init_data column_at 1) copy_between {3,((init_data.rows)) - 1}) max_of each const: true;
    init {
        create species: bug number: numberBugs;
        create species: predator number: 200; 
        let i type: int value: 0;  
    	loop from: 3 to: ((init_data.rows)) - 1 var: i {
   			let ind_i type: int value: init_data at {0,i}; 
   			let ind_j type: int value: init_data at {1,i}; 
			ask target: (stupid_grid ) grid_at {ind_i,ind_j} {
   				set foodProd value: init_data at {2,i};
			} 
		}
    }
    reflex shouldHalt when: (time > 1000) or (empty (bug as list)) {
        do action: halt ;
    }
}

environment  {
    grid stupid_grid width: width height: height torus: false {
        var color type: rgb init: [0, min([255, int(food * 255 *2)]), 0] as rgb value: [0,min([255, int(food * 255 * 2)]), 0] as rgb;
        var maxFoodProdRate type: float value: globalMaxFoodProdRate;
        var foodProd type: float;
        var food type: float init: 0.0 value: food + foodProd;
    }
}

entities {
    species bug schedules: (list (bug)) sort_by each.size skills: 1 {
        var size type: float init: gauss({initialBugSizeMean,initialBugSizeSD});
        var color type: rgb value: (size > 0) ? rgb( [255, 255/size, 255/size]) : rgb ([255, 255, 255]);
        var maxConsumption type: float value: globalMaxConsumption;
        var myPlace type: stupid_grid value: location as stupid_grid;
        
        init {
        	if condition: size<0 {
        		set size value: 0;
        	}
        }
        reflex basic_move {
            let destination type: stupid_grid value: last ((shuffle ((myPlace neighbours_at 4) where empty(each.agents))) sort_by (each.food));
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
                        create species: bug number: 1 with: [location::nest.location];
                    }
                }
                do action: die;
            }
        }
        aspect basic {
            draw shape: circle color: color size: size;
        }
    }
	species predator{
	    var color type:rgb init:'blue';
        var myPlace type: stupid_grid value:location as stupid_grid;
		 
		reflex hunt {
			let the_neighbours type: list value: myPlace neighbours_at 1;
			let the_neighbours_bug type:list value: the_neighbours accumulate (each.agents of_species bug);
     		let chosenPrey type: bug value: one_of( the_neighbours_bug);
		    if condition: chosenPrey != nil {
		     	let new_loc type: stupid_grid value: chosenPrey.location as stupid_grid;	
		     	if condition: empty(new_loc.agents of_species predator ){
		     		set location value: new_loc ;
		     		ask target: chosenPrey {
		     			do action: die;
		     		}
		     	}
		     else {
		     	set location value: one_of(the_neighbours as list);  
		     	set myPlace value: location as stupid_grid;
		     } 
		      
		     }
		} 
		
		aspect basic{ 
			draw shape: circle color:  color size: 2 ;
		}
	}  
}

output {
    display stupid_display {
        grid stupid_grid;
        species bug aspect: basic;
        species predator aspect: basic;
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
   // display series_display {
   //     chart name: 'Population history' type: series background: rgb('lightGray') {
   //         data name: 'Bugs' value: length(bugs) + poisson(3.0) / 0.0 color: 'blue';     
   //     }
   // }
   // monitor toto value: (length(list(bug)));
}