/**
 *  gridloading
 *  Author: patricktaillandier
 *  Description: 
 */

model gridloading

global {
	file grid_file <- file("../includes/asc_grid/hab10_10.asc");
	map colors <- map([1:: hsb(60,0.1,0.5), 2:: hsb(60,0.2,0.5),3:: hsb(60,0.3,0.5),4:: hsb(60,0.4,0.5),5:: hsb(60,0.5,0.5),6:: hsb(60,0.6,0.5),7:: hsb(60,0.7,0.5),8:: hsb(60,0.8,0.5),9:: hsb(60,0.9,0.5),10:: hsb(60,1.0,0.5) ]);
	geometry shape <- envelope(grid_file);
}

entities {
	grid cell file: grid_file {
		init {
			//write " grid_value : " + grid_value;
			color <- colors at int(grid_value);
		}
	}
}

experiment gridloading type: gui{
	output {
		display main2 type:opengl{
			grid cell texture:true;
		}
		display main{
			grid cell ;
		}
	}
}
