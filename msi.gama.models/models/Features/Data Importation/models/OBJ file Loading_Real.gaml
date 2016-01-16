/**
 *  Complex Object Loading
 *  Author: Arnaud Grignard
 *  Description: Display complex object (svg,obj and 3ds)
 */

model complexobjectloading   

global {
	geometry shape <- square(100);

	init { 
		create ComplexObject number:1{
		shape <- circle(1);
		}
	}  
} 

species ComplexObject skills:[moving]{
	
	reflex move{
		do wander;
	}
	aspect obj {
		draw shape asset3D:[file("./../includes/c.obj").path, file("./../includes/c.mtl").path] rotate3D:(time ::{1,0,0});
	}
			
}	

experiment Display  type: gui {
	output {
		display ComplexObject type:opengl background:#gray{
		  species ComplexObject aspect:obj;				
		}
	}
}
