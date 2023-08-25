/**
* Name: Visualisation of the primitive shapes
* Author: Arnaud Grignard
* Description: Model presenting a 3D display with all the primitive shapes existing in GAMA in 2D and 3D, with or without textures. 
* Tags: 3d, shape, texture
*/

model shape   

global {
	
	file gamaRaster <- file('../images/Gama.jpg');
	
	bool emptiness <- false;
	
	int size <- 10 ;
	list<geometry> geometries2D <-[ point([0,0]),circle(size),line ([{0,0},{size,size}]),polyline([{0,0},{size/2,size/2},{0,size}]),line ([{0,0},{size,size}]),polyline([{0,0},{size/2,size/2},{0,size}]),circle(size),square(size),rectangle(size,size*1.5),triangle(size),hexagon(size), square(size) - square(size / 2)];
	list<geometry> texturedGeometries2D <-[point([0,0]), circle(size),line ([{0,0},{size,size}]),polyline([{0,0},{size/2,size/2},{0,size}]),line ([{0,0},{size,size}]),polyline([{0,0},{size/2,size/2},{0,size}]),circle(size),square(size),rectangle(size,size*1.5),triangle(size),hexagon(size),square(size) - square(size / 2) ];	
	list<geometry> geometries3D <-[sphere(size), cone3D(size, size*2),line ([{0,0},{size,size}],1), polyline([{0,0},{size/2,size/2},{0,size}],1),plan ([{0,0},{size,size}],size),polyplan([{0,0},{size/2,size/2},{0,size}],size),cylinder(size,size),cube(size),box(size,size*1.5,size*0.5),pyramid(size),polyhedron([{-1*size/2,0.5*size/2}, {-0.5*size/2,1*size/2}, {0.5*size/2,1*size/2}, {1*size/2,0.5*size/2},{1*size/2,-0.5*size/2},{0.5*size/2,-1*size/2},{-0.5*size/2,-1*size/2},{-1*size/2,-0.5*size/2}],size), cube(size) - cube(size / 2)];
    list<geometry> texturedGeometries <-[sphere(size), cone3D(size, size*2), line ([{0,0},{size,size}],1), polyline([{0,0},{size/2,size/2},{0,size}],1), plan ([{0,0},{size,size}],size),polyplan([{0,0},{size/2,size/2},{0,size}],size),cylinder(size,size),cube(size),box(size,size*1.5,size*0.5),pyramid(size),polyhedron([{-1*size/2,0.5*size/2}, {-0.5*size/2,1*size/2}, {0.5*size/2,1*size/2}, {1*size/2,0.5*size/2},{1*size/2,-0.5*size/2},{0.5*size/2,-1*size/2},{-0.5*size/2,-1*size/2},{-1*size/2,-0.5*size/2}],size), cube(size) - cube(size / 2)];
    
   	int angle <- 0 update: (angle+1) mod 360;
	
	geometry shape <- rectangle(length(geometries3D)*size*2,size*6);

	init { 
		
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
	
	reflex rotate {
		myGeometry <- myGeometry rotated_by (1,{1,1,0});
	}
	
	aspect default {
		draw myGeometry color:#gamaorange at:location border:#gamablue wireframe: emptiness ;
    }
} 

species TexturedGeometry2D{  

	geometry myGeometry;
	file myTexture;
	
	aspect default {
		draw myGeometry texture:myTexture.path at:location rotate: angle::{0,1,0};
    }
} 
    
species Geometry3D{  

	geometry myGeometry;

	reflex rotate {
		myGeometry <- myGeometry rotated_by (-1,{1,0,0});
	}
	aspect default {
		draw myGeometry color:#gamaorange at:location border: #gamablue wireframe: emptiness ;
    }
}

species TexturedGeometry3D{  

	geometry myGeometry;
	file myTexture;

	aspect default {
		draw myGeometry rotate: (-angle::{0,0,1}) texture:myTexture.path at:location ;
    }
}

experiment "3D Shapes"  type: gui {
	parameter "Are geometries empty?" var: emptiness ;
	init {
		gama.pref_display_slice_number <- 64;
		gama.pref_texture_orientation <- false;
	}
	

	output synchronized:true{
		display View1 type:3d background:rgb(10,40,55)   {
			species Geometry2D aspect:default;
			species TexturedGeometry2D aspect:default;
			species Geometry3D aspect:default;
			species TexturedGeometry3D aspect:default;
		}

	}
}
