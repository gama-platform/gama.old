model Grid
// proposed by Arnaud Grignard

global {
	int width <- 25;
	int height <-25;
	
	init {    
		
		ask cell as list {
			set value <- rnd(255);
			set size <- value;
			set color <- [0, 0,value] as rgb  ;
			set elevation <-((value/100)^2);
			//set shape <- circle (size/(255*2));
		}
     
	} 
}
environment bounds: {width,height} { 
	grid cell width: width height: height neighbours: 4 torus: false {
		//rgb color <- rgb('white');
		
		int value <- rnd(100);
		int size;
		float elevation;
		

		aspect base {
			draw shape: geometry color: rgb('white'); 
			draw text: string(value) size: 1 color: rgb('black');
		}
		
		aspect colored {
			draw shape: geometry color: color; 
			//draw text: string(value) size: 1 color: rgb('black');
		}
		
		aspect size{
			set shape <- circle (size/(255*2));
			draw shape: geometry color: color; 
		}
		
		aspect elevation{
			draw shape: geometry color: rgb('blue') z:elevation;
			
		}
	} 
}
entities {	
}

experiment grid type:gui {
	output {
		display objects_display type:opengl{
			species cell aspect: base transparency: 0.5 refresh:false position: {0,0};
			species cell aspect: colored transparency: 0.5 refresh:false position: {30,0};
			species cell aspect: elevation transparency: 0.5 refresh:false position: {0,30};
			//FIXME: Does not work if size is not in the last plasce because it changes the shape.
			species cell aspect: size transparency: 0.5 refresh:false position:{30,30};
		}
	}
}
