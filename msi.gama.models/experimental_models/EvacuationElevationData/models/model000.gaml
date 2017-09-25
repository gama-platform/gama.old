/**
* Name: model001
* Author: gama
* Description: Describe here the model and its experiments
* Tags: Tag1, Tag2, TagN
*/

model model000

global {
	/** Insert the global definitions, variables and actions here */
	file my_csv_file <- csv_file("../includes/02_MienTrung.xyz","\t");
	init{
		
		matrix data <- matrix(my_csv_file);
		float minx <- 100000000000.0;
		float miny <- 100000000000.0;
		float minz <- 100000000000.0;
		
		float maxx <- 0.0;
		float maxy <- 0.0;
		float maxz <- 0.0;
		
		//loop on the matrix rows (skip the first header line)
		loop i from: 1 to: data.rows -1{
			//loop on the matrix columns
			float x <- float(data[0,i]);
			float y <- float(data[1,i]);
			
			//float x <- measure(0.0, 0.0, float(data[0,i]), 0.0);
			//float y <- measure(0.0, 0.0, 0.0, float(data[1,i]));
			float z <- float(data[2,i]);
			if (x < minx){
				minx <-x;
			}
			if (x > maxx){
				maxx <- x;
			}
			if (y < miny){
				miny <- y;
			}
			if (y > maxy){
				maxy <- y;
			}
			if (z < minz){
				minz <- z;
			}
			if (z > maxz){
				maxz <- z;
			}
			//write " " + x + " " + y + " " + z;
			/*  *
			create mypoint number:1{
				location <- {x,y,z};
			}
			/* */	
		}	
		list l <- [minx, miny, minz, maxx, maxy, maxz];
		write " " + l;
	}
	float measure(float lat1, float lon1, float lat2, float lon2){  // generally used geo measurement function
	    float R <- 6378.137; // Radius of earth in KM
	    float dLat <- lat2 * #pi / 180 - lat1 * #pi / 180;
	    float dLon <- lon2 * #pi / 180 - lon1 * #pi / 180;
	    float a <- sin(dLat/2) * sin(dLat/2) +
	    cos(lat1 * #pi / 180) * cos(lat2 * #pi / 180) *
	    sin(dLon/2) * sin(dLon/2);
	    float c <- 2 * atan2(sqrt(a), sqrt(1-a));
	    float d <- R * c;
	    return d * 1000; // meters
	}
}

species mypoint {	
	aspect base {
		draw shape color: color ;
	}
}

experiment model001 type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
		display city_display type:opengl {
			species mypoint aspect: base ;
		}
	}
}
