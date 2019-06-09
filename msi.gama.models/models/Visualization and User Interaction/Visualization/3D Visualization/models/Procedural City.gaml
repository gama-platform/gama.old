/**
* Name: Procedural City
* Author: Arnaud Grignard
* Description: Model with a 3D Display representing buildings with a random size. The model proposes two experiments : the first one represents 
*      the buildings with 3D shapes with textures, the second one without textures but the color of the buildings depends on the rotation of a ball-shaped light
* Tags: 3d, texture, light
*/

model procedural_city   

global {
	int number_of_building parameter: 'Number of Agents' min: 1 <- 300 category: 'Initialization';
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 500 category: 'Initialization';
	
	geometry shape <- square(width_and_height_of_environment);
		
	file roof_texture <- file('../images/building_texture/roof_top.jpg') ;		
	list textures <- [file('../images/building_texture/texture1.jpg'),file('../images/building_texture/texture2.jpg'),file('../images/building_texture/texture3.jpg'),file('../images/building_texture/texture4.jpg'),file('../images/building_texture/texture5.jpg'),
	file('../images/building_texture/texture6.jpg'),file('../images/building_texture/texture7.jpg'),file('../images/building_texture/texture8.jpg'),file('../images/building_texture/texture9.jpg'),file('../images/building_texture/texture10.jpg')];

	init { 
      create Building number:number_of_building{
      	     width <- (rnd(100)/100)*(rnd(100)/100)*(rnd(100)/100)*50+10;
			 depth <-	(rnd(100)/100)*(rnd(100)/100)*(rnd(100)/100*width)*10+10;
			shape <- box(width, width, depth) rotated_by rnd(360);
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
		 depth <-	(rnd(100)/100)*(rnd(100)/100)*(rnd(100)/100*width)*10+10;
		shape <- box(width, width, depth) rotated_by rnd(360);
	}
	
	aspect base {
		draw shape color:#white;
	}
	
	aspect textured {
		draw shape texture:[roof_texture.path,texture.path] color: rnd_color(255);
	}
}	


experiment DisplayTextured  type: gui {
	output {
	  display City type:opengl background:#white {
			species Building aspect:textured;							
		}
	}
}
experiment DisplayWithDynamicDiffuseLight  type: gui {
	output {
	  display City type:opengl background:rgb(10,40,55){
	  		light 1 type:point color:hsb((time mod 255) /255,1.0 ,0.5) position:{world.shape.width*0.5+ world.shape.width*1.5*sin(time*2),world.shape.width*0.5,world.shape.width*cos(time*2)} draw_light:true update:true;
			species Building aspect:base;									
		}
	}
}

