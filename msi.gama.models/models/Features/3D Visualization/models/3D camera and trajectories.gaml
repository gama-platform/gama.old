model boidsVisualization


import "../includes/Boids.gaml"

experiment start type: gui {
	
	float minimum_cycle_duration <- 0.03;
	output {
		display RealBoids  type:opengl ambient_light:255 z_fighting:false trace: 30{
			image name:'background' file:'../images/sky.jpg';
			species boids aspect: image  position:{0,0,0.1};
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
