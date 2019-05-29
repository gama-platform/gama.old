	/**
* Name: Camera Position
* Author: Arnaud Grignard 
* Description: Model presenting how to cameras in a 3D display
* Tags: 3d, light
*/

model camera_position   

global {

	init { 
		create object;
	}  
} 
 
species object skills:[moving]{  
	
	reflex move{
		do wander;
	}
	aspect default {
		draw sphere(1) at:location color:°white border:#gray;
    }
}


experiment Display  type: gui autorun:true{
	float minimum_cycle_duration<-0.01;

	output {
		layout #split;
		display DefautlCamera type:opengl{
			species object aspect:default;
		}
		display FixCameraPosition camera_interaction:false camera_pos:{world.shape.width/2,world.shape.height*2,world.shape.width*2} 
		camera_look_pos:{world.shape.width/2,world.shape.height/2,0} 
		camera_up_vector:{0.0,-1.0,0.0}type:opengl{
			species object aspect:default;
		}
		display DynamicCameraPosition type:opengl camera_pos:{world.shape.width*cos(cycle),world.shape.width*sin(cycle),world.shape.width*2} 
		camera_look_pos:{world.shape.width/2,world.shape.height/2,0} 
		camera_up_vector:{0.0,-1.0,0.0}{
			species object aspect:default;
		}
		
		display FollowObjectCamera type:opengl camera_interaction:false camera_pos:{int(first(object).location.x),int(first(object).location.y),250} 
		camera_look_pos:{int(first(object).location.x),first(object).location.y,0} 
		camera_up_vector:{0.0,-1.0,0.0}{
			species object aspect:default;
		}
		display FirstPerson type:opengl camera_interaction:true camera_pos:{int(first(object).location.x),int(first(object).location.y),10} 
		camera_look_pos:{cos(first(object).heading)*first(object).speed+int(first(object).location.x),
		sin(first(object).heading)*first(object).speed+int(first(object).location.y),10} 
		camera_up_vector:{0.0,0.0,1.0}{
			species object aspect:default;
		}
		display FocusPerson type:opengl focus:first(object){
			species object aspect:default;
		}
	}
}