model segregation_base

global {
	rgb color_1 <- rgb ("yellow") parameter: "Color of group 1:" category: "User interface";
	rgb color_2 <- rgb ("red") parameter: "Color of group 2:" category: "User interface";
	rgb color_3 <- rgb ("blue") parameter: "Color of group 3:" category: "User interface";
	rgb color_4 <- rgb ("orange") parameter: "Color of group 4:" category: "User interface";
	rgb color_5 <- rgb ("green") parameter: "Color of group 5:" category: "User interface";
	rgb color_6 <- rgb ("pink") parameter: "Color of group 6:" category: "User interface";   
	rgb color_7 <- rgb ("magenta") parameter: "Color of group 7:" category: "User interface";
	rgb color_8 <- rgb ("cyan") parameter: "Color of group 8:" category: "User interface";
	const black type: rgb <- rgb ("black");
	list colors <- [°yellow, °red, °blue, °orange, °green, °pink, °magenta, °cyan] of: rgb;
	int number_of_groups <- 2 max: 8 parameter: "Number of groups:" category: "Population";
	float density_of_people <- 0.7 parameter: "Density of people:" category: "Population" min: 0.01 max: 0.99;
	float percent_similar_wanted <- 0.5 min: float (0) max: float (1) parameter: "Desired percentage of similarity:" category: "Population";
	int dimensions <- 40 max: 400 min: 10 parameter: "Width and height of the environment:" category: "Environment";
	int neighbours_distance <- 2 max: 10 min: 1 parameter: "Distance of perception:" category: "Population";
	int number_of_people <- 0;
	int sum_happy_people <- 0 update: all_people count (each.is_happy);
	int sum_similar_neighbours <- 0 update: sum (all_people collect each.similar_nearby);
	int sum_total_neighbours <- 1 update: sum (all_people collect each.total_nearby) min: 1;
	list<agent> all_places <- [];
	list<base> all_people <- [];  
	action description {
		write
		"\\n\\u25B6 Description. \\n\\u25B6 Thomas Schelling model of residential segregation is a classic study of the effects of local decisions on global dynamics. Agents with mild preferences for same-type neighbors, but without preferences for segregated neighborhoods, can wind up producing complete segregation.\\n\\u25B6 In this model, agents populate a grid with a given *density*. They are in two different states : happy when the percentage of same-color neighbours is above their *desired percentage of similarity*; unhappy otherwise. In the latter case, they change their location randomly until they find a neighbourhood that fits their desire. \\n\\u25B6 In addition to the previous parameter, one can adjust the *distance of perception* (i.e.  the distance at which they consider other agents as neighbours) of the agents to see how it affects the global process. ";
	}

	init { 
		do description;
		do initialize_places;
		number_of_people <- int( length (all_places) * density_of_people);
		do initialize_people;
	}

	action initialize_places virtual: true;
	action initialize_people virtual: true;
}

entities {
	species base {
		rgb color;
		list<base> my_neighbours;
		int similar_nearby -> {
			(my_neighbours count (each.color = color))
		};
		int total_nearby -> {
			length (my_neighbours)
		};
		bool is_happy -> {similar_nearby >= (percent_similar_wanted * total_nearby )} ;
	}
}
