model segregation

import "../include/Common Schelling Segregation.gaml"
global torus: true{
	list<space> free_places <- [] ;
	list<space> all_places <- [] ;
	list<space> all_people <- [];
	geometry shape <- square(dimensions);
	action initialize_places {
		set all_places <- shuffle(space);
		set free_places <- shuffle(all_places);
	}

	action initialize_people {
		loop i from: 0 to: number_of_people - 1 {
			space pp <- all_places at i;
			remove pp from: free_places;
			add pp to: all_people;
			pp.color <- colors at (rnd(number_of_groups - 1));
		}

	}

	reflex migrate {
		ask copy(all_people) {
			do migrate;
		}

	}

}

entities {
	grid space parent: base width: dimensions height: dimensions neighbours: 8  {
		rgb color <- black;
		list<space> my_neighbours <- self neighbours_at neighbours_distance;
		action migrate {
			if !is_happy {
				space pp <- any(my_neighbours where (each.color = black));
				if (pp != nil) {
					free_places <+ self;
					free_places >- pp;
					all_people >- self;
					all_people << pp;
					set pp.color <- color;
					set color <- black;
				}
			}
		}
	}
}

experiment schelling type: gui {
	output {
		display Segregation {
			grid space;
		}

		display Charts {
			chart name: "Proportion of happiness" type: pie background: rgb("lightGray") style: exploded position: { 0, 0 } size: { 1.0, 0.5 } {
				data "Unhappy" value: number_of_people - sum_happy_people color: rgb("green");
				data "Happy" value: sum_happy_people color: rgb("yellow");
			}

			chart name: "Global happiness and similarity" type: series background: rgb("lightGray") axes: rgb("white") position: { 0, 0.5 } size: { 1.0, 0.5 }  x_range: 50{
				data "happy" color: rgb("blue") value: (sum_happy_people / number_of_people) * 100 style: spline;
				data "similarity" color: rgb("red") value: (sum_similar_neighbours / sum_total_neighbours) * 100 style: step;
			}

		}

	}

}
