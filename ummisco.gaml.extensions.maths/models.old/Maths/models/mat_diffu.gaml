/**
 *  matdiffu
 *  Author: hqnghi
 *  Description: 
 */

model matdiffu

/* Insert your model definition here */
global{
	int matsize<-100;
	geometry shape<-envelope(square(matsize));
	matrix mymask<-matrix(file("../images/mask.bmp"));
	
	matrix<float> mat<-	matrix([
//		[0, -1, 0],
//		[-1, 5, -1],
//		[0, -1, 0]
		
		[1/7, 0, 0, 0, 0],
		[1/7, 0, 0, 0, 0],
		[1/9,1.0,1.0,1.0,0.1],
		[1/7, 0, 0, 0, 0],
		[1/7, 0, 0, 0, 0]
		
		
//		[0, 0, 0],
//		[0,1/9,2/9],
//		[0,2/9,4/9]
	]);
	init{
		ask mycell at {matsize/2,matsize/2}{
			phero<-255;
		}
	}
//	reflex adding_new when:flip(0.05){
//		ask mycell at {rnd(matsize),rnd(matsize)}{
//			phero<-255;
//		}
//	}
	reflex do_diffu{
		
//		write ""+(mymask);
		diffusion var:phero on:mycell mat_diffu:mat cycle_length:5 mask:mymask; 
	}
}

entities{
	grid mycell width:matsize height:matsize{
		float phero<-0.0;		
//		float phero<-( (grid_x = 0) or (grid_y = 0) or (grid_x = (matsize - 1)) or (grid_y = (matsize - 1)) ) ?  0.0 : 1.0;
		aspect base{
			draw shape color:rgb(0,phero*10,0) depth: phero mod 10;
		}
	}
	
}

experiment exp1 type:gui{
	output{
		display "MAT_DIFFU"   type:opengl{
			species mycell aspect: base;
		}
	}
	
}