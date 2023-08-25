/**
* Name: segregationGoogleMap
* Author: 
* Description: A model showing the segregation of the people just by putting a similarity wanted parameter using agents 
* 	to represent the individuals and a grid to discretize space. Use the colors of the image to know if it is a possible space or not
* Tags: grid
*/
model segregation

//Import the model Common Schelling Segregation
import "../includes/Common Schelling Segregation.gaml"    

global {
	//List of all the free places
	list<space> free_places ; 
	//List of all the places
	list<space> all_places;
	//Shape of the environment
	geometry shape <- square(dimensions);
	//Percentage of similarity wanted by an agent
	float percent_similar_wanted <- 0.6;
	//Distance of perception of the neighbours
	int neighbours_distance <- 4; 
	//Number of groups of people
	int number_of_groups <- 3;
	list google_buildings  <- [rgb("#EBE6DC"), rgb("#D1D0CD"), rgb("#F2EFE9"), rgb("#EEEBE1"), rgb("#F9EFE8")] ;
	//List of all the available places
	list<space> available_places ;
	//Image file to load
	file bitmap_file_name <- file<unknown, int>("../images/hanoi.png") parameter: "Name of image file to load:" category: "Environment" ;
	matrix<int> map_colors;
 
 	//Action to initialize the people agents
	action initialize_people {
		create people number: number_of_people ;  
		all_people <- people as list ;  
	}
	//Action to initialize the places using the color in the image
	action initialize_places { 
		map_colors <- (bitmap_file_name) as_matrix {dimensions,dimensions} ;
		ask space as list {
			color <- rgb(map_colors at {grid_x,grid_y}) ;
		}
		all_places <- shuffle (space where (each.color in google_buildings)) ;
		free_places <- copy(all_places);
	}  
}
//Grid to discretize the space
grid space width: dimensions height: dimensions neighbors: 8 use_individual_shapes: false use_regular_agents: false frequency: 0 ; 
 
//Species people representing the people agent
species people parent: base  {
	rgb color <- colors at (rnd (number_of_groups - 1));
	//List of all the neighbours
	list<people> my_neighbours -> (self neighbors_at neighbours_distance) of_species people;
	
	//Launched at the initialization of the agent
	init {
		//Set the place of the agent as one of the free place
		location <- (one_of(free_places)).location; 
		remove location as space from: free_places;
	} 
	//Reflex to migrate the agent when it's not happy
	reflex migrate when: !is_happy { 
		add location as space to: free_places;
		location <- any(free_places).location;
		remove location as space from: free_places;
	}
	aspect geom {
		draw square(1) color: color  ;
	}
	aspect default {
		draw  square(2) color: #black ;
	}
}


experiment schelling type: gui {	
	output {
		display Segregation type:2d{
			image bitmap_file_name.path ;
			species people transparency: 0.5 aspect: geom;
		}	
		display Charts  type: 2d {
			chart "Proportion of happiness" type: pie background: #lightgray style: exploded position: { 0, 0 } size: { 1.0, 0.5 } {
				data "Unhappy" value: number_of_people - sum_happy_people color: #green;
				data "Happy" value: sum_happy_people color: #yellow;
			}

			chart "Global happiness and similarity" type: series background: #lightgray axes: #white position: { 0, 0.5 } size: { 1.0, 0.5 } x_range: 20 y_range: 20 {
				data "happy" color: #blue value: (sum_happy_people / number_of_people) * 100 style: spline fill: false;
				data "similarity" color: #red value: (sum_similar_neighbours / sum_total_neighbours) * 100 style: line fill: true ;
			}
		}
	}
}
