/**
* Name: Light definition
* Author: Julien Mazars
* Description: Model presenting how to manipulate lights in your 3D display (espetially spot lights and point lights)
* Tags: 3d, light
*/

model shape   

global {
	
	file gamaRaster <- file('../images/Gama.png');
	
	int size <- 10;
	list<geometry> geometries2D <-[point([0,0]),line ([{0,0},{size,size}]),polyline([{0,0},{size/2,size/2},{0,size}]),circle(size),square(size),rectangle(size,size*1.5),triangle(size),hexagon(size)];
	list<geometry> texturedGeometries2D <-[point([0,0]),line ([{0,0},{size,size}]),polyline([{0,0},{size/2,size/2},{0,size}]),circle(size),square(size),rectangle(size,size*1.5),triangle(size),hexagon(size)];	
	list<geometry> geometries3D <-[sphere(size/2),plan ([{0,0},{size,size}],size),polyplan([{0,0},{size/2,size/2},{0,size}],size),cylinder(size,size),cube(size),box(size,size*1.5,size*0.5),pyramid(size),polyhedron([{-1*size/2,0.5*size/2}, {-0.5*size/2,1*size/2}, {0.5*size/2,1*size/2}, {1*size/2,0.5*size/2},{1*size/2,-0.5*size/2},{0.5*size/2,-1*size/2},{-0.5*size/2,-1*size/2},{-1*size/2,-0.5*size/2}],size)];
    list<geometry> texturedGeometries <-[sphere(size/2),plan ([{0,0},{size,size}],size),polyplan([{0,0},{size/2,size/2},{0,size}],size),cylinder(size,size),cube(size),box(size,size*1.5,size*0.5),pyramid(size),polyhedron([{-1*size/2,0.5*size/2}, {-0.5*size/2,1*size/2}, {0.5*size/2,1*size/2}, {1*size/2,0.5*size/2},{1*size/2,-0.5*size/2},{0.5*size/2,-1*size/2},{-0.5*size/2,-1*size/2},{-1*size/2,-0.5*size/2}],size)];
    
   
	
	geometry shape <- rectangle(length(geometries3D)*size*2,size*6);

	init { 
		create lightMoving number:2;
		int curGeom2D <-0;
		create Geometry2D number: length(geometries2D){ 
			location <- {size+curGeom2D*size*2, 0, 0};	
			myGeometry <- geometries2D[curGeom2D];
			curGeom2D <- curGeom2D+1;
		}
		
		int curTextGeom2D <-0;
		create TexturedGeometry2D number: length(texturedGeometries2D){ 
			location <- {size+curTextGeom2D*size*2, size*2, 0};	
			myGeometry <- texturedGeometries2D[curTextGeom2D];
			myTexture <- gamaRaster;
			curTextGeom2D <- curTextGeom2D+1;		
		}
		
		int curGeom3D <-0;
		create Geometry3D number: length(geometries3D){ 
			location <- {size+curGeom3D*size*2, size*4.0, 0};	
			myGeometry <- geometries3D[curGeom3D];
			curGeom3D <- curGeom3D+1;
		} 
		
		int curTextGeom <-0;
		create TexturedGeometry3D number: length(texturedGeometries){ 
			location <- {size+curTextGeom*size*2, size*6.0, 0};	
			myGeometry <- texturedGeometries[curTextGeom];
			myTexture <- gamaRaster;
			curTextGeom <- curTextGeom+1;
		}
	}  
} 
 
species Geometry2D{  

	geometry myGeometry;
	
	aspect default {
		draw myGeometry color:°white at:location border:#red;
    }
} 

species TexturedGeometry2D{  

	geometry myGeometry;
	file myTexture;
	
	aspect default {
		draw myGeometry texture:myTexture.path at:location border:#red;
    }
} 
    
species Geometry3D{  

	geometry myGeometry;

	aspect default {
		draw myGeometry color:°white at:location border:#red;
    }
}

species TexturedGeometry3D{  

	geometry myGeometry;
	file myTexture;

	aspect default {
		draw myGeometry texture:myTexture.path at:location border:#red;
    }
}

species lightMoving skills:[moving] {
	reflex update {
		do wander amplitude:180.0;
	}
}

experiment Display  type: gui {
	output {
		// display using spot lights
		// we set the ambient light to 0 to see better the directional lights (as if we were at night time)
		display SpotLights type:opengl  background:rgb(10,40,55) ambient_light:0 {
			// we define 3 lights : the blue and red turn around the scene, changing their orientation so that the scene is always lightened
			// the green light does not change its position, but the angle of the spot changes
			light 1 type:spot position:{world.shape.width*cos(cycle)+world.shape.width/2,world.shape.height*sin(cycle)+world.shape.height/2,20} direction:{cos(cycle+180),sin(cycle+180),-1} color:#red draw_light:true quadratic_attenuation:0.0001;
			light 2 type:spot position:{world.shape.width*cos(cycle+180)+world.shape.width/2,world.shape.height*sin(cycle+180)+world.shape.height/2,20} direction:{cos(cycle),sin(cycle),-1} color:#blue draw_light:true quadratic_attenuation:0.0001;
			light 3 type:spot position:{world.shape.width/2,world.shape.height/2,world.shape.width/2} direction:{0,0,-1} color:#green draw_light:true spot_angle:30*(1+cos(2*cycle)) quadratic_attenuation:0.0001;
			species Geometry2D aspect:default;
			species TexturedGeometry2D aspect:default;
			species Geometry3D aspect:default;
			species TexturedGeometry3D aspect:default;
		}
		// display using point lights
		// we set the ambient light to 0 to see better the directional lights (as if we were at night time)
		display PointLights type:opengl  background:rgb(10,40,55) ambient_light:0 {
			// we define 3 lights : the blue and red turn around the scene
			// the green light change its location up and down, we can see the quadratic_attenuation effect : the farther the light is, the less power it has			
			light 1 type:point position:{world.shape.width*cos(cycle)+world.shape.width/2,world.shape.height*sin(cycle)+world.shape.height/2,20} color:#red draw_light:true quadratic_attenuation:0.0001;
			light 2 type:point position:{world.shape.width*cos(cycle+180)+world.shape.width/2,world.shape.height*sin(cycle+180)+world.shape.height/2,20} color:#blue draw_light:true quadratic_attenuation:0.0001;
			light 3 type:point position:{world.shape.width/2,world.shape.height/2,world.shape.width*cos(cycle)} color:#green draw_light:true quadratic_attenuation:0.0001;
			species Geometry2D aspect:default;
			species TexturedGeometry2D aspect:default;
			species Geometry3D aspect:default;
			species TexturedGeometry3D aspect:default;
		}
		// display using direction lights
		// we set the ambient light to 0 to see better the directional lights (as if we were at night time)
		display DirectionLights type:opengl  background:rgb(10,40,55) ambient_light:0 {
			// we define 3 lights : the blue and red change their direction
			// the green light change its intensity
			light 1 type:direction direction:{cos(cycle+180),sin(cycle+180),-1} color:#red draw_light:true;
			light 2 type:direction direction:{cos(cycle),sin(cycle),-1} color:#blue draw_light:true;
			light 3 type:direction direction:{0,0,-1} color:rgb(0,255*(1+cos(cycle)),0) draw_light:true;
			species Geometry2D aspect:default;
			species TexturedGeometry2D aspect:default;
			species Geometry3D aspect:default;
			species TexturedGeometry3D aspect:default;
		}
	}
}