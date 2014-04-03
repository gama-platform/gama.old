/**
 *  model1
 *  Author: Arno
 *  Description: 
 */
model model1

global {
	int size <- 1;
	int nbAgent <- 500;
	list<geometry> class <- [circle(2),circle(1.5),circle(1.1),circle(1),circle(0.5),circle(0.4),circle(0.2),circle(0.1),circle(0.09)];
	list code_couleur<- list([rgb(0,0,0)]);
	
	list distribution <-[0.01, 0.05, 0.1, 0.1, 0.5, 0.5 ,0.5, 0.5, 0.5];
	init {
		int i<-0;
		loop times:length(class){
			create cells number: nbAgent*distribution[i] {
			brightness <- 100.0 +rnd(100);
			color <-rgb(255,255,255, brightness);
			let curGeomtype <-i;
			myGeom <- class[i];	
			myClass <- myGeom;
			attributeToSort<-brightness;
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
	geometry myGeom;
	geometry myClass;
	
	aspect default {
		draw myGeom color: rgb(255,255,255,brightness) at: location;
	}

}

experiment Display type: gui {
	output {
		display View1 type: opengl background:rgb(0,0,0){ 
			species cells;
		}
	}

}

