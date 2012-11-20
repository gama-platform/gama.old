/**
 *  morpho3D
 *  Author: Arno
 *  Description: This model displays an awesome simulation of something ...
 */

model morpho3D

global { 

	int width_and_height_of_environment min: 10 <- 10000 ; 
	int nbPointOnSection parameter: 'Number of point of the section' min: 1 <- 90 category: 'Model';
	int nbSection parameter: "Number of section" min: 1 <-20 category: 'Model';
	int lenghtSection parameter: "Distance between 2 sections" min: 1 <-1000 category: 'Model';
	int meanDepth parameter: "Mean depth" min: 0  max: 500 <-100 category: 'Model';
	int noise parameter: "Noise" min:0 max:100 <-50 category: 'Model';
	
	bool isEmpty parameter: "empty"  <-false category : 'Display';


	init { 
		let i <- 1;
		create section number: nbSection { 
			set location <- {width_and_height_of_environment/2, width_and_height_of_environment*(i/nbSection)};
			do initZSection;
			set i <-i+1;	
		}
	
	}
	
	reflex increaseDepth{
		set meanDepth <- meanDepth + 100;	
	}
	
	reflex increaseNoise{
		//set noise <- noise + 1;
	}  
	  
} 
 
environment width: width_and_height_of_environment height: width_and_height_of_environment;  
 
  
entities { 
	species section {
		
		//Polyline in x,z
		geometry zSection;
		geometry zSection2;
		geometry river_channel;
		
		action initZSection{
			
			let zPoints type:list  of:point <- [];
			let zPoints2 type:list  of:point <- [];
			
			loop i from: 0 to: nbPointOnSection {
				
				let z1_noise <-   (-rnd(noise));
				let z2_noise <-   (-rnd(noise));         
                let tmpzPoint type:  point<- {(i/nbPointOnSection)*width_and_height_of_environment,location.y} add_z ((cos((i/nbPointOnSection)*180+90)*meanDepth)+z1_noise);
                
                let tmpzPoint2 type:  point<- {(i/nbPointOnSection)*width_and_height_of_environment,location.y+lenghtSection} add_z ((cos((i/nbPointOnSection)*180+90)*meanDepth)+z2_noise);
                
                add tmpzPoint to: zPoints;
                add tmpzPoint2 to: zPoints2;
               
            }
                        
     
            set zSection <- polyline(zPoints);
            set zSection2 <- polyline(zPoints2);
            let list_tmp type: list of: point <- list(reverse(zSection2.points));

            set river_channel <- polygon( zSection.points + list_tmp);
		}
		
		reflex update {
			do initZSection;
		}
			 
		aspect default { 
			draw geometry: river_channel color: rgb('blue') empty:isEmpty;
		}
	}
}

experiment morpho type: gui {
	output {
		display morpho refresh_every: 1 type: opengl{
			species section;
		}
	}
}

