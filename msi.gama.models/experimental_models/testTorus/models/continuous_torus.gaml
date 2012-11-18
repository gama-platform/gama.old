model Continuous
// proposed by Patrick Taillandier

global {
	init {
		create object with: [id::"red", shape :: circle(5) at_location {1.0,1.0}];
		create object with: [id::"yellow", shape :: circle(5) at_location {40.0,40.0}];
		create object with: [id::"green",shape :: circle(5) at_location world.location];
		 
		create object with: [id::"magenta",shape :: square(5) at_location {20.0,1.0}];
		
	}
} 
environment bounds: {50,50} torus: true{ 
	
}
entities {
	species object skills: [moving] {
		rgb color; 
		string id;
		aspect base {  
			draw geometry: shape color: color; 
		}
		
		init {
			set color <- rgb(id); 
		}
		reflex test1 {
			write "*********** TEST WITH SHAPES ***********";
			write "Test 1 : " + id + " closest to me : " +  (object closest_to (self)).id;
			write "Test 2 : " + id + " neighbours_at 20 : " +  (self neighbours_at (20.0)) collect (each.id);
			write "Test 3 : " + id + " at distance 20 : " +  (object at_distance (20.0)) collect (each.id);
			
			write "*********** TEST WITH POINTS ***********";
			write "Test 4 : " + id + " closest to me : " +  (object closest_to (self.location)).id;
			write "Test 5 : " + id + " neighbours_at 20 : " +  (location neighbours_at (20.0)) collect ((object(each).id));
		}
		
		int heading <- rnd(360);
		reflex test2 {
			do move speed: 1.0;
		}
	}
}

experiment main type: gui {
	output {
		display continuous_display {
			species object aspect: base;
			
		}
	}
	
}
