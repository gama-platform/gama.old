/**
 *  Procedural City
 *  Author: Arnaud Grignard
 *  Description: Display a procedural city with textured building
 */

model procedural_city   

global {
	int number_of_building parameter: 'Number of Agents' min: 1 <- 1000 category: 'Initialization';
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 500 category: 'Initialization';
	
	geometry shape <- square(width_and_height_of_environment);
		
	file roof_texture <- file('images/building_texture/roof_top.png') ;		
	list textures <- [file('images/building_texture/texture1.jpg'),file('images/building_texture/texture2.jpg'),file('images/building_texture/texture3.jpg'),file('images/building_texture/texture4.jpg'),file('images/building_texture/texture5.jpg'),
		file('images/building_texture/texture6.jpg'),file('images/building_texture/texture7.jpg'),file('images/building_texture/texture8.jpg'),file('images/building_texture/texture9.jpg'),file('images/building_texture/texture10.jpg')];
		
	init { 
		create Building number:number_of_building{
			width <-	(rnd(100)/100)*(rnd(100)/100)*(rnd(100)/100)*50+10;
			height<-width;
			depth <-	(rnd(100)/100)*(rnd(100)/100)*(rnd(100)/100*width)*10+10;
			angle <- rnd(360);
			texture <- textures[rnd(9)];
		}
	}  
} 

species Building{
	float width;
	float height;
	float depth;
	int angle;			
	file texture;
	
	reflex shuffle{
		width <- (rnd(100)/100)*(rnd(100)/100)*(rnd(100)/100)*50+10;
		height<- width;
		depth <- (rnd(100)/100)*(rnd(100)/100)*(rnd(100)/100*width)*8+8;
		angle <- rnd(360);	
	}
	aspect RoundCorner {
		draw box(width, height, depth) texture:[roof_texture.path,texture.path] rotate:angle;
	}
}	

experiment Display  type: gui {
	output {
		display Poincare refresh_every: 1  type:opengl ambient_light:50 background:rgb("gray"){
			species Building aspect:RoundCorner transparency:0 ;									
		}
	}
}
