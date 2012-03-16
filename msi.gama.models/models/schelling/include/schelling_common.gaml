model segregation

global {
	var colors type: list init: [color_1, color_2, color_3, color_4, color_5, color_6, color_7, color_8] ;
	var color_1 type: rgb init: rgb('yellow') parameter: 'Color of group 1:' category: 'User interface' ;
	var color_2 type: rgb init: rgb('red') parameter: 'Color of group 2:' category: 'User interface' ;
	var color_3 type: rgb init: rgb('blue') parameter: 'Color of group 3:' category: 'User interface' ;
	var color_4 type: rgb init: rgb('orange') parameter: 'Color of group 4:' category: 'User interface' ;
	var color_5 type: rgb init: rgb('green') parameter: 'Color of group 5:' category: 'User interface' ;
	var color_6 type: rgb init: rgb('pink') parameter: 'Color of group 6:' category: 'User interface' ;
	var color_7 type: rgb init: rgb('magenta') parameter: 'Color of group 7:' category: 'User interface' ;
	var color_8 type: rgb init: rgb('cyan') parameter: 'Color of group 8:' category: 'User interface' ;
	const black type: rgb init: rgb('black') ;
	var number_of_groups type: int init: 2 max: 8 parameter: 'Number of groups:' category: 'Population' ;
	var density_of_people type: float init: 0.7 parameter: 'Density of people:' category: 'Population' min: 0.01 max: 0.99 ;
	var percent_similar_wanted type: float init: 0.5 min: 0 max: 1 parameter: 'Desired percentage of similarity:' category: 'Population' ;
	var dimensions type: int init: 80 max: 400 min: 10 parameter: 'Width and height of the environment:' category: 'Environment' ;
	var neighbours_distance type: int init: 1 max: 10 min: 1 parameter: 'Distance of perception:' category: 'Population' ;
	var number_of_people type: int init: 0 ;
	var sum_happy_people type: int init: 0 value: all_people count (each.is_happy) ; 
	var sum_similar_neighbours type: int init: 0 value: sum (all_people collect each.similar_nearby) ;
	var sum_total_neighbours type: int init: 1 value: sum (all_people collect each.total_nearby) min: 1 ;
	var all_places type: list init: []  of: default; 
	var all_people type: list init: [] of: base ;
	action description {
		do action: write {
			arg message value:  '\\n\\u25B6 Description. \\n\\u25B6 Thomas Schelling model of residential segregation is a classic study of the effects of local decisions on global dynamics. Agents with mild preferences for same-type neighbors, but without preferences for segregated neighborhoods, can wind up producing complete segregation.\\n\\u25B6 In this model, agents populate a grid with a given *density*. They are in two different states : happy when the percentage of same-color neighbours is above their *desired percentage of similarity*; unhappy otherwise. In the latter case, they change their location randomly until they find a neighbourhood that fits their desire. \\n\\u25B6 In addition to the previous parameter, one can adjust the *distance of perception* (i.e.  the distance at which they consider other agents as neighbours) of the agents to see how it affects the global process. '  ;
		}
	}
	init {
		do action: description ;
		do action: initialize_places ; 
		set number_of_people value: length(all_places) * density_of_people ;
		do action: initialize_people ;
	}
	action initialize_places;
	action initialize_people; 
}
entities {
	species base {
		var color type: rgb ;
		var location type: point ;
		var my_neighbours type: list init: [] of: base ;
		var similar_nearby type: int init: 0 value:  (my_neighbours count (each.color = color))  ;
		var total_nearby type: int value: length(my_neighbours) ;
		var is_happy type: bool value: similar_nearby >= (percent_similar_wanted * total_nearby ) ;
		reflex migrate when: !is_happy {
			set location value: all_places first_with (empty(each.agents)) ;
		} 
	}
}
output {
	display Segregation {
		chart name: 'Proportion of happiness' type: pie background: rgb('lightGray') style: exploded {
			data Unhappy value: number_of_people - sum_happy_people ;
			data Happy value: sum_happy_people ;
		}
		chart name: 'Global happiness and similarity' type: series background: rgb('lightGray') axes: rgb('white') {
			data happy color: rgb('blue') value:  (sum_happy_people / number_of_people) * 100 style: spline ;
			data similarity color: rgb('red') value: float (sum_similar_neighbours / sum_total_neighbours) * 100 style: step ;
		}
	}
}
