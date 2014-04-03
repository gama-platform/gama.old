/**
 *  model1
 *  Author: Arno
 *  Description: 
 */
model model1

global {
	int size <- 1;
	int nbAgent <- 500;
	list<float> class <- [100,75,50,10]; //representing the depth of the cells
	list code_couleur<- list([rgb(0,0,0)]);
	
	list distribution <-[0.01, 0.05, 0.1, 0.1, 0.5, 0.5 ,0.5, 0.5, 0.5];
	init {
		int i<-0;
		loop times:length(class){
			create cells number: nbAgent*distribution[i] {
			brightness <- 100.0 +rnd(100);
			color <-rgb(255,255,255, brightness);
			let curGeomtype <-i;
			myGeom <-square(1+rnd(10));
			myDepth <- class[i];	
			myClass <- myDepth;
			attributeToSort<-myDepth;
		}
			i<-i+1;
		}
		

	}
}

species cells {
	rgb color;
	float brightness;
	float mySize;
	float attributeToSort;
	float myDepth;
	geometry myClass;
	geometry myGeom;
	
	aspect default {
		draw myGeom depth:myDepth color: rgb(255,255,255,brightness) at: location;
	}

}

experiment Display type: gui {
	output {
		display View1 type: opengl background:rgb(0,0,0){ 
			species cells;
		}
	}

}

