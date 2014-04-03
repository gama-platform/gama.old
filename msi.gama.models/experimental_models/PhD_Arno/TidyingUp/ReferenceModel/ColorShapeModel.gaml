/**
 *  model1
 *  Author: Arnaud Grignard
 *  Description: 
 */
model ShapeModel

global {
	float size <- 2.0;
	int nbAgent <- 200;
	
	list<rgb> class <- [°red, °green, °blue];
	list code_couleur<- list([rgb(255,255,255), rgb(255,230,0), rgb(255,247,153),rgb(45,137,185), rgb(68,199,244)]);	
	list distribution <-[0.3, 0.2, 0.1];
	init {
		int i<-0;
		loop times:length(class){
			create cells number: nbAgent*distribution[i] {
			myGeom <- circle(size);	
			mySize <-size * (1+rnd(10))/10.0;
			color <- class[i];		
			myClass <-class[i];
			attributeToSort<-mySize;		
		}
			i<-i+1;
		}
		

	}
}

species cells {
	rgb color;
	float mySize;
	float attributeToSort;
	geometry myGeom;
	rgb myClass;
	
	aspect default {
		draw myGeom  scaled_by mySize color: color border:°black at: location;
	}

}

experiment Display type: gui {
	output {
		display View1 type: opengl{ 
			species cells;
		}
	}

}

