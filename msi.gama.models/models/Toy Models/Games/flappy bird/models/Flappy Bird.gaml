/**
* Name: Flappy Bird, try to avoid obstacles to gain points.
* Author: Loris Henry
* Tags: Flappy Bird, game, user interaction
*/


model FlappyBird

global {
	
	
	float step <- 65#ms;
	float g <- 9.81 #m/(#s^2);
	float tuyau_speed <- 0.5 #m/#s;
	file pipe <- image_file("../includes/pipe9.png");
	file bird_image <- image_file("../includes/birdd.png");
	
	
	point param_size <- {3.0, 4.65}parameter:true;
	point off_set <- {0.035,0.16,0}parameter:true;
	float bird_to_size <- 5.2 parameter:true;
	point bird_offset <- {0.017, -0.012} parameter:true;
	point text_point <- {0.97, 0.18} parameter:true;
	float text_size <- 30.0 parameter:true;
	point graph_position <- {0.2, 0.2} parameter:true;
	
	geometry shape <- square(2#m);
	
	float speed_init <- -1.7#m/#s;
	int count<-0;
	bool game_over <- false;
	
	init {
		
		do reinit_model;
	}
	action reinit_model {
		
		ask texts {
			do die;
		}
		count<-0;
		game_over <- false;
	
		create bird {
			location <- point([1, 1]);
		}
		create texts {
			my_text <- "0";
			location <- text_point;
			f <- font("Flappy Bird Font", text_size);
		}
	}
	
	action to_game_over {
		game_over <- true;
		ask bird { 
			do die;
		}
		ask tuyau {
			do die;
		}
	}
	reflex add_tuyau when:not game_over and every(9/5#s) {
				create tuyau {
			
			location <- {3,rnd(0.5, 1.5)};
			
		}
	}

}

species bird frequency: game_over ? 0 : 1{
	float speed;
	bool impulsion <- false;
	float size <- 7.5#cm;
	rgb color <- #blue;	
	
	reflex move {
		speed <- speed + g*step;
		if impulsion {
			speed <- speed_init;
			impulsion <- false;
		}
		location <- point([location.x, location.y + speed*step]);
	}
	
	reflex collision when: circle(size,location) intersects union(tuyau collect each.fake_shape){
		ask world {do to_game_over;}
	}
	reflex border when: location.y > 2.0 - size or location.y < 0.2 + size {
		ask world {do to_game_over;}
	} 
	
	aspect default {
		draw circle(size) color:color;
	}
	aspect png {
		draw bird_image size:bird_to_size*size at: location+bird_offset rotate:atan(speed/(4*tuyau_speed));
	}
}

species tuyau {
	float speed <- tuyau_speed;
	
	geometry fake_shape1;
	geometry fake_shape2;
	geometry fake_shape3;
	geometry fake_shape4;
	geometry fake_shape5;
	geometry fake_shape6;
	geometry fake_shape;
	bool has_counted <- false;
	
	init {
		fake_shape1 <- line([{location.x, 0}, {location.x, location.y - 0.25}]);
		fake_shape3 <- rectangle({location.x - 0.22, location.y + 0.25}, {location.x + 0.216, location.y + 0.45});
		fake_shape4 <- rectangle({location.x - 0.22, location.y - 0.25}, {location.x + 0.216, location.y -0.05});
		
		
		fake_shape2 <- line([{location.x, location.y + 0.25}, {location.x, 2}]);
		
		fake_shape5 <- rectangle({location.x - 0.188, location.y + 0.45}, {location.x + 0.187, location.y + 2});
		fake_shape6 <- rectangle({location.x - 0.188, location.y - 2}, {location.x + 0.187, location.y - 0.45});
		fake_shape <- union(fake_shape3, fake_shape4, fake_shape5, fake_shape6);
	}
	
	reflex move {
		location <- point([location.x - speed*step, location.y]);
	}
	reflex update_shape {
		fake_shape1 <- line([{location.x, 0}, {location.x, location.y - 0.25}]);
		fake_shape2 <- line([{location.x, location.y + 0.25}, {location.x, 2}]);
		fake_shape3 <- rectangle({location.x - 0.22, location.y + 0.25}, {location.x + 0.216, location.y + 0.45});
		fake_shape4 <- rectangle({location.x - 0.22, location.y - 0.25}, {location.x + 0.216, location.y -0.45});
		fake_shape5 <- rectangle({location.x - 0.188, location.y + 0.45}, {location.x + 0.187, location.y + 2});
		fake_shape6 <- rectangle({location.x - 0.188, location.y - 2}, {location.x + 0.187, location.y - 0.45});
		fake_shape <- union(fake_shape3, fake_shape4, fake_shape5, fake_shape6);




	}
	reflex count when:location.x < 1.0 and not has_counted{
		count <- count + 1;
		has_counted <- true;
		write name;
		ask texts[0] {
			my_text <- count as string;
		}
	}
	
	aspect default {
		draw fake_shape1 color:#lime;
		draw fake_shape2 color:#lime;
	}
	aspect png {
		draw pipe size:param_size at: location - off_set;
	}
}

species texts {
	string my_text;
	font f;
	rgb color <- #black;
	
	aspect default {
		draw rectangle(2, 0.2) color:#orange at:{1,0.1};
		draw my_text color:color font:f size:text_size;
	}
}



experiment main {
	
	float minimum_cycle_time <- step;
	
	bool has_started<-false;
	
	output synchronized:true{
		
		layout consoles:false editors:false navigator:false parameters:false toolbars:false tray:false;
		
		
		display main fullscreen:true type:2d {
			
			image_layer "../includes/background.png";
			species bird aspect:png;
			
			species tuyau aspect:png;
			species texts;
			event "r" {
				ask world {
					do reinit_model;
				}
			}
			
			event " " {
				if not has_started {
					ask simulation {
						do resume;
						myself.has_started <- true;
						}
				
					}
				ask bird {impulsion <- true;
					
				}
			}
			graphics "Start" position:{0,0} size:{0.15, 0.6}{
				if not has_started{
				draw "Press space to play" font:font("FlappyBirdy", 70) color:#white;
				
				}
			}
			graphics "Game Over" position:{0,0} size:{0.25, 0.7}{
				if game_over{
					draw "Game Over" font:font("FlappyBirdy",120, #bold  ) color:#white;
					draw " Press R to reload" font:font("FlappyBirdy",70, #bold  ) color:#white at: {0.9, 1.4};
				}
				
			}
		}
	}
	
}