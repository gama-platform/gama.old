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
				draw box({20, 15 , 10}) color: rgb('green') at: {25,50,0};		
				draw polygon ([{0,0},{10,0},{20,10},{20,20},{0,20},{0,0}]) color: rgb('yellow') at: {0,75,0};
				draw polyhedron ([{0,0},{10,0},{20,10},{20,20},{0,20},{0,0}],10) color: rgb('yellow') at: {25,75,0}; 
				draw line ([{0,0},{10,10}]) at: {0,100,0};		
				draw plan ([{0,0},{10,10}],10) at: {25,100,0};
				draw polyline ([{0,0},{5,5},{10,0}]) at: {0,125,0};
				draw polyplan ([{0,0},{5,5},{10,0}],10) at: {25,125,0};
						
				draw geometry (line ([{75,0},{75,100}]));
				//Old style
				//Cylinder
				draw circle(10) color: rgb('red') depth:5 at: {100,0,0};
				//Sphere
				draw geometry (point([125,0])) color: rgb('red') depth:10;	
				//Cube
				draw square(20) color: rgb('blue') depth:20 at: {100,25,0};
				//box
				draw rectangle(20, 15) color: rgb('green') at: {100,50,0} depth:10; 
				//polyhedron
				draw polygon ([{0,0},{10,0},{20,10},{20,20},{0,20},{0,0}]) color: rgb('yellow') at: {100,75,0} depth:10;
				//plan
				draw line ([{0,0},{10,10}]) at: {100,100,0} depth:10;
				//polyplan
				draw line ([{0,0},{5,5},{10,0}]) at: {100,125,0} depth:10;			
			}					
		}
	}
}
