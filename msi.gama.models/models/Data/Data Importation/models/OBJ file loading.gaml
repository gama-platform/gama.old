/**
* Name: Complex Object Loading
* Author:  Arnaud Grignard
* Description: Provides a  complex geometry to agents (svg,obj or 3ds are accepted). The geometry becomes that of the agents.
* Tags:  load_file, 3d, skill, obj
*/

model obj_loading   

global {
	
	geometry shape <- square(10000);

	init { 
		create object{
			location <- world.location;
		}
	}  
} 

species object {
	
	geometry shape <- obj_file("../includes/teapot.obj") as geometry;


			
}	

experiment Display  type: gui {
	output {
		display complex  background:#gray type: 3d{
		  species object;				
		}
	}
}
