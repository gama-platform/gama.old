/**
 *  model1
 *  Author: Arno
 *  Description: 
 */
model model1

global {
	int size <- 1;
	int nbAgent <- 100;
	list<rgb> class <- [°orange,°white,°green,°yellow,°red];
	list code_couleur<- list([rgb(0,0,0)]);
	
	init {
		int i<-0;
		loop times:length(class){
			create cells number: nbAgent{
			brightness <- 100.0 +rnd(100);
			color <-class[i];
			let curGeomtype <-i;
			myGeom <- sphere(1);	
			attributeToSort<-brightness;
			myClass <- class[i];
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
	rgb myClass;
	
	aspect default {
		draw sphere(size) color: myClass at: location;
	}

}

experiment Display type: gui {
	output {
		display View1 type: opengl ambient_light:10 diffuse_light:75 background:rgb(0,0,0){ 
			species cells;
		}
	}

}

