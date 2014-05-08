model Tuto3D   
//Model 3 of the 3D tutorial  

global {
	init { 
		create cells number: 1000{ 
			location <- {rnd(100), rnd(100), rnd(100)};	
		} 
	}  
} 
    
species cells skills: [moving] {  
	rgb color;
	list<cells> neighbours;
	int offset;
	
	reflex move {
      do wander_3D;	
	}	
	
	reflex computeNeighbours {
      neighbours <- cells select ((each distance_to self) < 10);
    }
		
	aspect default {
		draw sphere(1) color:#orange;
		loop pp over: neighbours {
			draw line([self.location,pp.location]);
		}	
    }
}

experiment Display  type: gui {
	output {
		display View1 type:opengl background:rgb(10,40,55) {
			species cells;
		}
	}
}


