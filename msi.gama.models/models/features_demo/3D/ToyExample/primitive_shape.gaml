/**
 *  primitive_shape
 *  Author: Arnaud Grignard
 *  Description: Display the basic 3D shape in an opengl display
 */

model primitive_shape   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 1 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 25 ;
	rgb global_color;
	
	file imageRaster <- file('images/Gama.png') ;
	geometry shape <- rectangle(width_and_height_of_environment,width_and_height_of_environment/2);

	init { 
		
		global_color <- rgb("yellow");//global_color hsb_to_rgb ([0.25,1.0,1.0]);
		
		//2D Primitive Shape
		create myPoint number: 1{
			location <- {2,2};
		}
		create myLine number: 1 {
			location <- {6,2}; 
		}
		
		create myMultiLine number: 1 {
			location <- {10,2}; 
		}
		
		create mySquare number:1 {
			location <- {14,2};
		}
		
		create myPolygon number:1 {
			location <- {18,2};
		}
		
		create myTexture number:1 {
			location <- {22,2};
		}
		
		
		//3D primitive shape
		create mySphere number:1{
			location <- {2,6};
		}
		create myPlan number:1 {
			location <- {6,6};
		}
		
		create myMultiPlan number:1 {
			location <- {10,6};
		}
		
		create myCube number: number_of_agents { 
			location <- {14,6};
		}
		
		create myPolyhedron number: number_of_agents { 
			location <- {18,6};
		}  
	}  
} 
 
entities { 
	
	species myPoint{
		const color type: rgb <- [0, 175,100] as rgb;
		geometry shape <- geometry (point([location.x,location.y]));
		aspect Flat {
			draw shape color: global_color ;
			draw text: "Point" size: 1 color: rgb('black');
		}		
	}
	
	species myLine{
		const color type: rgb <- [33, 98,120] as rgb;
		geometry shape <- line ([{5,3},{7,1}]) ;		
		aspect Flat {
			draw shape color: global_color  ;
			draw "Line" size: 1 color: rgb('black'); 
		}
	}
	
	species myMultiLine{
		const color type: rgb <- [2, 78,104] as rgb;
		geometry shape <- polyline([{9,3},{9,2},{11,2},{11,1}]) ;		
		aspect Flat {
			draw shape color: global_color  ;
			draw "Multiline" size: 1 color: rgb('black'); 
		}
	}
		
	species mySquare{
		const color type: rgb <- [255, 131,0] as rgb;
		geometry shape <- rectangle({2, 2})  ;		
		aspect Flat {
			draw shape color: global_color  ;
			draw "Square" size: 1 color: rgb('black'); 
		}
	}	
		
	species myPolygon{
		const color type: rgb <- [255, 73,0] as rgb;
		geometry shape <- polygon([{17,1.5}, {17.5,1}, {18.5,1}, {19,1.5},{19,2.5},{18.5,3},{17.5,3},{17,2.5}]) ;		
		aspect Flat {
			draw shape color: global_color  ;
			draw "Polygon" size: 1 color: rgb('black');
		}
	}
	
	species myTexture{
		
		aspect image{
    		draw imageRaster size: 2.25;
    	}
	}
	
	//3D Object
	species mySphere{
		const color type: rgb <- [0, 175,100] as rgb;
		geometry shape <- geometry (point([location.x,location.y]));
		aspect Volume {	
			draw shape  color: global_color depth:0.1;
			draw "Sphere" size: 1 color: rgb('black');
		}		
	}
	
	species myPlan{
		const color type: rgb <- [33, 98,120] as rgb;
		geometry shape <- geometry (line ([{5,7.5},{7,5.5}]));		
		aspect Volume {
			draw shape  color: global_color depth:2 ;
			draw "Plan" size: 1 color: rgb('black');
		}
	}
	
	species myMultiPlan{
		const color type: rgb <- [2, 78,104] as rgb;
		geometry shape <- polyline([{9,7},{9,6},{11,6},{11,5}]) ;		
		aspect Volume {
			draw geometry: shape color: global_color depth:2 ;
			draw text: "Multiplan" size: 1 color: rgb('black');
		}
	}
		
	species myCube{
		const color type: rgb <- [255, 131,0] as rgb;	
		geometry shape <- square(2);
		aspect Volume {
			draw shape color: global_color depth:2 border: rgb('blue');
			draw "Cube" size: 1 color: rgb('black');
		}
	}
	

	species myPolyhedron{
		const color type: rgb <- [255, 73,0] as rgb;
		geometry shape <- polygon([{17,5.5}, {17.5,5}, {18.5,5}, {19,5.5},{19,6.5},{18.5,7},{17.5,7},{17,6.5}]);
		aspect Volume {
			draw shape color: global_color depth:2;
			draw "Polyhedron" size: 1 color: rgb('black');
		}
	}
	
	species my3DObject{	
		aspect file{
    		draw (imageRaster) size: 1;
    	}
	} 

}
experiment Display  type: gui {
	output {
		display Display  type:opengl ambient_light:100{//camera_pos:{100*cos(time),100*sin(time),100}{
			
			//image name: 'Background' file: imageRaster.path;
			
			species myPoint aspect:Flat ;
			species myLine aspect:Flat ;
			species myMultiLine aspect:Flat;
			species mySquare aspect:Flat;
			species myPolygon aspect:Flat;
			species myTexture aspect:image;
			
			species mySphere aspect:Volume;
			species myPlan aspect:Volume ;
			species myMultiPlan aspect:Volume;
			species myCube aspect:Volume ; 
			species myPolyhedron aspect:Volume ;
				
			
			species myPoint aspect:Flat position:{0,0,0.5};
			species myLine aspect:Flat position:{0,0,0.5};
			species myMultiLine aspect:Flat position:{0,0,0.5};
			species mySquare aspect:Flat position:{0,0,0.5};
			species myPolygon aspect:Flat position:{0,0,0.5} transparency:0.5;
			species myTexture aspect:image position:{0,0,0.5};
			
			species mySphere aspect:Volume position:{0,0,0.5};
			species myPlan aspect:Volume position:{0,0,0.5};
			species myMultiPlan aspect:Volume position:{0,0,0.5};
			species myCube aspect:Volume position:{0,0,0.5}; 
			species myPolyhedron aspect:Volume position:{0,0,0.5};
						
		}
	}
}
