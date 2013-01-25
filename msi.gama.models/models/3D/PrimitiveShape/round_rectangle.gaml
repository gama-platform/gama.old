/**
 *  round_rectangle
 *  Author: Arnaud Grignard
 *  Description: Display random rectangle with roundCorner
 */

model primitive_shape   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 100 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 250 ;
	rgb global_color;
	int maxSize;
	
	init { 
		
		set global_color <- rgb("yellow");//global_color hsb_to_rgb ([0.25,1.0,1.0]);

		set maxSize<- 10;
		create mySquare number:number_of_agents{
			set self.width <- rnd(maxSize)+1;
			set self.height <-rnd(maxSize)+1;
			set color <- color hsb_to_rgb ([(width*height)/(maxSize*maxSize),1.0,1.0]);
		}

	}  
} 
 
environment width: width_and_height_of_environment height: width_and_height_of_environment/2;  
 
 
entities { 
	
	species mySquare{
		//const color type: rgb <- [255, 131,0] as rgb;
		float width ;
		float height;
		rgb color;	
		
		reflex updateShape{
			set self.width <- rnd(maxSize);
			set self.height <-rnd(maxSize);
			set color <- color hsb_to_rgb ([(width*height)/(maxSize*maxSize),1.0,1.0]);
		}
		aspect RoundCorner {
			draw rectangle({self.width, self.height}) color: color rounded:true; 
		}
	}	


}
experiment display  type: gui {
	output {
		display Poincare refresh_every: 1   type:opengl ambiant_light:0.2{
			population mySquare aspect:RoundCorner;					
		}
	}
}
