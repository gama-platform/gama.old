/**
* Name: 3D Camera and Camera Positionning with Boids model
* Author: 
* Description: Model using the Boids model to represent the flight of boids following a goal and avoiding obstacles.
*     Five experiments are proposed with differents goals : understanding of the model used (Boids_gui), 3D Display (start), 3D Display with trajectories analysis of boids (trajectory_analysis),
*     one coupling both previous experiments and the time simulated with boids going higher and higher according to the time (SpaceTimeCube) and a model presenting the different views 
*     possible in the 3D Display : first person, third person and global view (Multiple view). 
* Tag: 3D Display, Camera Positioning
*/

model boidsVisualization

//import everything from Boids model, even the experiments
import "../includes/Boids.gaml"

experiment start type: gui {
	
	//each cycle will have a minimum duration of 0.03 second to slow the experiment allowing a better view of the dynamics
	float minimum_cycle_duration <- 0.03;
	
	//the type opengl allows the 3D Display instead of only 2D
	output {
		display RealBoids  type:opengl ambient_light:255 z_fighting:false {
			image name:'background' file:'../images/sky.jpg';
			species boids aspect: image  position:{0,0,0.1} trace: 30;
			species boids_goal transparency:0.2 position:{0,0,0.1};
			species obstacle position:{0,0,0.1}; 		
		}
	}
}

experiment trajectory_analysis type: gui {
	
	float minimum_cycle_duration <- 0.03;
	output {
		
		display RealBoids  type:opengl ambient_light:100{
			image name:'background' file:'../images/sky.jpg';
			species boids trace:true{
			    draw triangle(20) rotate: 90 + heading color: hsb (float(heading)/360.0,1.0,1.0) depth:8 ;	
			}
			species boids_goal transparency:0.2 position:{0,0,0.1};
			species obstacle position:{0,0,0.1}; 		
		}
	}
}

experiment SpaceTimeCube type: gui {
	
	float minimum_cycle_duration <- 0.03;
	output {
		display RealBoids  type:opengl ambient_light:50 diffuse_light:100{
			image name:'background' file:'../images/sky.jpg';
			species boids aspect: image transparency:0.5 position:{0,0,0.11};
			species boids_goal transparency:0.2 position:{0,0,0.1};
			species obstacle position:{0,0,0.1}; 		
		}
		
		display SpaceTimeCubeAll  type:opengl ambient_light:50 diffuse_light:100{
			image name:'background' file:'../images/sky.jpg';
			species boids trace:true{
			    draw triangle(20) rotate: 90 + heading color: hsb (float(heading)/360.0,1.0,1.0) depth:8 at: {location.x ,location.y,location.z+time};	
			}
			species boids_goal trace:true{
				draw sphere(10) color: #yellow at: {location.x ,location.y,location.z+time};
			}	
		}
				
	}
}

experiment MultipleView type: gui {
	float minimum_cycle_duration <- 0.03;
	output {
		display RealBoids   type:opengl ambient_light:255 {
			image name:'background' file:'../images/sky.jpg';
			species boids aspect: image  transparency:0.5 position:{0,0,0.25};
			species boids_goal transparency:0.2 position:{0,0,0.25};
			species obstacle ;
			species boids  aspect: image transparency:0.2 position:{0,0,0.24};		
		}
		//The facet camera_pos is the position of the camera in the 3D display and the facet camera_look_pos is the position where the camera is looking
		display ThirdPersonn  type:opengl camera_pos:{int(first(boids).location.x),-int(first(boids).location.y),250} 
		camera_look_pos:{int(first(boids).location.x),-(first(boids).location.y),0} {
		
			image name:'background' file:'../images/sky.jpg';
			species obstacle;
			species boids trace:true{
			    draw triangle(20) rotate: 90 + heading color: hsb (float(heading)/360.0,1.0,1.0) depth:8 ;	
			}
			species boids_goal trace:true{
				draw sphere(10) color: #yellow;
			}	
		}
		
		//The facet camera_up_vector allows the definition of the vector on which the top of the camera point at, it has to be perpendicular to the look vector
		display FirstPerson  type:opengl ambient_light:100 camera_pos:{int(first(boids).location.x),-int(first(boids).location.y),10} 
		camera_look_pos:{cos(first(boids).heading)*world.shape.width,-sin(first(boids).heading)*world.shape.height,0} camera_up_vector:{0.0,0.0,1.0} {	
			image name:'background' file:'../images/sky.jpg';
			species obstacle ;
			species boids trace:true{
			    draw triangle(20) rotate: 90 + heading color: hsb (float(heading)/360.0,1.0,1.0) depth:8 ;	
			}
			species boids_goal trace:true{
				draw sphere(10) color: #yellow;
			}		
		}
	}
}
