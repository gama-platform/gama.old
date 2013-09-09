model AugmentedGrid

/**
 *  AugmentedGrid
 *  Author: Arnaud Grignard
 *  Description: Initialize a grid with a random value between 0 and 255
 *  In TextDisplay only the value of the cell is displayed as a text
 *  In AugmentedDisplay the value of the cell is displayed:
 * 		1: Circle with a radius equal to the cellValue
 * 		2: Blue colored square
 *      3: Elevation + blue color
 * 		4: Elevation + hsb color
 */

global {

	int width parameter : "width" min 1<- 6 category: 'Initialization';
	int height parameter : "height" min 1 <-6 category: 'Initialization';
	
	float hue parameter: 'Hue (between 0.0 and 1.0)' min: 0.0 max:1.0 <- 0.66 ;
	
	init {    	
		//Initialize the value of each cell
	 	ask cell as list {		
		  set color <- hsb(hue,(cellValue/255),1.0);
	      set elevation <-((cellValue/100)^2);
		}  
	} 
}
environment bounds: {width,height} { 
	grid cell width: width height: height neighbours: 4 {

	    int cellValue <- rnd(255);
		float elevation;
	
		reflex changeCellValue{
			set cellValue <- rnd(255);	
			set color <- hsb(hue,(cellValue/255),1.0);
			set elevation <-((cellValue/100)^2);
		}

		aspect base {
			draw shape color: rgb('white'); 
			draw text: string(cellValue)  size:0.5 color: rgb('black');
		}
		
		aspect colored {
			draw shape color: color;	
		}
		
		aspect square{		
			draw shape color: color  border:color;		
		}
			
		aspect box{			
			draw shape color: color  depth:elevation border:color;		
		}
		
		aspect hsbElevation{	
			draw shape color: color hsb_to_rgb ([(cellValue/255),1.0,1.0]) depth:elevation border:color hsb_to_rgb ([(cellValue/255),1.0,1.0]);
		}
		
		aspect circle{ 
			draw circle (cellValue/(255*2)) color: color border:color; 
		}
		
		aspect sphere{ 
			draw sphere (cellValue/(255*2)) color: color border:color; 
		}
		
		aspect cylinder{ 
			draw circle (cellValue/(255*2)) color: color border:color depth:elevation; 
		}
		
		
	} 
}
entities {	
}


experiment AugmentedGrid type:gui {
	output {
		display Circle type:opengl ambient_light:100 polygonmode:true{		
			species cell aspect: circle  refresh:true position: {0,0};
		}
		
		display Cylinder type:opengl ambient_light:100 polygonmode:true{		
			species cell aspect: cylinder  refresh:true position: {0,0};
		}
		
		display Sphere type:opengl   ambient_light:100 polygonmode:true{		
			species cell aspect: sphere  refresh:true position: {0,0};
		}
		
		display Square  type:opengl ambient_light:100 polygonmode:true{		
			species cell aspect: square  refresh:true position: {0,0};
		}
		
		display Box  type:opengl ambient_light:100 polygonmode:true{		
			species cell aspect: box  refresh:true position: {0,0};
		}
		
		display hsb  type:opengl ambient_light:100 polygonmode:true{		
			species cell aspect: hsbElevation  refresh:true position: {0,0};
		}

	}
}
