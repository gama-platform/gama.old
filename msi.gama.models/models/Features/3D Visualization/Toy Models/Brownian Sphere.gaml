/**
 *  brownianSphere
 *  Author: Arnaud Grignard
 */


model brownianSphere   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 1000 category: 'Initialization';
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 1000 category: 'Initialization';  
	int radius parameter: 'Radius' min: 10 <- 10 ;
	
	list blueCombination <- [([0,113,188]),([68,199,244]),([157,220,249]),([212,239,252])];
	geometry shape <- square(width_and_height_of_environment);
	
	init { 
		create cells number: number_of_agents { 
			location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};
			color <- rgb((blueCombination)[rnd(3)]);
		} 
	}  
} 
    
species cells skills: [moving] {  
	rgb color;
	
	reflex move {
		  do wander_3D z_max:width_and_height_of_environment;
	}	
    	
	aspect default {
		  draw sphere(radius) color:color;	
    }
}
	

experiment Display  type: gui {
	output {
		display WanderingSphere type:opengl  ambient_light:0 diffuse_light:100 background:rgb(10,40,55){
			species cells;
		}
	}
}


