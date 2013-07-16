model circle3D   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 1000 ;
	int radius parameter: 'Radius' min: 10 <- 10 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 1000 ;  
	bool wander3D <- true parameter: 'Wander 3D';
	bool sphere <- true parameter: 'sphere';
	
	list blueCombination <- [([0,113,188]),([68,199,244]),([157,220,249]),([212,239,252])];

	init { 
		create cells number: number_of_agents { 
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};
			set color <- rgb((blueCombination)[rnd(3)]);
		} 
	}  
} 
  
environment width: width_and_height_of_environment height: width_and_height_of_environment;  
 
  
entities { 
	species cells skills: [moving] {  
		rgb color;
		reflex move {
			if(wander3D){
			  do wander_3D;
			}else{
			  do wander;
			}
			
		}		
		aspect default {
			if(sphere){
			  draw sphere(radius) ;	
			}else{
			  draw circle(radius) ;	
	      }
	    }
	}
}
experiment display  type: gui {
	output {
		display WanderingSphere type:opengl ambient_light:100 background: rgb('black') autosave:true{
			species cells;
		}
	}
}
