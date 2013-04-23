/**
 *  round_rectangle
 *  Author: Arnaud Grignard
 *  Description: Display random rectangle with roundCorner
 */

model primitive_shape   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 100 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 100 ;
	rgb global_color;
	int maxSize;
	
	list blueCombination <- [([0,113,188]),([68,199,244]),([157,220,249]),([212,239,252])];
	list blueNeutral <- [([0,107,145]),([211,198,173]),([241,223,183])];
	
	list ColorList <- [blueCombination,blueNeutral];
	
	init { 
		
		set global_color <- rgb("yellow");//global_color hsb_to_rgb ([0.25,1.0,1.0]);

		set maxSize<- 10;
		
		create mySquare number:number_of_agents{
			set self.width <- rnd(maxSize)+1;
			set self.height <-rnd(maxSize)+1;		
			set color <- rgb((ColorList[1])[rnd(2)]);
		}

	}  
} 
 
environment width: width_and_height_of_environment height: width_and_height_of_environment;  
 
 
entities { 
	
	species mySquare{
		//const color type: rgb <- [255, 131,0] as rgb;
		float width ;
		float height;
		rgb color;	
		
		reflex updateShape{
			set self.width <- rnd(maxSize);
			set self.height <-rnd(maxSize);
			set color <- rgb (blueCombination[rnd(3)]);
			
		}
		
		/*reflex UpdateHSBColor{
			set color <- color hsb_to_rgb ([(width*height)/(maxSize*maxSize),1.0,1.0]);
		}*/

		aspect RoundCorner {
			draw rectangle({self.width, self.height}) color: color rounded:true ; 
		}
	}	


}
experiment display  type: gui {
	output {
		display Poincare refresh_every: 1   type:opengl ambiant_light:50 polygonmode:true{
			species mySquare aspect:RoundCorner;					
		}
	}
}
