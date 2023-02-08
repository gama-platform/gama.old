/***
* Name: MondrianGenerator
* Author: ben
* Description: Model generating displays inspired by the "Composition II en rouge, bleu et jaune" of Piet Mondrian (1930)
* Tags: art, Mondrian, generator
***/

model MondrianGeneratorComposition

global {
	// Parameters
	int nb_max_squares <- 5;	
	int nb_max_lines <- 7;
	int nb_max_columns <- 7; 
	
	int nb_lines <- rnd(2,nb_max_lines) update: rnd(2,nb_max_lines);
	int nb_columns <- rnd(2,nb_max_columns) update: rnd(2,nb_max_columns);	
	int nb_squares <- rnd(2,nb_max_squares) update: rnd(2,nb_max_squares);
	
	list<rgb> colors <- [#yellow,#red,#blue];
	
	
	init {
		do new_paint;
	}
	
	reflex repaint {
		do new_paint;
	}
	
	// The action that first cleans the previous painting, then generates a new one. 
	action new_paint {
		float x_max <- world.shape.width;
		float y_max <- world.shape.height;
		
		ask lines {do die;}
		ask squares {do die;}		
		
		create lines number: nb_lines {
			float x <- rnd(x_max);
			shape <- line({x,0.0},{x,x_max}) + 1;
			horizontal <- false;
			
			if( !empty( (lines where !each.horizontal) overlapping self)) {
				do die;
			}		
		}
		
		create lines number: nb_columns {
			float y <- rnd(y_max);
			shape <- line({0.0,y},{y_max,y}) + 1;
			horizontal <- true;		
			
			if( !empty( (lines where each.horizontal) overlapping self)) {
				do die;
			}				
		}
		
		create squares number: nb_squares {
			list<lines> ll <- (2 among (lines where each.horizontal)) + (2 among (lines where !each.horizontal));
			geometry temp_shape;
			loop l over: ll {
				if(temp_shape = nil) {
					temp_shape <- world.shape - l.shape;
				} else {
					temp_shape <- temp_shape - l.shape;				
				}
			}

			shape <- one_of(temp_shape.geometries);
		}
		
		ask squares {
			list<squares> over_squares <- squares overlapping self;
			
			loop s over: over_squares {
				if(s covers shape) {
					do die;
				}
				shape <- shape - s;
			}
			if(!empty(over_squares)) {
				loop l over: lines {
					shape <- shape -l; 
				}
			}
		}
	}
}

species lines {
	bool horizontal;
	
	aspect default {
		draw shape color: #black ;
	}
}

species squares {
	rgb col <- one_of(colors);
	
	aspect default {
		draw shape color: col  ;
	}
}

experiment MondrianGenerator type: gui {
	float minimum_cycle_duration<-0.1;
	parameter "Nb max of squares: " var: nb_max_squares;
	parameter "Nb max of border: " var: nb_max_lines;
	parameter "Nb max of columns: " var: nb_max_columns;
	
	output {
		display map type: 3d axes:false{
			species lines ;											
			species squares ;	
		}
	}
}
