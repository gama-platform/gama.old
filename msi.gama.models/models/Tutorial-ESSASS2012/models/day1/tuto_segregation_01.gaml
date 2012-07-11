model tuto_segregation_agents

global {
	// parameters
	int number_of_people <- 200 ; 
	int dimensions <- 20 ; 

	init {
		create people number: number_of_people;
	}
}

environment width: dimensions height: dimensions;

entities {
	species people  { 
		rgb color <- rgb('blue');		
		
		aspect default { 
			draw shape: circle size: 1.0 color: color; 
		}
	}
} 

experiment schelling type: gui {	
	parameter 'Number of people' var: number_of_people 
				min: 1 max: 2000 category:'Init Environment';
	parameter 'Dimensions of the space' var: dimensions 
				min: 5 max: 100 category: 'Init Environment';
	
	output {		
		display Segregation {
			species people aspect: default;
		}			
	}
}
