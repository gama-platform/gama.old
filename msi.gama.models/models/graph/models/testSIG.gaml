/**
 *  testSIG
 *  Author: arnaudgrignard
 *  Description: 
 */
model testSIG
global {

}
environment{}  
entities {
	species object  {
		aspect default {
			draw shape: geometry color: 'gray' ;
		}
	}
	species object_simple  {
		rgb color <- rgb([rnd(255),rnd(255),rnd(255)]);
		aspect default {
			draw shape: geometry color: color ;
		}
	}
}
output {

	graphdisplaygl {
		
	}
}
