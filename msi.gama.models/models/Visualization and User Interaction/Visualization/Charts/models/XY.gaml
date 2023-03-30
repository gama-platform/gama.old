/**
* Name: XY Examples
* Author: Patrick Taillandier
* Description: A demonstration of how to display a simple xy chart
* Tags: gui, chart
*/


model example_xy

global {
	
	//values that will be recomputed at each simulation step
	float x <- rnd(10.0) update: rnd(10.0);
	float y <- a_function(x) update: a_function(x);
	
	//contains a serie of x,y values
	list<float> serie_x;
	list<float> serie_y;
	
	//function that we want to display
	float a_function(float val) {
		return val ^2;
	}
	
	//at the init of the simulation, we fill the serie_x and serie_y with 101 values
	init {
		loop i from: 0 to: 100 {
			float v <- (i / 50.0) - 1;
			serie_x << v;
			serie_y << a_function(v);
		}		
	}
}

experiment main {
	output {
		display charts  type: 2d {
			//chart displaying the values of serie_y according to serie_x (for i from 0 to 100, x = serie_x[i], y = serie_y[i])
			chart "serie_x and serie_y" type: xy size: {1.0,0.5}{
				 data legend: "x" value:rows_list(matrix([serie_x,serie_y])) ;
			}
			
			//at each simulation step, display the value of y according to the value of x
			chart "x and y" type: xy size: {1.0,0.5} position: {0,0.5}{
				 data legend: "x" value:[x,y] line_visible: false color: #green;
			}
		}
	}
}