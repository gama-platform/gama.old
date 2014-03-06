/**
 *  round_rectangle
 *  Author: Arnaud Grignard
 *  Description: Display random rectangle with roundCorner
 */

model primitive_shape   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 100 category: 'Initialization';
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 100 category: 'Initialization';
	
	int elevation parameter: 'Elevation' min: 0 <- 0 ;
	
	rgb global_color;
	int maxSize;
	
	file imageRaster <- file('images/Gama.png') ;
	
	list blueCombination <- [([0,113,188]),([68,199,244]),([157,220,249]),([212,239,252])];
	list blueNeutral <- [([0,107,145]),([211,198,173]),([241,223,183])];
	list ColorList <- [blueCombination,blueNeutral];
	
	geometry shape <- square(width_and_height_of_environment);
	init { 
		
		global_color <- rgb("yellow");//global_color hsb_to_rgb ([0.25,1.0,1.0]);

		maxSize<- 10;
		
		create mySquare number:number_of_agents{
			width <- float(rnd(maxSize)+1);
			height <-float(rnd(maxSize)+1);		
			color <- rgb((ColorList[0])[rnd(2)]);
		}
	}  
} 
 
entities { 
	
	species mySquare{

		float width ;
		float height;
		rgb color;	
		
		reflex updateShape{
			width <- float(rnd(maxSize));
			height <-float(rnd(maxSize));
			color <- rgb (blueCombination[rnd(3)]);
			
		}
		aspect RoundCorner {
			draw rectangle({self.width, self.height}) color: color border:color rounded:true depth:elevation; 
		}
	}	
	

}
experiment Display  type: gui {
	output {
		display Poincare refresh_every: 1  type:opengl ambient_light:50 {
			
			image imageRaster.path ;
			species mySquare aspect:RoundCorner transparency: 0.5 position: {0,0,0.1} ;	
												
		}
	}
}
