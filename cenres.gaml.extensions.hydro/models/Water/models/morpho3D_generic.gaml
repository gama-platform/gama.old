/**
 *  morpho3D
 *  Author: Arno
 *  Description: This model displays an awesome simulation of something ...
 */

model morpho3D

global { 

	int width_and_height_of_environment min: 10 <- 10000 ; 
	int nbPointOnSection <- 90;
	int nbSection <-10;
	int lenghtSection <- 1000;
	int meanDepth <-500;


	init { 
		let i <- 1;
		create section number: nbSection { 
			set location <- {width_and_height_of_environment/2, width_and_height_of_environment*(i/nbSection)};
			set start <- point({width_and_height_of_environment/2,0});
			set end <- point({width_and_height_of_environment/2,width_and_height_of_environment});
			set 2DRiver <- line([start,end]);
			do initZSection;
			set i <-i+1;	
		}
	
	}  
} 
 
environment width: width_and_height_of_environment height: width_and_height_of_environment;  
 
  
entities { 
	species section {
		point start;
		point end; 
		 
		//Polyline in x,y
		geometry 2DRiver;
		
		
		//Polyline in x,z
		geometry zSection;
		geometry zSection2;
		geometry lit;
		
		list zPoints of:point;
		list zPoints2 of: point;
		
		
		action initZSection{
			
			loop i from: 0 to: nbPointOnSection {
				
				let z1 <-   (-rnd(100));
				let z2 <-   (-rnd(100));         
                let tmpzPoint type:  point<- {(i/nbPointOnSection)*width_and_height_of_environment,location.y} add_z ((cos((i/nbPointOnSection)*180+90)*meanDepth)+z1);
                
                let tmpzPoint2 type:  point<- {(i/nbPointOnSection)*width_and_height_of_environment,location.y+lenghtSection} add_z ((cos((i/nbPointOnSection)*180+90)*meanDepth)+z2);
                
                add tmpzPoint to: zPoints;
                add tmpzPoint2 to: zPoints2;
               
            }
                        
     
            set zSection <- polyline(zPoints);
            set zSection2 <- polyline(zPoints2);
            let list_tmp type: list of: point <- list(reverse(zSection2.points));

            set lit <- polygon( zSection.points + list_tmp);
         
            

            

		}
		 
		aspect default { 
			draw geometry: 2DRiver color: rgb('blue');
			//draw geometry: zSection color: rgb('blue');
			//draw geometry: zSection2 color: rgb('blue');
			draw geometry: lit color: rgb('blue') empty:true;
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

