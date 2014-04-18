model BatchTest2Peaks

global { 
 	float x <- 0.0;	
 	float y <- 0.0;
 	float precision <- 3.0;
 	int scale <- 20;
 	
 	geometry focusOnShape <- box(100,100,160) at_location {50,50};
 	int flatness <-400;
 	
 	float height function: {heightFun(x,y)};
 	
 	float heightFun(float a, float b){
		return scale*(exp(-((a-30)^2+(b-40)^2)/flatness)+2*exp(-((a-60)^2+(b-80)^2)/flatness));
	}
 	
 	
 	
 	reflex  affiche_position {
 		write("x = "+string(x)+" y= "+string(y)+" z= "+height);
 	}   
 	
 	init {
 		
 	}
 	
}
//
//
//experiment Simulation type: gui { 
// 	output { 
// 		display basicdisplay type:opengl focus: focusOnShape ambient_light: 10 diffuse_light:100 background: rgb(10,40,55) {
//	    }
//	 }
//}


	
experiment HillClimbing type: batch repeat: 1 keep_seed: true until:( time > 0) {
		
	float truc(int a, int b){
		return a*b;
	}
 	
		 permanent { 
	 	display distribution refresh_every: 1{
	    		chart 'Distribution' type : series background :rgb('lightGray'){
					data "x" value: world.x color: rgb('blue');
					data "y" value: world.y color: rgb('red');				
				}
	    }

 		display basicdisplay type:opengl focus: focusOnShape ambient_light: 10 diffuse_light:100 refresh_every: 1 background: rgb(10,40,55) {
			graphics "curve" {
				
				loop i from: 0 to: 99 step:2{
					loop j from: 0 to: 99 step:2{
						draw line([{i,j,world.heightFun(i,j)},{i+1,j,world.heightFun(i+1,j)}]) color: °white;
						draw line([{i,j,world.heightFun(i,j)},{i,j+1,world.heightFun(i,j+1)}]) color: °white;											
					}
				}
		
				draw pacman(3,(1+cos(10*(x+y)))/5) at_location {x,y,world.heightFun(x,y)} color: °pink;
		//		draw sphere(2) at_location {x,y,((x-30)^2+(y-40)^2)/50};
				
			}
	        
	    }
	 }
	
	
	
    parameter 'x:' var: x min: 0.0 max: 100.0 step: 2;
    parameter 'y:' var: y min: 0.0 max: 100.0 step: 2;
    method hill_climbing iter_max: 10000 maximize : height;
}



experiment Exhaustive type: batch keep_seed: true repeat: 1 until: (time>0){
	 permanent { 
	 	display distribution refresh_every: 1{
	    		chart 'Distribution' type : pie background :rgb('lightGray'){
					data "x" value: world.x color: rgb('blue');
					data "y" value: world.y color: rgb('red');				
				}
	    }

 		display basicdisplay type:opengl focus: focusOnShape ambient_light: 10 diffuse_light:100 refresh_every: 1 background: rgb(10,40,55) {
			graphics "curve" {
				
				loop i from: 0 to: 99 step:2{
					loop j from: 0 to: 99 step:2{
						draw line([{i,j,world.heightFun(i,j)},{i+1,j,world.heightFun(i+1,j)}]) color: °white;
						draw line([{i,j,world.heightFun(i,j)},{i,j+1,world.heightFun(i,j+1)}]) color: °white;											
					}
				}
		
				draw pacman(3,(1+cos(10*(x+y)))/5) at_location {x,y,world.heightFun(x,y)} color: °pink;
		//		draw sphere(2) at_location {x,y,((x-30)^2+(y-40)^2)/50};
				
			}
	        
	    }
	 }
	
	parameter 'x:' var: x min: 0.0 max: 100.0 step: 2;
	parameter 'y:' var: y min: 0.0 max: 100.0 step: 2;
}
//
//experiment Annealing type: batch keep_seed: true repeat: 1 until: (time>0){
//	parameter 'x:' var: x min: 0.0 max: 6.0 step: 0.5;
//	parameter 'y:' var: y min: 0.0 max: 6.0 step: 0.5;
//	method annealing temp_init: 100  temp_end: 1 temp_decrease: 0.5nb_iter_cst_temp: 5 minimize : height;
//}


