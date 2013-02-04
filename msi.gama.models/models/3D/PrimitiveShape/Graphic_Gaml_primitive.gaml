/**
 *  round_rectangle
 *  Author: Arnaud Grignard
 *  Description: Display random rectangle with roundCorner
 */

model Graphic_primitive   

global {
	
} 
 
environment width: 100 height: 100;  
 
 
entities { 
	

}
experiment display  type: gui {
	output {
		display Poincare refresh_every: 1   type:opengl ambiant_light:0.2{
			graphics GraphicPrimitive{
				
				draw circle(10) color: rgb('red') at: {0,0,0};
				draw cylinder(10,5) color: rgb('red') at: {25,0,0};  
				draw sphere(10) color: rgb('red') at: {50,0,0}; 
				
				draw square(20) color: rgb('blue') at: {0,25,0};
				draw cube(20) color: rgb('blue') at: {25,25,0};
				
				draw rectangle(20, 15) color: rgb('green') at: {0,50,0}; 
				draw rectangle({20, 15}) color: rgb('green') at: {25,50,0}; 
				draw box({20, 15 , 10}) color: rgb('green') at: {50,50,0};
				
				draw polygon ([{0,0},{10,0},{10,10},{15,15},{10,15},{5,10},{0,0}]) color: rgb('yellow') at: {0,75,0};
				draw polyhedron ([{0,0},{10,0},{10,10},{15,15},{10,15},{5,10},{0,0}],10) color: rgb('yellow') at: {25,75,0}; 
			}					
		}
	}
}
