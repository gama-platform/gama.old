model Grid
// proposed by Arnaud Grignard

global {
	int width parameter : "width" min 1<- 25;
	int height parameter : "height" min 1 <-25;
	
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
		
		reflex changeValue{
			set value <- rnd(255);
			set size <- value;
			set color <- [0, 0,value] as rgb  ;
			set elevation <-((value/100)^2);	
		}

		aspect base {
			draw shape: geometry color: rgb('white'); 
			draw text: string(value) size: 1 color: rgb('black');
		}
		
		aspect colored {
			draw shape: geometry color: color z:0; 
		}
		
		aspect elevation{
			//FIXME: z:elevation change the z value of the shape it should not.
			draw shape: geometry color: rgb('blue') z:elevation;
			
		}
		
		aspect size{ 
			draw geometry: circle (size/(255*2)) color: color; 
		}
		
		
	} 
}
entities {	
}

experiment grid type:gui {
	output {
		display objects_display type:opengl{
			species cell aspect: base transparency: 0.5 refresh:false position: {0,0};
			species cell aspect: colored transparency: 0.5 refresh:true position: {width*1.1,0};
			species cell aspect: elevation transparency: 0.5 refresh:true position: {0,height*1.1};
			//FIXME: Does not work if size is not in the last plasce because it changes the shape.
			species cell aspect: size transparency: 0.5 refresh:true position:{width*1.1,height*1.1};
		}
	}
}
