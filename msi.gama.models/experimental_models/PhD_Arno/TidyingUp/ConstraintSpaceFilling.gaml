/**
 *  model1
 *  Author: Arno
 *  Description: 
 */
model abstractCleanUp

import "./ReferenceModel/OneClassModel.gaml"
global {

	string layoutType <- "lineLayout" among:["fillup","lineLayout","unifromlayout"];

	init {
		
		
	}
	
	reflex update{
		if(cycle>1){
			do Spiral;
		}
	}

	
	action randomDistributed{
		ask abstractCells{
		  location <-{rnd(100),rnd(100)};
		}
	}
	
	action Distributed{
	  	float size <- sqrt((world.shape.width^2 * 3.14159265359) /(length(abstractCells)));
	  	write "size" + size;
	    list<geometry> rectangles <- list<geometry>(to_rectangles(world.shape, {size,size}, false));
	    
	    loop while: (length(rectangles) < length(abstractCells)) {
	      size <- size * 0.99;
	      rectangles <- list<geometry>(to_rectangles(world.shape, {size,size}, false));
	     }
	       
		int i1 <- 0;
	    ask abstractCells{
	        location <- (rectangles[i1]).location;
	        i1 <- i1+ 1;
	    }
	}
	
	action Spiral{
		int i<-0;
		ask abstractCells{
		  location <-{world.shape.width/2 + i/2*cos((i/10)*90),world.shape.height/2 + i/2*sin((i/10)*90)};
		  i<-i+1;
		}
	}


	



}

species abstractCells mirrors: list(cells) {
	aspect abstract {
	  draw cells(target).myGeom scaled_by (cells(target).mySize) color: cells(target).color border: °black  at: location;	  	
	}

}




experiment Display type: gui {
	parameter "Layout Type" var:layoutType category:"Init";
	output {
		display AbstractView  type:opengl ambient_light:100  background:rgb(code_couleur[0]) draw_env:false{ 
		species cells;
		species abstractCells aspect: abstract position: { world.shape.width * 1.2, 0.0, 0 };
		}
	}

}

