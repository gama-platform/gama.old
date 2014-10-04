/**
 *  m1
 *  Author: hqnghi
 *  Description:  
 */
  
model comodelAnts
import "Ant Foraging (Complex).gaml" as myAnt
import "Boids.gaml" as myBoids
global {
	geometry shape<-envelope(900);
	
	init{
		create myBoids.Boids2 with:[shape::circle(10),width_and_height_of_environment::800,number_of_agents::50];
		create myAnt.Complete with:[gridsize::100,ants_number::100];
	}
	reflex dododo{
		
		
		ask (myAnt.Complete){
			do _step_;
		}
		ask (myBoids.Boids2){
			width_and_height_of_environment<-100;
			shape<-square(100);
			do _step_;
		}
	}
}
species Hunter skills:[moving]{
	point oldLoc<-location;
	reflex ss{
		oldLoc<-location;
		do wander;
		
	}
	aspect base{
		draw circle(1) color:#red;
	}
}

//species ant2 mirrors: first(myAnt.Complete).simulation.ant {
//	point location <- target.location update:  target.location;
//}
experiment comodel2exp type: gui {
	
	
	output {
		display "a_disp" {			
			image 'background' file:'../images/soil.jpg';
			species first(myBoids.Boids2).simulation.boids aspect: image;
			agents "agents_ant_grid" transparency: 0.5 position:(first(myBoids.Boids2).simulation.boids_goal) value: (first(myAnt.Complete).simulation.ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			agents "agents_ant" aspect:icon value:first(myAnt.Complete).simulation.ant  position:(first(myBoids.Boids2).simulation.boids_goal); 

		} 
	}
	
}
