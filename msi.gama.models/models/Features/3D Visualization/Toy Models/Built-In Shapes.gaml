/**
 *  primitive_shape
 *  Author: Arnaud Grignard
 *  Description: Display the basic 3D shape in an opengl display
 */

model shape   

global {
	
	int size <- 10;
	list<geometry> geometries2D <-[point([0,0]),line ([{0,0},{size,size}]),polyline([{0,0},{size/2,size/2},{0,size}]),circle(size),square(size),rectangle(size,size/2),triangle(size),rgbtriangle(size),polygon([{-1*size/2,0.5*size/2}, {-0.5*size/2,1*size/2}, {0.5*size/2,1*size/2}, {1*size/2,0.5*size/2},{1*size/2,-0.5*size/2},{0.5*size/2,-1*size/2},{-0.5*size/2,-1*size/2},{-1*size/2,-0.5*size/2}])];
	list<geometry> geometries3D <-[sphere(size/5),plan ([{0,0},{size,size}],size),polyplan([{0,0},{size/2,size/2},{0,size}],size),cylinder(size,size),cube(size),box(size,size*1.5,size*0.5),pyramid(size),rgbcube(size),polyhedron([{-1*size/2,0.5*size/2}, {-0.5*size/2,1*size/2}, {0.5*size/2,1*size/2}, {1*size/2,0.5*size/2},{1*size/2,-0.5*size/2},{0.5*size/2,-1*size/2},{-0.5*size/2,-1*size/2},{-1*size/2,-0.5*size/2}],size)];

	geometry shape <- rectangle(length(geometries3D)*size*2,size*8);
	
	file imageRaster <- file('images/Gama.png');

	init { 
		
		int curGeom2D <-0;
		create Geometry2D number: length(geometries2D){ 
			location <- {curGeom2D*size*2, 0, 0};	
			myGeometry <- geometries2D[curGeom2D];
			curGeom2D <- curGeom2D+1;
		}
		
		int curGeom3D <-0;
		create Geometry3D number: length(geometries3D){ 
			location <- {curGeom3D*size*2, size*2, 0};	
			myGeometry <- geometries3D[curGeom3D];
			curGeom3D <- curGeom3D+1;
		} 
		
		create texture2D number:1 {
			location <- {0,size*4,0};
		}
		
		create texture3D number:1 {
			location <- {0,size*6,0};
		}
	}  
} 
 
species Geometry2D{  

	geometry myGeometry;
	
	aspect default {
		draw myGeometry color:rgb('white') at:location;
    }
} 
    
species Geometry3D{  

	geometry myGeometry;

	aspect default {
		draw myGeometry color:rgb('white') at:location;
    }
}

species texture2D{	
	aspect default{
		draw imageRaster size:size;
	}
}
    	
species texture3D{
	aspect default{
		draw cube(size) texture:imageRaster.path;
	}
}



experiment Display  type: gui {
	output {
		display View1 type:opengl  diffuse_light:(100) background:rgb(10,40,55) {
			species Geometry2D;
			species Geometry3D;
			species texture2D;
			species texture3D;
		}
	}
}





