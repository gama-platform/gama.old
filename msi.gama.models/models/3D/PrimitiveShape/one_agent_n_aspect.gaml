/**
 *  one_agent_n_aspect
 *  Author: Arnaud Grignard
 *  Description: Create one agent with different aspect. Each agent wander at each step of the simulation.
 */

model one_agent_n_aspect   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 100 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 25;
	file imageRaster <- file('images/Gama.png') ;
	rgb myColor parameter: 'Color' <- rgb('blue');

	init { 		
		create myAgent number:number_of_agents; 
	}  
} 
 
environment width: width_and_height_of_environment height: width_and_height_of_environment; 
 
entities { 
	

	species myAgent skills: [moving]{
		
		aspect text{
			draw text: string (self) color: rgb('black');
		}	
		aspect point {
			draw geometry:geometry (point(self.location)); 
		}
		
		aspect sphere{
			draw sphere(0.1) color: myColor at:location;
		}
		aspect dynaSphere{
			let colorValue <- ((self.location.x + self.location.y)/(width_and_height_of_environment*2))*255;
			draw sphere(self.location.x/100 + self.location.y/100) color: rgb([colorValue, colorValue, colorValue]) at:location;
		}	
		
		aspect image{
			draw image: imageRaster size: 1;
		}
		 
		reflex wander{
		  do wander;	
		}	 
	}
}
experiment display  type: gui {
	output {
		display text refresh_every: 1 type:opengl{
			species myAgent aspect:text;	
		}
		display point refresh_every: 1 type:opengl{
			species myAgent aspect:point;	
		}
		display sphere refresh_every: 1 type:opengl{
			species myAgent aspect:sphere;	
		}
		display dynaSphere refresh_every: 1 type:opengl{
			species myAgent aspect:dynaSphere;	
		}
		display image{
			species myAgent aspect:image;
		}
	}
}
