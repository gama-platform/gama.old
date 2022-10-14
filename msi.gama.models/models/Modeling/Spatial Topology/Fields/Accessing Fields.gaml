/**
* Name: AccessingFields
* How to create simple fields and access/change the information they contain is covered in this model.
* One agent increases the values in the field, another one decreases them. 
* Author: Alexis Drogoul
* Tags: 
*/
model AccessingFields

global torus: true{
	geometry shape <- square(2000);
	field terrain <- field(300, 300);

	init {
		create increaser with: [location::location];
		create decreaser with: [location::location];
	}

	species increaser skills: [moving] {
		geometry shape <- square(40);

		reflex move {
			do wander amplitude: 2.0;
			loop s over: terrain cells_in self {
				terrain[geometry(s).location] <- terrain[geometry(s).location] + 1.0;
			}

		}
	}

	species decreaser skills: [moving] {
		geometry shape <- square(40);

		reflex move {
			do wander amplitude: 2.0;
			loop s over: terrain cells_in self {
				terrain[geometry(s).location] <- terrain[geometry(s).location] - 1.0;
			}

		}

	}

}

experiment "Show" {
	list<rgb> palette <- brewer_colors(any(brewer_palettes(0)));
	output {
		display Field type: 3d {
			mesh terrain color: palette triangulation: true smooth: 4;
		}

	}

}
