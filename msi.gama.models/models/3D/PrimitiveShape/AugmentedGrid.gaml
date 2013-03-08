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

	int width parameter : "width" min 1<- 6;
	int height parameter : "height" min 1 <-6;
	
	init {    	
		//Initialize the value of each cell
	 	ask cell as list {		
		 // set color <-   rgb ([0.0,0.0,cellValue]) ;
		  set color <- color hsb_to_rgb [0.66,(cellValue/255),1.0];
	      set elevation <-((cellValue/100)^2);
		}  
	} 
}
environment bounds: {width,height} { 
	grid cell width: width height: height neighbours: 4 torus: false {

	    int cellValue <- rnd(255);
		float elevation;
	
		reflex changeCellValue{
			set cellValue <- rnd(255);	
			set color <- [0, 0,cellValue] as rgb  ;
			set elevation <-((cellValue/100)^2);
			write "" + cellValue;
		}

		aspect base {
			draw shape color: rgb('white'); 
			draw text: string(cellValue) size: 1 color: rgb('black');
		}
		
		aspect colored {
			draw shape color: color;
			
		}
		
		aspect square{
			//FIXME: z:elevation change the z cellValue of the shape it should not.			
			draw shape color: color  border:color;		
		}
			
		aspect box{
			//FIXME: z:elevation change the z cellValue of the shape it should not.			
			draw shape color: color  depth:elevation border:color;		
		}
		
		aspect hsbElevation{	
			draw shape color: color hsb_to_rgb ([(cellValue/255),1.0,1.0]) depth:elevation border:color hsb_to_rgb ([(cellValue/255),1.0,1.0]);
		}
		
		aspect circle{ 
			draw circle (cellValue/(255*2)) color: color border:color; 
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
		/*display TextDisplay {
			species cell aspect: base  refresh:false position: {0,0};
		}*/
		
		display AugmentedDisplay  ambiant_light:0.5 polygonmode:true{	
		    species cell aspect: base  refresh:false position: {0,0};
		    species cell aspect: square  refresh:true position: {width*1.1,0};	
			species cell aspect: circle  refresh:true position: {width*2.3,0};
			species cell aspect: cylinder  refresh:true position: {width*3.5,0};
		}
		
		display Circle  ambiant_light:0.5 polygonmode:true{		
			species cell aspect: circle  refresh:true position: {0,0};
			species cell aspect: square;
			species cell aspect: cylinder;
		}
		
		display Square  ambiant_light:0.5 polygonmode:true{		
			species cell aspect: square  refresh:true position: {0,0};
		}
		
		display Cylinder ambiant_light:0.5 polygonmode:true{		
			species cell aspect: cylinder  refresh:true position: {0,0};
		}
		
		display Box  ambiant_light:0.5 polygonmode:true{		
			species cell aspect: box  refresh:true position: {0,0};
		}
	}
}
