model StupidModel1

global torus: true{
	init {
		create bug number: 100 {
			my_place <- one_of(cell);
			location <- my_place.location;
		}
	}
}

grid cell width: 100 height: 100 neighbours: 4 {
	list<cell> neighbours4 <- self neighbours_at 4;
}

species bug {
	cell my_place;
	reflex basic_move {
		cell destination <- shuffle(my_place.neighbours4) first_with empty(each.agents);
		if (destination != nil) {
			my_place <- destination;
			location <- destination.location;
		}
	}
	aspect basic {
		draw circle(0.5) color: #red;
	}

}

experiment stupidModel type: gui {
	output {
		display stupid_display {
			grid cell;
			species bug aspect: basic;
		}
	}
}
