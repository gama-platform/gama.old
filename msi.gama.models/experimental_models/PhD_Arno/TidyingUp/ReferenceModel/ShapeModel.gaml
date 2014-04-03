/**
 *  model1
 *  Author: Arnaud Grignard
 *  Description: 
 */
model ShapeModel

global {
	float size <- 1.0;
	int nbAgent <- 1000;
	
	list<geometry> class <- [circle(size), square(size), triangle(size), hexagon(size)];
	
	list code_couleur<- list([rgb(255,255,255), rgb(255,230,0), rgb(255,247,153),rgb(45,137,185), rgb(68,199,244)]);
	
	list distribution <-[0.1, 0.1, 0.1, 0.1];
	init {
		int i<-0;
		loop times:length(class){
			create cells number: nbAgent*distribution[i] {
			myGeom <- class[i];	
			saturation <- float(rnd(255));
			mySize <- size;//size + size * rnd(100) / 100.0;
			color <- rgb(rgb(code_couleur[i+1]).red,rgb(code_couleur[i+1]).green,rgb(code_couleur[i+1]).blue,saturation);		
			myClass <-myGeom;
			attributeToSort<-saturation;		
		}
			i<-i+1;
		}
		

	}
}

species cells {
	rgb color;
	float saturation;
	float mySize;
	float attributeToSort;
	geometry myGeom;
	geometry myClass;
	
	aspect default {
		//draw circle(2) color:rgb(255,230,0,125);
		draw myGeom scaled_by mySize color: color border:color at: location;
	}

}

experiment Display type: gui {
	output {
		display View1 type: opengl background: rgb(code_couleur[0]){ 
			species cells;
		}
	}

}

