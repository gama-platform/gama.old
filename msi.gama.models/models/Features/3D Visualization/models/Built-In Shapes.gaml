/**
* Name: Visualisation of the primitive shapes
* Author: Arnaud Grignard
* Description: Model presenting a 3D display with all the primitive shapes existing in GAMA in 2D and 3D, with or without textures. 
* Tag: 3D Display, Shapes, Textures
*/

model shape   

global {
	
	file gamaRaster <- file('../images/Gama.png');
	
	int size <- 10;
	list<geometry> geometries2D <-[point([0,0]),line ([{0,0},{size,size}]),polyline([{0,0},{size/2,size/2},{0,size}]),circle(size),square(size),rectangle(size,size/2),triangle(size),hexagon(size)];
	list<geometry> geometries3D <-[sphere(size/2),plan ([{0,0},{size,size}],size),polyplan([{0,0},{size/2,size/2},{0,size}],size),cylinder(size,size),cube(size),box(size,size*1.5,size*0.5),pyramid(size),polyhedron([{-1*size/2,0.5*size/2}, {-0.5*size/2,1*size/2}, {0.5*size/2,1*size/2}, {1*size/2,0.5*size/2},{1*size/2,-0.5*size/2},{0.5*size/2,-1*size/2},{-0.5*size/2,-1*size/2},{-1*size/2,-0.5*size/2}],size)];
    
    //The textures can be applied on sphere, cylinder, quad and rectangle
    list<geometry> texturedGeometries <-[sphere(size/2),plan ([{0,0},{size,size}],size),polyplan([{0,0},{size/2,size/2},{0,size}],size),cylinder(size,size),cube(size),box(size,size*1.5,size*0.5),point([0,0]),polyhedron([{-1*size/2,0.5*size/2}, {-0.5*size/2,1*size/2}, {0.5*size/2,1*size/2}, {1*size/2,0.5*size/2},{1*size/2,-0.5*size/2},{0.5*size/2,-1*size/2},{-0.5*size/2,-1*size/2},{-1*size/2,-0.5*size/2}],size)];
    
   
	
	geometry shape <- rectangle(length(geometries3D)*size*2,size*8);

	init { 
		
		int curGeom2D <-0;
		create Geometry2D number: length(geometries2D){ 
			location <- {size+curGeom2D*size*2, size*1.5, 0};	
			myGeometry <- geometries2D[curGeom2D];
			curGeom2D <- curGeom2D+1;
		}
		
		int curGeom3D <-0;
		create Geometry3D number: length(geometries3D){ 
			location <- {size+curGeom3D*size*2, size*4, 0};	
			myGeometry <- geometries3D[curGeom3D];
			curGeom3D <- curGeom3D+1;
		} 
		
		int curTextGeom <-0;
		create TexturedGeometry3D number: length(texturedGeometries){ 
			location <- {size+curTextGeom*size*2, size*6.5, 0};	
			myGeometry <- texturedGeometries[curTextGeom];
			myTexture <- gamaRaster;
			curTextGeom <- curTextGeom+1;
		}
	}  
} 
 
species Geometry2D{  

	geometry myGeometry;
	
	aspect default {
		draw myGeometry color:°orange at:location;
    }
} 
    
species Geometry3D{  

	geometry myGeometry;

	aspect default {
		draw myGeometry color:°orange at:location;
    }
}

species TexturedGeometry3D{  

	geometry myGeometry;
	file myTexture;

	aspect default {
		draw myGeometry texture:myTexture.path at:location;
    }
}

species Pie3D{  

	geometry myGeometry;

	aspect default {
		draw myGeometry at:location;
    }
}

experiment Display  type: gui {
	output {
		display View1 type:opengl diffuse_light:100 background:rgb(10,40,55) {
			species Geometry2D aspect:default;
			species Geometry3D aspect:default;
			species TexturedGeometry3D aspect:default;
		}

	}
}




