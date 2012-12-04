/**
 *  one_agent_n_aspect
 *  Author: Arnaud Grignard
 *  Description: Create one agent with different aspect. Each agent wander at each step of the simulation.
 */

model one_agent_n_aspect   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 100 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 25;
	file imageRaster <- file('Gama.png') ;
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
			draw geometry: geometry (point(self.location)) color: myColor z:0.1;
		}
		aspect dynaSphere{
			let colorValue <- ((self.location.x + self.location.y)/(width_and_height_of_environment*2))*255;
			draw geometry: geometry (point(self.location)) color: rgb([colorValue, colorValue, colorValue]) z:self.location.x/100 + self.location.y/100;
		}	
		
		aspect image{
			draw image: imageRaster.path size: 1;
		}
		do wander;
	}
}
experiment display  type: gui {
	output {
		display Display refresh_every: 1 type:opengl{
			species myAgent aspect:sphere;	
		}
	}
}
