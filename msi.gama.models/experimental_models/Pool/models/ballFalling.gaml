/**
 *  ballFalling
 *  Author: JG228105
 *  Description:  
 */   

model ballFalling
//Proposed by Javier Gil-Quijano
global{
	PhysicalWorld world2;
	init{
		create people; 
		create PhysicalWorld;
		set world2 <- first(PhysicalWorld as list);
		ask world2 {set registeredAgents <- people as list;}
	}
	reflex {
		ask world2 {do computeForces;}
	} 
}
species people skills: [physical]{ 
	reflex moveP {
		do update_physic physics_world: world2;
	}
	aspect base {
		draw shape: shape color: 'black' ;
	}
}
	
	output {
		display balls_display refresh_every: 1 {
			species people aspect: base;
		}
		
	}


