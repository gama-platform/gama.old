model tuto_segregation_02

global {
	// parameters
	int number_of_people <- 200 ; 
	int dimensions <- 20 ; 
	
	rgb color_1 <- rgb('yellow'); 
	rgb color_2 <- rgb('red');
	
	// Other global variables
	int number_of_groups <- 2;	
	list colors <- [color_1, color_2] of: rgb;
		
	init {
		create people number: number_of_people;
	}
}

environment width: dimensions height: dimensions;

entities {
	species people  { 
		int group_id;
		rgb color;
		
		init {
			set group_id <- rnd(number_of_groups - 1);		 // rnd(int) returns a random integer in [0,int]
			set color <- colors at (group_id);
		} 
								
		aspect default { 
			draw circle(1.0) color: rgb('blue'); 
		}
		
		aspect with_group_color { 
			draw circle(1.0) color: color; 
		}		
	}
} 

experiment schelling type: gui {	
	parameter 'Number of people' var: number_of_people min: 1 max: 2000 category:'Init Environment';
	parameter 'Dimensions of the space' var: dimensions min: 5 max: 100 category: 'Init Environment';
	parameter 'Color of group 1:' var: color_1 category: 'Configuration of groups';
	parameter 'Color of group 2:' var: color_2 category: 'Configuration of groups';
		
	
	output {		
		display Segregation {
			species people aspect: with_group_color;
		}			
	}
}