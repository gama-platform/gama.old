model StupidModel14

global torus: true{
	int numberBugs <- 100;
   	float globalMaxConsumption <- 1.0;
    	float globalMaxFoodProdRate <- 0.01;
    	float initialBugSizeMean <- 0.1;
	float initialBugSizeSD <- 0.03;
	init {
		create bug number: numberBugs {
			my_place <- one_of(cell);
			location <- my_place.location;
		}
	}
	reflex shouldHalt when: cycle = 999 or empty(bug)  {
		 do halt;
    	}
	reflex write_results {
		save [cycle,bug min_of each.size, mean (bug collect each.size),bug max_of each.size] type: "csv" to: "result.csv";
	}
	
}

grid cell width: 100 height: 100 neighbours: 4 {
	list<cell> neighbours4 <- self neighbours_at 4;
	list<cell> neighbours3 <- self neighbours_at 3;
	float maxFoodProdRate <- globalMaxFoodProdRate;
	float foodProd <- (rnd(1000) / 1000) * maxFoodProdRate;
	float food <- 0.0 update: food + foodProd;
}

species bug schedules: bug sort_by ( - each.size){
	cell my_place;
	float size <- max([0,gauss(initialBugSizeMean,initialBugSizeSD)]);
	float maxConsumption <- globalMaxConsumption;
	float survivalProbability <- 0.95;
	reflex basic_move {
		cell destination <- (my_place.neighbours4 where empty(each.agents) + my_place) with_max_of (each.food);
		if (destination != nil) {
			my_place <- destination;
			location <- destination.location;
		}
	}
	reflex grow {
		float transfer <- min([my_place.food,maxConsumption]);
		size <- size + transfer;
		my_place.food <- my_place.food - transfer;
	}
	reflex dying when: not flip(survivalProbability) {
    		do die;
        }
	reflex reproduce when: size >= 10 {
		list<cell> possible_nests <- my_place.neighbours3 where empty(each.agents);
       		create bug number: 5 {
           		cell nest <- one_of(possible_nests);
           		if condition: nest != nil {
            			remove nest from: possible_nests;
            			my_place <- nest;
            			location <- nest.location;
            			size <- 0.0;
           	 	} else {
            			do die;
           	 	}
       	 	}
      	  	do die;
  	}
	aspect basic {
		int val <- int(255 * (1 - min([1.0,size/10.0])));
		draw circle(0.5) color: rgb(255,val,val);
	}
} 

experiment stupidModel type: gui {
	parameter "numberBugs" var: numberBugs;
 	parameter "globalMaxConsumption" var: globalMaxConsumption;
  	parameter "globalMaxFoodProdRate" var: globalMaxFoodProdRate;	
  	parameter "initialBugSizeMean" var: initialBugSizeMean;
  	parameter "initialBugSizeSD" var: initialBugSizeSD;
	
  	output {
		display stupid_display {
			grid cell;
			species bug aspect: basic;
		}
		display histogram_display {
	            chart "Size distribution" type: histogram {
	                data "[0;10]" value: bug count (each.size < 10) color: #red;
	                data "[10;20]" value: bug count ((each.size > 10) and (each.size < 20)) color:#red;
	                data "[20;30]" value: bug count ((each.size > 20) and (each.size < 30)) color:#red;
	                data "[30;40]" value: bug count ((each.size > 30) and (each.size < 40)) color:#red;
	                data "[40;50]" value: bug count ((each.size > 40) and (each.size < 50)) color:#red;
	                data "[50;60]" value: bug count ((each.size > 50) and (each.size < 60)) color:#red;
	                data "[60;70]" value: bug count ((each.size > 60) and (each.size < 70)) color:#red;
	                data "[70;80]" value: bug count ((each.size > 70) and (each.size < 80)) color:#red;
	                data "[80;90]" value: bug count ((each.size > 80) and (each.size < 90)) color:#red;
	               data "[90;100]" value: bug count (each.size > 90) color:#red;
	        }
	    }
	    display series_display {
	        chart "Population history" type: series  {
	            data "Bugs" value: length(bug) color: #blue;            
	        }
    	}
	}
}