model segregation
  
import "../include/schelling_common.gaml"  
global {
	list free_places <- [] of: space; 
	list all_places <- [] of: space; 
	list all_people <- [] of: space;
	action initialize_places { 
		set all_places <- shuffle (space as list);
		set free_places <- shuffle(all_places);
	}
	action initialize_people {  
		loop i from: 0 to: number_of_people - 1 { 
			let pp <- all_places at i;
			remove pp from: free_places;       
			add pp to: all_people;  
			set pp.color <- colors at (rnd (number_of_groups - 1)) ;  
			let i <- self.location;
		} 
	} 
	reflex migrate {
		ask all_people as: space {  
			do migrate; 
		}             
	} 
} 
environment width: dimensions height: dimensions {
	grid space parent: base width: dimensions height: dimensions neighbours: 8 torus: true {
		rgb color <- black;
		list my_neighbours -> {(self neighbours_at neighbours_distance) select (each.color != black)} of: space;
		action migrate {
			if !is_happy { 
				let pp <- any(free_places); 
				add self to: free_places;
				remove self from: all_people;
				remove pp from: free_places;
				add pp to: all_people;
				set pp.color <- color; 
				set color <- black;
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
			chart name: 'Proportion of happiness' type: pie background: rgb('lightGray') style: exploded position: {0,0} size: {1.0,0.5}{
				data Unhappy value: number_of_people - sum_happy_people ;
				data Happy value: sum_happy_people ;
			}
			chart name: 'Global happiness and similarity' type: series background: rgb('lightGray') axes: rgb('white') position: {0,0.5} size: {1.0,0.5} {
				data happy color: rgb('blue') value:  (sum_happy_people / number_of_people) * 100 style: spline ;
				data similarity color: rgb('red') value: float (sum_similar_neighbours / sum_total_neighbours) * 100 style: step ;
			}
		}
	}
}
