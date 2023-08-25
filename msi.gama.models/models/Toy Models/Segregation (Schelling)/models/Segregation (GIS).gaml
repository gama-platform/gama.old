/**
* Name: segregationGIS
* Author: 
* Description: A model showing the segregation of the people just by putting a similarity wanted parameter using agents 
* 	to represent the individuals and GIS file for the places
* Tags: gis, shapefile
*/
model segregation

//Import the model Common Schelling Segregation
import "../includes/Common Schelling Segregation.gaml" 
global {
	//List of all the free places
	list<space> free_places  ;  
	//List of all the places
	list<space> all_places ;
	//Neighbours distance for the perception of an agent
	int neighbours_distance <- 50 min: 1 parameter: "Distance of perception:" category: "Population" max: 1000;
	//Shapefile to load
	file shape_file_name <- file("../gis/nha2.shp") parameter: "Shapefile to load:" category: "GIS specific";
	//Shape of the environment
	geometry shape <- envelope(shape_file_name);
	//Square meters per people in m2
	int square_meters_per_people <- 200 parameter: "Occupancy of people (in m2):" category: "GIS specific";
	
	//Action to initialize people agents
	action initialize_people { 
		//Create all the places with a surface given within the shapefile
		create space from: shape_file_name with: [surface :: float(read("AREA"))];
		all_places  <- shuffle(space);
		//Compute the number of people to create considering the density of people
		number_of_people <- int( density_of_people * sum (all_places collect (each.capacity))); 
		create people number: number_of_people;  
	    all_people <- people as list ; 
	    //Move all the people to a new place
		ask people  {  
			do move_to_new_place;       
		}   
	}      
	//Action to initialize the places
	action initialize_places {}   
	
} 

//Species people representing the people
species people parent: base { 
	//Size of the people agent
	float size const: true <- 2.0;
	//Color of the people agent  
	rgb color const: true <- colors at (rnd (number_of_groups - 1)); 
	int red const: true <- (color as list) at 0; 
	int green const: true <- (color as list) at 1;  
	int blue const: true <- (color as list) at 2;  
	//Building in which the agent lives
	space current_building <- nil;
	//List of all the neighbour people agents
	list<people> my_neighbours -> people at_distance neighbours_distance; 
	
	//Action to move to a new place
	action move_to_new_place {  
		current_building <- (shuffle(all_places) first_with (((each).capacity) > 0));
		ask current_building {
			do accept one_people: myself;   
		}
	}
	//Reflex to migrate to another place if the agent isn't happy
	reflex migrate when: !is_happy {
		if current_building != nil {
			ask current_building { 
				do remove_one one_people: myself;
			}
		} 
		do move_to_new_place;
	}

	aspect simple {
		draw circle(5) color: color border: #black;
	}
}

//Species space representing a space for a people agent to live in
species space {	
	//List of all the people agents living within
	list<people> insiders;
	rgb color <- rgb(255, 255, 255); 
	//Surface of the place
	float surface;
	//Capacity of the place
	int capacity  <- 1 + int(surface / square_meters_per_people);
	
	//Action to accept a people agent  
	action accept (people one_people) {
		add one_people to: insiders;
		location of one_people <- any_location_in(shape);
		capacity <- capacity - 1;
	}
	//Action to remove a people agent
	action remove_one (people one_people){
		remove one_people from: insiders;
		capacity <- capacity + 1;
	}
	aspect simple {
		color <- empty(insiders) ? #white : rgb ([mean (insiders collect each.red), mean (insiders collect each.green), mean (insiders collect each.blue)]);
		draw  square(40) color: color;
	}
	aspect gis {
		color <- empty(insiders) ? #white : rgb( [mean (insiders collect each.red), mean (insiders collect each.green), mean (insiders collect each.blue)]);
		draw shape color: color border: #black;
	} 
	aspect highlighted {
		color <- #blue;
		draw shape+10 color: color;
	}
}


experiment schelling type: gui {	
	output {
		display Town_display type:2d  {
			species space aspect: gis;
			species people  aspect: simple;
		}
		display Charts  type: 2d {
			chart "Proportion of happiness" type: histogram background: #lightgray gap:0.05 position: {0,0} size: {1.0,0.5}{
				data "Unhappy" value: number_of_people - sum_happy_people color: #green;
				data "Happy" value: sum_happy_people color: #yellow ;
			}
			chart "Global happiness and similarity" type: series background: #lightgray axes: #white position: {0,0.5} size: {1.0,0.5} {
				data "happy" color: #blue value:  ((sum_happy_people * 100) / number_of_people)  style: spline ;
				data "similarity" color: #red value:  (sum_similar_neighbours / sum_total_neighbours) * 100 style: step ;
			}
		}
	}
}
