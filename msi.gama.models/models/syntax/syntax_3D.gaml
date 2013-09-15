/**
 *  syntax3D
 *  Author: A. Grignard
 *  Description: An overview of the new Graphical 3D syntactic constructs that will be introduced in GAMA 1.6
 */

model syntax3D   

global {
	
} 
 
environment width: 200 height: 100;  
 
 
entities { 
	

}
experiment Display  type: gui {
	output {
		display Dislay refresh_every: 1   type:opengl ambient_light:100 draw_env:false{
			graphics 'GraphicPrimitive'{
				//2D PRIMITIVE

				draw line ([{0,0},{10,10}]) at: {0,0,0} color:rgb('orange');
				draw polyline ([{0,0},{5,5},{10,0}]) at: {25,0,0} color:rgb('orange');	
				draw circle(10) color: rgb('red') at: {50,0,0} border:rgb('yellow');
				draw square(20) color: rgb('blue') at: {75,0,0};
				draw rectangle(20, 15) color: rgb('green') at: {100,0,0};
				draw polygon ([{0,0},{10,0},{20,10},{20,20},{0,20},{0,0}]) color: rgb('yellow') at: {125,0,0};
				
				//3D PRIMITIVE
				draw plan ([{0,0},{10,10}],10) at: {0,50,0};
				draw polyplan ([{0,0},{5,5},{10,0}],10) at: {25,50,0};
				draw cylinder(10,5) color: rgb('red') at: {50,50,0};  
				draw sphere(10) color: rgb('red') at: {75,50,0}; 
				draw cube(20) color: rgb('blue') at: {100,50,0};
				draw box({20, 15 , 10}) color: rgb('green') at: {125,50,0};		
				draw polyhedron ([{0,0},{10,0},{20,10},{20,20},{0,20},{0,0}],10) color: rgb('yellow') at: {150,50,0}; 
					
						
			}					
		}
	}
}