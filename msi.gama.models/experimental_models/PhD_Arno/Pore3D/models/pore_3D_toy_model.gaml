model pore3D   

/**
 *  AugmentedGrid
 *  Author: Arnaud Grignard
 *  Description: Initialize a grid with a random value between 0 and 255
 *  In TextDisplay only the value of the cell is displayed as a text
 *  In AugmentedDisplay the value of the cell is displayed:
 * 		1: Circle with a radius equal to the cellValue
 * 		2: Blue colored square
 *      3: Elevation + blue color
 * 		4: Elevation + hsb color
 */

global {
	//graph myGraph;
	
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 1000 ;
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 100 ;  
	float distance <-100.0;
	float co2 <-0.1;
	
	file histogramm <- file('./../images/histogram.png');


	init { 
		create pore number: number_of_agents { 
			set location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment),rnd(width_and_height_of_environment)};
			radius <- rnd(100)/100.0;
			fructose <-radius;
			loop i from:0 to: length(bacterias)-1{
				bacterias[i]<-rnd(100);
			}
			
		} 
	} 
} 
  

 

species pore{ 
	rgb color; 
    float radius;	
    float fructose;    
    list<float> bacterias <- list_with (3,0.0);
    bool macroRepresentation <-true;
    
    action changeAspect{
    	macroRepresentation <-!macroRepresentation;
    }
    
    user_command "Change aspect" action: changeAspect;
    
    reflex update{
    	loop i from:0 to:length(bacterias)-1{
    	    fructose <- fructose-fructose*0.001*bacterias[i];
    	    
    	    
    	    
    	    
    	    co2 <-	co2 + bacterias[i]*0.0001;
    	    //write co2;
    	}
    }
    
	aspect default {
	  draw sphere(radius) color: °blue;	
    }
    
    aspect fructose {
    	if(macroRepresentation){
    		 draw sphere(radius) color: hsb(0.66,fructose,0.5);
    	}
    	else{
    		 draw sphere(radius) color: hsb(0.66,fructose,0.5);
    		 draw square(radius) texture:histogramm.path at:{location.x+radius,location.y-radius,location.z};
    		
    		 /*draw rectangle(radius,bacterias[0]/100.0) color: °blue at:{location.x,location.y,location.z};
    		 draw rectangle(radius,bacterias[1]/100.0) color: °blue at:{location.x+radius,location.y,location.z};
    		 draw rectangle(radius,bacterias[2]/100.0) color: °blue at:{location.x+2*radius,location.y,location.z};*/
    	}
	 
    }
}
	


experiment Pore3D  type:gui {
	output {
		display Pore3D type:opengl diffuse_light:100 ambient_light:10 background:rgb(255-co2,255-co2,255-co2){
			species pore aspect:fructose;
		}
	}
}
