	/**
* Name: Light definition
* Author: Arnaud Grignard & Julien Mazars
* Description: Model presenting how to manipulate lights (spot lights and point lights) in a 3D display 
* Tags: 3d, light
*/

model lighting   

global {

	init { 
		create lightMoving number:2;
		create GAMAGeometry2D number: 1{ 
			location <- {world.shape.width/2, world.shape.height/2, 0};	
		}
	}  
} 
 
species GAMAGeometry2D{  
	aspect default {
		draw sphere(10) at:location color:°white border:#gray;
    }
}

species lightMoving skills:[moving] {
	reflex update {
		do wander amplitude:180.0;
	}
}

experiment Display  type: gui autorun:true{
	float minimum_cycle_duration<-0.01;

	output {
		layout #split;
		// display using spot lights
		// we set the ambient light to 0 to see better the directional lights (as if we were at night time)
		display SpotLights type:opengl  background:rgb(10,40,55) ambient_light:0 {
			// we define 3 lights : the blue and red turn around the scene, changing their orientation so that the scene is always lightened
			// the green light does not change its position, but the angle of the spot changes
			light 1 type:spot position:{world.shape.width*cos(cycle)+world.shape.width/2,world.shape.height*sin(cycle)+world.shape.height/2,20} direction:{cos(cycle+180),sin(cycle+180),-1} color:#red draw_light:true quadratic_attenuation:0.0001;
			light 2 type:spot position:{world.shape.width*cos(cycle+180)+world.shape.width/2,world.shape.height*sin(cycle+180)+world.shape.height/2,20} direction:{cos(cycle),sin(cycle),-1} color:#blue draw_light:true quadratic_attenuation:0.0001;
			light 3 type:spot position:{world.shape.width/2,world.shape.height/2,world.shape.width/2} direction:{0,0,-1} color:#green draw_light:true spot_angle:30*(1+cos(2*cycle)) quadratic_attenuation:0.0001;
			species GAMAGeometry2D aspect:default;
		}
		// display using point lights
		// we set the ambient light to 0 to see better the directional lights (as if we were at night time)
		display PointLights type:opengl  background:rgb(10,40,55) ambient_light:0 {
			// we define 3 lights : the blue and red turn around the scene
			// the green light change its location up and down, we can see the quadratic_attenuation effect : the farther the light is, the less power it has			
			light 1 type:point position:{world.shape.width*cos(cycle)+world.shape.width/2,world.shape.height*sin(cycle)+world.shape.height/2,20} color:#red draw_light:true quadratic_attenuation:0.0001;
			light 2 type:point position:{world.shape.width*cos(cycle+180)+world.shape.width/2,world.shape.height*sin(cycle+180)+world.shape.height/2,20} color:#blue draw_light:true quadratic_attenuation:0.0001;
			light 3 type:point position:{world.shape.width/2,world.shape.height/2,world.shape.width*cos(cycle)} color:#green draw_light:true quadratic_attenuation:0.0001;
			species GAMAGeometry2D aspect:default;
		}
		// display using direction lights
		// we set the ambient light to 0 to see better the directional lights (as if we were at night time)
		display DirectionLights type:opengl  background:rgb(10,40,55) ambient_light:0 {
			// we define 3 lights : the blue and red change their direction
			// the green light change its intensity
			light 1 type:direction direction:{cos(cycle+180),sin(cycle+180),-1} color:#red draw_light:true;
			light 2 type:direction direction:{cos(cycle),sin(cycle),-1} color:#blue draw_light:true;
			light 3 type:direction direction:{0,0,-1} color:rgb(0,255*(1+cos(cycle)),0) draw_light:true;
			species GAMAGeometry2D aspect:default;
		}
	}
}